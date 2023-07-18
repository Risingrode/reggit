package com.itheima.reggie.controller;

// 套餐管理

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.R;
import com.itheima.reggie.dto.DishDto;
import com.itheima.reggie.dto.SetmealDto;
import com.itheima.reggie.entity.*;
import com.itheima.reggie.service.CategoryService;
import com.itheima.reggie.service.DishService;
import com.itheima.reggie.service.SetmealDishService;
import com.itheima.reggie.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/setmeal")
public class SetmealController {

    @Autowired
    private SetmealService setmealService;

    @Autowired
    private SetmealDishService setmealDishService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private DishService dishService;

    // 新增套餐，涉及两张表的操作：套餐表和菜品表；
    @PostMapping
    @CacheEvict(value = "setmealCache",allEntries = true )
    public R<String> save(@RequestBody SetmealDto setmealDto){
        setmealService.saveWithDish(setmealDto);
        return R.success("新增套餐成功");
    }

    // 套餐分页查询
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name){
        Page<Setmeal> pageInfo = new Page<>(page,pageSize);
        Page<SetmealDto> dtoPage = new Page<>(page,pageSize);
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(name != null,Setmeal::getName,name);
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);
        setmealService.page(pageInfo,queryWrapper);
        //对象的拷贝  注意这里要把分页数据的·全集合·records给忽略掉
        BeanUtils.copyProperties(pageInfo,dtoPage,"records");
        List<Setmeal> records = pageInfo.getRecords();
        //对records对象进行处理然后封装好赋值给list
        // TODO： 看不太懂了，主要是架构不太明白
        List<SetmealDto> list = records.stream().map((item)->{
            SetmealDto setmealDto = new SetmealDto();
            BeanUtils.copyProperties(item,setmealDto);
            Long categoryId = item.getCategoryId();
            Category category = categoryService.getById(categoryId);
            if (category != null){
                String categoryName = category.getName();
                setmealDto.setCategoryName(categoryName);
            }
            return setmealDto;
        }).collect(Collectors.toList());
        dtoPage.setRecords(list);
        return R.success(dtoPage);
    }

    // 套餐删除，删除套餐的时候是要首先判断删除的数据有没有关联数据,所以这里也涉及到了两张表
    @DeleteMapping
    @CacheEvict(value = "setmealCache",allEntries = true )
    public R<String> delete(@RequestParam List<Long> ids){
        setmealService.removeWithDish(ids);
        return R.success("套餐数据删除成功");
    }

    // 根据条件查询套餐数据
    @GetMapping("/list")
    //注意这里我们使用的是从参数中获取数据来生成动态的key,并且加了这个缓存的注解后，返回的对象对应的实体类必须要实现了序列化，否则会报错
    @Cacheable(value = "setmealCache", key="#setmeal.categoryId+'_'+ #setmeal.status")
    public R<List<Setmeal>> list(Setmeal setmeal){
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(setmeal.getCategoryId()!=null,Setmeal::getCategoryId,setmeal.getCategoryId());
        queryWrapper.eq(setmeal.getStatus()!=null,Setmeal::getStatus,setmeal.getStatus());
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);
        List<Setmeal> list = setmealService.list(queryWrapper);
        return R.success(list);
    }

    // 对菜品批量或者是单个 进行停售或者是起售
    @PostMapping("/status/{status}")
    //这个参数这里一定记得加注解才能获取到参数，否则这里非常容易出问题
    public R<String> status(@PathVariable("status") Integer status,@RequestParam List<Long> ids){
        setmealService.updateSetmealStatusById(status,ids);
        return R.success("售卖状态修改成功");
    }

    // 回显套餐数据：根据套餐id查询套餐
    @GetMapping("/{id}")
    public R<SetmealDto> getData(@PathVariable Long id){
        SetmealDto setmealDto = setmealService.getData(id);
        return R.success(setmealDto);
    }

    // 修改套餐信息
    @PutMapping
    public R<String> edit(@RequestBody SetmealDto setmealDto){
        if (setmealDto==null){
            return R.error("请求异常");
        }
        if (setmealDto.getSetmealDishes()==null){
            return R.error("套餐没有菜品,请添加套餐");
        }
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        Long setmealId = setmealDto.getId();
        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SetmealDish::getSetmealId,setmealId);
        setmealDishService.remove(queryWrapper);
        for (SetmealDish setmealDish : setmealDishes) {
            setmealDish.setSetmealId(setmealId);
        }
        setmealDishService.saveBatch(setmealDishes);
        setmealService.updateById(setmealDto);
        return R.success("套餐修改成功");
    }

    // 获取指定套餐 ID (SetmealId) 对应的菜品列表
    @GetMapping("/dish/{id}")
    public R<List<DishDto>> dish(@PathVariable("id") Long SetmealId){
        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SetmealDish::getSetmealId,SetmealId);
        List<SetmealDish> list = setmealDishService.list(queryWrapper);
        List<DishDto> dishDtos = list.stream().map((setmealDish) -> {
            DishDto dishDto = new DishDto();
            // 根据属性的名称和类型来匹配源对象和目标对象的属性。如果属性名称相同且类型不一致，会尝试进行类型转换。
            BeanUtils.copyProperties(setmealDish, dishDto);
            Long dishId = setmealDish.getDishId();
            Dish dish = dishService.getById(dishId);
            // 这两次拷贝并不是多余的，而是分别从不同对象中获取属性值，然后复制到 dishDto 对象中，确保 dishDto 包含了所有需要的属性。
            BeanUtils.copyProperties(dish, dishDto);
            return dishDto;
        }).collect(Collectors.toList());
        return R.success(dishDtos);
    }
}

package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.R;
import com.itheima.reggie.dto.DishDto;
import com.itheima.reggie.entity.*;
import com.itheima.reggie.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/dish")
@Slf4j
public class DishController {

    @Autowired
    private DishService dishService;// 菜品

    @Autowired
    private SetmealDishService setmealDishService;// 套餐菜品服务

    @Autowired
    private SetmealService setmealService;// 套餐  这个菜品是属于哪一个套餐

    @Autowired
    private DishFlavorService dishFlavorService;// 菜品口味 微辣 中辣 重辣

    @Autowired
    private CategoryService categoryService;// 分类  川菜 粤菜 饮品

    @Autowired
    private RedisTemplate redisTemplate;// redis模板

    // 新增菜品
    @PostMapping
    public R<String> save(@RequestBody DishDto dishDto) {
        dishService.saveWithFlavor(dishDto);
        //清理某个分类下面的菜品缓存
        String key = "dish_" + dishDto.getCategoryId() + "_1";
        redisTemplate.delete(key);
        return R.success("新增菜品成功");
    }

    // 菜品信息查询
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name) {
        // 第几页 每一页多少个
        Page<Dish> dishPage = new Page<>(page, pageSize);
        Page<DishDto> dishDtoPage = new Page<>(page, pageSize);
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        // 添加过滤条件 注意判断是否为空  使用对name的模糊查询
        queryWrapper.like(name != null, Dish::getName, name);
        queryWrapper.orderByDesc(Dish::getUpdateTime);
        // 去数据库进行分页查询  参数分别是：分页对象 + 查询条件构造器
        dishService.page(dishPage, queryWrapper);
        // 获取当前页的记录列表
        List<Dish> records = dishPage.getRecords();
        // 对每一个元素进行转化
        List<DishDto> list = records.stream().map((item) -> {
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(item, dishDto);
            Long categoryId = item.getCategoryId();
            Category category = categoryService.getById(categoryId);
            if (category != null) {
                String categoryName = category.getName();
                dishDto.setCategoryName(categoryName);
            }
            return dishDto;
        }).collect(Collectors.toList());// 将 Stream 中的元素收集到一个新的列表中，方便后续操作和处理。

        // dishPage 对象的属性值拷贝到 dishDtoPage 对象中，同时排除属性名为 "records" 的属性。
        // 注意：属性名匹配是基于名称的，而不考虑类型。
        BeanUtils.copyProperties(dishPage, dishDtoPage, "records");
        dishDtoPage.setRecords(list);
        //因为上面处理的数据没有分类的id,这样直接返回R.success(dishPage)虽然不会报错，但是前端展示的时候这个菜品分类这一数据就为空
        //所以进行了上面的一系列操作
        return R.success(dishDtoPage);
    }

    // 根据id来查询菜品信息和对应的口味信息
    @GetMapping("/{id}")
    // @PathVariable ： URL 地址传递的 id 值就可以被获取到
    public R<DishDto> get(@PathVariable Long id) {  //这里返回什么数据是要看前端需要什么数据,不能直接想当然的就返回Dish对象
        DishDto dishDto = dishService.getByIdWithFlavor(id);
        return R.success(dishDto);
    }

    // 修改菜品
    @PutMapping
    public R<String> update(@RequestBody DishDto dishDto) {
        dishService.updateWithFlavor(dishDto);
        String key = "dish_" + dishDto.getCategoryId() + "_1";
        // 删除全部
        // redisTemplate.delete("*");
        redisTemplate.delete(key);
        return R.success("新增菜品成功");
    }

    // 菜品批量删除和单个删除
    // 1.判断要删除的菜品在不在售卖的套餐中，如果在那不能删除
    // 2.要先判断要删除的菜品是否在售卖，如果在售卖也不能删除
    // 3.如果要删除的菜品没有在售卖的套餐中，也没有在售卖，那么就可以删除
    @DeleteMapping
    public R<String> delete(@RequestParam("ids") List<Long> ids) {
        LambdaQueryWrapper<SetmealDish> setmealDishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        //  in 方法可以进行多个值的判断
        setmealDishLambdaQueryWrapper.in(SetmealDish::getDishId, ids);
        List<SetmealDish> SetmealDishList = setmealDishService.list(setmealDishLambdaQueryWrapper);
        //如果菜品没有关联套餐，直接删除就行  其实下面这个逻辑可以抽离出来，这里我就不抽离了
        if (SetmealDishList.size() == 0) {
            dishService.deleteByIds(ids);
            LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.in(DishFlavor::getDishId, ids);
            dishFlavorService.remove(queryWrapper);
            return R.success("菜品删除成功");
        }

        //如果菜品有关联套餐，并且该套餐正在售卖，那么不能删除
        ArrayList<Long> Setmeal_idList = new ArrayList<>();
        for (SetmealDish setmealDish : SetmealDishList) {
            Long setmealId = setmealDish.getSetmealId();
            Setmeal_idList.add(setmealId);
        }
        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setmealLambdaQueryWrapper.in(Setmeal::getId, Setmeal_idList);
        List<Setmeal> setmealList = setmealService.list(setmealLambdaQueryWrapper);
        for (Setmeal setmeal : setmealList) {
            Integer status = setmeal.getStatus();
            if (status == 1) {
                return R.error("删除的菜品中有关联在售套餐,删除失败！");
            }
        }

        // 删除指定的菜品数据
        dishService.deleteByIds(ids);
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(DishFlavor::getDishId, ids);
        // 删除菜品口味信息
        dishFlavorService.remove(queryWrapper);
        return R.success("菜品删除成功");
    }

    // 根据条件查询对应的菜品数据
    @GetMapping("/list")
    public R<List<DishDto>> list(Dish dish) { //会自动映射的
        List<DishDto> dishDtoList = null;
        //根据前端传传过来的参数动态的构造key
        String key = "dish_" + dish.getCategoryId() + "_" + dish.getStatus();
        //先从redis中获取缓存数据
        dishDtoList = (List<DishDto>) redisTemplate.opsForValue().get(key);
        if (dishDtoList != null) {
            //如果存在，直接返回，无需查询数据库
            return R.success(dishDtoList);
        }

        //如果不存在，直接查询数据库,并且将查询到的菜品数据缓存到redis中
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(dish.getCategoryId() != null, Dish::getCategoryId, dish.getCategoryId());
        queryWrapper.eq(Dish::getStatus, 1);
        queryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);
        List<Dish> list = dishService.list(queryWrapper);
        // 进行集合的泛型转化 将一个集合中的元素类型转换为另一种类型
        dishDtoList = list.stream().map((item) -> {
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(item, dishDto);
            Long categoryId = item.getCategoryId();
            Category category = categoryService.getById(categoryId);
            if (category != null) {
                String categoryName = category.getName();
                dishDto.setCategoryName(categoryName);
            }
            Long dishId = item.getId();
            LambdaQueryWrapper<DishFlavor> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            // select * from dish_flavor where dish_id = ?
            lambdaQueryWrapper.eq(DishFlavor::getDishId, dishId);
            List<DishFlavor> dishFlavorList = dishFlavorService.list(lambdaQueryWrapper);
            dishDto.setFlavors(dishFlavorList);
            // 返回给流操作的收集器，即 collect(Collectors.toList())。
            return dishDto;
        }).collect(Collectors.toList());
        //将查询到的菜品数据缓存到redis中   设置过期时间60分钟
        redisTemplate.opsForValue().set(key, dishDtoList, 60, TimeUnit.MINUTES);
        return R.success(dishDtoList);
    }

    // 修改菜品售卖状态
    @PostMapping("/status/{status}")
    //这个参数这里一定记得加注解才能获取到参数，否则这里非常容易出问题
    public R<String> status(@PathVariable("status") Integer status, @RequestParam List<Long> ids) {
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        // ids != null: 这是一个条件判断，用于判断传入的菜品ID列表是否为null。如果为null，则不执行in方法。
        queryWrapper.in(ids != null, Dish::getId, ids);
        List<Dish> list = dishService.list(queryWrapper);
        for (Dish dish : list) {
            if (dish != null) {
                dish.setStatus(status);//设置菜品的售卖状态为指定的状态
                dishService.updateById(dish);//更新菜品的售卖状态
            }
        }
        return R.success("售卖状态修改成功");
    }
}
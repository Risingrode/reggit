package com.itheima.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.common.CustomException;
import com.itheima.reggie.entity.Category;
import com.itheima.reggie.entity.Dish;
import com.itheima.reggie.entity.Setmeal;
import com.itheima.reggie.mapper.CategoryMapper;
import com.itheima.reggie.service.CategoryService;
import com.itheima.reggie.service.DishService;
import com.itheima.reggie.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

// 表示 CategoryServiceImpl 的实例在被调用时才会被实例化。
@Lazy
@Service
// CategoryServiceImpl 是一个服务实现类，它继承了通用的服务实现类 ServiceImpl，并实现了 CategoryService 接口。
// 通过泛型参数的指定，可以将 CategoryMapper 作为数据访问层的接口，并指定 Category 作为实体类类型。
// 这样，CategoryServiceImpl 就具备了对 Category 实体对象进行数据库操作的能力，并可以提供业务方法给控制器或其他调用方使用。
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    @Autowired
    private DishService dishService;

    @Autowired
    private SetmealService setmealService;

    // 根据id删除 分类，删除之前需要进行判断是否有关联数据
    @Override
    public void remove(Long id) {
        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        dishLambdaQueryWrapper.eq(Dish::getCategoryId,id);
        int count = dishService.count(dishLambdaQueryWrapper);
        //查询当前分类是否关联了菜品，如果已经管理，直接抛出一个业务异常
        if (count > 0){
            throw new CustomException("当前分类项关联了菜品,不能删除");
        }
        //查询当前分类是否关联了套餐，如果已经管理，直接抛出一个业务异常
        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setmealLambdaQueryWrapper.eq(Setmeal::getCategoryId,id);
        int setmealCount = setmealService.count(setmealLambdaQueryWrapper);
        if (setmealCount > 0){
            throw new CustomException("当前分类项关联了套餐,不能删除");
        }
        //正常删除
        super.removeById(id);
    }
}

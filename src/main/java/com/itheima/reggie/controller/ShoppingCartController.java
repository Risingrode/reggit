package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.itheima.reggie.common.BaseContext;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.ShoppingCart;
import com.itheima.reggie.service.ShoppingCartService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/shoppingCart")
public class ShoppingCartController {

    @Autowired
    private ShoppingCartService shoppingCartService;

    // 添加购物车
    @PostMapping("/add")
    public R<ShoppingCart> add(@RequestBody ShoppingCart shoppingCart) {
        // 拿到当前用户的id
        Long currentId = BaseContext.getCurrentId();
        shoppingCart.setUserId(currentId);
        Long dishId = shoppingCart.getDishId();
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId, currentId);
        if (dishId != null) {// 菜品
            queryWrapper.eq(ShoppingCart::getDishId, dishId);
        } else {// 菜单
            queryWrapper.eq(ShoppingCart::getSetmealId, shoppingCart.getSetmealId());
        }
        //查询当前菜品或者套餐是否在购物车中
        ShoppingCart cartServiceOne = shoppingCartService.getOne(queryWrapper);
        if (cartServiceOne != null) {
            // 如果已经存在,就在原来的数量基础上加一
            Integer number = cartServiceOne.getNumber();
            // 对对象进行操作
            cartServiceOne.setNumber(number + 1);
            // 对数据库进行操作
            shoppingCartService.updateById(cartServiceOne);
        } else {
            shoppingCart.setNumber(1);
            shoppingCart.setCreateTime(LocalDateTime.now());
            shoppingCartService.save(shoppingCart);
            cartServiceOne = shoppingCart;
        }
        return R.success(cartServiceOne);
    }

    // 查看购物车
    @GetMapping("list")
    public R<List<ShoppingCart>> list() {
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId, BaseContext.getCurrentId());
        queryWrapper.orderByAsc(ShoppingCart::getCreateTime);
        List<ShoppingCart> list = shoppingCartService.list(queryWrapper);
        return R.success(list);
    }

    // 清空购物车
    @DeleteMapping("/clean")
    public R<String> clean() {
        shoppingCartService.clean();
        return R.success("清空购物车成功");
    }

    // 客户端的套餐或者是菜品数量减少设置，没必要设置返回值
    // 当调用该方法时，会启动一个事务，并在方法执行结束时根据方法的执行结果决定是提交事务还是回滚事务。
    @Transactional
    @PostMapping("/sub")
    public R<ShoppingCart> sub(@RequestBody ShoppingCart shoppingCart) {
        Long dishId = shoppingCart.getDishId();
        // 菜品
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        if (dishId != null) {
            queryWrapper.eq(ShoppingCart::getDishId, dishId);
            queryWrapper.eq(ShoppingCart::getUserId, BaseContext.getCurrentId());
            ShoppingCart cart1 = shoppingCartService.getOne(queryWrapper);
            cart1.setNumber(cart1.getNumber() - 1);
            Integer LatestNumber = cart1.getNumber();
            if (LatestNumber > 0) {
                shoppingCartService.updateById(cart1);
            } else if (LatestNumber == 0) {
                shoppingCartService.removeById(cart1.getId());
            } else if (LatestNumber < 0) {
                return R.error("操作异常");
            }
            return R.success(cart1);
        }
        // 套餐
        Long setmealId = shoppingCart.getSetmealId();
        if (setmealId != null) {
            queryWrapper.eq(ShoppingCart::getSetmealId, setmealId).eq(ShoppingCart::getUserId, BaseContext.getCurrentId());
            ShoppingCart cart2 = shoppingCartService.getOne(queryWrapper);
            cart2.setNumber(cart2.getNumber() - 1);
            Integer LatestNumber = cart2.getNumber();
            if (LatestNumber > 0) {
                shoppingCartService.updateById(cart2);
            } else if (LatestNumber == 0) {
                shoppingCartService.removeById(cart2.getId());
            } else if (LatestNumber < 0) {
                return R.error("操作异常");
            }
            return R.success(cart2);
        }
        //如果两个大if判断都进不去
        return R.error("操作异常");
    }
}

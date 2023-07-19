package com.itheima.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.itheima.reggie.common.BaseContext;
import com.itheima.reggie.common.CustomException;
import com.itheima.reggie.entity.*;
import com.itheima.reggie.mapper.OrderMapper;
import com.itheima.reggie.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Slf4j
@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Orders> implements OrderService {

    @Autowired
    private ShoppingCartService shoppingCartService;

    @Autowired
    private UserService userService;

    @Autowired
    private AddressBookService addressBookService;

    @Autowired
    private OrderDetailService orderDetailService;

    // 用户下单
    @Override
    @Transactional
    public void submit(Orders orders) {
        //前端请求携带的参数是没有用户id的,所以要获取用户id
        Long userId = BaseContext.getCurrentId();
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId,userId);
        //查询当前用户的购物车数据
        List<ShoppingCart> shoppingCarts = shoppingCartService.list(queryWrapper);
        if (shoppingCarts == null || shoppingCarts.size()==0){
            throw new CustomException("购物车为空,不能下单");
        }
        User user = userService.getById(userId);
        Long addressBookId = orders.getAddressBookId();
        AddressBook addressBook = addressBookService.getById(addressBookId);
        if (addressBook==null){
            throw new CustomException("地址信息有误,不能下单");
        }
        Long orderId = IdWorker.getId();//使用工具生成订单号
        orders.setId(orderId);
        //进行购物车的金额数据计算 顺便把订单明细给计算出来
        AtomicInteger amount = new AtomicInteger(0);
        List<OrderDetail> orderDetails = shoppingCarts.stream().map((item)->{
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrderId(orderId);
            orderDetail.setNumber(item.getNumber());
            orderDetail.setDishFlavor(item.getDishFlavor());
            orderDetail.setDishId(item.getDishId());
            orderDetail.setSetmealId(item.getSetmealId());
            orderDetail.setName(item.getName());
            orderDetail.setImage(item.getImage());
            orderDetail.setAmount(item.getAmount());//单份的金额
            //addAndGet进行累加 item.getAmount()单份的金额  multiply乘  item.getNumber()份数
            amount.addAndGet(item.getAmount().multiply(new BigDecimal(item.getNumber())).intValue());
            return orderDetail;
        }).collect(Collectors.toList());
        //orders.setOrderDetails(orderDetails);
        //向订单插入数据,一条数据  因为前端传过来的数据太少了,所以我们需要对相关的属性进行填值
        orders.setOrderTime(LocalDateTime.now());
        orders.setCheckoutTime(LocalDateTime.now());
        orders.setStatus(2);
        //Amount是指订单总的金额
        orders.setAmount(new BigDecimal(amount.get()));//总金额
        orders.setUserId(userId);
        orders.setNumber(String.valueOf(orderId));
        if (user.getName() != null){
            orders.setUserName(user.getName());
        }
        if (addressBook.getConsignee() != null){
            orders.setConsignee(addressBook.getConsignee());
        }
        orders.setPhone(addressBook.getPhone());
        orders.setAddress((addressBook.getProvinceName() == null ? "" : addressBook.getProvinceName())
                + (addressBook.getCityName() == null ? "" : addressBook.getCityName())
                + (addressBook.getDistrictName() == null ? "" : addressBook.getDistrictName())
                + (addressBook.getDetail() == null ? "" : addressBook.getDetail()));
        this.save(orders);
        orderDetailService.saveBatch(orderDetails);
        //清空购物车数据  queryWrapper封装了userId我们直接使用这个条件来进行删除就行
        shoppingCartService.remove(queryWrapper);
    }

    @Override
    public List<ShoppingCart> againAdd(List<OrderDetail> orderDetailList) {
        Long userId = BaseContext.getCurrentId();
        List<ShoppingCart> shoppingCartList = orderDetailList.stream().map((item) -> {
            //把从order表中和order_details表中获取到的数据赋值给这个购物车对象
            ShoppingCart shoppingCart = new ShoppingCart();
            shoppingCart.setUserId(userId);
            shoppingCart.setImage(item.getImage());
            Long dishId = item.getDishId();
            Long setmealId = item.getSetmealId();
            if (dishId != null) {
                //如果是菜品那就添加菜品的查询条件
                shoppingCart.setDishId(dishId);
            } else {
                //添加到购物车的是套餐
                shoppingCart.setSetmealId(setmealId);
            }
            shoppingCart.setName(item.getName());
            shoppingCart.setDishFlavor(item.getDishFlavor());
            shoppingCart.setNumber(item.getNumber());
            shoppingCart.setAmount(item.getAmount());
            shoppingCart.setCreateTime(LocalDateTime.now());
            return shoppingCart;
        }).collect(Collectors.toList());

        return shoppingCartList;
    }
}
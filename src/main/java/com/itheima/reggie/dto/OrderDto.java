package com.itheima.reggie.dto;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.itheima.reggie.entity.OrderDetail;
import com.itheima.reggie.entity.Orders;
import lombok.Data;

import java.util.List;

// 订单细节
@Data
public class OrderDto extends Orders  {
   // 订单的分页信息
   private List<OrderDetail> orderDetails;

}

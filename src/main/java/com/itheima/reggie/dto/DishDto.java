package com.itheima.reggie.dto;

import com.itheima.reggie.entity.Dish;
import com.itheima.reggie.entity.DishFlavor;
import lombok.Data;
import java.util.ArrayList;
import java.util.List;

// Dto: 数据传输对象（Data Transfer Object），用于封装菜品信息和口味信息的对象。
@Data
public class DishDto extends Dish {
    // 菜品的口味信息
    private List<DishFlavor> flavors = new ArrayList<>();
    // 菜品所属的分类名称
    private String categoryName;
    // 菜品的份数
    private Integer copies;
}

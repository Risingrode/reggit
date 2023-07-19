package com.itheima.reggie.dto;

import com.itheima.reggie.entity.Setmeal;
import com.itheima.reggie.entity.SetmealDish;
import lombok.Data;
import java.util.List;

@Data
public class SetmealDto extends Setmeal {
    // 套餐的菜品信息
    private List<SetmealDish> setmealDishes;
    // 套餐所属的分类名称
    private String categoryName;
}

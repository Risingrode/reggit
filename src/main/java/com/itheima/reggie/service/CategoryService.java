package com.itheima.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.reggie.entity.Category;
import org.springframework.context.annotation.Lazy;

public interface CategoryService extends IService<Category> {

    //定义自己需要的方法
    void remove(Long id);
}

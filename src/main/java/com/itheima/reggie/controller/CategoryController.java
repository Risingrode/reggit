package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.Category;
import com.itheima.reggie.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// 分类管理
@Slf4j
// 将一个类声明为控制器类，并自动将方法的返回值转换为 JSON 格式的响应数据。
@RestController
@RequestMapping("/category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    // 新增套餐分类
    @PostMapping
    public R<String> save(@RequestBody Category category) {
        log.info("category:{}", category);
        categoryService.save(category);
        return R.success("新增分类成功!!!");
    }

    // 分页查询
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize) {
        // 创建一个分页构造器
        // 这两个参数表示要查询的页码和每页的数据量。这些参数通过 URL 查询参数传递，如：/users?page=1&size=10。
        Page<Category> categoryPage = new Page<>(page, pageSize);
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        //添加排序条件 ，根据sort字段进行升序
        queryWrapper.orderByAsc(Category::getSort);
        categoryService.page(categoryPage, queryWrapper);
        return R.success(categoryPage);
    }

    // 根据id来删除分类的数据
    @DeleteMapping
    public R<String> delete(@RequestParam("ids") Long id) { //注意这里前端传过来的数据名字是：ids
        // 这里的remove是自定义的
        categoryService.remove(id);
        return R.success("分类信息删除成功");
    }

    // 根据id修改分类
    // @RestController 注解不包含 @RequestBody 注解。
    // @RequestBody 注解用于将请求体中的数据映射到方法的参数上
    // @ResponseBody 注解用于将方法的返回值，转换为 JSON 格式的响应数据。
    @PutMapping
    public R<String> update(@RequestBody Category category) {
        categoryService.updateById(category);
        return R.success("修改分类信息成功");
    }

    // 根据条件查询分类数据
    @GetMapping("/list")
    //这个接口接收到参数其实就是一个前端传过来的type,这里之所以使用Category这个类来接受前端的数据，是为了以后方便
    //因为这个Category类里面包含了type这个数据,返回的数据多了，你自己用啥取啥就行
    //@RestController 注解已经包含了 @ResponseBody 注解的功能，因此在使用 @RestController 注解的控制器方法中，不需要再单独添加 @ResponseBody 注解。
    private R<List<Category>> list(Category category) {
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(category.getType() != null, Category::getType, category.getType());
        queryWrapper.orderByAsc(Category::getSort).orderByDesc(Category::getUpdateTime);
        List<Category> list = categoryService.list(queryWrapper);
        return R.success(list);
    }

}

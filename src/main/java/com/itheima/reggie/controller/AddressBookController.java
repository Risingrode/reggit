package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.BaseContext;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.AddressBook;
import com.itheima.reggie.service.AddressBookService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

// 对于lombok下面的@Data注解：是生成所有属性的get和set方法，命名规则是get或者set后谜案加上该属性的名字
// 地址管理
@Slf4j
@RestController
@RequestMapping("/addressBook")
public class AddressBookController {
    // @Autowired 注解标记一个属性时，Spring 容器会自动为该属性注入相应的对象实例。
    // 控制器可以直接使用 AddressBookService 对象，无需手动创建、管理或传递该对象的实例。
    @Autowired
    private AddressBookService addressBookService;

    // 新增
    @PostMapping
    //@RequestBody 请求体中的 JSON 数据反序列化为 AddressBook 对象（该对象已经继承了序列化器），并将其赋值给 addressBook 方法参数。
    public R<AddressBook> save(@RequestBody AddressBook addressBook) {
        //使用前面封装的localThread来保持这个数据
        //getCurrentId() 是static属性，整个项目都可以访问到
        addressBook.setUserId(BaseContext.getCurrentId());
        log.info("addressBook:{}", addressBook);
        // mybatis-plus的save函数
        addressBookService.save(addressBook);
        return R.success(addressBook);
    }

    // 根据地址id删除用户地址
    @DeleteMapping
    // 从请求中获取名为 ids 的参数值，并将其赋值给 id 变量。
    public R<String> delete(@RequestParam("ids") Long id) {
        if (id == null) {
            return R.error("请求异常");
        }
        LambdaQueryWrapper<AddressBook> queryWrapper = new LambdaQueryWrapper<>();
        // queryWrapper.eq(AddressBook::getId, id)：使用 eq 方法设置查询条件，要求地址簿的 ID 属性与指定的 id 值相等。
        // queryWrapper.eq(AddressBook::getUserId, BaseContext.getCurrentId())：使用 eq 方法设置查询条件，要求地址簿的用户 ID 属性与当前登录用户的 ID 相等。
        queryWrapper.eq(AddressBook::getId, id).eq(AddressBook::getUserId, BaseContext.getCurrentId());
        addressBookService.remove(queryWrapper);
        //addressBookService.removeById(id);  直接使用这个removeById不严谨
        //- 删除了其他用户拥有的地址簿记录，可能引发安全性问题。
        //- 没有检查当前登录用户是否有权限删除该地址簿记录，可能导致数据不一致性。
        return R.success("删除地址成功");
    }

    //修改收货地址
    @PutMapping
    public R<String> update(@RequestBody AddressBook addressBook) {
        if (addressBook == null) {
            return R.error("请求异常");
        }
        // 自动识别addressBook中的id,根据id进行数据更新
        addressBookService.updateById(addressBook);
        return R.success("修改成功");
    }

    // 设置默认地址,1表示默认地址
    // 把用户当前设置的默认地址改为非默认，然后把新传入的地址设置为默认地址。
    @PutMapping("default")
    public R<AddressBook> setDefault(@RequestBody AddressBook addressBook) {
        log.info("addressBook:{}", addressBook);
        LambdaUpdateWrapper<AddressBook> wrapper = new LambdaUpdateWrapper<>();
        // 当前用户id和传过来的id相等 根据用户id修改成非默认地址
        wrapper.eq(AddressBook::getUserId, BaseContext.getCurrentId());
        // 把传过来的对象设置为非默认地址
        wrapper.set(AddressBook::getIsDefault, 0);
        // SQL:update address_book set is_default = 0 where user_id = ?
        addressBookService.update(wrapper);
        addressBook.setIsDefault(1);
        // SQL:update address_book set is_default = 1 where id = ?
        addressBookService.updateById(addressBook);
        return R.success(addressBook);
    }

    // 根据id查询地址
    @GetMapping("/{id}")
    public R get(@PathVariable Long id) {
        // 查询数据库中对应ID的通讯录条目。
        AddressBook addressBook = addressBookService.getById(id);
        if (addressBook != null) {
            return R.success(addressBook);
        } else {
            return R.error("没有找到该对象");
        }
    }

    // 查询默认地址
    @GetMapping("default")
    public R<AddressBook> getDefault() {
        LambdaQueryWrapper<AddressBook> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AddressBook::getUserId, BaseContext.getCurrentId());
        queryWrapper.eq(AddressBook::getIsDefault, 1);
        //SQL:select * from address_book where user_id = ? and is_default = 1
        // getOne 方法的设计是只返回第一条找到的符合条件的记录，即使数据库中有多条记录符合这个条件。
        AddressBook addressBook = addressBookService.getOne(queryWrapper);
        if (null == addressBook) {
            return R.error("没有找到该对象");
        }
        return R.success(addressBook);
    }

    // 查询指定用户的全部地址
    @GetMapping("/list")
    // AddressBook addressBook 前端不会传过来参数，因为没啥用，所以这里没有@RequestBody注解
    // 在该方法中，参数 addressBook 是作为查询条件传递的，并不需要从请求体中获取 JSON 数据进行反序列化。
    public R<List<AddressBook>> list(AddressBook addressBook) {
        addressBook.setUserId(BaseContext.getCurrentId());
        log.info("addressBook:{}", addressBook);
        LambdaQueryWrapper<AddressBook> queryWrapper = new LambdaQueryWrapper<>();
        // 第一个参数是一个布尔值，表示是否添加这个条件。
        queryWrapper.eq(null != addressBook.getUserId(), AddressBook::getUserId, addressBook.getUserId());
        // 降序排列
        queryWrapper.orderByDesc(AddressBook::getUpdateTime);
        // SQL:select * from address_book where user_id = ? order by update_time desc
        // 执行了查询操作，根据指定的查询条件从数据库中获取相应的地址簿列表。list() 方法返回一个列表，包含满足查询条件的地址簿对象。
        return R.success(addressBookService.list(queryWrapper));
    }
}

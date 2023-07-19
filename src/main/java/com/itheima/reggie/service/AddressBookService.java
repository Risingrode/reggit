package com.itheima.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.reggie.entity.AddressBook;

// 服务层
// 使用 IService 接口中定义的常用数据库操作方法，如保存实体、根据ID查询实体、根据条件查询实体等。
// 该接口的作用是定义一组操作，封装了具体业务逻辑的实现细节。
// 如果没有 AddressBookService 接口，业务逻辑代码就会直接写在 Controller 层
public interface AddressBookService extends IService<AddressBook> {

}

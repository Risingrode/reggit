package com.itheima.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.entity.AddressBook;
import com.itheima.reggie.mapper.AddressBookMapper;
import com.itheima.reggie.service.AddressBookService;
import org.springframework.stereotype.Service;


@Service
public class AddressBookServiceImpl extends ServiceImpl<AddressBookMapper, AddressBook> implements AddressBookService {
    // 服务层则负责实现具体的业务逻辑，通过调用数据访问层（如数据库访问）来获取、处理和存储数据。
}

package com.itheima.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itheima.reggie.entity.AddressBook;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

//`@Mapper` 注解用于标识这是一个 MyBatis 的 Mapper 接口，它通常与 MyBatis 框架一起使用。
//在这个示例中，`AddressBookMapper` 接口继承了 `BaseMapper<AddressBook>` 接口。
//`BaseMapper` 是 MyBatis-Plus 提供的基础 Mapper 接口，它提供了一些常用的数据库操作方法，如插入、更新、删除和查询等。
//通过继承 `BaseMapper<AddressBook>`，`AddressBookMapper` 就具备了对 `AddressBook` 实体类进行数据库操作的能力。
//你可以在 `AddressBookMapper` 接口中定义自己的方法，也可以直接使用 `BaseMapper` 接口中的方法。
//在使用时，你可以注入 `AddressBookMapper` 对象，并调用其方法来执行相应的数据库操作，如插入数据、更新数据、删除数据或查询数据等。
@Mapper
public interface AddressBookMapper extends BaseMapper<AddressBook> {
    // 数据访问层
}

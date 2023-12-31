package com.itheima.reggie.common;

import lombok.Data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;


//通用返回结果类，服务端响应的数据最终都会封装成此对象
// @Data 注解可以自动生成 Java 类的所有基本方法，如 toString()、equals()、hashCode() 和 getters/setters 等。
@Data
public class R<T> implements Serializable {
    //编码：1成功，0和其它数字为失败
    private Integer code;
    //错误信息
    private String msg;
    //数据
    private T data;
    //动态数据
    private Map map = new HashMap<>();
    //为什么使用static 声明方法
    //意味着该方法属于类级别，意味着无需创建类的实例就可以直接调用该方法。因此，static 方法可以在类的任何地方被调用，而不依赖于对象的创建。

    public static <T> R<T> success(T object) {
        R<T> r = new R<T>();
        r.data = object;
        r.code = 1;
        return r;
    }

    //第一个<T>：表示在方法签名中声明了一个泛型类型参数 T。这个类型参数可以在方法体内部使用，用于表示一个具体的类型。
    //R<T>：表示返回类型为 R<T>，其中 R 是一个泛型类，泛型参数为 T。这意味着返回的对象的类型将与传入的参数的类型相对应。
    public static <T> R<T> error(String msg) {
        R r = new R();
        r.msg = msg;
        r.code = 0;
        return r;
    }

    public R<T> add(String key, Object value) {
        this.map.put(key, value);
        // 返回 this 的作用是支持链式调用
        return this;
    }
}

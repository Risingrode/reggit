package com.itheima.reggie.common;

//在这个特定的情况下，将 `threadLocal` 声明为静态变量有以下原因：
//1. 保持一致性：通过将 `threadLocal` 声明为静态变量，可以确保在整个应用程序的不同部分中访问和操作相同的 `ThreadLocal` 实例。这样，无论是在 `BaseContext` 类内部还是在其他类中，都可以通过 `BaseContext.threadLocal` 访问到相同的实例。
//2. 全局可访问：静态变量可以在整个应用程序中全局访问，而不需要创建类的实例。这意味着可以从任何地方调用 `BaseContext.setCurrentId()` 和 `BaseContext.getCurrentId()` 方法来设置和获取当前用户ID，而无需实例化 `BaseContext` 类。
//3. 线程隔离：通过使用静态的 `ThreadLocal` 变量，可以确保每个线程都拥有自己的用户ID副本，避免了线程之间的干扰。每个线程可以独立地设置和获取自己的用户ID，而不会影响其他线程。
// 基于ThreadLocal封装工具类，用户保存和获取当前登录用户id
public class BaseContext {
    //用来存储用户id
    private static ThreadLocal<Long> threadLocal = new ThreadLocal<>();

    public static void setCurrentId(Long id) {
        threadLocal.set(id);
    }

    public static Long getCurrentId() {
        return threadLocal.get();
    }
}

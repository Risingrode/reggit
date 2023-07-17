package com.itheima.reggie.common;


//通过自定义异常类，您可以在应用程序中抛出和捕获特定的异常，以表示业务逻辑出现错误或异常情况。通过提供错误消息，可以更好地描述和调试异常的原因。
//您可以在应用程序的适当位置使用 `throw new CustomException("错误消息")` 语句来抛出自定义异常，
// 并在适当的地方使用 `try-catch` 块捕获和处理异常。这样，您可以更好地控制和处理业务逻辑中的异常情况。
public class CustomException extends RuntimeException {
    public CustomException(String message){
        super(message);
    }
}



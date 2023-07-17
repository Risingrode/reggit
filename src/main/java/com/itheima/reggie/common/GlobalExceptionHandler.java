package com.itheima.reggie.common;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import java.sql.SQLIntegrityConstraintViolationException;

// 全局异常处理
@Slf4j
@ResponseBody
// 它会拦截带有@RestController和@Controller注解的控制器类。
@ControllerAdvice(annotations = {RestController.class, Controller.class})
public class GlobalExceptionHandler {
    //处理SQLIntegrityConstraintViolationException异常的方法
    //主键冲突 唯一索引冲突 外键约束冲突 非空约束冲突 检查约束冲突
    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public R<String> exceptionHandle(SQLIntegrityConstraintViolationException exception){
        //直接输出到控制台，如果需要真正的log文件，需要配置一下
        log.error(exception.getMessage());
        if (exception.getMessage().contains("Duplicate entry")){
            //获取已经存在的用户名，这里是从报错的异常信息中获取的
            String[] split = exception.getMessage().split(" ");// 使用空格把字符串那分割成字符串数组
            String msg = split[2] + "这个菜品分类名已经存在";
            return R.error(msg);
        }
        return R.error("未知错误");
    }
    // 处理自定义异常
    @ExceptionHandler(CustomException.class)
    public R<String> exceptionHandle(CustomException exception){
        log.error(exception.getMessage());
        //这里拿到的message是业务类抛出的异常信息，我们把它显示到前端
        return R.error(exception.getMessage());
    }
}

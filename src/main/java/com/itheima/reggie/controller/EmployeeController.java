package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.Employee;
import com.itheima.reggie.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    @PostMapping("/login")
    // HttpServletRequest : 用于接收前端的请求
    // 比如：前端传递的数据，前端传递的请求头，前端传递的cookie，客户端的IP地址，User-Agent，Session等等
    public R<Employee> login(HttpServletRequest request, @RequestBody Employee employee) {//接收前端的json数据,这个json数据是在请求体中的
        String password = employee.getPassword();
        // 字符串进行MD5加密并返回十六进制字符串表示
        password = DigestUtils.md5DigestAsHex(password.getBytes());
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Employee::getUsername, employee.getUsername());
        Employee emp = employeeService.getOne(queryWrapper);
        if (emp == null) {
            return R.error("用户不存在");
        }
        if (!emp.getPassword().equals(password)) {
            return R.error("密码不正确");
        }
        if (emp.getStatus() == 0) {
            return R.error("账号已禁用");
        }
        // session 建立在服务器中
        request.getSession().setAttribute("employee", emp.getId());
        return R.success(emp);
    }

    // 退出功能
    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest request) {
        request.getSession().removeAttribute("employee");
        return R.success("退出成功");
    }

    // 新增员工
    @PostMapping()
    public R<String> save(HttpServletRequest request, @RequestBody Employee employee) {
        // 对新增的员工设置初始化密码123456
        // 为什么把密码转化成字节流？ MD5通常接受字节数组作为输入进行计算
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));
        employeeService.save(employee);
        return R.success("新增员工成功");
    }

    // 员工信息分页
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name) {
        Page pageInfo = new Page(page, pageSize);
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StringUtils.isNotEmpty(name), Employee::getName, name);
        queryWrapper.orderByDesc(Employee::getUpdateTime);
        // 调用下面方法，查询结果会被填充到 pageInfo 对象中。
        employeeService.page(pageInfo, queryWrapper);
        return R.success(pageInfo);
    }

    // 根据id修改员工信息
    @PutMapping
    public R<String> update(HttpServletRequest request, @RequestBody Employee employee) {
        Long empId = (Long) request.getSession().getAttribute("employee");
        employee.setUpdateTime(LocalDateTime.now());
        employee.setUpdateUser(empId);
        employeeService.updateById(employee);
        return R.success("员工信息修改成功");
    }

    // 根据id查询员工
    @GetMapping("/{id}")
    public R<Employee> getById(@PathVariable Long id) {
        Employee employee = employeeService.getById(id);
        if (employee != null) {
            return R.success(employee);
        }
        return R.error("没有查询到该员工信息");
    }
}

package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.User;
import com.itheima.reggie.service.UserService;
import com.itheima.reggie.utils.ValidateCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    // 发送手机短信验证码
    @PostMapping("/sendMsg")
    // HttpSession session 参数是通过方法参数注入的 HttpSession 对象。通过该对象，可以访问和操作当前用户的会话信息
    public R<String> sendMsg(@RequestBody User user, HttpSession session) {
        String email = user.getPhone();
        String subject = "瑞吉外卖";
        //StringUtils.isNotEmpty字符串非空判断
        if (StringUtils.isNotEmpty(email)) {
            //发送一个四位数的验证码,把验证码变成String类型
            String code = ValidateCodeUtils.generateValidateCode(4).toString();
            String text = "【瑞吉外卖】您好，您的登录验证码为：" + code + "，请尽快登录";
            log.info("验证码为：" + code);
            //发送短信
            userService.sendMsg(email, subject, text);
            //将验证码保存到缓存当中
            redisTemplate.opsForValue().set(email, code, 1, TimeUnit.MINUTES);
            return R.success("验证码发送成功");
        }
        return R.error("验证码发送异常，请重新发送");
    }

    // 移动端用户登录
    @PostMapping("/login")
    public R<User> login(@RequestBody Map map, HttpSession session) {
        // System.out.println(map);
        String phone = map.get("phone").toString();
        String code = map.get("code").toString();

        Object codeInSession = redisTemplate.opsForValue().get(phone);

        if (codeInSession != null && codeInSession.equals(code)) {
            LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(User::getPhone, phone);
            User user = userService.getOne(queryWrapper);
            if (user == null) {
                user = new User();
                user.setPhone(phone);
                user.setStatus(1);
                user.setName(phone.substring(0, 6));
                userService.save(user);
            }
            //这一行容易漏。。保存用户登录状态
            session.setAttribute("user", user.getId());
            // redisTemplate.delete(phone);
            return R.success(user);
        }
        return R.error("登录失败");
    }
    // 退出功能
    @PostMapping("/loginout")
    public R<String> logout(HttpServletRequest request) {
        request.getSession().removeAttribute("user");
        return R.success("退出成功");
    }
}

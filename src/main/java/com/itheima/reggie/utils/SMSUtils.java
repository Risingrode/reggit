package com.itheima.reggie.utils;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsRequest;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.profile.DefaultProfile;

// 短信发送工具类
public class SMSUtils {
    public static void sendMessage(String signName, String templateCode, String phoneNumbers, String param) {
        DefaultProfile profile = DefaultProfile.getProfile("cn-hangzhou", "LTAI5tRLzXXVU2xg1Kkcoeuc", "gqa6iv1QUBW5t04P7aiT8DfK6ljPw6"); // 创建默认的阿里云配置文件
        IAcsClient client = new DefaultAcsClient(profile); // 创建阿里云短信发送客户端
        SendSmsRequest request = new SendSmsRequest(); // 创建发送短信请求对象
        request.setSysRegionId("cn-hangzhou"); // 设置请求的区域ID，这里使用的是杭州区域
        request.setPhoneNumbers(phoneNumbers); // 设置要发送短信的手机号码
        request.setSignName(signName); // 设置短信签名
        request.setTemplateCode(templateCode); // 设置短信模板CODE
        request.setTemplateParam("{\"code\":\"" + param + "\"}"); // 设置短信模板参数，这里使用 JSON 格式传递参数
        try {
            SendSmsResponse response = client.getAcsResponse(request); // 发送短信并获取响应
            System.out.println("短信发送成功");
        } catch (ClientException e) {
            e.printStackTrace();
        }
    }
}

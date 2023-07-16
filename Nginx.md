# Nginx

## 介绍



## 安装

配置虚拟机环境

## 命令

`tree`:树形目录

- conf 配置文件
- html 静态文件
- logs 日志文件
- sbin 脚本文件 用于启动停止nginx服务

### 打开sbin目录
- ./nginx -v  检查版本号
- ./nginx -t  测试文件
- ./nginx 启动nginx   不要忘记关闭防火墙 systemctl stop firewalld
- ./nginx -s stop 关闭nginx服务
- ./nginx -s reload 重新加载文件

需要在`etc/profile`中加入nginx的环境变量，就不用像前面那样麻烦配置

### 配置文件

- 全局块
  - 和nginx有关的全局配置
- events块
  - 和网络连接的有关配置
- http块   代理，缓存，日志记录，虚拟主机
  - http 全局块
  - server块
    - server全局块
    - location块


## 部署静态资源

nginx 相对于tomcat 更加高效

```
    server {
        listen       80;
        server_name  localhost;
        
        location / {
            root   html;    # 目录
            index  index.html index.htm; # 默认打开index.html文件
        }
```


### 反向代理

> 用户不需要知道目标服务器的地址，也无须再客户端做任何设定

### 正向代理

> 用户知道目标服务器，通过代理服务器访问目标服务器


### 操作

需要2台服务器

配置反向代理：
```
server{
    listen 82;
    server_name localhost;
    location/{
        proxy_pass http://192.168.138.101:8080; # 反向代理设置，把请求转发到指定服务
    }
}
```

## 负载均衡













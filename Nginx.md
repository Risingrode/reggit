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

- 应用集群：把同一个应用部署到多台服务器上面，组成集群，接收负载均衡器分发的请求
- 负载均衡器：把用户请求根据对应的负载均衡算法分发到应用集群中的一台服务器进行处理

```
http {
    upstream backend { # 定义一组服务器
        # 使用轮询算法：以循环的方式询问下面服务器
        # weight 表示权重
        server backend1.example.com weight=10;
        server backend2.example.com weight=5;
    }

    server {
        listen 80;
        server_name localhost;
        location / {
            # 反向代理
            proxy_pass https://backend;
        }
    }
}
```

负载均衡策略是用来决定网络流量如何在多个服务器上分发的规则或算法。下面是一些常见的负载均衡策略：

1. **轮询（Round Robin）**：这是最简单的负载均衡策略，每个新的请求都发送到下一个服务器。如果服务器列表是A、B、C，那么第一个请求将发送到A，第二个请求发送到B，第三个请求发送到C，第四个请求发送到A，以此类推。

2. **最少连接（Least Connections）**：在这种策略下，新的请求会发送到当前连接数最少的服务器。如果服务器A有10个活跃连接，服务器B有5个活跃连接，服务器C有7个活跃连接，那么新的请求将会发送到服务器B。

3. **IP Hash**：这种策略使用客户端IP地址的哈希值来决定请求应发送到哪个服务器。这样，来自同一IP地址的所有请求都将发送到同一服务器，除非该服务器不可用。

4. **权重轮询（Weighted Round Robin）**：这是轮询策略的改进版本。每个服务器都被分配了一个权重，权重大的服务器会处理更多的请求。例如，如果服务器A的权重是3，B的权重是2，C的权重是1，那么在6个请求中，A会处理3个，B处理2个，C处理1个。

5. **权重最少连接（Weighted Least Connections）**：这是最少连接策略的改进版本。和权重轮询类似，每个服务器都被分配了一个权重，但新的请求会发送到连接数/权重最小的服务器。

6. **URL Hash**：该策略将根据请求URL的哈希结果将请求分发到不同的服务器，所有请求同一URL的请求都会被路由到同一台服务器。这对于保证某些类型的数据局部性非常有用。

这些负载均衡策略都有各自的优点和缺点，选择哪一种策略取决于你的特定需求和应用场景。






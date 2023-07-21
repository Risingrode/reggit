# 主从复制

## 主从复制的原理
> 主从复制主要用于备份和提高读取性能。在主从复制中，有一个主服务器（Master）和一个或多个从服务器（Slave）。

1. **主服务器**：主服务器处理写操作（INSERT, UPDATE, DELETE等）。每当主服务器完成一个写操作，它都会在其二进制日志（Binary Log）中记录下这个操作。
- 二进制日志是一个记录主服务器所有写操作的日志文件，每个事件（写操作）在二进制日志中都有一个唯一的位置。

2. **从服务器**：从服务器开始时会从主服务器复制其整个数据集（这个过程叫做快照）。之后，从服务器将持续读取主服务器的二进制日志，并在本地应用这些日志中的写操作，从而保持与主服务器的数据同步。
- 从服务器也可以配置成定期从主服务器取得二进制日志的更新。


## 使用

准备好2台服务器
实现主从复制的步骤大致如下：

1. 在主库上操作：
   首先，你需要在主库中打开二进制日志（binary log），这是 MySQL 的一种日志文件，用于记录数据的修改情况。你可以在 MySQL 配置文件（如 `my.ini`）中添加或修改以下配置：
   ```bash
   [mysqld]
   log-bin=mysql-bin # 开启二进制日志
   server-id=1       # 设置 server-id，主库和从库的 server-id 必须不同
   ```
   然后重启 MySQL 服务以应用配置更改。

   接着，你需要创建一个用于主从复制的用户，并给予该用户复制的权限。假设你想创建的用户名为 `repl`，密码为 `password`，可以运行以下 SQL 命令：

   ```sql
   GRANT REPLICATION SLAVE ON *.* TO 'repl'@'%' IDENTIFIED BY 'password';
   ```

   最后，你需要获取当前的二进制日志文件名和位置，可以通过运行 `SHOW MASTER STATUS;` 命令获得。这些信息在接下来设置从库的时候需要用到。

2. 在从库上操作：
   同样的，你需要在 MySQL 配置文件中添加或修改以下配置：
   ```bash
   [mysqld]
   server-id=2 # 设置 server-id，主库和从库的 server-id 必须不同
   ```
   然后重启 MySQL 服务以应用配置更改。

   接着，你需要配置从库连接到主库，并开始复制。这里假设主库的 IP 地址为 `192.168.1.100`，二进制日志文件名为 `mysql-bin.000001`，位置为 `120`，可以运行以下 SQL 命令：

   ```sql
   CHANGE MASTER TO
       MASTER_HOST='192.168.1.100',
       MASTER_USER='repl',
       MASTER_PASSWORD='password',
       MASTER_LOG_FILE='mysql-bin.000001',
       MASTER_LOG_POS=120;
   ```

   最后，启动复制过程，运行以下 SQL 命令：

   ```sql
    START SLAVE;
   ```

## 说明

* 写(增删改)操作在master库中，比如:update xxx set status = xxx 
* 读(查询)操作在slave库中,比如:select * from xxx


# Sharding-jdbc 增强版JDBC

## 配置读写分离规则

```bash
spring:
  shardingsphere:
    datasource:
      names: ds0, ds1
      ds0:
        type: com.zaxxer.hikari.HikariDataSource
        driver-class-name: com.mysql.jdbc.Driver
        jdbc-url: jdbc:mysql://localhost:3306/ds0
        username: root
        password: password
      ds1:
        type: com.zaxxer.hikari.HikariDataSource
        driver-class-name: com.mysql.jdbc.Driver
        jdbc-url: jdbc:mysql://localhost:3306/ds1
        username: root
        password: password
    masterslave:
      # 负载均衡算法
      load-balance-algorithm-type: round_robin # 轮询
      # 最终数据库名称 这个库的名称主从数据库都要有
      name: dataSource
      master-data-source-name: ds0
      slave-data-source-names: ds1
    props:
      sql:
        # 可以打印执行的sql数据，方便调试
        show: true
  # 允许bean覆盖
  main:
    allow-bean-definition-overriding: true
```



























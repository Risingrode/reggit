
# Redis

## 启动服务

* Linux:./redis-serve
### Linux的redis如何允许windows连接

- 找到conf 配置文件
- 查找bind关键字，选择后面的ip地址，把该行注释掉
- 注意防火墙问题

----

* windows:
* redis-cli -h localhost -p 6379
* auth 密码

## 普通命令
 > 连接远程redis命令
* .\redis-cli.exe -h 虚拟机ip -p 端口 -a redis密码
* keys *   检查所有的key

## 数据类型(是指value的数据类型)

### 字符串

1. set key value  设置指定key的值
2. get key         获取值
3. setex key seconds value   设置指定key的值，把古琦欧i时间设为second秒
4. setnx key value      只有在key不存在时设置key值


### 哈希
> 存储对象

* hset key field value   把hash表中的key字段的field值设为value
* hget key field         获取指定字段的值
* hdel key field         删除指定字段
* hkeys key              获取哈希表中所有字段
* hvals key              获取哈希表中所有的值
* hgetall key            获取哈希表在指定key的所有字段和值 
```txt
localhost:6379> hset 001 name xiaoming
(integer) 1
localhost:6379> hset 001 age 20
(integer) 1
localhost:6379> hget 001 name
"xiaoming"
localhost:6379> hget 001 age
"20"
localhost:6379> hkeys 001
1) "name"
2) "age"
localhost:6379> hvals 001
1) "xiaoming"
2) "20"
localhost:6379> keys *
1) "h1"
2) "h"
3) "mykey"
4) "001"
```

### 列表
> 任务队列

字符串列表，按照插入顺序来排序。

1. [x] lpush key val1 [val2]   插入一个或者多个值到列表头部
2. [x] lrange key start end 范围查询,end=-1时表示全部查询
3. [x] rpop key 移除并且获取最后一个元素
4. [x] llen 获取长度
5. [x] brpop key1 [key2] timeout 如果列表没有这个元素，会阻塞，会暂停后续命令的执行，直到有元素可用或超时发生。


### set

> 无序，不允许重复元素

元素是string类型

* sadd myset a b c d   向myset数据库里面插入a,b,c,d
* smembers myset       输出内容
* sadd myset a          插入a 不过会报错--不允许重复
* sadd myset2 ab d a b
* smembers myset2
* sinter myset myset2   交集
* sunion myset myset2   并集
* sdiff myset myset2   myset减去myset2  差集
* srem myset a            删除myset中的a元素




### Zset(有序集合)
> 排行榜

没有重复元素，每个个元素都会关联到一个double类型的分数，从小到大排序

1. zadd myzset 7 a 8 j 9 i 2 k
2. zrange myzset 0 -1              显示所有元素不显示分数
3. zrange myzset 0 -1 withscores   显示所有元素以及对应的分数
4. zincrby myzset 20 k             对k元素的分数加上20
5. zrem myzset j                   删除j元素


## 全局命令

* ctrl + l   清屏
* keys *     查看所有key
* exists [n] 检测n是否存在
* type [n]    检查n是什么类型
* ttl  [n]    检测存活时间
* del [n]     删除n


# 在java中操作redis




























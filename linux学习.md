# 学习



## vim修改配置

- 退出vim 
  - esc
- 保存退出
  - :wq
- 强制退出
  - :q!

## Xshell

1. 找到 ip addr 
2. 拿到ip地址  ens33 下面的东西

 > 新建文件->配置文件 
 
在xshell中需要输入用户名和密码

## Linux常用命令

- ls  list        查看当前目录下的内容
- pwd             print work directory,查看当期那所在目录
- touch [文件名]   如果文件不存在，新建文件
- rm [文件名]      删除指定文件
- rmdir [文件名]   删除文件夹

 > 在执行Linux时，显示乱码,修改其编码模式。

```shell
  # 默认语言为英语（美国）并使用UTF-8编码。
  echo 'LANG ="en_US.UTF-8"' >> /etc/profile
  # 重新加载配置文件
  source /etc/profile
```

## 命令使用技巧

* Tab键 自动补全
* 使用上下键调出曾经使用过的命令
* ctrl+l 实现清屏的效果

## ls

- -a 显示所有文件及其目录
- -l 显示文件详细信息
- -al 上面两个的综合

简化：ll

## cat

* cat [文件]     显示文件内容
* cat -n [文件]  显示文件内容，并且显示行号

## more

> 如果文件太多，分页看

more [文件]

* 回车键 向下滚动一行
* 空格键 向下滚动一屏
* b     返回上一屏
* q     退出

## tail

> 查看文件末尾内容

`tail [-n]  filename`

显示文件最后n行内容,通常用于 log 查询

## mkdir

创建双层目录

`mkdir -p it/test`
> -p选项表示递归创建目录，即如果父级目录不存在，则同时创建父级目录和子级目录。

## rmdir
 > rmdir是一个用于删除空目录的命令。它只能删除空目录，如果目录中包含文件或其他子目录，则无法删除。


## rm

> 删除文件或者目录

-r  把目录中的所有文件逐一删除
-f  强制删除
-rf 随便删除   使用频率最多

` rm -r directory`

## cp

> 复制文件或者目录

`cp [-r] src tar`

- -r 代表着复制的是文件夹
- cp -r it/* ./it1  把it目录下的所有文件复制到it1下面
- cp -r it/  ./it1  把it目录和其目录下的内容复制到it1下面


## mv

> 重命名或者移动位置

`mv src tar`

- mv 1.txt 2.txt <pre>把1.txt 该名成 2.txt
- mv 1.txt it/   <pre>把1.txt 移动到 it目录下面
- mv it/ it1/    <pre>把it目录移动到it1目录中
- mv 1.txt it/2.txt  <pre>把1.txt移动到it目录中,并且改名为2.txt
- mv 1.txt it/   <pre>把1.txt 移动到it目录下面 


## tar

`tar [-zcxvf] filename [files]`

#### 打包命令,十分重要
包文件后缀是.tar 只是对文件进行了打包,并没有压缩<br>
后缀是 .tar.gz 表示打包的时候还进行了压缩

- z 对文件进行压缩或者解压
- c 打包
- x 解包
- v 显示命令的执行过程
- f 指定包文件名称

#### 常用组合 ：
- cvf  打包
- zcvf 打包且压缩 
- xvf  解包
- zxvf 解压缩包

## vi 和 vim

> vim 是从vi 进化的

`yum install vim`

#### 命令模式

#### 插入模式
按下a i o 任意一个键即可

#### 底行模式
* 可以对文件内容进行查找，显示行号，退出等操作
* 在命令模式下按下[:,/]就可以进入底行模式
* 按下/ ,可以对文件内容进行查找
* 按下： ,wq 保存退出 q! 不保存退出  set nu 显示行号

## find
> 查找命令

1. find . -name "*.java" 在当前文件夹下面找java文件
2. find /it -name "*.java" 在 it 文件夹下面找java文件

## grep
> 从指定文件中查找指定的文本内容

1. grep hello 1.txt    查找1.txt文件中出现hello 字符串的位置
2. grep hello *.java   查找当前目录中所有.java结尾的文件中包含 hello 的位置



















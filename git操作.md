# git 操作


全局配置

 > git config --global user.name "用户名"   //设置用户名，
 
 > git config --global user.email "邮箱"   //设置邮箱

查看信息

 > git config --list --global


 查看远程仓库
 > git remote

查看远程仓库详细信息
 > git remote -v

拉取最新仓库代码
 > git pll

添加远程仓库

 > git remote add origin <url>


推送

 > git push --set-upstream origin master

用于将本地分支的提交推送到远程仓库的 master 分支，并建立本地分支与远程分支的关联。

然后才能直接使用下面命令 

 > git push


-------------

 > git push origin master
> 
 > git pull origin master
> 
 > git commmit -m 'nihao'  <文件名>

如果本地仓库不是从远程创建的，直接拉取会报错

在git pull命令后面加上：--allow-unrelated-histories






















## android开发规范简版

###基本保证
* AndroidStuido工具检查无编译警告 (文件右上角出现绿色对勾，非必不可以，不要使用忽略警告的注解来达到要求)
* 适量注释，便于后人维护和理解
* 类写明作者： __中文名__
* todo格式：  __// TODO(2017/12/28) by 赵麟 ，理由+建议解决办法__

### 一、命名
全局常量、全局变量、方法名的定义一定要加修饰符
##### 1.1 变量命名
规则：采用小驼峰命名法，首字母小写。变量名应简短且能描述其用途，尽量避免拼音，无意义地缩写。除非是临时变量，否则不建议使用单个字符的变量名，如i, j, k。
	
	private String mUserName          //成员变量首字母使用m标明
	private static String sUserName   //静态变量用首字母s标明
	String userName		              //局部变量不需前缀
	
##### 1.2 常量命名
规则：常量使用全大写字母加下划线的方式命名

	    public static final String RECOMMEND_LIST_SHOW = "recommend_list_show";
	    
##### 1.3 控件变量名(待定)
规则：逻辑名 + view

	Button sendNewsView;   
	
##### 1.4 控件id
模块名_逻辑名

	android:id="@+id/news_send"  //样例
	
### 二、语法
* "{"大括号 放在表达式右边，不要单独一行
* if 、while 后边必须要跟{}
* if 、while 、for 之类的总嵌套不要超3层

案例一

```
错误
if (a) {
    if (b) {
        if (c) {
            if (d) {
        
            }
        }
    }
}

优化
if (a && b && c && d) {
    
}
```

案例二

```
private void a(){
    if (a){
        if (b) {
            if (d) {
            
            }else{
        
            }
        }
    }else{
        if (c) {
            if (d){
        
            }else{
        
            }
        }
    }
}

优化：就是超了三层要根据情况，优雅的拆开
private void aa(){
    if (a){
        if (b) {
            bb(d);
        }
    }else{
        if (c) {
            bb(d);
        }
    }
}

private void bb(boolean isD){
    if (isD) {
            
    }else{
        
    }
}

```
	
### 三、位置
按照区域划分各个内容的位置

	```
	class A {
	   静态常量
	   全局变量
	   构造方法
	   Override的父类方法
	   public 方法
	   private 方法
	   定义的内部类
	   定义的内部接口
	   定义的内部枚举
	}
	
	```
	
  	
### 四、Annotations
* 必须使用@Override
* 非private方法的返回值,参数一定要有@Nullable @NonNull进行修饰
* 非private成员变量一定要有@Nullable @NonNull进行修饰

### 五、AndroidStudio辅助配置
* 自动命名前缀 Java | Code Generation -> Field s & Static field s
* 快速引入|删除类 Auto Import ->全部勾选上

### 六、常用快捷键
* Alt + Cmd + l 	代码格式化(包括xml资源文件)
* Cmd + Shift + U 字母大小写转换
* Shift + F6 	Rename
* Ctrl + Alt + O  Remove unused imports
* Alt + enter import

### 七、其他
* 每个方法代码行数尽量控制最好不超过50行
* 对于具体的变量数字，尽量static final化，杜绝magic number.
* 对于具体的字符串，也尽量static final化.
* 尽量使用String.format()取代直接拼接String.
* 如果是在原来代码基础上进行修改，不要引入任何格式变化，尤其禁止自动格式化，童子军规则：你走时比来时还干净。

### 八 代码review和仓库规则
  - review工具 http://gerrit.yzbo.tv/
  - 登录账号jira相同，登录成功后设置秘钥拉取代码
    ![添加ssh key](./企业微信截图_ffc41d69-0d22-4b07-996a-bfa1c1e22e36.png)
  - 参考文档
    + <a href='http://wiki.xiaokaxiu.com/pages/viewpage.action?pageId=14944569'>代码评审流程</a>
    + <a href='https://git-scm.com/'>git</a>
    + <a href='https://www.sourcetreeapp.com'>source tree</a>
  - 使用方法
    + 提交粒度要小，包含一个最小逻辑（不要超过10个文件，每个文件修改尽量独立）
    + 符合上面的代码规范
    + 使用AndroidStudio默认的代码风格google strle，不要随便跳转保持代码风格统一，避免全局代码格式化
  - 常用命令
```
git config --add gitreview.track true

git clone ssh://zhaolin@gerrit.yzbo.tv:29418/android_yzb_live && scp -p -P 29418 zhaolin@gerrit.yzbo.tv:hooks/commit-msg .git/hooks/

git add xxx.file

git commit -m "提交内容说明"

git review
```



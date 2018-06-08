功能：
    将标准话单文件（Vo_HW_BIN_731_20180119_000003_DECODE）导入redis（列表形式）,从redis取数据，导入Mysql库表hymdb.CDR_VOICE_731

环境准备：
1、redis安装并启动
2、mysql安装并启动
3、修改org.spring.springboot.FileToRedis.java文件路径

操作步骤：
1、执行org.spring.springboot.FileToRedis.java：将标准话单文件导入redis（列表形式）
2、执行主程序org.spring.springboot.Application.java：启动tomcat
3、浏览器输入 http://localhost:8080/api/Vo_HW_BIN_731_20180119_000003_DECODE ：（get触发从redis向Mysql表 导入数据  ）
#标准数据文件入库
1、支持Mysql、Oracle
2、新加文件增加tbl配置文件
3、支持新增数据、全量数据
4、支持文件重入库（覆盖旧数据，保留新数据）

#配置方式：
1、配置/imp/src/main/resources/application.properties
	#配置目标路径，自动创建BAK和ERROR文件夹用于保存成功和失败（部分失败）的文件，子目录与file.sourcesDir一致
	file.targetDir = D:\\Work\\ 
	#扫描路径，文件存放路径
	file.sourcesDir = D:\\Work\\SOURCES 
	#定时任务配置
	scheduler.timer=*/5 * * * * ?
	#数据库类型，mysql或oracle
	dataBase.type= mysql
	#全局设置隔多少行进行clearBatch，单个数据表内可再次设置
	data.clearRows = 100
	#端口
	server.port=8085
	
2、配置表属性/imp/src/main/resources/table/*.tbl，新增文件可直接增加配置文件
	#文件名的正则表达式，只扫描匹配的文件
	data.fileName= (MP_ALL_20\\d{2}[01]{1}\\d{1}[0123]{1}\\d{1}_\\d{3}.txt)
	#数据库中的表名
	data.tableName =bps_mdo_mp_all
	#是否有头尾文件，1：有 0：无
	data.head =0
	#分隔符分割，与data.length二选一
	data.separator = |
	#定长切割，与data.separator二选一，如{12,50,10,100,10,100,1,1,11,8,8}
	data.length =
	#数据类型，s：新增 i：全量（插入时删除历史数据）
	data.type = s
	#设置隔多少行进行clearBatch，覆盖全局设置的data.clearRows变量
	data.clearNumber = 10
	#数据表是否在末尾增加文件名(FILE_NAME)、插入时间(INSERT_time)字段，data.type = s时需要设置为1
	data.table.insertFlag = 1
	
3、建数据表（mysql为例，oracle需要修改字段类型）
	（1）日志表：
		CREATE TABLE `log_file_audit_daily` (
			`FILE_NAME` VARCHAR(50) NOT NULL,
			`TOTAL_COUNT` INT(11) NOT NULL,
			`SUCCESS_COUNT` INT(11) NOT NULL,
			`FAIL_COUNT` INT(11) NOT NULL,
			`INPUT_DATE` DATETIME NULL DEFAULT NULL,
			`MODIFY_DATE` DATETIME NULL DEFAULT NULL
		)
	（2）数据表（例，样例数据见/imp/data/MP_ALL_20160101_001.txt）：
		CREATE TABLE `bps_mm_ippcode_all` (
			`IPP_CODE` VARCHAR(6) NOT NULL,
			`AP_NAME` VARCHAR(200) NULL DEFAULT NULL,
			`IP_CODE` VARCHAR(6) NOT NULL,
			`IP_NAME` VARCHAR(200) NOT NULL,
			`IP_DIVIDE` VARCHAR(3) NOT NULL,
			`EFFECTIVE_DATE` DATETIME NULL DEFAULT NULL,
			`EXPIRE_DATE` DATETIME NULL DEFAULT NULL,
			`FILE_NAME` VARCHAR(50) NOT NULL,        --data.table.insertFlag = 1时添加
			`INSERT_TIME` DATETIME NULL DEFAULT NULL --data.table.insertFlag = 1时添加
		)


4、更改数据库配置/imp/src/main/resources/jdbc-mysql.properties、/imp/src/main/resources/jdbc-oracle.properties

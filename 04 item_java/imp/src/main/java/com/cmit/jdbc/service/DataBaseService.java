package com.cmit.jdbc.service;

import java.util.regex.Pattern;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


/**
* function：根据参数选择数据库类型，对接口提供get参数方法
* author：heym
* create time：2018-12-07
*/
@Service
public class DataBaseService {
	@Value("${dataBase.type}")
	private String inputDataBase;
	private static String dataBase;
	
	private static final String mysql = "mysql";
	private static final String oracle = "oracle";
	private static Logger log = LoggerFactory.getLogger(DataBaseService.class);

	/**
	 * 设置数据库
	 */
	@PostConstruct
	public void set() {//建完实例后执行
		String mysqlPattern = "(?i)mysql";
		if(Pattern.matches(mysqlPattern,inputDataBase)) {
			dataBase = mysql ;
			log.info("初始化数据库为Mysql");
			return ;
		}
		String oraclePattern = "(?i)oracle";
		if(Pattern.matches(oraclePattern,inputDataBase)) {
			dataBase = oracle;
			log.info("初始化数据库为Oracle");
			return ;
		}
		dataBase = mysql ;
		log.error("数据库未明确：database.type配置不正确，默认选择mysql");
	}
	
	/**
	 * 获取数据库配置文件路径
	 * @return
	 */
	public static String getConfPath() {
		switch(dataBase) {
		case(mysql):
			return "src/main/resources/jdbc-mysql.properties";
		case(oracle):
			return "src/main/resources/jdbc-oracle.properties";
		}
		return null;
	}
	/**
	 * 获取表结构的SQL
	 * @return
	 */
	public static String getSelectSQL() {
		switch(dataBase) {
		case(mysql):
			return "SELECT COLUMN_NAME,COLUMN_TYPE  FROM information_schema.columns where TABLE_NAME='";
		case(oracle):
			return "SELECT t.COLUMN_NAME,REGEXP_REPLACE (t.DATA_TYPE, '[0-9]+', '') ||t.DATA_LENGTH  from  User_Tab_Columns t where t.TABLE_NAME='";
		}
		return null;
	}
	
	public static String getDataBase() {
		return dataBase;
	}

	public static String getMysql() {
		return mysql;
	}

	public static String getOracle() {
		return oracle;
	}
}

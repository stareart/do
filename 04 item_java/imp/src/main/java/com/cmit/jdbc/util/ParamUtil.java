package com.cmit.jdbc.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;


/**
* function：获取表属性
* author：heym
* create time：2018-12-07
*/
public class ParamUtil {
	private static String tableDir = "src/main/resources/table";
	private static List<File> fileList = new ArrayList<File>();
	private static List<HashMap<String,String>> tableList = new ArrayList<HashMap<String,String>>();
	public static void main(String[] args) {
		List<HashMap<String, String>> tableList = getTable();
		for(HashMap<String, String> table :tableList ) {
			System.out.println(table.toString());
		}
	}
	
	/**
	 * 获取所有配置文件
	 * 
	 */
	public static void getFiles() {
		File fileDir = new File(tableDir);
		if(null != fileDir && fileDir.isDirectory()) {
			File[] files = fileDir.listFiles();
			for(int i=0;i<files.length;i++) {
				String fileName = files[i].getAbsolutePath();
				if(fileName.endsWith(".tbl")) {
					fileList.add(files[i]);
				}
			}
		}
	}
	
	/**
	 * 获取配置文件中的表属性
	 * 
	 */
	public static List<HashMap<String, String>>  getTable() {
		HashMap<String, String> table =  null;
		getFiles();
		for(File file:fileList) {
	        try {
	        	table = new HashMap<String,String>();
	        	/**通过属性文件获取参数值**/
	            Properties properties = new Properties();
				FileInputStream fileInputStream = new FileInputStream(file.getAbsolutePath());
				properties.load(fileInputStream);
				table.put("file", file.getName().split("\\.")[0]);
				table.put("fileNameRule",properties.getProperty("data.fileName"));
				table.put("tableName",properties.getProperty("data.tableName"));
				table.put("head",properties.getProperty("data.head"));
				String separator = properties.getProperty("data.separator");
				String lenArray = properties.getProperty("data.length");
				table.put("splitArray",(!"".equals(separator))?separator:lenArray);
				table.put("type",properties.getProperty("data.type"));
				table.put("insertFlag",properties.getProperty("data.table.insertFlag"));
				table.put("clearNumber",properties.getProperty("data.clearNumber"));
			} catch (IOException e) {
	            e.printStackTrace();
	        }
	        tableList.add(table);
		}
		return tableList;
	}
}

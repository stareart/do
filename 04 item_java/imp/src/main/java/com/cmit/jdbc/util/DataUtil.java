package com.cmit.jdbc.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
* function：文件到数据库
* author：heym
* create time：2018-12-07
*/
public class DataUtil {
	
	private int[] lengthArrays;
	int lineNumber = 0;
	private String fileCode;
	private String fileName;
	private static String intFlag = "int";
	private static String charFlag = "char";
	private static String dateFlag = "date";
	List<String> splitRecord=null;
	private static Logger log = LoggerFactory.getLogger(DataUtil.class);
	
	/**
	 * 文件导入数据库
	 * @param file
	 * @param tableMap
	 * @return boolean
	 */
	public boolean fileToData(File file, HashMap<String, String> tableMap) {
		BufferedReader br =null;
		int errorNumber = 0;
		Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        String currentTime = sdf.format(date);//入库时间
		try {
			String record= null;
			//获取文件
			fileCode = resolveCode(file);
			fileName = file.getName();
			br = new BufferedReader(new InputStreamReader(new FileInputStream(file),fileCode));
			//获取表属性
			int split = setSpliteType(tableMap.get("splitArray"));//判断切分方式
			int head = Integer.valueOf(tableMap.get("head"));//头标识
			int insertFlag = Integer.valueOf(tableMap.get("insertFlag"));//是否有文件名、插入时间
			String type = tableMap.get("type");//数据类型,s：新增  i：全量  a:增量
			int clearNumber=10000;//隔多少行清理内存，默认10000
			if(!"".equals(tableMap.get("clearNumber"))||tableMap.get("clearNumber")!="0") {//从参数表获取数据
				clearNumber = Integer.valueOf(tableMap.get("clearNumber"));
			}
			// 检查表配置参数
			if(split==-1) {
				log.error("{}.tbl文件data.separator参数切分规则不明确",tableMap.get("file"));
				return false;
			}
			if("".equals(type)||"ais".indexOf(type)==-1) {
				log.error("{}.tbl文件data.type数据类型不明确",tableMap.get("file"));
				return false;
			}
			if("s".indexOf(type)!=-1&&insertFlag!=1) {
				log.error("{}.tbl文件新增数据需要添加文件名字段",tableMap.get("file"));
				return false;
			}
			DBUtil dbUtil = new DBUtil(tableMap.get("tableName"),type);
			if(dbUtil.getColumnList() == null) {
				log.error("{}表未找到",tableMap.get("tableName"));
				return false;
			}
			List<List<String>>  columnTypeList= setColumnType(dbUtil.getColumnList());
			Long startTime = System.currentTimeMillis();
			//删除数据，全量全表删除，新增进行回滚
			switch(type) {
			case("i"):
				dbUtil.truncateBatch();
				break;
			case("s"):
				dbUtil.deleteBatch(file.getName());
				break;
			}
			// 入库
			while(null!=(record=br.readLine()) ) {
				lineNumber+=1;
				if(lineNumber<=head) continue;//头记录
				if(br.ready()==false && head==1) break;//尾记录
				switch(split){//切分
					case(0)://定长
						splitRecord=splitByLength(record,lengthArrays);
						break;
					case(1)://分隔符
						splitRecord=splitBySymbol(record,tableMap.get("splitArray"));
						break;
				}
				if(insertFlag==1) {//添加文件名和插入时间
					splitRecord.add(file.getName());
					splitRecord.add(currentTime);
				}
				if(checkRecord(columnTypeList)){//校验通过
					dbUtil.addBatch(splitRecord);
					if((lineNumber-head)%clearNumber==0) {
						dbUtil.clearBatch();
					}
				}else {
					//错单
					//log.error("{}文件第{}行格式与数据库不匹配",file.getName(),lineNumber);
					errorNumber+=1;
				}
			}
			dbUtil.clearBatch();
			int commitRowCount = dbUtil.commit();//提交
			if(commitRowCount!=-1) {//写日志
				log.info("入库记录：{}入库成功{}行、失败{}行，耗时{}秒",file.getName(),commitRowCount,errorNumber,(System.currentTimeMillis()-startTime)/1000);
				LogDBUtil logDB = new LogDBUtil();
				String[] logRecord = {file.getName(),String.valueOf(commitRowCount+errorNumber)
						,String.valueOf(commitRowCount),String.valueOf(errorNumber),currentTime,currentTime};
				logDB.insert(logRecord);
			}
		} catch (UnsupportedEncodingException e) {
			log.error("{}文件编码异常：{}",file.getName(),e.getMessage());
			return false;
		} catch (FileNotFoundException e) {
			log.error("{}文件未找到：{}",file.getName(),e.getMessage());
			return false;
		} catch (IOException e) {
			log.error("{}文件IO异常：{}",file.getName(),e.getMessage());
			return false;
		} catch(NumberFormatException e){
			log.error("{}.tbl配置不正确：{}",tableMap.get("file"),e.getMessage());
			return false;
		}
		finally {
			try {
				br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if(errorNumber==0) return true; else return false;
	}
	
	/**
	 * 设置切分方式
	 * @param spliteType
	 */
	private int setSpliteType(String spliteType) {
		String[] sArrays = spliteType.split("\\,|\\{|\\}| ");
		List<String> tmp = new ArrayList<String>();
        for(String str:sArrays){//去除空值
            if(str!=null && str.length()!=0){
                tmp.add(str);
            }
        }
	    sArrays = tmp.toArray(new String[0]);
		int slen = sArrays.length;
		if(slen==0) {
			return -1;
		}else if(slen!=1) {
			lengthArrays = new int[slen];
			for(int i=0;i<slen;i++) {
				lengthArrays[i] = Integer.valueOf(sArrays[i]);
			}
			return 0;
		}else {
			return 1;
		}
	}
	
	/**
	 * 分隔符切割
	 * @param record
	 * @param symbol
	 * @return
	 */
	private List<String> splitBySymbol(String record,String symbol){
		List<String> splitRecord = new ArrayList<String>();
		String [] splitStr= record.split("\\" + symbol,-1);
		for(String s:splitStr)splitRecord.add(s);
		return  splitRecord;
	}
	
	/**
	 * 定长切割
	 * @param record
	 * @param lengthArrays
	 * @return
	 */
	private List<String> splitByLength(String record,int[] lengthArrays){
		List<String> splitRecord =new ArrayList<String>();
		int beginIndex = 0;//开始位置，从0开始截取
		String recordField=null;//行记录分解
		try {
			for(int i=0;i<lengthArrays.length;i++) {
			recordField = new String(record.getBytes(fileCode),beginIndex,lengthArrays[i],fileCode);
			splitRecord.add(recordField.trim());
			beginIndex += lengthArrays[i];//开始位置后移
			}
		} catch (UnsupportedEncodingException e) {
			return splitRecord;
		} catch (StringIndexOutOfBoundsException e) {//长度异常
			return splitRecord;
		}
		return splitRecord;
	}
	
	/**
	 * 获取文件编码
	 * @param path
	 * @return 文件编码
	 */
	private String resolveCode(File file)  {  
		String path = file.getPath();
        InputStream inputStream = null;
        String code = "GBK";
		try {
			inputStream = new FileInputStream(path);
			byte[] head = new byte[3];    
			inputStream.read(head);
	        if (head[0] == -1 && head[1] == -2 )    
	            code = "UTF-16";    
	        else if (head[0] == -2 && head[1] == -1 )    
	            code = "Unicode";    
	        else if(head[0]==-17 && head[1]==-69 && head[2] ==-65)    
	            code = "UTF-8";   
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}  finally {
			try {
				inputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
        return code;  
    } 
	
	/**
	 * 抽取字段类型和长度
	 * @param columnList
	 * @return 字段列表
	 */
	private List<List<String>> setColumnType(List<List<String>> columnList){
		List<String> oneTypeList = null;
   	 	List<List<String>> typeList = new ArrayList<List<String>>();
		String columnType = null;
		String matchType = null;
		String matchLength = null;
		String intPattern = ".*(?i)int.*";
		String datePattern = ".*(?i)date.*";
		String intLengthPattern = "\\d+";
		for(int i=0;i<columnList.get(0).size();i++) {
			matchLength ="";
			oneTypeList =new ArrayList<String>();
			columnType = columnList.get(1).get(i);
			matchType = charFlag;//默认char
			if(Pattern.matches(intPattern,columnType)) matchType = intFlag;
			if(Pattern.matches(datePattern,columnType)) matchType = dateFlag;
			Pattern r = Pattern.compile(intLengthPattern);
			Matcher match = r.matcher(columnType);
			if(match.find() && matchType!=dateFlag)matchLength = match.group(0);//日期不检查长度，赋值空
		    oneTypeList.add(matchType);
		    oneTypeList.add(matchLength);
		    typeList.add(oneTypeList);
		}
		return typeList;
	}
	
	/**
	 * 记录校验
	 * @param splitRecord
	 * @param columnList
	 * @return
	 */
	private boolean checkRecord(List<List<String>> columnList) {
		String columnType = null;
		String field = null;
		int columnLength ;
		// 日期正则，"20011220000000" 
		String datePattern ="(\\d{2}|\\d{4})(?:\\-)?([0]{1}\\d{1}|[1]{1}[0-2]{1})(?:\\-)?([0-2]{1}\\d{1}|[3]{1}[0-1]{1})(?:\\s)?([0-1]{1}\\d{1}|[2]{1}[0-3]{1})(?::)?([0-5]{1}\\d{1})(?::)?([0-5]{1}\\d{1})";
		if(splitRecord.size()!=columnList.size()) {
			log.error("{}文件第{}行（字段数检查错误）：切分字段数{}，数据库字段数{}",fileName,lineNumber,splitRecord.size(),columnList.size());
			return false;//切分长度检查
		}
		for(int i=0;i<splitRecord.size();i++) {
			columnType = columnList.get(i).get(0);
			columnLength = "".equals(columnList.get(i).get(1))?1000:Integer.valueOf(columnList.get(i).get(1));//无长度默认一个较大值
			field = splitRecord.get(i);
			
			if(field.length()>Integer.valueOf(columnLength)) {
				log.error("{}文件第{}行（字段长度检查错误）：{}字段长度{}、数据库长度{}",fileName,lineNumber,field,field.length(),columnLength);
				return false;//字段长度检查
			}
			
			if(columnType==intFlag) {//整型检查
				try{
					Integer.valueOf(field);
				}catch (NumberFormatException e) {
					log.error("{}文件第{}行（整型检查错误）：{}字段非整型",fileName,lineNumber,field);
					return false;
				}
			}
			
			if(columnType==dateFlag) {//日期检查
				field = field.replaceAll("[^0-9]", "");
				if(field.length()==8) {
					field+="000000";
					splitRecord.set(i, field);
				}
				if(!(field.length()==14 && Pattern.matches(datePattern,field))) {
					log.error("{}文件第{}行（日期检查错误）：{}字段非日期",fileName,lineNumber,field);
					return false;
				}
			}
		}
		return true;
	}
	

}

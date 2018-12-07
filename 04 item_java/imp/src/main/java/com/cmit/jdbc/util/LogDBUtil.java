package com.cmit.jdbc.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cmit.jdbc.service.DataBaseService;

/**
* function：入库日志
* author：heym
* create time：2018-12-07
*/
public class LogDBUtil {
	private StringBuilder insertSql=null;
	private StringBuilder updateSql=null;
	private List<List<String>> columnList =null;
	private Connection connection = null;
	private PreparedStatement insertStatement = null;
	private PreparedStatement updateStatement = null;
	private final int fileNameIndex = 0;
	private final int inputTimeIndex = 4;
	private static Logger log = LoggerFactory.getLogger(LogDBUtil.class);
	
	
	/**
	 * 构造函数：1、查询数据库表结构，2、构造一次插入SQL
	 * @param tableName
	 */
    public  LogDBUtil() {
    	try {
    		String tableName = "LOG_FILE_AUDIT_DAILY";
			columnList = queryColumn(tableName);
	    	StringBuilder columnSql = new StringBuilder();
	        StringBuilder recordSql = new StringBuilder();
	        StringBuilder updateRecordSql = new StringBuilder();
	        // 插入日志语句构建
	        if(DataBaseService.getDataBase()==DataBaseService.getMysql()) {
	        	 int i = 0;
	        	 for(String column:columnList.get(0)) {
	 	            columnSql.append(i == 0 ? "" : ",");
	 	            columnSql.append(column);
	 	            recordSql.append(i == 0 ? "" : ",");
	 	            recordSql.append("?");
	 	            i++;
	 	        }
	        }else {//oracle需要对日期做转换
	        	String datePattern = ".*(?i)date.*";
	        	for(int i=0;i<columnList.get(0).size();i++) {
	        		columnSql.append(i == 0 ? "" : ",");
	 	            columnSql.append(columnList.get(0).get(i));
	 	            recordSql.append(i == 0 ? "" : ",");
	        		if(Pattern.matches(datePattern,columnList.get(1).get(i))) {
	        			recordSql.append("to_date(?,'yyyyMMddhh24:mi:ss')");
	        		}else {
	        			recordSql.append("?");
	        		}
	        	}
	        }
	        // 更新日志语句构建
	        if(DataBaseService.getDataBase()==DataBaseService.getMysql()) {
	        	 int i = 0;
	        	 for(String column:columnList.get(0)) {
	        		 if(i==inputTimeIndex) {
	 	        		i++;
	 	        		continue;
	 	        	}
	 	        	updateRecordSql.append(i == 0 ? "" : ",");
	 	        	updateRecordSql.append(column);
	 	        	updateRecordSql.append("=?");
	 	            i++;
	 	        }
	        }else {//oracle需要对日期做转换
	        	String datePattern = ".*(?i)date.*";
	        	for(int i=0;i<columnList.get(0).size();i++) {
	        		if(i==inputTimeIndex) {
	 	        		continue;
	 	        	}
	        		updateRecordSql.append(i == 0 ? "" : ",");
		        	updateRecordSql.append(columnList.get(0).get(i));
	        		if(Pattern.matches(datePattern,columnList.get(1).get(i))) {
	        			updateRecordSql.append("=to_date(?,'yyyyMMddhh24:mi:ss')");
	        		}else {
	        			updateRecordSql.append("=?");
	        		}
	        	}
	        }
	        
	        /**开始拼插入的sql语句**/
	        insertSql = new StringBuilder();
	        insertSql.append("INSERT INTO ");
	        insertSql.append(tableName);
	        insertSql.append(" (");
	        insertSql.append(columnSql);
	        insertSql.append(" ) VALUES (");
	        insertSql.append(recordSql);
	        insertSql.append(" )");
	        /**开始拼更新的sql语句**/
	        updateSql = new StringBuilder();
	        updateSql.append("UPDATE ");
	        updateSql.append(tableName);
	        updateSql.append(" SET ");
	        updateSql.append(updateRecordSql);
	        updateSql.append(" WHERE FILE_NAME = ");
	        updateSql.append(" (?)");
    	} catch (SQLException e) {
			e.printStackTrace();
		}
    }
    
    
    /**
     * 插入日志，先更新，若更新无效则插入
     * @return boolean
     */
    public boolean insert(String[] splitRecord) {
        try {
        	 /**从数据库连接池中获取数据库连接**/
			connection = DBConnectionPool.getInstance().getConnection();
			/**设置不自动提交，以便于在出现异常的时候数据库回滚**/
	    	connection.setAutoCommit(false);
	    	/**执行SQL预编译*--更新操作*/
	    	updateStatement = connection.prepareStatement(updateSql.toString());
	    	int j =1;
            for(int i=0;i<splitRecord.length;i++) {
            	if(i==inputTimeIndex)continue;
            	if(i==fileNameIndex)updateStatement.setObject(splitRecord.length,splitRecord[i]);
            	updateStatement.setObject(j,splitRecord[i]);
            	j++;
    		}
            int updateRows = updateStatement.executeUpdate();
	    	if(updateRows==0) {
		    	/**执行SQL预编译*--插入操作*/
	            insertStatement = connection.prepareStatement(insertSql.toString());
	            for(int i=0;i<splitRecord.length;i++) {
					insertStatement.setObject(i+1,splitRecord[i]);
	    		}
	    		insertStatement.addBatch();
	    		insertStatement.executeBatch();
	    		log.info("插入入库日志：{}",splitRecord[fileNameIndex]);
	    	}else {
	    		log.info("更新入库日志：{}",splitRecord[fileNameIndex]);
	    	}
    		connection.commit();
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}finally {
			try {
	            if (insertStatement != null) {
	                insertStatement.close();
	            }
	            if (connection != null) {
	                connection.close();
	            }
            }catch (SQLException e) {
            }
        }
        return true;
    }
    
    /**
     * 返回字段名和字段类型
     * @param tableName
     * @return
     * @throws SQLException
     */
    public List<List<String>> queryColumn(String tableName) throws SQLException {
    	 List<String> nameColumnList = null;
    	 List<String> typeColumnList = null;
    	 List<List<String>> columnList = new ArrayList<List<String>>();
    	 Connection connection = null;
         PreparedStatement preparedStatement = null;
         ResultSet resultSet = null;
         String sql = DataBaseService.getSelectSQL()+tableName+"'";
         try {
             /**获取数据库连接池中的连接**/
             connection = DBConnectionPool.getInstance().getConnection();
             preparedStatement = connection.prepareStatement(sql);
             /**执行sql语句，获取结果集**/
             resultSet = preparedStatement.executeQuery();
             nameColumnList = new ArrayList<String>();
        	 typeColumnList = new ArrayList<String>();
             while(resultSet.next()) {
            	 nameColumnList.add(resultSet.getString(1));
            	 typeColumnList.add(resultSet.getString(2));
             }
             columnList.add(nameColumnList);
             columnList.add(typeColumnList);
         } catch (Exception e) {
             e.printStackTrace();
         } finally {
             if (resultSet != null) {
                 resultSet.close();
             }
             if (preparedStatement != null) {
                 preparedStatement.close();
             }
             if (connection != null) {
                 connection.close();
             }
         }
         return columnList;
    }

	public  List<List<String>> getColumnList() {
		return columnList;
	}
}

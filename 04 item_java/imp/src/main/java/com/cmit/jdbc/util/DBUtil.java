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
* function：数据库，构造连接，提交编译
* author：heym
* create time：2018-12-07
*/
public class DBUtil {
	private int line =0;
	private StringBuilder insertSql=null;
	private StringBuilder deleteByFileNameSql=null;
	private StringBuilder truncateSql=null;
	private List<List<String>> columnList =null;
	private Connection connection = null;
	private PreparedStatement insertStatement = null;
	private PreparedStatement deleteByFileNameStatement = null;
	private PreparedStatement truncateStatement = null;
	private String type = null;
	private int commitRowCount = 0;
	private static Logger log = LoggerFactory.getLogger(DBUtil.class);
	

	/**
	 * 构造函数：1、查询数据库表结构  2、构造SQL 3、获取连接
	 * @param tableName
	 */
    DBUtil(String tableName,String inputType) {
    	try {
    		type = inputType;
    		/**从数据库连接池中获取数据库连接**/
			connection = DBConnectionPool.getInstance().getConnection();
			/**设置不自动提交，以便于在出现异常的时候数据库回滚**/
	    	connection.setAutoCommit(false);
			columnList = queryColumn(tableName);
			if(columnList == null) return ;
	    	StringBuilder columnSql = new StringBuilder();
	        StringBuilder recordSql = new StringBuilder();
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
	       
	        /**开始拼插入的sql语句**/
	        insertSql = new StringBuilder();
	        insertSql.append("INSERT INTO ");
	        insertSql.append(tableName);
	        insertSql.append(" (");
	        insertSql.append(columnSql);
	        insertSql.append(") VALUES (");
	        insertSql.append(recordSql);
	        insertSql.append(")");
	        switch(type) {
	        	case("i"):
	        		/**开始拼全量的删除sql语句**/
	    	        truncateSql = new StringBuilder();
	    	        truncateSql.append("DELETE FROM ");
	    	        truncateSql.append(tableName);
	    	        truncateStatement = connection.prepareStatement(truncateSql.toString());
	    	        break;
	        	case("s"):
	        		/**开始拼回滚的删除sql语句**/
	        		deleteByFileNameSql = new StringBuilder();
	        	  	deleteByFileNameSql.append("DELETE FROM ");
		 	        deleteByFileNameSql.append(tableName);
		 	        deleteByFileNameSql.append(" WHERE ");
		 	        deleteByFileNameSql.append(columnList.get(0).get(columnList.get(0).size()-2));//获取文件名字段
		 	        deleteByFileNameSql.append(" = (?)");
		 	        deleteByFileNameStatement = connection.prepareStatement(deleteByFileNameSql.toString());
		 	        break;
	        }
	        insertStatement = connection.prepareStatement(insertSql.toString());
    	} catch (SQLException e) {
			e.printStackTrace();
		}
    }
    
    
    /**
           * 构造连接
     * @return boolean
     */
    public boolean connect() {
        try {
        	 /**从数据库连接池中获取数据库连接**/
			connection = DBConnectionPool.getInstance().getConnection();
			/**设置不自动提交，以便于在出现异常的时候数据库回滚**/
	    	connection.setAutoCommit(false);
	    	/**执行SQL预编译**/
            insertStatement = connection.prepareStatement(insertSql.toString());
            deleteByFileNameStatement = connection.prepareStatement(deleteByFileNameSql.toString());
            truncateStatement = connection.prepareStatement(truncateSql.toString());
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
        return true;
    }
   
    /**
           *   整表删除,用于全量表
     * @return
     */
    public boolean truncateBatch() {
    	try {
    		if(DataBaseService.getDataBase()==DataBaseService.getMysql()) {
				truncateStatement.addBatch();
				int[] deleteRows = truncateStatement.executeBatch();
				log.info("删除操作：全量数据删除{}行，删除语句：{}",deleteRows[0],truncateSql);
    		}else {
    			truncateStatement.execute();
				log.info("删除操作：全量数据删除完毕，删除语句：{}",truncateSql);
    		}
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
    	return true;
    }
    
    /**
	   * 根据文件名删除，用于新增表
     * @return
     */
    public boolean deleteBatch(String fileName) {
    	try {
    		deleteByFileNameStatement.setObject(1, fileName);
    		deleteByFileNameStatement.addBatch();
    		int[] deleteByFileNameRows = deleteByFileNameStatement.executeBatch();
    		log.info("删除操作：新增数据回滚{}行，回滚语句：{}", deleteByFileNameRows[0],deleteByFileNameSql);
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
    	return true;
    }
    /**
     * 单条插入，通用
     * @param splitRecord
     * @return boolean
     */
    public boolean addBatch(List<String> splitRecord) {
    	try {
    		for(int i=0;i<splitRecord.size();i++) {
				insertStatement.setObject(i+1,splitRecord.get(i));
    		}
    		insertStatement.addBatch();
    	} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
        return true;
    }
    
    
    /**
     * 提交编译并清理本地内存
     * @return boolean
     */
    public boolean clearBatch() {
    	try {
    		int[] insertNumber = insertStatement.executeBatch();
    		int affectRowCount = insertNumber.length;
            commitRowCount += affectRowCount;
            log.info("插入操作（{}）：{}行，插入语句：{}", ++line,affectRowCount,insertSql);
			insertStatement.clearBatch();
		} catch (SQLException e) {
			log.error("编译错误（{}）：{}",++line, e.getMessage());
			return false;
		}
    	return true;
    }
    
    /**
     * 数据库提交并关闭连接
     * @return
     */
    public int  commit() {
    	try {
			connection.commit();
			log.info("成功提交到数据库");
		} catch (SQLException e) {
			e.printStackTrace();
			return -1;
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
    	return commitRowCount;
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
        	 boolean nullFlag = true;
             while(resultSet.next()) {
            	 nameColumnList.add(resultSet.getString(1));
            	 typeColumnList.add(resultSet.getString(2));
            	 nullFlag = false;
             }
             if(nullFlag) return null;//结果集为空，表未找到
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

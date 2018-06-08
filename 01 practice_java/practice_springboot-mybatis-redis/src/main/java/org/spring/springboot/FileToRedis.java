package org.spring.springboot;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import redis.clients.jedis.Jedis;

/**
 * *执行主程序前，先向redis导入数据
 * 功能：保存文本文件到Redis数据库
 * @param host  redis所在主机的ip
 * @param path  文件所在的路径（包含文件名及文件后缀）
 * @param key   文件的key值
 * @throws Exception
 */

public class FileToRedis {
	
	private static String fileName = "Vo_HW_BIN_731_20180119_000003_DECODE";
	private static String host = "127.0.0.1";
	//修改为文件目录
	private static String path = "D:\\GitHub\\springboot-learning-example2\\springboot-mybatis-redis\\src\\main\\resources\\"+fileName;
	private static String key = fileName;

	
	/**
	 * 文件转存为hash
	 */
    private void fileToHash() {
    	//建立一个连接
        Jedis redis = new Jedis(host, 6379);
    	//把文本的每一行数据都读入到map中，<行号，每行的数据>。
        Map<String, String> map = new HashMap();
        String data = null;
        int i = 0;
        try(BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(new File(path)),"GBK"))){

            while ((data = br.readLine())!= null) {
                
                i++;
                map.put(i+"",data);
            }
            //数据存储到redis中，hash表
            redis.hmset(key, map);
        }catch (IOException e){
            e.printStackTrace();
        }
        redis.close();
        
    }
    
	/**
	 * 文件转存为集合
	 */
    private void fileToSet() {
    	//建立一个连接
        Jedis redis = new Jedis(host, 6379);
    	//把文本的每一行数据都读入到set中
        String data = null;
        try(BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(new File(path)),"GBK"))){

            while ((data = br.readLine())!= null) {
            	//数据存储到redis中，set表
            	redis.sadd(key, data);
            }
        }catch (IOException e){
            e.printStackTrace();
        }
        redis.close();
    }
    
	/**
	 * 文件转存为列表
	 */
    private void fileToList() {
    	//建立一个连接
        Jedis redis = new Jedis(host, 6379);
    	//把文本的每一行数据都读入到set中
        String data = null;
        try(BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(new File(path)),"GBK"))){

            while ((data = br.readLine())!= null) {
            	//数据存储到redis中，set表
            	redis.lpush(key, data);
            }
        }catch (IOException e){
            e.printStackTrace();
        }
        redis.close();
    }
    
    public static void main(String[] args) {
        FileToRedis fileToRedis = new FileToRedis();
        //fileToRedis.fileToHash();
        //fileToRedis.fileToSet();
        fileToRedis.fileToList();
        System.out.println("完成数据导入redis！");
    }

}
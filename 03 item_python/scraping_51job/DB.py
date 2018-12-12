# -*- coding: utf-8 -*-
"""
Created on Thu Aug  2 22:27:05 2018

@author: star
"""
import pymysql
import re
import Log
import datetime

config = {
        'host': '127.0.0.1',
        'port': 3306,
        'db': 'db',
        'user': 'root',
        'passwd': 'passwd',
        'charset':'utf8mb4',
        'cursorclass':pymysql.cursors.DictCursor
        }


#获取公参数据
def getTableDict(table):
    conn = pymysql.connect(**config)
    conn.autocommit(1)
    cursor = conn.cursor()
    
    try:
        table_dict = {}
        cursor.execute('SELECT * FROM %s' %table)
        results = cursor.fetchall()
        table_dict = {}
        for result in results:
            table_dict[result['key']] = result['value']
        return table_dict
    except:
        import traceback
        traceback.print_exc()
        # 发生错误时会滚
        conn.rollback()
    finally:
        # 关闭游标连接
        cursor.close()
        # 关闭数据库连接
        conn.close()

def addStyle(table):
    table_list1 = ['param_welfare','param_education','param_company','param_scale','param_industry','param_function']
    table_list2 = ['param_numbers']
    #添加方式为自增
    for i in range(0,len(table_list1)):
        if(table==table_list1[i]):
            return 1
    #添加方式为招聘人数
    for i in range(0,len(table_list2)):
        if(table==table_list2[i]):
            return 2
    
def modTable(unknowList,table):
    conn = pymysql.connect(**config)
    conn.autocommit(1)
    cursor = conn.cursor()
    
    try:
        flag = addStyle(table)
        #添加方式为自增
        if(flag == 1):
            cursor.execute('SELECT max(value) as mv FROM %s' %table)
            maxValue = int(cursor.fetchone()['mv'])
            for new in unknowList:
                maxValue +=1
                cursor.execute('insert into %s values ("%s",%d)' %(table,new,maxValue))
                Log.info('insert into %s values ("%s",%d)' %(table,new,maxValue))
        #招聘人数
        if(flag == 2):
            for new in unknowList:
                num = re.findall(re.compile(r'招(.*?)人'),new)
                try:
                    num = int(num[0])
                    cursor.execute('insert into %s values ("%s",%d)' %(table,new,num))
                    Log.info('insert into %s values ("%s",%d)' %(table,new,num))
                except:#未知字符，从99之后开始加
                    cursor.execute('SELECT max(value) as mv FROM %s' %table)
                    maxValue = int(cursor.fetchone()['mv'])
                    maxValue +=1
                    cursor.execute('insert into %s values ("%s",%d)' %(table,new,maxValue))
                    Log.info('insert into %s values ("%s",%d)' %(table,new,maxValue))
    except:
        import traceback
        traceback.print_exc()
        # 发生错误时会滚
        conn.rollback()
    finally:
        # 关闭游标连接
        cursor.close()
        # 关闭数据库连接
        conn.close()

def insert(list):
    conn = pymysql.connect(**config)
    conn.autocommit(1)
    cursor = conn.cursor()
    try:
        tmp =''
        for i in range(0,len(list)):
            tmp = tmp+"'"+str(list[i]) +"',"
            if(i==7 or i==8):
                tmp = tmp+"'"+' ' +"',"
        tmp = tmp + "'"+str(datetime.datetime.now())+"'"#省份和地区暂时补充为空
        cursor.execute('insert into jobdata values (%s)' %tmp)
        Log.info("插入成功")
        return True
    except Exception as e:
        Log.error("插入失败：%s %s"%(e,'insert into jobdata values (%s)' %tmp))
        import traceback
        traceback.print_exc()
        # 发生错误时会滚
        conn.rollback()
        return False
    finally:
        # 关闭游标连接
        cursor.close()
        # 关闭数据库连接
        conn.close()

def log(key,url,flag):
    conn = pymysql.connect(**config)
    conn.autocommit(1)
    cursor = conn.cursor()
    try:
        tmp = "'" + key + "','" + url + "'," + str(flag) + ",'" + str(datetime.datetime.now())+"'"
        cursor.execute('insert into implog values (%s)' %tmp)
    except Exception as e:
        Log.error("插入失败：%s %s"%(e,'insert into implog values (%s)' %tmp))
        import traceback
        traceback.print_exc()
        # 发生错误时会滚
        conn.rollback()
        return False
    finally:
        # 关闭游标连接
        cursor.close()
        # 关闭数据库连接
        conn.close()
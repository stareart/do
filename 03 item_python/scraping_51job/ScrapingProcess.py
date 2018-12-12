# -*- coding: utf-8 -*-
"""
Created on Sun Jul 29 10:58:58 2018

@author: star
"""
import re
import random
import requests
from lxml import html
import Rule
import DB
import Log
import datetime
import time


uapools=[
    "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_7_3) AppleWebKit/535.20 (KHTML, like Gecko) Chrome/19.0.1036.7 Safari/535.20",
    "Opera/9.80 (Macintosh; Intel Mac OS X 10.6.8; U; fr) Presto/2.9.168 Version/11.52"]

   
def getLinks(page):
    url ='http://search.51job.com/list/'+city+',000000,0000,00,9,99,'+key+',2,'+ str(page)+'.html'
    headers={'Host':'search.51job.com','Upgrade-Insecure-Requests':'1','User-Agent':random.choice(uapools)}
    print(url)
    r= requests.get(url,headers,timeout=10)
    r.encoding = 'GBK'
    s=requests.session()
    s.keep_alive = False
    reg = re.compile(r'class="t1 ">.*? <a target="_blank" title=".*?" href="(.*?)".*? <span class="t2">', re.S)
    links = re.findall(reg, r.text)
    print("页码链接:{}，个数：{}".format(url,len(links)))
    return links

#多页处理，下载到文件
def getContent(link):
    print("爬取链接:"+link)
    headers={'Host':'search.51job.com','Upgrade-Insecure-Requests':'1','User-Agent':random.choice(uapools)} 
    r=requests.get(link,headers,timeout=10)#proxies = proxy
    s=requests.session()
    s.keep_alive = False
    r.encoding = 'GBK'
    try:
        htmlText=html.fromstring(r.text)[0]#职位为空时返回空列表和链接
        #工作名称
        job=dealChar(htmlText.xpath(Rule.job()))
        #公司
        company = dealChar(htmlText.xpath(Rule.company()))[0]
        #福利
        welfare=htmlText.xpath(Rule.label())
        #地区、工作年限、学历、招聘人数、发布时间
        msgltype = dealChar(htmlText.xpath(Rule.msgltype()))
        numbers = ''
        publish = ''
        experience = ''
        education = ''
        area = ''
        for i in range(1,len(msgltype)):
            if i<5:
                if re.search( r'招', msgltype[i], re.M|re.I):
                    numbers = msgltype[i]#招聘人数
                elif re.search( r'发布', msgltype[i], re.M|re.I):
                    publish = msgltype[i]#发布时间
                elif re.search( r'经验', msgltype[i], re.M|re.I):
                    experience = msgltype[i]#工作年限要求
                else:
                    education = msgltype[i]#学历
        area = msgltype[0]#工作地点
        #收入
        salary = htmlText.xpath(Rule.salary())
        #公司信息
        companytype= re.findall(re.compile(Rule.companytype(),re.S),r.text)
        scale = re.findall(re.compile(Rule.scale(),re.S),r.text)
        industry = re.findall(re.compile(Rule.industry(),re.S),r.text)
        #任职要求
        #requirement=dealChar(htmlText.xpath(Rule.jobMessage()))
        requirement=dealChar(re.findall(re.compile(Rule.requirement(),re.S),r.text))
        #工作类别
        function=htmlText.xpath(Rule.jobType())[0].split('/')
        #地址
        address=dealChar(htmlText.xpath(Rule.contact()))
        #===合并列表
        contentList=[job,company,welfare,education,numbers,publish,experience,salary,area,companytype,scale,industry,requirement,function,address,[link]]
        Log.info('数据获取成功:%s'%contentList)
        return contentList 
    except Exception as e:
        Log.error('数据获取失败:第%s行 原因：%s %s'%(e.__traceback__.tb_lineno,e,link))
        return []
    

#去除字符
def dealChar(list):
    for i in range(0,len(list)):
        list[i]=re.sub("[ \r\n\t\xa0]", '', list[i])
        list[i]=re.sub(r"<(.*?)>", '', list[i])
    while '' in list:
        list.remove('')
    return list

#数据转化
def dealData(list):
    try:
        #转列表
        for i in range(0,len(list)):
            if(type(list[i])==str):
                list[i]=[list[i]]
        '''直接转化：岗位0、公司1、地区8、地址14、链接15 不处理'''
        for index in [0,1,8,14,15]:
            if(len(list[index])!=0):
                list[index] = list[index][0]
            else:
                list[index] = ''
        '''特殊转化：发布时间5'''
        publish = re.findall(re.compile(r'(\d{2})-(\d{2})发布'),list[5][0])
        list[5] = datetime.datetime.strptime(str(datetime.datetime.now().year)+publish[0][0]+publish[0][1],'%Y%m%d')
        '''特殊转化：工作经验6'''
        experience =  re.findall(re.compile(r'\d*'),list[6][0])
        while '' in experience:
            experience.remove('')
        tmpLen = len(experience)
        if(tmpLen==0):
            list[6] = '0-99'
        elif(tmpLen==1):
            list[6] = experience[0]+'-99'
        else:
            list[6] = experience[0]+'-'+experience[1]
        '''特殊转化：工资7'''
        unit = ['/小时','元/天','千/月','万/月','万/年']
        unit_real = ''
        for un in unit:
            if(re.match('(.*)'+un,list[7][0])!=None):
                salary= re.findall(re.compile(r'[\d+\.\d]*'),list[7][0])
                unit_real = un
                break
        while '' in salary:
            salary.remove('')
        #单位转为元/月
        if(unit_real == unit[0]):
            salary[0] = float(salary[0])*8*21
            if(len(salary)==2):
                salary[1] = float(salary[1])*8*21
            else:
                salary.append(salary[0])
        elif(unit_real == unit[1]):
            salary[0] = float(salary[0])*21
            if(len(salary)==2):
                salary[1] = float(salary[1])*21
            else:
                salary.append(salary[0])
        elif(unit_real == unit[2]):
            salary[0] = float(salary[0])*1000
            if(len(salary)==2):
                salary[1] = float(salary[1])*1000
            else:
                salary.append(salary[0])
        elif(unit_real == unit[3]):
            salary[0] = float(salary[0])*10000
            if(len(salary)==2):
                salary[1] = float(salary[1])*10000
            else:
                salary.append(salary[0])
        elif(unit_real == unit[4]):
            salary[0] = round(float(salary[0])*10000/12.0,1)
            if(len(salary)==2):
                salary[1] = round(float(salary[1])*10000/12.0,1)
            else:
                salary.append(salary[0])
        list[7] = str(salary[0])+'-'+str(salary[1])
        '''特殊转化：工作要求12'''
        requirement =''
        for line in list[12]:
            requirement += line
        list[12] = requirement
        '''编码：福利2、学历3、招聘人数4、公司类型9、规模10、行业11、职能13'''
        table_relation = [['param_welfare',2],['param_education',3],['param_numbers',4],['param_company',9],['param_scale',10],['param_industry',11],['param_function',13]]
        for table in table_relation:
            while True:
                table_dict = DB.getTableDict(table[0]);
                unknowList = []
                for key in list[table[1]]:
                    if key not in table_dict:#存在未知值
                        unknowList.append(key)
                if(len(unknowList)!=0):#往数据库插值
                    DB.modTable(unknowList,table[0])
                else:#全部能匹配，赋值 |value1|value2|
                    tmp_str = "|"
                    for key in list[table[1]]:
                        tmp_str = tmp_str+str(table_dict[key])+'|'
                    break
            #替换
            list[table[1]] = tmp_str
        Log.info('数据清理成功:%s'%list)
        return True
    except Exception as e:
        Log.error('数据处理失败:第%s行 原因：%s %s'%(e.__traceback__.tb_lineno,e,list))
        return False
    
   

def __init__():
	#北京,上海,广州,深圳,杭州
	#010000,020000,030200,040000,080200
	city='040000'
	key='采购'   
	for i in range(1,600):
		print('正在爬取第{}页信息:'.format(i))
		Log.info('城市%s %s正在爬取第%d页信息:'%(city,key,i))
		links=getLinks(i)
		for link in links:
			list = getContent(link)
			flag = 0
			if(len(list)!=0):
				if(dealData(list)):
					if(DB.insert(list)):
						flag = 1
			DB.log(key,link,flag)
			time.sleep(random.uniform(0.1,0.5))
# -*- coding: utf-8 -*-
"""
Created on Wed Aug  1 23:11:25 2018

@author: star
"""

def job():
    return '//div[@class="tHeader tHjob"]//h1/text()'

def company():
    return '//p[@class="cname"]/a/text()'
    
def label():
    return '//div[@class="t1"]/span/text()'
    
def education():
    return r'<span class="sp4"><em class="i2"></em>(.*?)</span>'
    
def msgltype():
    return '//p[@class="msg ltype"]/text()'
    
def time():
    return r'<span class="sp4"><em class="i4"></em>(.*?)</span>'
    
def salary():
    return '//div[@class="cn"]/strong/text()'
    
def area():
    return '//div[@class="tHeader tHjob"]//span[@class="lname"]/text()'
    
def numbers():
    return r'<span class="sp4"><em class="i3"></em>(.*?)</span>'
    
    
def workyear():
    return r'<span class="sp4"><em class="i1"></em>(.*?)</span>'
    
def describe():
    return r'<div class="bmsg job_msg inbox">(.*?)任职要求'
    
def requirement():
    return r'<div class="bmsg job_msg inbox">(.*?)<div class="mt10">'

def companyMessage():
    return '//div[@class="com_tag"]/p/text()'
    
def companytype():
    return r'<span class="i_flag"></span>(.*?)</p>'

def scale():
    return r'<span class="i_people"></span>(.*?)</p>'

def industry():
    return r'<span class="i_trade"></span>(.*?)</p>'
    
def jobMessage():
    return '//div[@class="bmsg job_msg inbox"]/*/text()' 
    
def jobType():
    return '//span[@class="el"]/text()'
    
def contact():
    return '//div[@class="bmsg inbox"]/p/text()'
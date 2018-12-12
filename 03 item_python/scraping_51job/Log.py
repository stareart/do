# -*- coding: utf-8 -*-
"""
Created on Sun Aug  5 15:58:35 2018

@author: star
"""
import logging
import datetime
import sys


def info(message):
    className= sys._getframe().f_back.f_code.co_name
    logger = logging.getLogger(className)
    logger.setLevel(logging.INFO)
    formatter = logging.Formatter("%(asctime)s - %(name)s - %(levelname)s - %(message)s")
    info = logging.FileHandler("Info%s.log"%datetime.date.today())
    info.setFormatter(formatter)
    logger.addHandler(info)
    logger.info(message)
    logger.removeHandler(info)

def error(message):
    className= sys._getframe().f_back.f_code.co_name
    logger = logging.getLogger(className)
    logger.setLevel(logging.ERROR)
    formatter = logging.Formatter("%(asctime)s - %(name)s - %(levelname)s - %(message)s")
    error = logging.FileHandler("Error%s.log"%datetime.date.today())
    error.setFormatter(formatter)
    logger.addHandler(error)
    logger.error(message)
    logger.removeHandler(error)

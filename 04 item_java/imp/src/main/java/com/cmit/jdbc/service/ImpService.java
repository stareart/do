package com.cmit.jdbc.service;
import java.io.File;
import java.util.HashMap;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.cmit.jdbc.util.DataUtil;
import com.cmit.jdbc.util.FileUtil;
import com.cmit.jdbc.util.ParamUtil;

/**
* function：文件入库，主入口
* author：heym
* create time：2018-12-07
*/
@Service
public class ImpService {
	@Value("${file.sourcesDir}")
	private String sourcesDir;
	@Value("${file.targetDir}")
	private String targetDir;
	private static Logger log = LoggerFactory.getLogger(ImpService.class);

	@Scheduled(cron = "${scheduler.timer}")
    public void  impService() {
		// 目的文件目录不能与源文件目录相同或为其子目录
		if(targetDir.startsWith(sourcesDir)) {
			log.warn("目的文件目录不能与源文件目录相同或为其子目录");
			return ;
		}
    	// 获取表参数
    	List<HashMap<String, String>> tableList = ParamUtil.getTable();
    	// 复制目录
    	FileUtil.copySourceDir(sourcesDir, targetDir);
    	// 文件名格式
    	File dealFile = null;
    	for(HashMap<String, String> tableMap:tableList) {
    		List<File> files = FileUtil.fileFilter(sourcesDir,tableMap.get("fileNameRule"));
    		for(File file:files) {
    			log.info("扫描到文件：{}",file.getAbsoluteFile());
    			dealFile = FileUtil.moveFile(file,sourcesDir, targetDir,0);
    			DataUtil dataUtil = new DataUtil();
    			if(dataUtil.fileToData(dealFile,tableMap)) {
    				FileUtil.moveFile(dealFile,sourcesDir, targetDir,1);
    			}else {
    				FileUtil.moveFile(dealFile,sourcesDir, targetDir,2);
    			}
        	}
    	}
    }
    
}

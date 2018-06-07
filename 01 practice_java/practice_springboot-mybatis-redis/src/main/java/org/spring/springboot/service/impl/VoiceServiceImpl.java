package org.spring.springboot.service.impl;

import java.text.SimpleDateFormat;
import java.util.Date;
import org.spring.springboot.dao.VoiceDao;
import org.spring.springboot.domain.Voice;
import org.spring.springboot.service.VoiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;


/**
 * 语音结算逻辑实现类
 * @author heym 2018/06/05
 *
 */
@Service
public class VoiceServiceImpl implements VoiceService{
	
	@Autowired
    private VoiceDao voiceDao;
	
	@Autowired
    private Voice voice;
	
	@Autowired
	private StringRedisTemplate stringRedisTemplate;
	
	/**
	 *  redis标准话单入详单表
	 *  1.从redis取数据
	 *  2.插入Mysql hymdb.CDR_VOICE_731
	 */
	public void insertVoiceCDR(String oriFileName) {
		
		ListOperations<String, String> operList = stringRedisTemplate.opsForList();
		if (operList.size(oriFileName) != 0) {
			//获取当前时间
			Date day=new Date();    
			SimpleDateFormat dt = new SimpleDateFormat("yyyyMMdd"); 
            for(String oneCDR:operList.range(oriFileName, 1, -2)) {
              String[] oneSplitCDR = oneCDR.split("\\|");
              //设置voice对象值
              voice.setOriFileName(oneSplitCDR[0]);
              voice.setRecordType(oneSplitCDR[1]);
              voice.setCallingNumber(oneSplitCDR[2]);
              voice.setCalledNumber(oneSplitCDR[3]);
              voice.setCallStartTm(oneSplitCDR[4]);
              voice.setCallDuration(oneSplitCDR[5]);
              voice.setCalledMSRN(oneSplitCDR[6]);
              voice.setIncomingTkgp16(oneSplitCDR[7]);
              voice.setIncomingTkgp(oneSplitCDR[8]);
              voice.setOutgoingTkgp16(oneSplitCDR[9]);
              voice.setOutgoingTkgp(oneSplitCDR[10]);
              voice.setCdrSeq(oneSplitCDR[11]);
              voice.setCauseForTerm(oneSplitCDR[12]);
              voice.setMscNum(oneSplitCDR[13]);
              voice.setCallReference("");//暂时设置为空
              voice.setDbInsrDt(Integer.parseInt(dt.format(day)));
              voice.setDbInsrTm(day);
              //导入dao层
              voiceDao.insertVoiceCDR(voice);
            }
            
        }else {
        	System.out.println("redis 数据为空,长度："+operList.size(oriFileName));
        }
		

	}
}

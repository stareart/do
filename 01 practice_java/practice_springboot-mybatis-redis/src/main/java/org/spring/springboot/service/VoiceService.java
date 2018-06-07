package org.spring.springboot.service;



/**
 * 语音结算逻辑接口类
 * @author heym 2018/06/05
 *
 */
public interface VoiceService {
	
	/**
	 *  redis标准话单入详单表
	 */
	public void insertVoiceCDR(String oriFileName);

}

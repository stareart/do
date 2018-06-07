package org.spring.springboot.dao;


import org.spring.springboot.domain.Voice;

/**
 *  语音结算DAO接口类
 * @author heym 2018/06/06
 *
 */

public interface VoiceDao {
	
	public void insertVoiceCDR(Voice voice);

	
}

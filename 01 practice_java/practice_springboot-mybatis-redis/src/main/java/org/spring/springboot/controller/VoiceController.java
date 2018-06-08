package org.spring.springboot.controller;


import org.spring.springboot.service.VoiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * 语音结算逻辑控制层
 * @author heym 2018/06/05
 *
 */
@RestController
public class VoiceController {

	@Autowired
	private VoiceService voiceService;
	//get触发从redis向Mysql表 导入数据       http://localhost:8080/api/Vo_HW_BIN_731_20180119_000003_DECODE
	@RequestMapping(value = "/api/{oriFileName}", method = RequestMethod.GET)
    public void inserVoiceCDR(@PathVariable("oriFileName") String oriFileName) {
         voiceService.insertVoiceCDR(oriFileName);
    }
}

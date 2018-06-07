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
	
	@RequestMapping(value = "/api/{oriFileName}", method = RequestMethod.GET)
    public void inserVoiceCDR(@PathVariable("oriFileName") String oriFileName) {
         voiceService.insertVoiceCDR(oriFileName);
    }
}

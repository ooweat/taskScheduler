package kr.co.ooweat.taskScheduler.model;

import java.util.Map;
import lombok.Data;

import java.util.HashMap;
import java.util.List;

@Data
public class AlertModel {
	String receiver;
	String templateType;

	String service;
	String title;
	String content;
	String sendDate;

	HashMap<String, String> paramMap;	//Map 형식의 파라미터
	List<Map<String, Object>> paramListMap;	//List<Map> 형식의 파라미터
}

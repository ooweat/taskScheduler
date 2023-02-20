package kr.co.ooweat.taskScheduler.controller;

import kr.co.ooweat.taskScheduler.common.Util;
import kr.co.ooweat.taskScheduler.mappers.oracle.manager.ManagerMapper;
import kr.co.ooweat.taskScheduler.service.BatchService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;

import javax.mail.MessagingException;
import java.io.UnsupportedEncodingException;

@Slf4j
@CrossOrigin("*")
@EnableAutoConfiguration
@RestController
public class BatchController {

	@Autowired
	private BatchService batchService;

	@Autowired
	private ManagerMapper managerMapper;

	@Value("${charge.use.flag}")
	private String chargeFlag;

	//HINT: workDayCheck() - Oracle DB 기준으로 작업일(오늘)이 영업일이면 진행~
	/**
	 * 스케쥴러
	 * 1. 매일 00 시 00 분 05초에 조건에 따라 충전하는 스케쥴러
	 *     1: 초(0-59)
	 *     2: 분(0-59)
	 *     3: 시(0-23)
	 *     4: 일(1-31)
	 *     5: 월(1-12)
	 *     6: 요일(0-7) 0, 7 : 일요일 / 1: 월요일 / 6:토요일
	* */

	@Scheduled(cron="${batch.5min.crond}") // 5분마다
	public void t5minCheck() throws UnsupportedEncodingException, MessagingException {
		log.info("Path: {}", Util.pathUtils().methodName());
		log.info("[{}]",Util.dateUtils().now());
		batchService.processAliveCheck();
	}

	@Scheduled(cron="${batch.halfHours.crond}") // 30분마다
	public void halfHoursCheck() throws UnsupportedEncodingException, MessagingException {
		log.info("Path: {}", Util.pathUtils().methodName());
		log.info("[{}]",Util.dateUtils().now());
		if(workDayCheck()){	

		}
	}

	@Scheduled(cron="${batch.workHours.crond}") // 1시간마다(영업일)
	public void workHoursCheck(){
		log.info("Path: {}", Util.pathUtils().methodName());
		log.info("[{}]",Util.dateUtils().now());
		if(workDayCheck()){	

		}
	}

	@Scheduled(cron="${batch.morning.crond}") // 07시 40분 부터 09시 40분까지 1시간 간격(영업일)
	public void halfOf7to9Check() throws UnsupportedEncodingException, MessagingException {
		log.info("Path: {}", Util.pathUtils().methodName());
		log.info("[{}]",Util.dateUtils().now());
		if(workDayCheck()){	

		}
	}

	@Scheduled(cron="${batch.workHalfHours.crond}") // 9시부터 30분마다(영업일)
	public void workHalfHoursCheck() throws UnsupportedEncodingException, MessagingException {
		log.info("Path: {}", Util.pathUtils().methodName());
		log.info("[{}]",Util.dateUtils().now());
		if(workDayCheck()){	

		}
	}

	@Scheduled(cron="${batch.morning.oneOff.crond}") // 아침 9시 한 번
	public void oneOffOnMorning() throws UnsupportedEncodingException, MessagingException {
		log.info("Path: {}", Util.pathUtils().methodName());
		log.info("[{}]",Util.dateUtils().now());
		if(workDayCheck()){	

		}
	}

	private boolean workDayCheck(){
		return managerMapper.workDayCheck() == 1 ? true : false;
	}
}

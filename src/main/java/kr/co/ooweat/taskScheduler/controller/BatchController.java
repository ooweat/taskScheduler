package kr.co.ooweat.taskScheduler.controller;

import kr.co.ooweat.taskScheduler.service.BatchService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;

import javax.mail.MessagingException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

/**
 * @author chris
 * @version 1.2.1
 * @class BatchController
 * @Created 2022-06-21
 * @Desc : 기존 crontab을 대체할 Spring Batch
 **/

@Slf4j
@CrossOrigin("*")
@EnableAutoConfiguration
@RestController
public class BatchController {

    @Autowired
    private BatchService batchService;
    
    /**
     * 스케쥴러 1. 매일 00 시 00 분 05초에 조건에 따라 충전하는 스케쥴러 1: 초(0-59) 2: 분(0-59) 3: 시(0-23) 4: 일(1-31) 5:
     * 월(1-12) 6: 요일(0-7) 0, 7 : 일요일 / 1: 월요일 / 6:토요일
     */

    @Scheduled(cron = "${batch.ones.9AM.crond}") // 아침 9시 한 번
    public void ones9AM() throws IOException, MessagingException {
        batchService.vmDiskUsageCheck();
    }

    //NOTE: 2023.03.13 twkim 영업일로 별도 처리
    @Scheduled(cron = "${batch.ones.9AM.crond}") // 아침 9시 한 번(영업일)
    public void ones9AMWorkTime() throws IOException, MessagingException {

        if (isWorkDay()) {    //작업일(오늘)이 영업일이면 진행~

        }
    }

    @Scheduled(cron = "${batch.1min.crond}") // 1분마다
    public void every1Min() throws UnsupportedEncodingException, MessagingException {

        batchService.processAliveCheck();
    }

    @Scheduled(cron = "${batch.5min.crond}") // 5분마다
    public void every5Min() throws UnsupportedEncodingException, MessagingException {

    }

    @Scheduled(cron = "${batch.30min.crond}") // 30분마다
    public void every30Min() throws UnsupportedEncodingException, MessagingException {

    }

    @Scheduled(cron = "${batch.1hour.workTime.crond}") // 1시간마다
    public void every1Hour() {

    }

    @Scheduled(cron = "${batch.1hour.atMorning.crond}") // 07시 40분 부터 09시 40분까지 1시간 간격(영업일)
    public void every1HourAtMorning() throws UnsupportedEncodingException, MessagingException {

        if (isWorkDay()) {    //작업일(오늘)이 영업일이면 진행~

        }
    }

    @Scheduled(cron = "${batch.30min.workTime.crond}") // 9시부터 30분마다(영업일)
    public void every30MinWorkTime() throws UnsupportedEncodingException, MessagingException {

        if (isWorkDay()) {    //작업일(오늘)이 영업일이면 진행~

        }
    }

    @Scheduled(cron = "${batch.monthly.crond}") //매월 말일(0시에)
    public void monthly() throws UnsupportedEncodingException, MessagingException {

        batchService.createTable();
    }

    private boolean isWorkDay() {
        return true;
        //return managerMapper.workDayCheck(Util.dateUtils().yyyyMMdd(0)) == 1 ? true : false;
    }
}

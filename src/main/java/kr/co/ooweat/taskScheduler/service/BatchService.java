package kr.co.ooweat.taskScheduler.service;

import kr.co.ooweat.taskScheduler.common.Alert;
import kr.co.ooweat.taskScheduler.common.ServerDescEnum;
import kr.co.ooweat.taskScheduler.common.Util;
import kr.co.ooweat.taskScheduler.model.AlertModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.util.*;

import static kr.co.ooweat.taskScheduler.common.ServerDescEnum.SERVICE_KAKAO_ALERT;

/**
 * The type Batch service
 */
@Slf4j
@Service
@EnableAutoConfiguration
public class BatchService {

    //DESC: VM 디스크 용량 알림 메소드
    public void vmDiskUsageCheck() throws IOException, MessagingException {
        //NOTE: twkim 2023.02.22 리팩토링으로 인한 코드 수정
        List<HashMap<String, String>> dataList = new ArrayList<>();
        for (ServerDescEnum serverDescEnum : ServerDescEnum.values()) {
            dataList.add(Util.connectVM(serverDescEnum, 2));
        }

        Alert.smtp(Util.alertModelMaking("system", "ALERT", "vmDiskUsageCheck",
                "서버 사용량 알림", "서버 사용량 알림", Util.dateUtils().now(),
                dataList));
    }

    //프로세스 생존여부 확인 메소드
    public void processAliveCheck() throws MessagingException, UnsupportedEncodingException {
        try {
            (new Socket(SERVICE_KAKAO_ALERT.getHost(), 8081)).close();
            //TODO: 서비스가 동작하지 않으면, 대처는 어떻게 처리할 것인지?
        } catch (IOException e) {
            log.error("서비스 연결되지 않음");
            Alert.kakaoAlert(Util.alertModelMaking("system", "RM", "KakaoAlertProcess",
                    "1", SERVICE_KAKAO_ALERT.getDescription() + " 프로세스(오라클) 미동작 \n"
                            + "vmmsTrade, 데이터베이스 상태 확인 요망", Util.dateUtils().now()));

            Alert.kakaoAlert(Util.alertModelMaking("010-7320-4955", "RM", "KakaoAlertProcess",
                    "1", SERVICE_KAKAO_ALERT.getDescription() + " 프로세스(오라클) 미동작 \n"
                            + "vmmsTrade, 데이터베이스 상태 확인 요망", Util.dateUtils().now()));
            //cprProcess();
        }
    }

    private AlertModel alertModelMaking(String receiver, String templateType, String service, String title, String content, String sendDate) {
        AlertModel alertModel = new AlertModel();
        alertModel.setReceiver(receiver);
        alertModel.setTemplateType(templateType);
        alertModel.setService(service);
        alertModel.setTitle(title);
        alertModel.setContent(content);
        alertModel.setSendDate(sendDate);
        return alertModel;
    }

    private AlertModel alertModelMaking(String receiver, String templateType, String service, String title, String content, String sendDate, List<HashMap<String, String>> paramListMap) {
        AlertModel alertModel = new AlertModel();
        alertModel.setReceiver(receiver);
        alertModel.setTemplateType(templateType);
        alertModel.setService(service);
        alertModel.setTitle(title);
        alertModel.setContent(content);
        alertModel.setSendDate(sendDate);
        alertModel.setParamListMap(paramListMap);

        return alertModel;
    }

    //NOTE: Spring Scheduler 에 따라 테이블 생성토록.
    public void createTable() throws MessagingException, UnsupportedEncodingException {
        int createTableCount = 0;
        //NOTE: 카카오 Log Table(Void)
        //managerMapper.createKakaoLogYYYYMM(Util.dateUtils().yyyyMM(1));
        //NOTE: MMS Log Table(Void)
        //managerMapper.createMMSLogYYYYMM(Util.dateUtils().yyyyMM(1));

        //createTableCount += managerMapper.checkCreatedKakaoLogTable(Util.dateUtils().yyyyMM(1));
        //createTableCount += managerMapper.checkCreatedMMSLogTable(Util.dateUtils().yyyyMM(1));
        if (createTableCount > 1) {
            Alert.smtp(alertModelMaking("system", "ALERT", "createTable",
                "테이블 생성완료", "월별 테이블 생성 완료(Kakao, MMS)", Util.dateUtils().now()));
            Alert.kakaoAlert(alertModelMaking("system", "ALERT",
                "※MariaDB", "월별 테이블" + Util.dateUtils().yyyyMM(1) + " 생성",
                "월별 테이블 자동 생성 완료 알림(Kakao, MMS)",
                Util.dateUtils().now()));
        } else {
            Alert.kakaoAlert(alertModelMaking("system", "RM", "createTable",
                "※MariaDB", "월별 테이블 생성 실패 ", Util.dateUtils().now()));
        }
    }
}

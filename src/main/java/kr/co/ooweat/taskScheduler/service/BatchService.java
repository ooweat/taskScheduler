package kr.co.ooweat.taskScheduler.service;

import kr.co.ooweat.taskScheduler.common.Alert;
import kr.co.ooweat.taskScheduler.common.Util;
import kr.co.ooweat.taskScheduler.mappers.maria.manager.ManagerMapper;
import kr.co.ooweat.taskScheduler.model.AlertModel;
import kr.co.ooweat.taskScheduler.common.ServiceList;
import kr.co.ooweat.taskScheduler.model.ServiceServer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.util.*;

/**
 * The type Batch service
 */
@Slf4j
@Service
@EnableAutoConfiguration
public class BatchService {

    @Autowired
    private ManagerMapper managerMapper;

    //마리아 Node Count Check
    public void ooweatMariaNodeCountCheck() throws UnsupportedEncodingException, MessagingException {
        int galleraNodeCount = Integer.parseInt(String.valueOf(managerMapper.galleraInfo().get(0).get("Value")));
        if (galleraNodeCount < 3) {        //현재 node 의 갯수는 3개이다.
            Alert.kakaoAlert(alertModelMaking("system", "RM", "MariaNode", "※DATABASE", "Maria 노드점검요망(정상: 3) / 노드 Count: " + galleraNodeCount, Util.dateUtils().now()));
            Alert.smtp(alertModelMaking("system", "RM", "MariaNode", "※DATABASE", "Maria 노드점검요망(정상: 3) / 노드 Count: " + galleraNodeCount, Util.dateUtils().now()));
        } else {
            log.info("MariaDB Node Count::{}", galleraNodeCount + " //정상");
        }
    }

    //DESC: VM 디스크 용량 알림 메소드
    public void vmDiskUsageCheck() throws IOException, MessagingException {
        //NOTE: twkim 2023.02.22 리팩토링으로 인한 코드 수정
        List<HashMap<String, String>> dataList = new ArrayList<>();
        for (ServiceServer serviceServer : ServiceServer.values()) {
            dataList.add(Util.connectVM(serviceServer));
        }

        Alert.smtp(alertModelMaking("system", "ALERT", "vmDiskUsageCheck",
            "서버 사용량 알림", "서버 사용량 알림", Util.dateUtils().now(),
            dataList));
    }

    //프로세스 생존여부 확인 메소드
    public void processAliveCheck() throws MessagingException, UnsupportedEncodingException {
        try {
            //알림톡 프로세스 확인
            (new Socket(ServiceList.SERVICE_KAKAO_ALERT.getHost(), ServiceList.SERVICE_KAKAO_ALERT.getPort())).close();
        } catch (IOException e) {
            log.error("서비스 연결되지 않음");
            Alert.smtp(alertModelMaking(
                    "system",
                    "RM",
                    "KakaoAlertProcess",
                    "프로세스 확인",
                    ServiceList.SERVICE_KAKAO_ALERT.getDescription(), Util.dateUtils().now()));
            throw new RuntimeException(e);
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
        managerMapper.createKakaoLogYYYYMM(Util.dateUtils().yyyyMM(1));
        //NOTE: MMS Log Table(Void)
        managerMapper.createMMSLogYYYYMM(Util.dateUtils().yyyyMM(1));

        createTableCount += managerMapper.checkCreatedKakaoLogTable(Util.dateUtils().yyyyMM(1));
        createTableCount += managerMapper.checkCreatedMMSLogTable(Util.dateUtils().yyyyMM(1));
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

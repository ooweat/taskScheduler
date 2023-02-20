package kr.co.ooweat.taskScheduler.service;

import kr.co.ooweat.taskScheduler.common.Alert;
import kr.co.ooweat.taskScheduler.common.Util;
import kr.co.ooweat.taskScheduler.mappers.maria.manager.ManagerMapper;
import kr.co.ooweat.taskScheduler.model.AlertModel;
import kr.co.ooweat.taskScheduler.common.ServiceList;
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
}

package kr.co.ooweat.taskScheduler.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.co.ooweat.taskScheduler.common.Alert;
import kr.co.ooweat.taskScheduler.common.ServerDescEnum;
import kr.co.ooweat.taskScheduler.common.Util;
import kr.co.ooweat.taskScheduler.model.AlertModel;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.HttpClientBuilder;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.util.*;
import org.springframework.util.ObjectUtils;

import static kr.co.ooweat.taskScheduler.common.ServerDescEnum.SERVICE_KAKAO_ALERT;
import static kr.co.ooweat.taskScheduler.common.Util.alertModelMaking;

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
        List<Map<String, Object>> dataList = new ArrayList<>();
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

    private AlertModel alertModelMaking(String receiver, String templateType, String service, String title, String content, String sendDate, List<Map<String, Object>> paramListMap) {
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
    
    public void remainCheck(List<Map<String, String>> placeList) throws MessagingException, UnsupportedEncodingException {
        try {
            for (Map<String, String> rule : placeList) {
                List<Map<String, Object>> remainSeatList = new ArrayList<>();
                int remainCount = 0;
                HttpClient client = HttpClientBuilder.create().build(); // HttpClient 생성
                HttpGet getRequest = new HttpGet(rule.get("api-url")); //GET 메소드 URL 생성
                //getRequest.addHeader("x-api-key", none); //KEY 입력
            
                HttpResponse response = client.execute(getRequest);
            
                //Response 출력
                if (response.getStatusLine().getStatusCode() == 200) {
                    ResponseHandler<String> handler = new BasicResponseHandler();
                    String body = handler.handleResponse(response);
                    JSONParser jsonParser = new JSONParser();
                
                    JSONObject convertedObject = (JSONObject) jsonParser.parse(body);
                    JSONObject resDataObject = (JSONObject) convertedObject.get("data");
                
                    for (Object jsonObject : (JSONArray) resDataObject.get("remainSeat")) {
                        JSONObject toastObject = (JSONObject) jsonObject;
                        remainCount += Integer.parseInt(
                            String.valueOf((Long) toastObject.get("remainCnt")));
                        remainSeatList.add(getMapFromJSONObject((JSONObject) jsonObject));
                    }
                    //10월 23일
                
                } else {
                    System.out.println(
                        "response is error : " + response.getStatusLine().getStatusCode());
                }
            
                if (remainCount > 0) {
                    Alert.smtp(alertModelMaking("ooweat@kakao.com", "ALERT", "remain",
                        rule.get("place"), rule.get("site-url"), Util.dateUtils().now(), remainSeatList));
                } else {
                    log.info("0");
                }
            }
        } catch (Exception e) {
            System.err.println(e.toString());
        }
    }
    
    public static Map<String, Object> getMapFromJSONObject(JSONObject obj) {
        if (ObjectUtils.isEmpty(obj)) {
            log.error("BAD REQUEST obj : {}", obj);
            throw new IllegalArgumentException(String.format("BAD REQUEST obj %s", obj));
        }
        
        try {
            return new ObjectMapper().readValue(obj.toString(), Map.class);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }
}

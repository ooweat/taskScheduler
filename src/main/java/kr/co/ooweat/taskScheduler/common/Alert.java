package kr.co.ooweat.taskScheduler.common;

import com.google.gson.Gson;
import kr.co.ooweat.taskScheduler.model.AlertModel;
import lombok.extern.slf4j.Slf4j;

import javax.activation.CommandMap;
import javax.activation.MailcapCommandMap;
import javax.mail.*;
import javax.mail.internet.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Properties;
/*
        PURPOSE: 알림발송 - (알림톡: 카카오 / SMTP: Google)
        MEMO:
         1. API 가 운영중에 있다고 가정한다.
         2. API 의 도메인은 api.ooweat.co.kr 이다.
         3. Post 방식으로 통신하며, Content-Type 은 application/json;utf-8 이다.
         4. AP I는 token 과 level 이 있어야만 사용할 수 있다. ('보안' 및 '결제 구독형' 의 비즈니스 모델을 가짐.)
         5. API 의 응답 값에 의존한다. (응답코드/응답메시지에 의존)
     */
@Slf4j
public class Alert {
    //TODO: 파라미터를 던지면 API 가 알아서 처리하도록 가정
    public static void kakaoAlert(AlertModel alertModel) {
        final String apiUrl = "http://api.ooweat.co.kr/common/alert/kakao";
        final String token = "tokenParam";      //TODO: 실제 구문은 암호화 구문으로
        final String level = "1";

        HttpURLConnection conn;

        try {

            conn = (HttpURLConnection) new URL(apiUrl).openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json;utf-8");
            conn.setRequestProperty("token", token);
            conn.setRequestProperty("level", level);
            conn.setRequestProperty("Accept-Encoding", "html/text");
            conn.setDoOutput(true);

            //스트림을 통한 GSON(Json) 전송
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream(), "utf-8"));
            Gson gson = new Gson();
            bw.write((String) gson.toJson(alertModel));
            bw.flush();
            bw.close();

            //서버에서 보낸 응답 데이터 수신 받기
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"));
            String returnMsg = br.readLine();
            System.out.println("응답메시지 : " + returnMsg);

            //HTTP 응답 코드 수신
            int responseCode = conn.getResponseCode();
            if (responseCode == 400) {
                System.out.println("400 : 명령을 실행 오류");
            } else if (responseCode == 500) {
                System.out.println("500 : 서버 에러.");
            } else { //정상 . 200 응답코드 . 기타 응답코드
                System.out.println("알림톡 전송완료");
            }
        } catch (IOException ie) {
            System.out.println("IOException " + ie.getCause());
            ie.printStackTrace();
        } catch (Exception e) {
            System.out.println("Exception " + e.getCause());
            e.printStackTrace();
        }

    }
}

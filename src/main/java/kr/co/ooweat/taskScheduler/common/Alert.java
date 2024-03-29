package kr.co.ooweat.taskScheduler.common;

import com.google.gson.Gson;
import javax.annotation.Resource;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Component;

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
            BufferedWriter bw = new BufferedWriter(
                new OutputStreamWriter(conn.getOutputStream(), "utf-8"));
            Gson gson = new Gson();
            bw.write((String) gson.toJson(alertModel));
            bw.flush();
            bw.close();
            
            //서버에서 보낸 응답 데이터 수신 받기
            BufferedReader br = new BufferedReader(
                new InputStreamReader(conn.getInputStream(), "utf-8"));
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
    
    public static void smtp(AlertModel alertModel)
        throws MessagingException, UnsupportedEncodingException {
        OOWEAT ooweat = new OOWEAT();
        String user = ooweat.getMailId();   //발신메일 (이 사람 계정으로 보내겠다.)
        final String name = "[ooweat]";
        String password = ooweat.getMailPass();     //암호화된 키
        final String port = "587";
        final String bodyEncoding = "UTF-8"; //콘텐츠 인코딩
        
        // SMTP 서버 정보를 설정한다.
        Properties prop = new Properties();
        prop.put("mail.smtp.host", "smtp.gmail.com");
        prop.put("mail.smtp.port", port);
        prop.put("mail.smtp.auth", "true");
        prop.put("mail.smtp.ssl.trust", "smtp.gmail.com");
        prop.put("mail.smtp.starttls.enable", "true");
        prop.put("mail.smtp.ssl.protocols", "TLSv1.2");
        
        Session session = Session.getDefaultInstance(prop, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(user, password);
            }
        });
        
        //메시지 포맷을 담기위해 생성
        MimeMessage message = new MimeMessage(session);
        // 메일 콘텐츠 설정
        Multipart mParts = new MimeMultipart();
        MimeBodyPart mTextPart = new MimeBodyPart();
        //이메일 헤더 설정
        message.setHeader("content-Type", "text/html");
        //발신자 설정
        message.setFrom(new InternetAddress(user, name, bodyEncoding));
        // MIME 타입 설정
        MailcapCommandMap MailcapCmdMap = (MailcapCommandMap) CommandMap.getDefaultCommandMap();
        MailcapCmdMap.addMailcap(
            "text/html;; x-java-content-handler=com.sun.mail.handlers.text_html");
        MailcapCmdMap.addMailcap(
            "text/xml;; x-java-content-handler=com.sun.mail.handlers.text_xml");
        MailcapCmdMap.addMailcap(
            "text/plain;; x-java-content-handler=com.sun.mail.handlers.text_plain");
        MailcapCmdMap.addMailcap(
            "multipart/*;; x-java-content-handler=com.sun.mail.handlers.multipart_mixed");
        MailcapCmdMap.addMailcap(
            "message/rfc822;; x-java-content-handler=com.sun.mail.handlers.message_rfc822");
        CommandMap.setDefaultCommandMap(MailcapCmdMap);
        
        //NOTE: 수신자메일 //팀별 메일 또는 개인 메일
        // 영업팀 / CS팀 / 개발팀 / 개인 메일
        InternetAddress[] toSales = {
            new InternetAddress("sales@ooweat.co.kr"),
        };
        InternetAddress[] toCs = {
            new InternetAddress("cs@ooweat.co.kr"),
        };
        InternetAddress[] toSystem = {
            new InternetAddress("dev@ooweat.co.kr"),
        };
        
        InternetAddress[] toTarget = {new InternetAddress(alertModel.getReceiver())};
        StringBuffer sb = new StringBuffer();
        
        //메시징 예외처리
        try {
            //NOTE: 메일제목
            message.setSubject(
                "※스케줄러 발송알림[" + alertModel.getService() + "] " + alertModel.getTitle());
            
            //HINT: Header
            sb.append(
                "<div style=\"padding:40px; margin:0 auto; width:456px;\">\n" +
                    "    <table align=\"center\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\" style=\"width:456px; padding:0; margin:0 auto; font-size:14px; font-family:'Malgun Gothic', '맑은고딕', dotum,'돋움',sans-serif; color:#666; background:#fff; border:1px solid #6777ef;\">\n"
                    +
                    "        <tbody>" +
                    "            <tr>" +
                    "               <td>\n" +
                    "                <table>\n" +
                    "                    <tbody>" +
                    "                       <tr>" +
                    "                           <td colspan=\"2\" style=\"margin:0; width: 456px; height: 40px; padding:0; background-color: white;text-align: center; \"></td>"
                    +
                    "                       </tr>" +
                    "                       <tr style=\"height:100px;\">" +
                    "                           <td align=\"center;\">\n" +
                    "                               <a href=\"#\" title=\"새창열림\" target=\"_blank\" rel=\"noreferrer noopener\">\n"
                    +
                    "                               \t<img src=\"https://lh3.googleusercontent.com/wNuZO8qGHWZUuDepTo2N7tRHFlj2z7jmJ_2b7Z6EswVgBY16ao_bcVdE5VrLpTMRQ1jxfMjaQHaHpg4TKgOP87GedEop9FsU48Ukh6s=s660\" "
                    +
                    "                               alt=\"Logo\" style=\"border:0; vertical-align:middle\" loading=\"lazy\">\n"
                    +
                    "                               </a>\n" +
                    "                           </td>" +
                    "                       </tr>" +
                    "                       <tr>" +
                    "                            <td>\n" +
                    "                            <table>\n" +
                    "                                <colgroup><col width=\"35\"><col width=\"386\"><col width=\"35\"></colgroup>\n"
                    +
                    "                                <tbody><tr><td style=\"margin:0; padding:0; background-color: white;\">&nbsp;</td><td style=\"margin:0; padding:0; background-color: white;\">\n"
                    +
                    "                                        <table>\n" +
                    "                                            <colgroup><col width=\"386\"></colgroup>\n"
                    +
                    "                                            <tbody><tr><td style=\"font-size:14px; letter-spacing:-1px; line-height:24px;\">\n"
                    +
                    "                                                    안녕하세요. [ooweat] 자동발송 메일입니다.<br><br>\n"
            );
            
            //HINT: Body
            if (alertModel.getService().equalsIgnoreCase("check")) {
                alertModel.setTitle("[Task 알림]");
                sb.append(
                    "Task 프로세스에서 " + alertModel.getSendDate() + " 시경에 <br> \n"
                        + "알림을 발송하였습니다.\n " +
                        "</td>" +
                        "</tr>" +
                        "<br>"
                        + "<tr>"
                        + "<td style=\"margin:0; padding:0; height:24px; background-color: white;\"></td>"
                        +
                        "</tr>" +
                        "<tr>" +
                        "<td style=\"margin:0; background-color: white; padding:0 0 0; font-size:14px; letter-spacing:-1px; color:#000000; font-family:'Malgun Gothic', '맑은고딕', dotum,'돋움',sans-serif; line-height:24px;\">\n"
                        +
                        "													   "
                        + "감사합니다." +
                        "<br>\n");
            } else if (alertModel.getService().equalsIgnoreCase("remain")) {
                sb.append(
                    "\n알림일시: " + alertModel.getSendDate() + "\n\n" +
                        "</td>" +
                        "</tr>" +
                        "<br><br>");
                for (int i = 0; i < alertModel.getParamListMap().size(); i++) {
                    String seatType = (String) alertModel.getParamListMap().get(i).get("seatGradeName");
                    String remainCount = String.valueOf(alertModel.getParamListMap().get(i).get("remainCnt"));
                    sb.append("<tr>"
                        + "<td style=\"margin:0; padding:0; height:24px; background-color: white;\">"+
                        seatType+": "+ remainCount + "석 남음</td>" +
                        "</tr>");
                }
                sb.append("<br><tr><td><a href='"+alertModel.getContent()+"' target='_blank'><h1>예매 링크</h1></a></td></tr>");
                sb.append("<br><tr>" +
                    "<td style=\"margin:0; background-color: white; padding:0 0 0; font-size:14px; letter-spacing:-1px; color:#000000; font-family:'Malgun Gothic', '맑은고딕', dotum,'돋움',sans-serif; line-height:24px;\">\n"
                    + "감사합니다." +
                    "<br>\n");
            } else if (alertModel.getService().equalsIgnoreCase("vmDiskUsageCheck")) {
                //NOTE: Content subTitle
                sb.append(
                    Util.StringUtil.contentSubTitle(alertModel.getTitle(),
                        "리포트",
                        "발송되었습니다."
                    )
                );
                //NOTE: Content Table Header
                sb.append(Util.StringUtil.contentTableHeader());
                sb.append("" +
                    "<table cellspacing=\"0\" width=\"100%\" style=\"table-layout: fixed; margin-top: 16px; border: 1px solid rgb(219, 219, 219); border-collapse: collapse; color: rgb(85, 85, 85); font-family: 나눔고딕, NanumGothic, sans-serif; font-size: 12px; line-height: 18px;\"> \n"
                    +
                    "         <thead style=\"box-sizing: border-box;\">" +
                    "               <tr style=\"box-sizing: border-box;\">" +
                    "                       <th colspan=\"1\" rowspan=\"1\" style=\"box-sizing: border-box; padding: 5px 10px; border: 1px solid rgb(238, 238, 240); text-align: center; vertical-align: middle; background: rgb(245, 245, 247);\"> Host IP </th>"
                    +
                    "                       <th colspan=\"1\" rowspan=\"1\" style=\"box-sizing: border-box; padding: 5px 10px; border: 1px solid rgb(238, 238, 240); text-align: center; vertical-align: middle; background: rgb(245, 245, 247);\"> 서비스 </th>"
                    +
                    "                       <th colspan=\"1\" rowspan=\"1\" style=\"box-sizing: border-box; padding: 5px 10px; border: 1px solid rgb(238, 238, 240); text-align: center; vertical-align: middle; background: rgb(245, 245, 247);\"> 파티션 </th>"
                    +
                    "                       <th colspan=\"1\" rowspan=\"1\" style=\"box-sizing: border-box; padding: 5px 10px; border: 1px solid rgb(238, 238, 240); text-align: center; vertical-align: middle; background: rgb(245, 245, 247);\"> 할당경로 </th>"
                    +
                    "                       <th colspan=\"1\" rowspan=\"1\" style=\"box-sizing: border-box; padding: 5px 10px; border: 1px solid rgb(238, 238, 240); text-align: center; vertical-align: middle; background: rgb(245, 245, 247);\"> 할당용량 </th>"
                    +
                    "                       <th colspan=\"1\" rowspan=\"1\" style=\"box-sizing: border-box; padding: 5px 10px; border: 1px solid rgb(238, 238, 240); text-align: center; vertical-align: middle; background: rgb(245, 245, 247);\"> 사용중 </th>"
                    +
                    "                       <th colspan=\"1\" rowspan=\"1\" style=\"box-sizing: border-box; padding: 5px 10px; border: 1px solid rgb(238, 238, 240); text-align: center; vertical-align: middle; background: yellow;\"> 사용량(%) </th>"
                    +
                    "                       <th colspan=\"1\" rowspan=\"1\" style=\"box-sizing: border-box; padding: 5px 10px; border: 1px solid rgb(238, 238, 240); text-align: center; vertical-align: middle; background: rgb(245, 245, 247);\"> 총 메모리 </th>"
                    +
/*                    "                       <th colspan=\"1\" rowspan=\"1\" style=\"box-sizing: border-box; padding: 5px 10px; border: 1px solid rgb(238, 238, 240); text-align: center; vertical-align: middle; background: rgb(245, 245, 247);\"> free 메모리 </th>"
                    +*/
                    "                       <th colspan=\"1\" rowspan=\"1\" style=\"box-sizing: border-box; pading: 5px 10px; border: 1px solid rgb(238, 238, 240); text-align: center; vertical-align: middle; background: rgb(245, 245, 247);\"> 남은 메모리 </th>"
                    +
                    "                       <th colspan=\"1\" rowspan=\"1\" style=\"box-sizing: border-box; padding: 5px 10px; border: 1px solid rgb(238, 238, 240); text-align: center; vertical-align: middle; background: yellow;\"> 사용량(%) </th>"
                    +
                    
                    "               </tr>" +
                    "         </thead> \n" +
                    "         <tbody style=\"box-sizing: border-box;\">");
                //NOTE: Content Table
                for (int i = 0; i < alertModel.getParamListMap().size(); i++) {
                    float usedMem = 0;
                    float totalMem = 0;
                    
                    String memA = alertModel.getParamListMap().get(i).get("MemAvailable")
                        .toString();
                    String memT = alertModel.getParamListMap().get(i).get("MemTotal").toString();
                    usedMem =
                        memA.contains("MB") ?
                            Float.parseFloat(
                                memA.replaceAll("MB", "")) / 1000
                            :
                                Float.parseFloat(
                                    memA.replaceAll("GB", ""));
                    
                    totalMem =
                        memT.contains("MB") ?
                            Float.parseFloat(
                                memT.replaceAll("MB", "")) / 1000
                            :
                                Float.parseFloat(
                                    memT.replaceAll("GB", ""));
                    //MEMO: twkim 2023.02.14 가독성을 위해 아래 시안으로 변경
                    sb.append("         <tr style=\"box-sizing: border-box;\">" +
                        "               <td style=\"box-sizing: border-box; padding: 5px 10px; border: 1px solid rgb(238, 238, 240); vertical-align: middle;\">"
                        + alertModel.getParamListMap().get(i).get("host") + " </td>" +
                        "               <td style=\"box-sizing: border-box; padding: 5px 10px; border: 1px solid rgb(238, 238, 240); vertical-align: middle;\">"
                        + alertModel.getParamListMap().get(i).get("service") + " </td>" +
                        "               <td style=\"box-sizing: border-box; padding: 5px 10px; border: 1px solid rgb(238, 238, 240); vertical-align: middle;\">"
                        + alertModel.getParamListMap().get(i).get("partition") + " </td>" +
                        "               <td style=\"box-sizing: border-box; padding: 5px 10px; border: 1px solid rgb(238, 238, 240); vertical-align: middle;\">"
                        + alertModel.getParamListMap().get(i).get("path") + " </td>" +
                        "               <td style=\"box-sizing: border-box; padding: 5px 10px; border: 1px solid rgb(238, 238, 240); text-align: center; vertical-align: middle;\">"
                        + alertModel.getParamListMap().get(i).get("size") + " </td>" +
                        "               <td style=\"box-sizing: border-box; padding: 5px 10px; border: 1px solid rgb(238, 238, 240); text-align: center; vertical-align: middle;\">"
                        + alertModel.getParamListMap().get(i).get("used") + " </td>" +
                        "               <td style=\"box-sizing: border-box; padding: 5px 10px; border: 1px solid rgb(238, 238, 240); text-align: center; vertical-align: middle;\">"
                        + alertModel.getParamListMap().get(i).get("percent") + " </td>" +
                        "               <td style=\"box-sizing: border-box; padding: 5px 10px; border: 1px solid rgb(238, 238, 240); text-align: center; vertical-align: middle;\">"
                        + alertModel.getParamListMap().get(i).get("MemTotal") + " </td>" +
/*                        "               <td style=\"box-sizing: border-box; padding: 5px 10px; border: 1px solid rgb(238, 238, 240); text-align: center; vertical-align: middle;\">"
                        + alertModel.getParamListMap().get(i).get("MemFree") + " </td>" +*/
                        "               <td style=\"box-sizing: border-box; padding: 5px 10px; border: 1px solid rgb(238, 238, 240); text-align: center; vertical-align: middle;\">"
                        + alertModel.getParamListMap().get(i).get("MemAvailable") + " </td>" +
                        "               <td style=\"box-sizing: border-box; padding: 5px 10px; border: 1px solid rgb(238, 238, 240); text-align: center; vertical-align: middle;\">"
                        + String.valueOf(
                            (100 - Math.round(usedMem / (totalMem / 100) * 1000) / 1000.0))
                        .substring(0, 5)
                        + "%" + " </td>" +
                        "           </tr>");
                }
                sb.append("         </tbody>" +
                    "</table>");
                
                //NOTE: Content Table Footer
                sb.append(Util.StringUtil.contentTableFooter());
            }
            //HINT: Footer
            sb.append(
                "                                                </td></tr><tr><td style=\"margin:0; padding:0; height:34px; background-color: white;\"></td></tr><tr><td style=\"margin:0; background-color: white; padding:0 0 0; font-size:14px; letter-spacing:-1px; color:#000000; font-family:'Malgun Gothic', '맑은고딕', dotum,'돋움',sans-serif; line-height:24px;\">\n"
                    +
                    "                                                <div style=\"background-color: #fb9306;padding: 5% 0%;\" align=\"center\">\n"
                    +
                    "                                                \t<span style=\"text-align: center;font-weight: bold;color: white;font-size: 2em;\">"
                    +
                    "SYSTEM : " + alertModel.getService() + "</span>\n" +
                    "                                                </div>\n" +
                    "                                                </td></tr><tr><td style=\"margin:0; padding:0; height:53px; background-color: white;\"></td></tr><tr><td style=\"margin:0; text-align: center; background-color: white; padding:0 0 0; font-size:14px; letter-spacing:-1px; font-weight:bold; color:#005aab; line-height:24px;\">\n"
                    +
                    "                                                    ooweat ⓒ All Rights Reserved.\n"
                    +
                    "                                                </td></tr><tr><td style=\"margin:0; padding:0; height:32px; background-color: white;\"></td></tr></tbody>\n"
                    +
                    "                                        </table>\n" +
                    "                                    </td><td style=\"margin:0; padding:0; background-color: white; \">&nbsp;</td></tr></tbody><tbody></tbody></table>\n"
                    +
                    "                        </td></tr></tbody>\n" +
                    "                </table>\n" +
                    "            </td></tr></tbody>\n" +
                    "    </table>\n" +
                    "</div>");
            
            if (Util.stringUtil(alertModel.getReceiver()).in("sales", "cs", "dev")) {
                switch (alertModel.getReceiver()) {
                    case "sales":
                        message.addRecipients(Message.RecipientType.TO, toSales); //받는 이
                        message.addRecipients(Message.RecipientType.CC, toSystem); //참조
                        break;
                    case "cs":
                        message.addRecipients(Message.RecipientType.TO, toCs); //받는 이
                        message.addRecipients(Message.RecipientType.CC, toSystem); //참조
                        break;
                    case "dev":
                        message.addRecipients(Message.RecipientType.TO, toSystem); //받는 이
                        break;
                }
            } else {
                message.addRecipients(Message.RecipientType.TO, toTarget);
            }
            
            mTextPart.setText(sb.toString(), bodyEncoding, "html");
            mParts.addBodyPart(mTextPart);
            message.setContent(mParts);
            Transport.send(message);
            log.info("SEND: {}", "OK");
            
        } catch (SendFailedException e) {
            message.addRecipients(Message.RecipientType.TO, toSystem); //받는 이
            message.setSubject("※메일발송실패[" + alertModel.getService() + "] " + alertModel.getTitle());
            sb.append(
                alertModel.toString() + " <br> \n"
                    + "수신자의 사유로 메일 발송을 실패하였습니다. <br> \n"
                    + "</td></tr>"
                    + "<tr>"
                    + "<td style=\"margin:0; padding:0; height:24px; background-color: white;\"></td></tr><tr><td style=\"margin:0; background-color: white; padding:0 0 0; font-size:14px; letter-spacing:-1px; color:#000000; font-family:'Malgun Gothic', '맑은고딕', dotum,'돋움',sans-serif; line-height:24px;\">\n<br>\n"
            );
            mTextPart.setText(sb.toString(), bodyEncoding, "html");
            mParts.addBodyPart(mTextPart);
            message.setContent(mParts);
            Transport.send(message);
            log.error("SEND: {}", "FAIL // 수신자의 사유로 발송되지 않습니다.");
        } catch (AddressException e) {
            e.printStackTrace();
            log.error("SEND: {}", "FAIL // 주소가 올바르지 않습니다.");
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}

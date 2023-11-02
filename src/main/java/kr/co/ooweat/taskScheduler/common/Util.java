package kr.co.ooweat.taskScheduler.common;

import com.jcraft.jsch.*;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import kr.co.ooweat.taskScheduler.model.AlertModel;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Properties;


//TODO: 2023.03.13 twkim Java.util.Date -> Java.time.LocalDateTime 으로 변경
@Slf4j
public class Util {

    //날짜 & 시간 관련 Utils
    public static DateUtils dateUtils() {
        return new DateUtils();
    }

    public static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyyMM");

    public static class DateUtils {

        public String now() {
            return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        }

        public String yyyyMM() {
            return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMM"));
        }

        public String yyyyMM(int option) {
            String result = null;
            if (option == 0) {
                result = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMM"));
            } else if (option > 0) {    //NOTE: 미래
                result = LocalDateTime.now().plusMonths(option)
                        .format(DateTimeFormatter.ofPattern("yyyyMM"));
            } else if (option < 0) {    //NOTE: 과거
                option = option * -1;
                result = LocalDateTime.now().minusMonths(option)
                        .format(DateTimeFormatter.ofPattern("yyyyMM"));
            }
            return result;
        }

        //NOTE: 기존 Service 대응하기 위함.
        public String yyyyMMdd() {
            return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        }

        //NOTE: 범위 지정 시 Option 을 넣어 사용.
        public String yyyyMMdd(int dayOption) {
            return calcYYYYMMDD(dayOption);
        }

        public String yyyyMMddHH(int dayOption, int hourOption) {
            String day = null, hour = null;
            day = calcYYYYMMDD(dayOption);

            if (hourOption == 0) {
                hour = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH"));
            } else if (hourOption > 0) {    //NOTE: 미래
                hour = LocalDateTime.now().plusHours(hourOption)
                        .format(DateTimeFormatter.ofPattern("HH"));
            } else if (hourOption < 0) {    //NOTE: 과거
                hourOption = hourOption * -1;
                hour = LocalDateTime.now().minusHours(hourOption)
                        .format(DateTimeFormatter.ofPattern("HH"));
            }
            return day+hour;
        }

        public String calcYYYYMMDD(int option){
            String day = null;
            if (option == 0) {
                day = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
            } else if (option > 0) {    //NOTE: 미래
                day = LocalDateTime.now().plusDays(option)
                        .format(DateTimeFormatter.ofPattern("yyyyMMdd"));
            } else if (option < 0) {    //NOTE: 과거
                option = option * -1;
                day = LocalDateTime.now().minusDays(option)
                        .format(DateTimeFormatter.ofPattern("yyyyMMdd"));
            }
            return day;
        }
        public String hhmmss() {
            return LocalDateTime.now().format(DateTimeFormatter.ofPattern("HHmmss"));
        }

        public String hh() {
            //(('0' + (date.getMonth() + 1)).slice(-2))
            String hh = "0"+LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH"));
            return hh.substring(hh.length() - 2);
        }

        public int dayOfWeek() {
            return LocalDateTime.now().getDayOfWeek().getValue();
        }

    }

    public static String getMethodName() {
        return Thread.currentThread().getStackTrace()[2].getMethodName();
    }

    public static String getCurrentDD() {
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("dd");
        return sdf.format(date);
    }

    //문자열 Util
    public static StringUtil stringUtil(String param) {
        return new StringUtil(param);
    }

    public static class StringUtil {

        private String value;

        public StringUtil(String value) {
            this.value = value;
        }

        //SQL 의 그것과 같음 in
        public boolean in(String... values) {
            for (String v : values) {
                if (v.equals(value)) {
                    return true;
                }
            }
            return false;
        }

        //SQL 의 그것과 같음 notIn
        public boolean notIn(String... values) {
            for (String v : values) {
                if (v.equals(value)) {
                    return false;
                }
            }
            return true;
        }

        public static String contentEnterRow() {
            return "                    <tr>" +
                    "                        <td height=\"6\"></td>" +
                    "                    </tr>" +
                    "                    <tr>" +
                    "                        <td height=\"1\" style=\"background:#d5d5d5\"></td>" +
                    "                        <td height=\"1\" style=\"background:#d5d5d5\"></td>" +
                    "                    </tr>" +
                    "                    <tr>" +
                    "                        <td height=\"6\"></td>" +
                    "                    </tr>"
                    ;
        }

        public static String contentSubTitle(String v1, String v2, String v3) {
            return "<tr>" +
                    "            <td style=\"padding-top:24px; padding-right:27px; padding-bottom:32px; padding-left:20px\">"
                    +
                    "                <table align=\"left\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\""
                    +
                    "                       style=\"font-family:'나눔고딕',NanumGothic,'맑은고딕',Malgun Gothic,'돋움',Dotum,Helvetica,'Apple SD Gothic Neo',Sans-serif;\">"
                    +
                    "                    <tbody>" +
                    "                    <tr>" +
                    "                        <td height=\"6\"></td>" +
                    "                    </tr>" +
                    "                    <tr>" +
                    "                        <td style=\"font-size:14px;color:#696969;line-height:24px;font-family:'나눔고딕',NanumGothic,'맑은고딕',Malgun Gothic,'돋움',Dotum,Helvetica,'Apple SD Gothic Neo',Sans-serif;\">"
                    +
                    "                            " + v1 + " <span style=\"color:#009E25\">" + v2
                    + " </span><strong>" + v3 + "</strong><br>" +
                    "                        </td>" +
                    "                    </tr>";
        }

        public static String contentTableHeader() {
            return "                    <tr>" +
                    "                        <td height=\"24\"></td>" +
                    "                    </tr>" +
                    "                    <tr>" +
                    "                        <td style=\"font-size:14px;color:#696969;line-height:24px;font-family:'나눔고딕',NanumGothic,'맑은고딕',Malgun Gothic,'돋움',Dotum,Helvetica,'Apple SD Gothic Neo',Sans-serif;\">"
                    +
                    "                            <table cellpadding=\"0\" cellspacing=\"0\" style=\"width:100%;margin:0;padding:0\">"
                    +
                    "                                <tbody>" +
                    "                                <tr>" +
                    "                                    <td height=\"23\"" +
                    "                                        style=\"font-weight:bold;color:#000;vertical-align:top;font-family:'나눔고딕',NanumGothic,'맑은고딕',Malgun Gothic,'돋움',Dotum,Helvetica,'Apple SD Gothic Neo',Sans-serif;\">"
                    +
                    "                                        정보" +
                    "                                    </td>" +
                    "                                </tr>" +
                    "                                <tr>" +
                    "                                    <td height=\"2\" style=\"background:#424240\"></td>"
                    +
                    "                                </tr>" +
                    "                                <tr>" +
                    "                                    <td height=\"20\"></td>" +
                    "                                </tr>" +
                    "                                <tr>" +
                    "                                    <td>" +
                    "                                        <table cellpadding=\"0\" cellspacing=\"0\" style=\"width:100%;margin:0;padding:0\">"
                    +
                    "                                            <tbody>\n";
        }

        public static String contentTableRow(String tit, String cont) {
            return "<tr>" +
                    "<td width='110'" +
                    "    style='padding-bottom:5px;color:#696969;line-height:23px;vertical-align:top;font-family:'나눔고딕',NanumGothic,'맑은고딕',Malgun Gothic,'돋움',Dotum,Helvetica,'Apple SD Gothic Neo',Sans-serif;'>"
                    +
                    tit +
                    "</td>" +
                    "<td style='padding-bottom:5px;color:#000;line-height:23px;vertical-align:top;font-family:'나눔고딕',NanumGothic,'맑은고딕',Malgun Gothic,'돋움',Dotum,Helvetica,'Apple SD Gothic Neo',Sans-serif;'>"
                    +
                    cont +
                    "</td>" +
                    "</tr>";
        }

        public static String contentTableFooter() {
            return
                    "</tbody>" +
                            "</table>" +
                            "</td>" +
                            "</tr>" +
                            "<tr>" +
                            "<td height='20'></td>" +
                            "</tr>" +
                            "<tr>" +
                            "<td height='1' style='background:#d5d5d5'></td>" +
                            "</tr>" +
                            "</tbody>" +
                            "</table>" +
                            "</td>" +
                            "</tr>" +
                            "</tbody>" +
                            "</table>" +
                            "</td>" +
                            "</tr>";
        }
    }

    public static Map<String, Object> connectVM(ServerDescEnum serverDescEnum, int option) {
        int port = 22;
        String user = "ooweat";
        String password = "";
        switch (serverDescEnum.getSignType()) {
            case 1: //NOTE: 기존에 사용중이던 Server
                password = "oldPw";
                break;
            case 2: //NOTE: 뉴타닉스에 설치된 차세대 Server(VM)
                password = "newPw";
                break;
        }

        Session session = null;
        Channel channel = null;
        String[] commandCustom = null;
        BufferedReader commandReader = null;
        Map<String, Object> hmap = new HashMap<>();

        // 2. 세션 객체를 생성한다 (사용자 이름, 접속할 호스트, 포트를 인자로 준다.)
        try {
            JSch jsch = new JSch();
            session = jsch.getSession(user, serverDescEnum.getHost(), port);
            session.setPassword(password);
            Properties config = new Properties();
            config.put("StrictHostKeyChecking", "no");
            session.setConfig(config);
            session.connect();
            channel = session.openChannel("exec");

            //NOTE: ssh 채널 객체로 변환
            ChannelExec channelExec = (ChannelExec) channel;
            //NOTE: 결과 Message 수신
            commandReader = new BufferedReader(new InputStreamReader(channelExec.getInputStream()));

            switch (option) {
                case 1: //NOTE: 재시작 옵션
                    channelExec.setCommand("./restart_kakao_service.sh");
                    channelExec.connect();
                    hmap.put("result", "0000");
                    break;
                case 2: //NOTE: 사용량 확인 옵션
                    channelExec.setCommand("echo $(df -kh " + serverDescEnum.getPartition() + ") " +
                            "$(awk '$3==\"kB\"{" +
                            "if ($2>1024^2){$2=$2/1024^2;$3=\"GB\";" +
                            "} else if ($2>1024){$2=$2/1024;$3=\"MB\";}} 1' /proc/meminfo | column -t | grep Mem)");
                    channelExec.connect();
                    commandCustom = commandReader.readLine().split(" ");
                    hmap.put("host", serverDescEnum.getHost());
                    hmap.put("partition", commandCustom[7]);
                    hmap.put("service", serverDescEnum.getDescription());
                    hmap.put("path", commandCustom[12]);
                    hmap.put("size", commandCustom[8]);
                    hmap.put("used", commandCustom[9]);
                    hmap.put("percent", commandCustom[11]);
                    hmap.put("MemTotal", commandCustom[14] + " " + commandCustom[15]);
                    hmap.put("MemFree", commandCustom[17] + " " + commandCustom[18]);
                    hmap.put("MemAvailable", commandCustom[20] + " " + commandCustom[21]);
                    break;
            }
        } catch (JSchException | IOException e) {
            log.error("접속실패", hmap.get("host"));
            e.printStackTrace();
        } finally {
            if (channel != null) {
                channel.disconnect();
            }
            if (session != null) {
                session.disconnect();
            }
            return hmap;
        }
    }

    //DESC: alter 모델에 맞게 설정해주도록 설정
    public static AlertModel alertModelMaking(String receiver, String templateType, String service,
                                              String title, String content, String sendDate) {
        AlertModel alertModel = new AlertModel();
        alertModel.setReceiver(receiver);
        alertModel.setTemplateType(templateType);
        alertModel.setService(service);
        alertModel.setTitle(title);
        alertModel.setContent(content);
        alertModel.setSendDate(sendDate);
        return alertModel;
    }

    //DESC: alter 모델에 맞게 설정해주도록 설정 2
    public static AlertModel alertModelMaking(String receiver, String templateType, String service,
                                              String title,
                                              String content, String sendDate, List<Map<String, Object>> paramListMap) {
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

    public static Map<String, String> dateRangeMaking() {
        Map<String, String> dateRange = new HashMap<>();
        log.info(String.valueOf(Util.dateUtils().dayOfWeek()));
        //NOTE: 무조건 -31 일부터 조회하도록 수정

        dateRange.put("sDate", Util.dateUtils().yyyyMMdd(-31));
        /*if (Util.dateUtils().dayOfWeek() == 1) {
            dateRange.put("sDate", Util.dateUtils().yyyyMMdd(-2));
        } else {
            dateRange.put("sDate", Util.dateUtils().yyyyMMdd(0));
        }*/

        dateRange.put("eDate", Util.dateUtils().yyyyMMdd(0));

        return dateRange;
    }

    //NOTE: 추후 사용을 위해 구현 테스트만..
    /*public void existSendData() throws MessagingException, UnsupportedEncodingException {
        int sendCount = 0;
        String num = "";
        log.info("시작: {}",String.valueOf(sendCount));

        if(sendCount > 0) {
            log.info("이미 발송 보냈으나 수정 되었는지 재확인");
            if(!num.equals("0220001234")){
                log.info("오류 그대로네: {}",String.valueOf(sendCount));
                //TODO: 재발송?
            } else{
                sendCount = 0;
                log.info("오류가 개선됨: {}",String.valueOf(sendCount));
                log.info("개선되었습니다. 메시지 발송");
                Alert.smtp(Util.alertModelMaking("test", "ALERT", "KakaoAlertProcess",
                    "2", SERVICE_KAKAO_ALERT.getDescription(), Util.dateUtils().now()));
            }
        } else {
            if(!num.equals("0264582000")){
                sendCount = 1;
                log.info("오류 메시지 최초 발송: {}",String.valueOf(sendCount));
            } else{
                sendCount = 0;
                log.info("이상무(초기화): {}",String.valueOf(sendCount));
            }
        }
    }*/

}

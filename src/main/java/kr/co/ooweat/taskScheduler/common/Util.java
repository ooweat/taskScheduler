package kr.co.ooweat.taskScheduler.common;

import com.jcraft.jsch.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Properties;
import kr.co.ooweat.taskScheduler.model.ServiceServer;
import lombok.extern.slf4j.Slf4j;
import java.time.format.DateTimeFormatter;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Map;

@Slf4j
public class Util {
    //경로 관련 Utils
    public static PathUtils pathUtils() {
        return new PathUtils();
    }
    public static class PathUtils {
        public String className() {
            return Thread.currentThread().getStackTrace()[2].getClassName();
        }

        public String methodName() {
            return Thread.currentThread().getStackTrace()[2].getMethodName();
        }

        public String classWithMethodName() {
            return
                Thread.currentThread().getStackTrace()[2].getClassName() +"."+
                    Thread.currentThread().getStackTrace()[2].getMethodName();
        }
    }

    //날짜 & 시간 관련 Utils
    public static DateUtils dateUtils() {
        return new DateUtils();
    }

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
                result = LocalDateTime.now().plusMonths(option).format(DateTimeFormatter.ofPattern("yyyyMM"));
            } else if (option < 0) {    //NOTE: 과거
                option = option * -1;
                result = LocalDateTime.now().minusMonths(option).format(DateTimeFormatter.ofPattern("yyyyMM"));
            }
            return result;
        }

        //NOTE: 기존 Service 대응하기 위함.
        public String yyyyMMdd() {
            return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        }
        //NOTE: 범위 지정 시 Option 을 넣어 사용.
        public String yyyyMMdd(int option) {
            String result = null;
            if (option == 0) {
                result = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
            } else if (option > 0) {    //NOTE: 미래
                result = LocalDateTime.now().plusDays(option).format(DateTimeFormatter.ofPattern("yyyyMMdd"));
            } else if (option < 0) {    //NOTE: 과거
                option = option * -1;
                result = LocalDateTime.now().minusDays(option).format(DateTimeFormatter.ofPattern("yyyyMMdd"));
            }
            return result;
        }

        public String hhmmss() {
            return LocalDateTime.now().format(DateTimeFormatter.ofPattern("HHmmss"));
        }

        public int dayOfWeek() {
            return LocalDateTime.now().getDayOfWeek().getValue();
        }

    }

    //Response Util
    public static ResponseUtils responseUtils() {
        return new ResponseUtils();
    }
    public static class ResponseUtils {
        public static ResponseEntity<JSONObject> simpleReturn(String returnCode) throws JSONException {
            return new ResponseEntity<>(jsonUtils().simpleReturn(returnCode), HttpStatus.OK);
        }

        public static ResponseEntity<JSONObject> responseParsing(String code, String message, String description) throws JSONException {
            return new ResponseEntity<>(jsonUtils().responseParsing(code, message, description), HttpStatus.OK);
        }

        public static ResponseEntity<JSONObject> paramReturn(String returnCode, String param) throws JSONException {
            return new ResponseEntity<>(jsonUtils().paramReturn(returnCode, param), HttpStatus.OK);
        }
    }

    //Json return Util
    public static JsonUtils jsonUtils() {
        return new JsonUtils();
    }
    public static class JsonUtils {
        JSONObject json = new JSONObject();

        public JSONObject responseParsing(String code, String message, String description) throws JSONException {
            JSONObject json = new JSONObject();
            json.put("code", code);
            json.put("message", message);
            json.put("description", description);
            return json;
        }

        public JSONObject simpleReturn(String returnCode) throws JSONException {
            json.put("resultMsg", ResponseCode.SUCCESS_REQUEST.getMessage());
            json.put("code", returnCode);
            return json;
        }

        public JSONObject paramReturn(String returnCode, String param) throws JSONException {
            json.put("resultMsg", ResponseCode.SUCCESS_REQUEST.getMessage());
            json.put("code", returnCode);
            json.put("param", param);
            return json;
        }

        public static JSONObject convertMapToJson(Map<String, String> map) throws JSONException {
            JSONObject json = new JSONObject();
            for (Map.Entry<String, String> entry : map.entrySet()) {
                String key = entry.getKey();
                Object value = entry.getValue();
                json.put(key, value);
            }
            return json;
        }
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

    public static HashMap<String, String> connectVM(ServiceServer serviceServer) {
        int port = 22;
        String user = "";
        String password = "";
        switch (serviceServer.getSignType()) {
            case 1: //NOTE: ROOT User <- Danger
                user = "root";
                password = "password1";
                break;
            case 2: //NOTE: Service User
                user = "ooweat";
                password = "password2";
                break;
        }

        Session session = null;
        Channel channel = null;
        String[] commandCustom = null;
        BufferedReader commandReader = null;
        HashMap<String, String> hmap = new HashMap<>();

        // 2. 세션 객체를 생성한다 (사용자 이름, 접속할 호스트, 포트를 인자로 준다.)
        try {
            JSch jsch = new JSch();
            session = jsch.getSession(user, serviceServer.getHost(), port);
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

            //NOTE: CMD 서버의 용량 / 메모리 사용량을 계산
            channelExec.setCommand("echo $(df -kh " + serviceServer.getPartition() + ") " +
                "$(awk '$3==\"kB\"{" +
                "if ($2>1024^2){$2=$2/1024^2;$3=\"GB\";" +
                "} else if ($2>1024){$2=$2/1024;$3=\"MB\";}} 1' /proc/meminfo | column -t | grep Mem)");
            channelExec.connect();
            commandCustom = commandReader.readLine().split(" ");
            hmap.put("host", serviceServer.getHost());
            hmap.put("partition", commandCustom[7]);
            hmap.put("service", serviceServer.getDescription());
            hmap.put("path", commandCustom[12]);
            hmap.put("size", commandCustom[8]);
            hmap.put("used", commandCustom[9]);
            hmap.put("percent", commandCustom[11]);
            hmap.put("MemTotal", commandCustom[14] + " " + commandCustom[15]);
            hmap.put("MemFree", commandCustom[17] + " " + commandCustom[18]);
            hmap.put("MemAvailable", commandCustom[20] + " " + commandCustom[21]);
        } catch (JSchException | IOException e) {
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

}

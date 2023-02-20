package kr.co.ooweat.taskScheduler.common;

import lombok.extern.slf4j.Slf4j;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.Date;
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
        Date date = new Date();

        public String now() {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            return sdf.format(date);
        }

        public String yyyy() {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
            return sdf.format(date);
        }
        public String MM() {
            SimpleDateFormat sdf = new SimpleDateFormat("MM");
            return sdf.format(date);
        }

        public String yyyyMM() {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");
            return sdf.format(date);
        }

        public String yyyyMMdd() {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
            return sdf.format(date);
        }

        public String hhmmss() {
            SimpleDateFormat sdf = new SimpleDateFormat("HHmmss");
            return sdf.format(date);
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
    public static StringUtils stringUtils(String param) {
        return new StringUtils(param);
    }

    public static class StringUtils {
        private String value;

        public StringUtils(String value) {
            this.value = value;
        }

        //SQL 의 그것과 같음 in
        public boolean in(String... values) {
            for (String v : values) {
                if (v.equals(value)) return true;
            }
            return false;
        }

        //SQL 의 그것과 같음 notIn
        public boolean notIn(String... values) {
            for (String v : values) {
                if (v.equals(value)) return false;
            }
            return true;
        }

        /**
         * num : num 이하의 난수 생성
         * @param num
         * @return
         */
        public static String rtnRnd(int num) {
            String rtnNum="";
            String strNum = Integer.toString(num);
            int rnd = 0;
            try {
                //java.security.SecureRandom 클래스는 예측할 수 없는 seed를 이용하여 강력한 난수를 생성
                SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
                rnd = sr.nextInt(num);
            }catch(Exception ex) {
                ex.printStackTrace();
            }
            rtnNum = rtnAppendNumChar(rnd,"0",strNum.length());

            if( Long.parseLong(rtnNum) == 0) {
                rtnNum = null;
            }//

            return rtnNum;
        }

        /**
         * num 앞에 특정 숫자 붙이기
         * @param iNum
         * @param ch
         * @param lim
         * @return
         */
        public static String rtnAppendNumChar(Integer iNum, String ch, int lim) {
            StringBuffer sb = new StringBuffer();
            String rtnData="";
            int i=0;
            if(iNum!=null) {
                String txt_data = iNum.toString();
                if(txt_data.length() < lim) {
                    for(i=0;i< lim - txt_data.length();i++) {
                        sb.append(ch);
                    }//
                    sb.append(txt_data);
                    rtnData = sb.toString();
                }else {
                    rtnData = txt_data;
                }
            }//

            return rtnData;
        }
    }

}

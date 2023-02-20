package kr.co.ooweat.taskScheduler.common;

import com.fasterxml.jackson.annotation.JsonFormat;
import kr.co.ooweat.taskScheduler.model.EnumModel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

//NOTE: 응답값에 대한 내용을 ENUM CLASS 로 작성하여 공통 규칙을 만든다.
// '성공' OR '실패(오류/에러)' 로 나눌 가능성을 열어두자.
@RequiredArgsConstructor
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum ResponseCode implements EnumModel {
    //TYPE: 성공 - 0000 고정
    SUCCESS_REQUEST("0000", "데이터 처리 성공", "정상처리"),
    SUCCESS_SEARCH("0000", "데이터 조회 성공", "정상처리"),
    SUCCESS_INSERT("0000", "데이터 입력 성공", "정상처리"),
    SUCCESS_COUPON("0000", "쿠폰 발권 완료", "정상처리"),
    //TYPE: 최상단 오류
    ERROR_SERVER("5000", "서버 오류 발생", "시스템 점검 중입니다."),
    //TYPE: CRUD 오류 - 511x
    ERROR_MODIFY("5111", "수정 실패", "원인 파악 후 재시도 요망"),
    ERROR_DELETE("5112", "삭제 실패", "원인 파악 후 재시도 요망"),
    ERROR_REQUEST("5113", "데이터 처리 실패", "재시도 요망"),
    ERROR_SEARCH("5114", "데이터 조회 실패", "재시도 요망"),
    ERROR_INSERT("5115", "데이터 입력 실패", "재시도 요망"),
    //TYPE: 유효성 검사 - 6xxx / 인증: 61xx / 형식: 62xx / 값 : 63xx  
    IS_NOT_SUBSCRIBER("6101", "구독자가 아님", "구독자만 사용가능합니다."),
    EMPTY_TOKEN("6102", "TOKEN 데이터 누락", "필수 데이터가 누락되었습니다."),
    EMPTY_UNIQUE_NO("6103", "고유번호 데이터 누락", "필수 데이터가 누락되었습니다."),
    ILLEGAL_HEADER("6104", "TOKEN 과 고유번호 미일치(인증 실패)", "필수 데이터가 일치하지 않습니다."),
    NOT_EXIST_TOKEN("6105", "유효하지 않은 TOKEN", "시스템에 존재하지 않거나 유효하지 않은 TOKEN 입니다."),
    NOT_EXIST_UNIQUE_NO("6106", "유효하지 않은 고유번호", "시스템에 존재하지 않거나 유효하지 않은 고유번호 입니다."),

    //HINT: 확장성을 고려한 옵션
    NOT_EXIST_GROUP("6111", "유효하지 않은 요청 group", "시스템에 존재하지 않거나 유효하지 않은 GROUP 입니다."),
    NOT_EXIST_ID("6112", "유효하지 않은 요청 id", "시스템에 존재하지 않거나 유효하지 않은 id 입니다."),
    NOT_EXIST_TYPE("6113", "유효하지 않은 요청 type", "시스템에 존재하지 않거나 유효하지 않은 type 입니다."),
    //HINT: 형식 체크
    NOT_LITERAL("6211", "문자열 형식이 아님", "STRING 형식으로 요청 바랍니다."),
    NOT_NUMERIC("6212", " 0 이상의 숫자(양수)형식이 아님", "INTEGER 또는 LONG 형식으로 요청 바랍니다."),
    NOT_SUPPORT_TYPE("6213", "지원하지 않는 형식의 Parameter", "올바른 형식으로 요청 바랍니다."),
    NOT_SUPPORT_DATE_FORMAT("6214", "지원하지 않는 날짜 형식", "날짜 형식을 확인하시기 바랍니다."),
    NOT_SUPPORT_TIME_FORMAT("6215", "지원하지 않는 시간 형식", "시간 형식을 확인하시기 바랍니다."),
    NOT_SUPPORT_PHONE_FORMAT("6216", "지원하지 않는 연락처 형식", "연락처 형식을 확인하시기 바랍니다."),
    MIN_VALUE("6221", "최소 값 기준치 미달", "최소 기준에 미달합니다."),
    MAX_VALUE("6222", "최대 값 기준치 초과", "최대 기준을 초과합니다."),
    //HINT: 값 체크
    EMPTY_PARAM_BLANK_OR_NULL("6300", "Request Parameter 빈 값, NULL 또는 공백", "요청 데이터가 빈 값, NULL 또는 공백 입니다."),
    EMPTY_PARAM_01("6301", "01번 Parameter 데이터 누락", "01번 Parameter 가 누락되었습니다."),
    EMPTY_PARAM_02("6302", "02번 Parameter 데이터 누락", "02번 Parameter 가 누락되었습니다."),
    EMPTY_PARAM_03("6303", "03번 Parameter 데이터 누락", "03번 Parameter 가 누락되었습니다."),
    EMPTY_PARAM_04("6304", "04번 Parameter 데이터 누락", "04번 Parameter 가 누락되었습니다."),
    EMPTY_PARAM_05("6305", "05번 Parameter 데이터 누락", "05번 Parameter 가 누락되었습니다."),
    EMPTY_PARAM_06("6306", "06번 Parameter 데이터 누락", "06번 Parameter 가 누락되었습니다."),
    EMPTY_PARAM_07("6307", "07번 Parameter 데이터 누락", "07번 Parameter 가 누락되었습니다."),
    EMPTY_PARAM_08("6308", "08번 Parameter 데이터 누락", "08번 Parameter 가 누락되었습니다."),
    EMPTY_PARAM_09("6309", "09번 Parameter 데이터 누락", "09번 Parameter 가 누락되었습니다."),
    
    //TYPE: 강제 에러
    TEST_ERROR("9999", "억까 ERROR", "ERROR");

    @Getter
    private String code;
    @Getter
    private String description;

    @Getter
    private String message;

    ResponseCode(String code, String description, String message) {
        this.code = code;
        this.description = description;
        this.message = message;
    }

    @Override
    public String getKey() {
        return this.code;
    }

    @Override
    public String getValue() {
        return this.message;
    }
}

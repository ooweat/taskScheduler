package kr.co.ooweat.taskScheduler.common;

import lombok.Getter;

public enum ServerDescEnum {
    SERVICE_KCS_Online_01("172.29.100.101", 22, "KCS 온라인 01", "/", 2),
    SERVICE_KCS_Online_02("172.29.100.102", 22, "KCS 온라인 02", "/", 2),
    SERVICE_KCS_Online_03("172.29.100.103", 22, "KCS 온라인 03", "/", 2),
    SERVICE_KCS_Online_04("172.29.100.104", 22, "KCS 온라인 04", "/", 2),
    SERVICE_KCS_Online_05("172.29.100.105", 22, "KCS 온라인 05", "/", 2),

    SERVICE_JTN_Online_01("172.29.100.108", 22, "JTN 온라인 01", "/", 2),
    SERVICE_JTN_Online_02("172.29.100.109", 22, "JTN 온라인 02", "/", 2),

    SERVICE_SMARTPAY_01("172.29.100.110", 22, "간편결제 01", "/", 2),
    SERVICE_SMARTPAY_02("172.29.100.111", 22, "간편결제 02", "/", 2),
    SERVICE_BATCH("172.29.100.10", 22, "배치", "/app", 1),
    SERVICE_BATCH_SERVER("172.29.100.20", 22, "배치결과 처리서버", "/app", 1),
    SERVICE_TCP_DEV("172.29.100.29", 22, "TCP 개발", "/app", 1),
    SERVICE_BATCH_NEW("172.29.100.116", 22, "차세대 배치", "/app", 2),
    SERVICE_KAKAO_ALERT("172.29.100.149", 22, "알림톡", "/", 1);

    @Getter
    private String host;
    @Getter
    private int port;
    @Getter
    private String description;
    @Getter
    private String partition;
    @Getter
    private int signType;


    ServerDescEnum(String host, int port, String description, String partition, int signType){
        this.host = host;
        this.port = port;
        this.description = description;
        this.partition = partition;
        this.signType = signType;
    }
}

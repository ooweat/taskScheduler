package kr.co.ooweat.taskScheduler.common;

import lombok.Getter;

public enum ServiceList {
    SERVICE_KAKAO_ALERT("127.0.0.1", 11001, "알림톡 전송");

    @Getter
    private String host;
    @Getter
    private int port;
    @Getter
    private String description;

    ServiceList(String host, int port, String description){
        this.host = host;
        this.port = port;
        this.description = description;
    }
}

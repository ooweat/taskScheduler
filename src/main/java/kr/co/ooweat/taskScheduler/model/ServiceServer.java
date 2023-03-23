package kr.co.ooweat.taskScheduler.model;

import lombok.Getter;

public enum ServiceServer {
    SERVICE_TEST_Online_011("172.29.100.011", 22, "테스트 011", "/", 2),
    SERVICE_TEST_Online_012("172.29.100.012", 22, "테스트 012", "/", 2),
    SERVICE_TEST_Online_013("172.29.100.013", 22, "테스트 013", "/", 2),
    SERVICE_TEST_Online_014("172.29.100.014", 22, "테스트 014", "/", 2),
    SERVICE_TEST_Online_015("172.29.100.015", 22, "테스트 015", "/", 2),

    SERVICE_BACKUP_051("172.29.100.051", 22, "백업 051", "/", 2),
    SERVICE_BACKUP_052("172.29.100.052", 22, "백업서버 052", "/", 2),
    SERVICE_BATCH("172.29.100.10", 22, "배치서버", "/", 1);

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


    ServiceServer(String host, int port, String description, String partition, int signType){
        this.host = host;
        this.port = port;
        this.description = description;
        this.partition = partition;
        this.signType = signType;
    }
}

package kr.co.ooweat.taskScheduler.common;

import lombok.Getter;

public enum SmtpTitleEnum {
    QNA("문의하기"),
    welpoint("카드결제시스템 충전배치"),
    createTable("테이블 생성"),
    MariaNode("MariaNode 확인"),
    DDC("DDC 확인"),
    DDCDuplicateCheck("VAN CODE 중복 등록"),
    AdjustCheck("마감정산 오류"),
    PAYCO("페이코 매입 자동 재진행"),
    prepayUnregisteredTidCheck("선불 미등록 단말기 알림"),
    ddcAdjustCheck("DDC 마감주기 오류"),
    KakaoAlertProcess("카카오 알림톡 프로세스"),
    vmDiskUsageCheck("VM 사용량 알림"),
    rsfCheck("RSF 배치현황 알림")
    ;

    @Getter
    private String title;

    SmtpTitleEnum(String title){
        this.title = title;
    }
}

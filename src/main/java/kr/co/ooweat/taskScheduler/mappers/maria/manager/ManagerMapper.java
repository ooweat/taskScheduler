package kr.co.ooweat.taskScheduler.mappers.maria.manager;

import org.apache.ibatis.annotations.Mapper;

import java.util.HashMap;
import java.util.List;

@Mapper
public interface ManagerMapper {
    int workDayCheck(String today);
    //마리아 배치
    List<HashMap<String, Object>> galleraInfo();
    int createKakaoLogYYYYMM(String yyyyMM);
    int createMMSLogYYYYMM(String yyyyMM);
    int checkCreatedKakaoLogTable(String yyyyMM);
    int checkCreatedMMSLogTable(String yyyyMM);
}

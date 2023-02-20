package kr.co.ooweat.taskScheduler.mappers.maria.manager;

import org.apache.ibatis.annotations.Mapper;

import java.util.HashMap;
import java.util.List;

@Mapper
public interface ManagerMapper {
    //마리아 배치
    List<HashMap<String, Object>> galleraInfo();
}

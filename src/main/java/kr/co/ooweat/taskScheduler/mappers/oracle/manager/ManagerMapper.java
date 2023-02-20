package kr.co.ooweat.taskScheduler.mappers.oracle.manager;

import org.apache.ibatis.annotations.Mapper;

import java.util.HashMap;
import java.util.List;
@Mapper
public interface ManagerMapper {
    //오늘이 영업일인지 확인
    int workDayCheck();
}

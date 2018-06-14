package cn.smarthse.business.dao.system;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import cn.smarthse.business.model.system.ASyslog;
import cn.smarthse.business.model.system.ASyslogExample;
import cn.smarthse.framework.generic.GenericDao;

public interface ASyslogMapper extends GenericDao<ASyslog>{
    int countByExample(ASyslogExample example);

    int deleteByExample(ASyslogExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(ASyslog record);

    int insertSelective(ASyslog record);

    List<ASyslog> selectByExampleWithBLOBs(ASyslogExample example);

    List<ASyslog> selectByExample(ASyslogExample example);

    ASyslog selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") ASyslog record, @Param("example") ASyslogExample example);

    int updateByExampleWithBLOBs(@Param("record") ASyslog record, @Param("example") ASyslogExample example);

    int updateByExample(@Param("record") ASyslog record, @Param("example") ASyslogExample example);

    int updateByPrimaryKeySelective(ASyslog record);

    int updateByPrimaryKeyWithBLOBs(ASyslog record);

    int updateByPrimaryKey(ASyslog record);
}
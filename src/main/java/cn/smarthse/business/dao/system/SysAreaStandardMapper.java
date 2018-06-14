package cn.smarthse.business.dao.system;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import cn.smarthse.business.model.system.SysAreaStandard;
import cn.smarthse.business.model.system.SysAreaStandardExample;

public interface SysAreaStandardMapper {
    int countByExample(SysAreaStandardExample example);

    int deleteByExample(SysAreaStandardExample example);

    int deleteByPrimaryKey(Long id);

    int insert(SysAreaStandard record);

    int insertSelective(SysAreaStandard record);

    List<SysAreaStandard> selectByExample(SysAreaStandardExample example);

    SysAreaStandard selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") SysAreaStandard record, @Param("example") SysAreaStandardExample example);

    int updateByExample(@Param("record") SysAreaStandard record, @Param("example") SysAreaStandardExample example);

    int updateByPrimaryKeySelective(SysAreaStandard record);

    int updateByPrimaryKey(SysAreaStandard record);
}
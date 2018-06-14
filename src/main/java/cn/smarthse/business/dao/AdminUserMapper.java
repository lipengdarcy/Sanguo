package cn.smarthse.business.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import cn.smarthse.business.model.AdminUser;
import cn.smarthse.business.model.AdminUserExample;
import cn.smarthse.framework.generic.GenericDao;

public interface AdminUserMapper extends GenericDao<AdminUser> {

	/**
	 * 自定义查询用户列表
	 * 
	 * @param name
	 *            用户名或者姓名
	 */
	List<AdminUser> query(@Param("name") String name);	

	int countByExample(AdminUserExample example);

	int deleteByExample(AdminUserExample example);

	int deleteByPrimaryKey(Integer id);

	int insert(AdminUser record);

	int insertSelective(AdminUser record);

	List<AdminUser> selectByExample(AdminUserExample example);

	AdminUser selectByPrimaryKey(Integer id);

	int updateByExampleSelective(@Param("record") AdminUser record, @Param("example") AdminUserExample example);

	int updateByExample(@Param("record") AdminUser record, @Param("example") AdminUserExample example);

	int updateByPrimaryKeySelective(AdminUser record);

	int updateByPrimaryKey(AdminUser record);
}
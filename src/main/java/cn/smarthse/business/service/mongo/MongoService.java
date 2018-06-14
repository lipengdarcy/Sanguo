package cn.smarthse.business.service.mongo;

import java.util.List;

import cn.smarthse.business.model.system.SysAreaStandard;
import cn.smarthse.framework.model.JqGridData;
import cn.smarthse.framework.model.JqGridParam;

public interface MongoService {

	List<SysAreaStandard> getAllArea();

	void addList(List<SysAreaStandard> list);

	/**
	 * 分页查询数据,返回grid格式的数据
	 * 
	 */
	public JqGridData<SysAreaStandard> getGridData(JqGridParam param);
}

package cn.smarthse.business.service.ASystem;

import java.util.List;

import cn.smarthse.business.model.system.SysAreaStandard;
import cn.smarthse.framework.model.JqGridData;
import cn.smarthse.framework.model.JqGridParam;

public interface AreaService {

	List<SysAreaStandard> getAll();

	public JqGridData<SysAreaStandard> getAll(JqGridParam param);
}

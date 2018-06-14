package cn.smarthse.business.service.ASystem.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;

import cn.smarthse.business.dao.system.SysAreaStandardMapper;
import cn.smarthse.business.model.system.SysAreaStandard;
import cn.smarthse.business.model.system.SysAreaStandardExample;
import cn.smarthse.business.service.ASystem.AreaService;
import cn.smarthse.framework.model.JqGridData;
import cn.smarthse.framework.model.JqGridParam;

@Service
public class AreaServiceImpl  implements AreaService {

	@Resource
	SysAreaStandardMapper SysAreaStandardMapper;

	@Override
	public List<SysAreaStandard> getAll() {
		return SysAreaStandardMapper.selectByExample(new SysAreaStandardExample());
	}

	/**
	 * 分页查询数据,返回grid格式的数据
	 * 
	 */
	@Override
	public JqGridData<SysAreaStandard> getAll(JqGridParam param) {
		SysAreaStandardExample e = new SysAreaStandardExample();
		//ASysareaExample.Criteria c = e.createCriteria();
		SysAreaStandardMapper.selectByExample(e);
		if (!StringUtils.isEmpty(param.getSidx()))
			e.setOrderByClause(param.getSidx() + " " + param.getSord());
		PageHelper.startPage((int) param.getPage(), (int) param.getRows());
		Page<SysAreaStandard> list = (Page<SysAreaStandard>) this.getAll();
		JqGridData<SysAreaStandard> data = new JqGridData<SysAreaStandard>(list, param);
		return data;
	}

}

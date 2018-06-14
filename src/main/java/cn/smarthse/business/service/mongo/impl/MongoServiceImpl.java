package cn.smarthse.business.service.mongo.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import cn.smarthse.business.dao.mongo.MongoAreaDao;
import cn.smarthse.business.model.system.SysAreaStandard;
import cn.smarthse.business.service.mongo.MongoService;
import cn.smarthse.framework.model.JqGridData;
import cn.smarthse.framework.model.JqGridParam;

@Service
public class MongoServiceImpl implements MongoService {

	@Resource
	MongoAreaDao MongoAreaDao;

	@Override
	public List<SysAreaStandard> getAllArea() {
		return MongoAreaDao.findAll();
	}

	/**
	 * 分页查询数据,返回grid格式的数据
	 * 
	 */
	@Override
	public JqGridData<SysAreaStandard> getGridData(JqGridParam param) {
		PageRequest pageRequest = new PageRequest((int) param.getPage() - 1, (int) param.getRows());
		Page<SysAreaStandard> page = MongoAreaDao.findAll(pageRequest);
		JqGridData<SysAreaStandard> data = new JqGridData<SysAreaStandard>(page);
		return data;
	}

	@Override
	public void addList(List<SysAreaStandard> list) {
		MongoAreaDao.insert(list);
	}

}

package cn.smarthse.business.service.mongo.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import cn.smarthse.business.dao.mongo.MongoLogDao;
import cn.smarthse.business.dao.system.ASyslogMapper;
import cn.smarthse.business.model.system.ASyslog;
import cn.smarthse.business.service.mongo.MongoLogService;
import cn.smarthse.framework.generic.GenericDao;
import cn.smarthse.framework.generic.GenericServiceImpl;
import cn.smarthse.framework.model.JqGridData;

@Service
public class MongoLogServiceImpl extends GenericServiceImpl<ASyslog> implements MongoLogService {

	@Resource
	MongoLogDao LogDao;

	@Resource
	ASyslogMapper ASyslogMapper;

	@Override
	public GenericDao<ASyslog> getDao() {
		return ASyslogMapper;
	}

	@Override
	public List<ASyslog> getAllFromMysql() {
		return ASyslogMapper.selectByExample(null);
	}

	@Override
	public void addList(List<ASyslog> list) {
		LogDao.save(list);
	}

	/**
	 * 分页查询日志数据,返回grid格式的数据
	 * 
	 */
	@Override
	public JqGridData<ASyslog> getAll(PageRequest pageRequest) {
		Page<ASyslog> data = LogDao.findAll(pageRequest);
		JqGridData<ASyslog> result = new JqGridData<ASyslog>(data);
		return result;
	}

	/**
	 * 查询所有日志数据,返回List格式的数据
	 * 
	 */
	@Override
	public List<ASyslog> getAll() {
		List<ASyslog> list = new ArrayList<ASyslog>();
		Iterable<ASyslog> data = LogDao.findAll();
		Iterator<ASyslog> it = data.iterator();
		while (it.hasNext()) {
			list.add(it.next());
		}
		return list;
	}

}

package cn.smarthse.business.service.mongo;

import java.util.List;

import org.springframework.data.domain.PageRequest;

import cn.smarthse.business.model.system.ASyslog;
import cn.smarthse.framework.model.JqGridData;

public interface MongoLogService {

	List<ASyslog> getAllFromMysql();

	List<ASyslog> getAll();

	public JqGridData<ASyslog> getAll(PageRequest pageRequest);

	void addList(List<ASyslog> list);
}

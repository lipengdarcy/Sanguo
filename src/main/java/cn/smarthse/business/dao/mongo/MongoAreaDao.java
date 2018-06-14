package cn.smarthse.business.dao.mongo;

import org.springframework.data.mongodb.repository.MongoRepository;

import cn.smarthse.business.model.system.SysAreaStandard;

/**
 * mongodb 行政区域
 */


public interface MongoAreaDao extends MongoRepository<SysAreaStandard, String> {

}

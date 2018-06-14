package cn.smarthse.business.controller.mongo;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.smarthse.business.model.system.ASyslog;
import cn.smarthse.business.model.system.SysAreaStandard;
import cn.smarthse.business.service.ASystem.AreaService;
import cn.smarthse.business.service.mongo.MongoLogService;
import cn.smarthse.business.service.mongo.MongoService;
import cn.smarthse.framework.model.JqGridData;
import cn.smarthse.framework.model.JqGridParam;

@Controller
@RequestMapping("/mongo")
public class MongoController {

	@Autowired
	private AreaService AreaService;

	@Autowired
	private MongoService MongoService;

	@Autowired
	private MongoLogService LogService;

	@RequestMapping()
	public String init(ModelMap m) {
		//List<SysAreaStandard> areaList = MongoService.getAllArea();
		//m.put("areaList", areaList);
		return "Mongo/mongoList";
	}

	/**
	 * 系统日志页面
	 */
	@RequestMapping("logList")
	public String logList(ModelMap m) {
		return "Mongo/logList";
	}

	/**
	 * 系统日志页面数据
	 */
	@ResponseBody
	@RequestMapping("logData")
	public JqGridData<ASyslog> logData(JqGridParam param, ModelMap m, HttpSession session) {
		JqGridData<ASyslog> data = LogService.getAll(param.buildPageRequest());
		return data;
	}

	/**
	 * 行政区域页面
	 */
	@RequestMapping("areaList")
	public String areaList(ModelMap m) {
		return "Mongo/areaList";
	}

	/**
	 * 行政区域数据
	 */
	@ResponseBody
	@RequestMapping("areaData")
	public JqGridData<SysAreaStandard> areaData(JqGridParam param) {
		JqGridData<SysAreaStandard> data = MongoService.getGridData(param);
		return data;
	}

	/**
	 * mysql导入mongo 行政区域数据
	 */
	@RequestMapping("import")
	@ResponseBody
	public String inportFromMysql(ModelMap m) {
		List<SysAreaStandard> list = AreaService.getAll();
		// MongoService.addList(list);
		return "mysql数据导入mongo成功，导入记录数：" + list.size();
	}

}

package cn.smarthse.business.controller.ASystem;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.smarthse.business.model.system.SysAreaStandard;
import cn.smarthse.business.service.ASystem.AreaService;
import cn.smarthse.framework.model.JqGridData;
import cn.smarthse.framework.model.JqGridParam;

/**
 * 行政区域
 */
@Controller
@RequestMapping("/area")
public class AreaController {
	@Autowired
	private AreaService AreaService;

	@RequestMapping()
	public String init(ModelMap m) {
		return "common/areaList";
	}

	@RequestMapping("/getData")
	@ResponseBody
	public JqGridData<SysAreaStandard> get(JqGridParam param) {
		JqGridData<SysAreaStandard> list = AreaService.getAll(param);
		return list;
	}

}

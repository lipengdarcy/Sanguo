package cn.smarthse.framework;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * 系统提示信息
 */
public class Message {

	// 错误信息提示
	public static final ResourceBundle bundle = ResourceBundle.getBundle("Message", Locale.getDefault());
	public static final String common403 = "a.common.403";
	public static final String common404 = "a.common.404";
	public static final String common500 = "a.common.500";

	public static final String username_cannotbe_null = "a.user.username_cannotbe_null";
	public static final String get_user_failed = "a.user.get_user_failed";
	public static final String user_not_exist = "a.user.user_not_exist";
	public static final String user_password_error = "a.user.user_password_error";
	public static final String user_vcode_error = "a.user.user_vcode_error";

	public static final String user_forbidden = "a.user.user_forbidden";
	public static final String user_expire = "a.user.user_expire";
	
	public static final String user_login_success = "a.user.user_login_success";
	public static final String user_logout_success = "a.user.user_logout_success";
	public static final String mobile_vcode_error = "a.user.mobile_vcode_error";
	// 定时发送时间必须超过30分钟
	public static final String notice_timesend_timesmall = "a.user.notice_timesend_timesmall";
	// Staff为NULL时，提示用户未被授权
	public static final String user_not_authorization = "a.user.user_not_authorization";

	// 首页提示信息

	/**
	 * #1.1 放射诊疗三同时 a.f.a1=下列设备处于放射诊疗未许可状态，请及时开展放射诊疗三同时工作或录入往年的设备性能及放射诊疗场所检测报告：{0}
	 */
	public static final String a_f_a1 = "a.f.a1";
	/**
	 * #1.1 辐射安全三同时 a.f.a2=下列设备处于辐射诊疗未许可状态，请及时开展辐射诊疗三同时工作或录入往年的设备性能及辐射诊疗场所检测报告：{0}
	 */
	public static final String a_f_a2 = "a.f.a2";

	/**
	 * #1.2 辐射源项
	 * a.e.a1={0}还未选定，<a href\="{1}/EDevice/{2}/detail?id\={3}">点此进行完善。</a>
	 */
	public static final String a_e_a1 = "a.e.a1";
	/**
	 * a.e.a2=没有可选人员信息，<a href\="{0}/staff/addStaff">点此前往增加人员。</a>
	 */
	public static final String a_e_a2 = "a.e.a2";
	/**
	 * a.e.a3=目前辐射源项的信息完整度为{0}%，下列设备信息不完善：{1}
	 */
	public static final String a_e_a3 = "a.e.a3";
	/**
	 * a.e.a4=项目验收前，请对辐射源项信息进行核实。以评价报告或厂家技术说明书为准。
	 */
	public static final String a_e_a4 = "a.e.a4";

	/**
	 * #1.3 机房区域
	 * a.b.a1=<a href\="javascript:editRoomForThird({1});">{0}的尺寸面积未填写，点此进行完善。</a>
	 */
	public static final String a_b_a1 = "a.b.a1";

	/**
	 * e.b.a2=<a href\=
	 * "javascript:editRoomForThird({1});">{0}的尺寸面积不符合要求，请及时修改，并进行完善。</a>
	 */
	public static final String a_b_a2 = "a.b.a2";

	/**
	 * #1.4 人员
	 * a.c.a1=目前人员信息完整度为{0},<a href\="{0}/staff/addStaff?isThirdParty=0" target\=
	 * "_blank">点此前往完善人员信息。</a>
	 */
	public static final String a_c_a1 = "a.c.a1";

	/**
	 * #1.4 人员 a.c.a2=下列人员未取得放射工作人员证：{0}。
	 */
	public static final String a_c_a2 = "a.c.a2";

	/**
	 * #1.4 人员 a.c.a3=下列人员未取得辐射安全与防护培训合格证书：{0}。
	 */
	public static final String a_c_a3 = "a.c.a3";

	/**
	 * #1.5 a.d.a1={0}的个人防护用品配备不足，点此完善防护用品信息。
	 */
	public static final String a_d_a1 = "a.d.a1";

	/**
	 * #1.5 a.d.a2={0}的警示标识设置不足，点此完善警示标识信息。
	 */
	public static final String a_d_a2 = "a.d.a2";

	/**
	 * #1.6 a.h.a1=<a href\="{0}/law" target\="_blank">请及时制定放射防护管理制度。</a>
	 */
	public static final String a_h_a1 = "a.h.a1";

	/**
	 * #1.6 a.h.a2=开展放射诊疗工作，应制定相应的放射防护管理制度。如：《放射诊疗质量保证制度》、《受检者告知制度》、《放射防护安全管理制度》、
	 * 《放射工作人员管理制度》、《放射事故应急处理预案》、《设备操作规程》等。
	 */
	public static final String a_h_a2 = "a.h.a2";

	/**
	 * #1.6 a.h.a2=开展辐射工作，应制定相应的辐射防护管理制度。如：《诊疗质量保证制度》、《受检者告知制度》、《辐射防护安全管理制度》、《
	 * 辐射工作人员管理制度》、《辐射事故应急处理预案》、《设备操作规程》等。
	 */
	public static final String a_h_a3 = "a.h.a3";
	/**
	 * #1.7 a.b.a3=请及时成立放射防护管理领导小组。<a href\="{0}/funcdept" target\=
	 * "_blank">点此完善组织机构信息。</a>
	 */
	public static final String a_b_a3 = "a.b.a3";
	/**
	 * #1.7 a.b.a4=请及时成立辐射防护管理领导小组。<a href\="{0}/funcdept" target\=
	 * "_blank">点此完善组织机构信息。</a>
	 */
	public static final String a_b_a4 = "a.b.a4";

	/**
	 * #1.7
	 * a.b.a5=请及时成立应急预案领导小组。<a href\="{0}/funcdept" target\="_blank">点此完善组织机构信息。</a>
	 */
	public static final String a_b_a5 = "a.b.a5";
	/**
	 * 2.职业健康检查 #2 a.health.m1=下列人员近期内未进行职业健康检查：{0}。
	 */
	public static final String a_health_m1 = "a.health.m1";
	/**
	 * 有<a href\="javascript\:void(0);">{0}</a>个体检非正常的人员*/
	public static final String a_health_m2 = "a.health.m2";
	/**
	 * 3.个人剂量监测 #3
	 * a.monitor.m1=原有放射工作人员历次个人剂量监测结果请补充录入。新进人员请在上岗前委托具有相应资质的服务机构进行个人剂量监测，
	 * 以免影响项目验收。收到监测报告后及时录入。点此完善个人剂量监测信息。
	 */
	public static final String a_monitor_m1 = "a.monitor.m1";
	public static final String a_monitor_m2 = "a.monitor.m2";
	public static final String a_monitor_m3 = "a.monitor.m3";
	public static final String a_monitor_m4 = "a.monitor.m4";
	/**
	 * 4.设备性能检测 #4 a.device.check.m1=原有放射工作人员历次个人剂量监测结果请补充录入。
	 * 新进人员请在上岗前委托具有相应资质的服务机构进行个人剂量监测，以免影响项目验收。收到监测报告后及时录入。点此完善个人剂量监测信息。
	 */
	public static final String a_device_check_m1 = "a.device.check.m1";
	public static final String a_device_check_m2 = "a.device.check.m2";
	public static final String a_device_check_m3 = "a.device.check.m3";
	public static final String a_device_check_m4 = "a.device.check.m4";
	public static final String a_device_check_m5 = "a.device.check.m5";
	/**
	 * 5.工作场所检测 #5 a.area.check.m1=1.下列场所没有检测信息，如果已获得许可，请及时录入往年的放射诊疗场所检测报告：{0}
	 */
	public static final String a_area_check_m1 = "a.area.check.m1";
	public static final String a_area_check_m2 = "a.area.check.m2";
	public static final String a_area_check_m3 = "a.area.check.m3";
	public static final String a_area_check_m4 = "a.area.check.m4";
	public static final String a_area_check_m5 = "a.area.check.m5";
	public static final String a_area_check_m6 = "a.area.check.m6";
	public static final String a_area_check_m7 = "a.area.check.m7";
	/**
	 * 6.个人防护用品 #6 a.shield.m1={0}的个人防护用品配备不足，点此完善防护用品信息。
	 */
	public static final String a_shield_m1 = "a.shield.m1";
	/**
	 * #6 a.shield.m2={0}的{1}出厂日期{3}已超过6年使用有效期，请及时购买新防护用品并更新防护用品信息。
	 */
	public static final String a_shield_m2 = "a.shield.m2";
	/**
	 * 7.警示标识 #7 a.mark.m1={0}的警示标识设置不足，点此完善警示标识信息。
	 */
	public static final String a_mark_m1 = "a.mark.m1";
	/**
	 * 8.人员证书 #8 a.staffcert.m1=下列人员未取得放射人员工作证：{0}。
	 */
	public static final String a_staffcert_m1 = "a.staffcert.m1";
	/**
	 * #8 a.staffcert.m2=下列人员未取得辐射安全与防护培训合格证书：{0}。
	 */
	public static final String a_staffcert_m2 = "a.staffcert.m2";
	/**
	 * a.staffcert.m3={0}人放射工作人员培训即将过期。
	 */
	public static final String a_staffcert_m3 = "a.staffcert.m3";
	/**
	 * a.staffcert.m4={0}人辐射安全与防护培训即将过期。
	 */
	public static final String a_staffcert_m4 = "a.staffcert.m4";
	/**
	 * a.staffcert.m5={0}人放射工作人员培训已过期。
	 */
	public static final String a_staffcert_m5 = "a.staffcert.m5";
	/**
	 * a.staffcert.m6={0}人辐射安全与防护培训已过期。
	 */
	public static final String a_staffcert_m6 = "a.staffcert.m6";
	/**
	 * 9.监测设备 #9 a.device.monitor.m1=1、开展X射线影像诊断工作，宜配备X、γ剂量监测仪。
	 */
	public static final String a_device_monitor_m1 = "a.device.monitor.m1";
	public static final String a_device_monitor_m2 = "a.device.monitor.m2";
	public static final String a_device_monitor_m3 = "a.device.monitor.m3";
	public static final String a_device_monitor_m4 = "a.device.monitor.m4";
	public static final String a_device_monitor_m5 = "a.device.monitor.m5";
	public static final String a_device_monitor_m6 = "a.device.monitor.m6";
	public static final String a_device_monitor_m7 = "a.device.monitor.m7";
	/**
	 * 10.三合同告知 #10 a.contract.m1=下列放射工作人员未进行合同告知：{0}
	 */
	public static final String a_contract_m1 = "a.contract.m1";

	/**
	 * 微信推送消息
	 */
	public static final String a_pushmessage_m1 = "a.pushmessage.m1";
	public static final String a_pushmessage_m2 = "a.pushmessage.m2";
	public static final String a_pushmessage_m3 = "a.pushmessage.m3";
	public static final String a_pushmessage_m4 = "a.pushmessage.m4";
	public static final String a_pushmessage_m5 = "a.pushmessage.m5";
	public static final String a_pushmessage_m6 = "a.pushmessage.m6";
	public static final String a_pushmessage_m7 = "a.pushmessage.m7";
	public static final String a_pushmessage_m8 = "a.pushmessage.m8";
	public static final String a_pushmessage_m9 = "a.pushmessage.m9";
	public static final String a_pushmessage_m10 = "a.pushmessage.m10";
	public static final String a_pushmessage_m11 = "a.pushmessage.m11";
	public static final String a_pushmessage_m12 = "a.pushmessage.m12";
	public static final String a_pushmessage_m13 = "a.pushmessage.m13";

}

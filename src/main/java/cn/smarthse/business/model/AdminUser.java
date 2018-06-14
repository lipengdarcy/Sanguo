package cn.smarthse.business.model;

import java.util.Date;

import lombok.Data;

public @Data class AdminUser {
	
	/**
	 * 登录验证码
	 */
	private String verification;


	/**
	 */
	private Integer id;

	/**
	 * 行政区域ID
	 */
	private Long areaId;

	/**
	 * 行政区域名称
	 */
	private String areaName;

	/**
	 * 用户名
	 */
	private String userName;

	/**
	 * 密码
	 */
	private String passWord;

	/**
	 * 盐值
	 */
	private String salt;

	/**
	 * 姓名
	 */
	private String realName;

	/**
	 * 性别
	 */
	private String gender;

	/**
	 * 所在部门
	 */
	private String dept;

	/**
	 * 职务
	 */
	private String jobTitle;

	/**
	 * 联系电话
	 */
	private String tel;

	/**
	 * 角色说明 1：国家级管理员 2：国家级普通用户 3：省级管理员 4：省级普通用户 5：市级管理员 6：市级普通用户 7：区级管理员 8：区级普通用户
	 */
	private Byte role;

	/**
	 * 创建时间
	 */
	private Date createTime;

	/**
	 * 更新时间
	 */
	private Date updateTime;

	/**
	 * 用户头像
	 */
	private Integer userIcon;
	
	/**
	 * 用户头像路径
	 */
	private String userIconUrl;


	/**
	 * 是否有效
	 */
	private Byte isValid;

	/**
	 * 角色名称
	 */
	private String roleName;

	public String getUserRoleName() {
		if (this.role == null)
			return null;

		switch (this.role) {
		case 1:
			return "国家级管理员";
		case 2:
			return "国家级普通用户";
		case 3:
			return "省级管理员";
		case 4:
			return "省级普通用户";
		case 5:
			return "市级管理员";
		case 6:
			return "市级普通用户";
		case 7:
			return "区级管理员";
		case 8:
			return "区级普通用户";
		default:
			return null;
		}
	}
}
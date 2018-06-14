package cn.smarthse.framework.security;

import org.apache.shiro.authc.UsernamePasswordToken;

/**
 * 登录Token
 * 
 */
public class ShiroUsernamePasswordToken extends UsernamePasswordToken {

	private static final long serialVersionUID = 1795833011374373298L;
	
	//用户类型， 1：医院端用户； 2：监管端用户
	private String userType;

	private int loginType = 1;
	
	//图形验证码
	private String captcha;
	
	//动态验证码
	private String vcode;
	
	//第三方平台TokenKey
	private String token;

	/**
	 * @return the loginType
	 */
	public int getLoginType() {
		return loginType;
	}

	/**
	 * @param loginType the loginType to set
	 */
	public void setLoginType(int loginType) {
		this.loginType = loginType;
	}

	/**
	 * @return the captcha
	 */
	public String getCaptcha() {
		return captcha;
	}

	/**
	 * @param captcha the captcha to set
	 */
	public void setCaptcha(String captcha) {
		this.captcha = captcha;
	}

	/**
	 * @return the vcode
	 */
	public String getVcode() {
		return vcode;
	}

	/**
	 * @param vcode the vcode to set
	 */
	public void setVcode(String vcode) {
		this.vcode = vcode;
	}

	/**
	 * @return the token
	 */
	public String getToken() {
		return token;
	}

	/**
	 * @param token the token to set
	 */
	public void setToken(String token) {
		this.token = token;
	}
	
	public ShiroUsernamePasswordToken(){}
	
	/**
	//0-单点登录（通过token处理）
	//1-用户名+密码登录
	//2-用户名+密码+图形验证码登录
	//3-用户名+动态验证码
	//4-第三方
	//5-企业号+密码+图形验证码登录
	 * @param loginType 登录方式
	 */
	public ShiroUsernamePasswordToken(int loginType){
		this.loginType = loginType;
	}

	public String getUserType() {
		return userType;
	}

	public void setUserType(String userType) {
		this.userType = userType;
	}
	
}

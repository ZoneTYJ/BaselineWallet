package com.vfinworks.vfsdk.common;

import java.util.regex.Pattern;

/**
 * 校验器
 * 
 * @author xiaoshengke
 * 
 */
public class Validator {

	/**
	 * 正则表达式：验证手机号
	 */
//	public static final String REGEX_MOBILE = "^((13[0-9])|(14[5,7])|(15[^4,\\D])|(17[6-8])|(18[0-9]))\\d{8}$";
	public static final String REGEX_MOBILE = "^((13[0-9])|(14[0-9])|(15[0-9])|(17[0-9])|(18[0-9]))\\d{8}$";
	/**
	 * 不能为纯数字，纯字母，纯特殊字符
	 */
//	public static final String LOGIN_PASSWORD = "^(?![0-9]+$)(?![a-zA-Z]+$)(?![_.=+]+$)[\\w.=+]{6,20}$";	//不能为纯数字，纯字母，纯特殊字符(_.=+)
	public static final String LOGIN_PASSWORD = "((?=.*\\d)(?=.*\\D)|(?=.*[a-zA-Z])(?=.*[^a-zA-Z]))^[\\S]{6,20}$";
//	public static final String LOGIN_PASSWORD = "^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z]{6,20}$";	//不能为纯数字，纯字母
	/**
	 * 姓名
	 */
	public static final String NAME = "^(([\u4e00-\u9fa5]{2,8}))$";
	/**
	 * 校验手机号
	 * 
	 * @param mobile
	 * @return 校验通过返回true，否则返回false
	 */
	public static boolean isMobile(String mobile) {
		return Pattern.matches(REGEX_MOBILE, mobile);
	}

	/**
	 * 判断登录密码是否合法
	 * @param pwd
	 * @return
     */
	public static boolean isLoginPasswordLegal(String pwd){
		return Pattern.matches(LOGIN_PASSWORD,pwd);
	}

	/**
	 * 判断是否是姓名
	 * @param name
	 * @return
     */
	public static boolean isName(String name){
		return Pattern.matches(NAME,name);
	}

	public static void main(String[] args) {
	}
}

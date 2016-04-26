package com.servlet.action;

import com.model.UserBean;
import com.service.LoginService;
import com.utils.CheckInputUtil;

public class LoginAction {

	private UserBean user;

	/**
	 * 
	 * @Description：校验用户登录信息
	 * @param userBean
	 * @return
	 */
	public String login(UserBean userBean) {

		String userName = userBean.getUserName();
		String password = userBean.getPassword();
		LoginService loginService = new LoginService();
		try {
			// 校验前台输入的用户和密码是否合法
			if (CheckInputUtil.checkInput(userName, password)) {
				user.setUserName(userName);
				user.setPassword(password);
				user = loginService.checkUser(userBean);
				// 用户验证合法
				if (userBean != null) {
					return "success";
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "fail";
	}

	public void setUser(UserBean user) {
		this.user = user;
	}

}

package com.service;

import com.dao.LoginDao;
import com.model.UserBean;

/**
 * 
 * <p>
 * Class:LoginServiceImpl
 * </p>
 * <p>
 * Description: 用户登录校验service层接口实现
 * </p>
 * <p>
 * Copyright: USTC
 * </p>
 * 
 * @author Oukailiang
 * @version 1.0.0
 */
public class LoginService {
	private LoginDao loginDao = new LoginDao();

	/**
	 * 
	 * @Description：校验用户身份是否合法
	 * @param userBean
	 * @return
	 */
	public UserBean checkUser(UserBean userBean) {
		return loginDao.checkUser(userBean);
	}

	/**
	 * 
	 * @Description：注册用户信息
	 * @param userBean
	 * @return
	 */
	public boolean registUser(UserBean userBean) {
		return loginDao.registUser(userBean);
	}

}

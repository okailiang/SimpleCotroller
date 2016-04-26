package com.servlet;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.model.Action;
import com.model.Result;
import com.model.UserBean;
import com.utils.CheckInputUtil;
import com.utils.ParseDIUtils;
import com.utils.SaxParseXmlUtils;

public class LoginController extends HttpServlet {
	/** serialVersionUID */
	private static final long serialVersionUID = -16790640755682343L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String action = "";
		String errorInfo = null;
		// 存读取的controller.xml数据
		Object resultString = null;
		// 存action依赖注入对象集合
		List<String> diList = null;
		Class<?> c = null;
		Object obj = null;
		Method method = null;
		String actionClass = null;
		try {
			// 获得前台请求的action
			action = getRequestAction(req);
			// 用sax解析Controller.xml
			SaxParseXmlUtils parseXmlUtils = new SaxParseXmlUtils()
					.parseXMLFile();
			// 判断是否有对应的action,如有则返回action对象
			Action ac = parseXmlUtils.isHasAction(action);
			if (ac != null) {
				if (getUserBean(req) != null) {// 判断前台请求的数据是否合法
					// 获得前台请求的用户数据
					UserBean user = getUserBean(req);
					actionClass = ac.getCla().getCname();
					ParseDIUtils diUtils = new ParseDIUtils().getDI();
					// 判断action对应的类是否配置依赖注入bean
					if (diUtils != null && diUtils.isHasBean(actionClass)) {
						// 依赖注入集合
						diList = diUtils.getDIList();
						// 默认第一个为该action对应类
						c = Class.forName(diList.get(0));
						obj = c.newInstance();
						// 获得配置依赖注入的actionBean实例，并实例化action中的子bean对象
						getActionBean(diList, obj);

					} else {
						// 得到解析action请求对应的类， 通过反射机制实例化
						c = Class.forName(actionClass);
						// 实例化
						obj = c.newInstance();
					}
					// 调用class中的method
					method = c.getMethod(ac.getCla().getMethod(),
							UserBean.class);

					// 回调action中请求对应的方法，并返回success或fail字符串
					resultString = method.invoke(obj, user);
					// 解析到配置文件中的result标签
					Set<Result> results = ac.getResults();
					// false表示没有和配置文件中result匹配的
					boolean resultSign = false;
					for (Result result : results) {
						// 与result标签下的name比对
						if (((String) resultString).equals((result
								.getResultName()))) {
							resultSign = true;
							// 比对result下的type标签，并完成跳转，到value标签中的页面
							if (result.getType().equals("forward")) {
								req
										.setAttribute("userName", user
												.getUserName());
								req.setAttribute("id", user.getId());

								req.getRequestDispatcher(result.getValue())
										.forward(req, resp);
								return;
							} else if (result.getType().equals("redirect")) {
								req.setAttribute("errorInfo", "*用户名或密码错误！");
								resp.sendRedirect(result.getValue());
							}
							break;
						}
					}
					if (!resultSign) {
						// 如果没有匹配到result中相应的值，跳转到配置错误文件
						errorInfo = "返回值和配置中不匹配错误！";
					}
				} else {
					errorInfo = "请求数据不合法！";
				}
			} else {// 没有找到对应的请求
				errorInfo = "404错误，没有请求的资源！";
			}
		} catch (Exception e) {
			errorInfo = "请求数据不合法！";
			e.getStackTrace();
		}
		if (errorInfo != null) {
			req.setAttribute("errorInfo", errorInfo);
			req.getRequestDispatcher("/jsp/configError.jsp").forward(req, resp);
		}
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		doGet(req, resp);
	}

	/**
	 * 
	 * @Description：获得配置依赖注入bean的实例
	 * @param diList
	 * @param obj
	 * @throws Exception
	 */
	public void getActionBean(List<String> diList, Object obj) throws Exception {
		int diSize = diList.size();
		BeanInfo bInfoObject = null;
		Class<?> c = null;
		Object o = obj;
		// list中第一个数默认为action对象
		for (int i = 0; i < diSize - 1; i++) {
			// 通过反射加载该类
			c = Class.forName(diList.get(i));
			// 依据Bean产生一个相关的BeanInfo类
			bInfoObject = Introspector.getBeanInfo(c, c.getSuperclass());
			// 内省成员属性:用于获取该Bean中的所有允许公开的成员属性，以PropertyDescriptor数组的形式返回
			PropertyDescriptor[] mPropertyArray = bInfoObject
					.getPropertyDescriptors();
			// 将含有setter的成员变量实例化一个对象
			for (int j = 0; j < mPropertyArray.length; j++) {
				// 获得该成员变量的数据类型
				Class<?> propertyType = mPropertyArray[j].getPropertyType();
				// 创建该成员变量同类型的实例化对象
				Object propertyObj = propertyType.newInstance();
				// 获得该成员变量的set方法
				Method setMethod = mPropertyArray[j].getWriteMethod();
				// 将创建的实例化对象通过set方法赋给该成员变量
				setMethod.invoke(o, propertyObj);
			}
			o = new Object();
		}
	}

	/**
	 * 
	 * @Description：得到request中请求的资源
	 * @param request
	 * @return
	 */
	private String getRequestAction(HttpServletRequest request) {
		String path = request.getServletPath().toString();
		String action = "";
		// 获得前台提交的请求
		if (path != null) {
			path = path.replace("/", "");
			action = path.substring(0, path.indexOf("."));
		}
		return action;
	}

	/**
	 * 
	 * @Description：将前台传来的用户名和密码封装成对象
	 * @param request
	 * @return
	 */
	private UserBean getUserBean(HttpServletRequest request) {
		UserBean userBean = new UserBean();
		String userName = request.getParameter("userName");
		String pwd = request.getParameter("password");
		if (CheckInputUtil.checkInput(userName, pwd)) {
			userBean.setUserName(userName);
			userBean.setPassword(pwd);
			return userBean;
		}
		return null;
	}
}

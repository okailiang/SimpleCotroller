package com.utils;

import java.util.ArrayList;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

/**
 * <p>
 * Class:ParseDIUtils
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: USTC
 * </p>
 * 
 * @author Oukailiang
 * @version 1.0.0
 */
public class ParseDIUtils {
	private Element rootNode = null;
	private List<String> diList = new ArrayList<String>();

	public ParseDIUtils getDI() {
		try {
			// 创建saxReader对象
			SAXReader reader = new SAXReader();
			// 通过read方法读取一个文件 转换成Document对象
			Document document = reader.read(ParseDIUtils.class.getClassLoader()
					.getResourceAsStream("di.xml"));
			// 获取根节点元素对象
			rootNode = document.getRootElement();
		} catch (DocumentException e) {
			e.printStackTrace();
		}
		return this;
	}

	/**
	 * 
	 * @Description：判断是否有该bean对象，如有则获得该bean对象及关联的子bean对象
	 * @param beanName
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public boolean isHasBean(String beanName) {
		boolean hasSign = false;
		String beanClass = null;
		String beanRef = null;
		// 获得所有的bean节点
		List<Element> beanList = rootNode.elements("bean");
		// 遍历属性节点
		for (Element bean : beanList) {
			if (beanName.equals(bean.elementText("name"))) {
				beanClass = bean.elementTextTrim("class");
				diList.add(beanClass);
				beanRef = bean.element("property").elementTextTrim("ref-class");
				hasSign = true;
				break;
			}
		}
		// 获得子bean
		if (beanRef != null) {
			isHasBean(beanRef);
		}
		return hasSign;
	}

	public List<String> getDIList() {
		return diList;
	}
}

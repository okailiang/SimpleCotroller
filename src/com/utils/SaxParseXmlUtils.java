package com.utils;

import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.model.Action;
import com.model.ActionClass;
import com.model.Result;

public class SaxParseXmlUtils extends DefaultHandler {

	private Result result;
	private Set<Result> results;
	private Action action;
	private ActionClass cla;
	private Set<Action> actions;
	private String preTag;

	@Override
	public void startDocument() throws SAXException {
		results = new HashSet<Result>();
		actions = new HashSet<Action>();
	}

	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {
		if (action != null) {
			String data = new String(ch, start, length);
			if ("name".equals(preTag)) {
				action.setName(data);
			}
			if ("cname".equals(preTag)) {
				cla.setCname(data);
			}
			if ("method".equals(preTag)) {
				cla.setMethod(data);
			}
			if ("rname".equals(preTag)) {
				result.setResultName(data);
			}
			if ("type".equals(preTag)) {
				result.setType(data);
			}
			if ("value".equals(preTag)) {
				result.setValue(data);
			}
		}
	}

	@Override
	public void startElement(String uri, String localName, String name,
			Attributes attr) throws SAXException {
		if ("action".equals(name)) {
			action = new Action();
		}
		if ("class".equals(name)) {
			cla = new ActionClass();
		}
		if ("result".equals(name)) {
			result = new Result();
		}
		preTag = name;
	}

	@Override
	public void endElement(String uri, String localName, String name)
			throws SAXException {
		if (action != null && "action".equals(name)) {
			action.setCla(cla);
			action.setResults(results);
			actions.add(action);
			action = null;
			results = new HashSet<Result>();
		}
		if (cla != null && "cla".equals(name)) {
			cla = null;
		}
		if (result != null && "result".equals(name)) {
			results.add(result);
			result = null;
		}
		preTag = null;
	}

	public Set<Action> getActions() {
		return actions;
	}

	public Set<Result> getResults() {
		return results;
	}

	public Action isHasAction(String curAction) {
		for (Action ac : actions) {
			// 获得解析的action
			if (curAction.equals(ac.getName())) {
				return ac;
			}
		}
		return null;
	}

	// 解析文档
	public SaxParseXmlUtils parseXMLFile() throws Exception {
		// 得到SAX解析器的工厂实例
		SAXParserFactory factory = SAXParserFactory.newInstance();
		// 从SAX工厂实例中获得SAX解析器
		SAXParser saxParser = factory.newSAXParser();
		// 把要解析的XML文档转化为输入流，以便DOM解析器解析它
		InputStream is = SaxParseXmlUtils.class.getClassLoader()
				.getResourceAsStream("controller.xml");

		SaxParseXmlUtils handle = new SaxParseXmlUtils();
		// 解析XML文档
		saxParser.parse(is, handle);
		is.close();
		actions = handle.getActions();
		return this;
	}

}

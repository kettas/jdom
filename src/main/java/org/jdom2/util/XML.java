package org.jdom2.util;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jdom2.Attribute;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.jdom2.xpath.XPath;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * XML简化操作类
 * 
 * @author kettas 3:11:54 PM
 */
public class XML {
	private static Document document = null;
	/**
	 * xml文件编码格式
	 */
	public static final String ENCODE = "UTF-8";

	/**
	 * 是否存在此文件 <br>
	 * 如果不存在就抛出异常，存在就返回此文件
	 * 
	 * @param file
	 *            文件对象
	 * @return File
	 * @throws IOException
	 */
	protected static File readFile(File file) throws IOException {
		try {
			if (file.exists()) {
				return file;
			}
		} catch (Exception e) {
			throw new IOException("找不到文件(" + file + ")", e);
		}
		throw new IOException("找不到文件(" + file + ")");
	}

	public static class IgnoreDTDEntityResolver implements EntityResolver {
		public InputSource resolveEntity(String publicId, String systemId)
				throws SAXException, IOException {
			return new InputSource(new ByteArrayInputStream(
					"<?xml version='1.0' encoding='UTF-8'?>".getBytes()));
		}
	}

	/**
	 * <pre>
	 * 将文件转换为Document对象
	 * <code>Document doc=XML.fileToDocument(new File("c://abc/a.xml"));</code>
	 * </pre>
	 * 
	 * @param file
	 *            文件路径
	 * @return Document
	 * @throws JDOMException
	 */
	public static Document fileToDocument(File file) throws JDOMException {
		try {
			SAXBuilder sax = new SAXBuilder();
			sax.setEntityResolver(new IgnoreDTDEntityResolver());
			document = sax.build(readFile(file));
			return document;
		} catch (FileNotFoundException e) {
			document = null;
			e.printStackTrace();
			throw new JDOMException("FileNotFoundException:" + e.getMessage(),
					e);
		} catch (IOException e) {
			document = null;
			e.printStackTrace();
			throw new JDOMException(
					"Can't find [" + file.getPath() + "]file !", e);
		} catch (Exception e) {
			document = null;
			throw new JDOMException(e.getMessage(), e);
		}
	}

	/**
	 * <pre>
	 * 自定义表达式的方式来读取配置文件信息:并指定你需要操作的文件的路径
	 * <code>List list=XML.fileToDocument(new File("c://abc/a.xml"),"/rss");</code>
	 * </pre>
	 * 
	 * @param file
	 *            xml 文件路径
	 * @param express
	 *            xpath查询表达式
	 * @return List&lt;Element>
	 * @throws JDOMException
	 */
	public static List queryToElementList(File file, String express)
			throws JDOMException {
		return queryToElementList(file, XPath.newInstance(express));
	}

	/**
	 * <pre>
	 * 自定义表达式的方式来读取配置文件信息:并指定你需要操作的文件的路径
	 * <code>List list=XML.fileToDocument(new File("c://abc/a.xml"),"/rss");</code>
	 * </pre>
	 * 
	 * @param fileText
	 *            xml 文件路径
	 * @param express
	 *            xpath查询表达式
	 * @return List&lt;Element>
	 * @throws JDOMException
	 */
	public static List queryToElementList(String fileText, String express)
			throws JDOMException {
		Document doc = null;
		try {
			java.io.Reader in = new StringReader(fileText);
			doc = (new SAXBuilder()).build(in);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return queryToElementList(doc, express);
	}

	/**
	 * 根据您的设置的Xpath语法查询XML文件并返回查询结果
	 * 
	 * <pre>
	 * File file = new File(&quot;c:\\rss.xml&quot;);
	 * List list = XML.queryToElementList(file,
	 * 		XPath.newInstance(&quot;/rss/channel/title&quot;));
	 * for (Element e : list) {
	 * 	System.out.println(&quot;名称:&quot; + e.getText());
	 * }
	 * </pre>
	 * 
	 * @param file
	 * @param xPath
	 * @return List
	 * @throws JDOMException
	 */
	public static List queryToElementList(File file, XPath xPath)
			throws JDOMException {
		return xPath.selectNodes(fileToElement(file));
	}

	/**
	 * 根据您的设置的Xpath语法查询Document并返回查询结果
	 * 
	 * <pre>
	 * Document doc = XML.fileToDocument(new File(&quot;c:/xml.xml&quot;));
	 * List list = org.jdom.XML.queryToElementList(doc,
	 * 		&quot;/rss/channel/title[@name='abc']&quot;);
	 * for (Element e : list) {
	 * 	System.out.println(&quot;名称:&quot; + e.getText());
	 * }
	 * </pre>
	 * 
	 * @param doc
	 * @param express
	 * @return List
	 * @throws JDOMException
	 */
	public static List queryToElementList(Document doc, String express)
			throws JDOMException {
		return XPath.newInstance(express).selectNodes(doc);
	}

	/**
	 * 查询文件顶层节点.
	 * 
	 * @param file
	 *            文件路径
	 * @return Element
	 * @throws JDOMException
	 */
	public static Element fileToElement(File file) throws JDOMException {
		return fileToDocument(file).getRootElement();
	}

	/**
	 * 查询文件所有节点.
	 * 
	 * @param file
	 * @return List
	 * @throws JDOMException
	 */
	public static List fileToElementList(File file) throws JDOMException {
		return fileToElement(file).getChildren();
	}

	/**
	 * 自定义Xpath查询表达式，查询指定的文件并简单封装为List of Map，并返回.
	 * 
	 * @deprecated
	 * @param file
	 *            需要查询的文件路径
	 * @param express
	 *            xpath查询表达式
	 * @return List
	 * @throws JDOMException
	 */
	public static List<Map<String, String>> queryToMapList(File file,
			String express) throws JDOMException {
		return elementToMap(queryToElementList(file, express));
	}

	/**
	 * 自定义Xpath查询表达式，查询指定的文件并简单封装为List of Map，并返回.
	 * 
	 * @deprecated
	 * @param file
	 *            需要查询的文件路径
	 * @param express
	 *            xpath查询表达式
	 * @return List
	 * @throws JDOMException
	 */
	public static List<Map<String, String>> queryAttributeToMapList(File file,
			String express) throws JDOMException {
		return elementAttributeToMap(true, queryToElementList(file, express));
	}

	/**
	 * 将节点封装成为List of Map.不推荐使用.(不支持节点属性,只支持节点)
	 * 
	 * @deprecated
	 * @param elementList
	 * @return List
	 */
	public static List<Map<String, String>> elementToMap(List elementList) {
		return elementAttributeToMap(false, elementList);
	}

	/**
	 * 将当前xml节点数据读取以map形式返回
	 * 
	 * @param readAttribute
	 *            返回的查询所得的数据时是否返回节点属性值(标签属性)
	 * @param element
	 * @return Map
	 */
	private static Map toMap(boolean readAttribute, Element element) {
		int j = 0;
		Map<String, String> map = new HashMap<String, String>();
		// 读取所有属性
		List<Attribute> as = element.getAttributes();
		for (j = 0; readAttribute && as != null && j < as.size(); j++) {
			Attribute _as = as.get(j);
			map.put(_as.getName(), element.getAttributeValue(_as.getName()));
		}
		// 读取所有子节点
		List listElement = element.getChildren();
		for (j = 0; listElement != null && j < listElement.size(); j++) {
			Element element2 = (Element) listElement.get(j);
			map.put(element2.getName(), element2.getText());
		}
		return map;
	}

	/**
	 * xml查询到的所有子节点中的属性返回(List of map)
	 * 
	 * @param readAttribute
	 *            是否返回节点属性值
	 * @param elementList
	 * @return List
	 */
	public static List<Map<String, String>> elementAttributeToMap(
			boolean readAttribute, List elementList) {
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		for (int i = 0; list != null && i < elementList.size(); i++) {
			Element element = (Element) elementList.get(i);
			list.add(toMap(readAttribute, element));
		}
		return list;
	}

	/**
	 * 将Document对象保存为一个xml文件
	 * 
	 * <pre>
	 * Document doc = new Document();
	 * Element e = new Element(&quot;r&quot;);
	 * doc.setRootDocument(e);
	 * XML.documentToFile(doc, new File(&quot;c://abc/a.xml&quot;));
	 * </pre>
	 * 
	 * @param document
	 *            对象
	 * @param saveFile
	 *            文件保存地址
	 * @return boolean
	 */
	public static boolean documentToFile(Document document, File saveFile) {
		return documentToFile(document, saveFile, ENCODE);
	}

	/**
	 * 将Document对象保存为一个xml文件
	 * 
	 * <pre>
	 * Document doc = new Document();
	 * Element e = new Element(&quot;r&quot;);
	 * doc.setRootDocument(e);
	 * XML.documentToFile(doc, new File(&quot;c://abc/a.xml&quot;));
	 * </pre>
	 * 
	 * @param document
	 *            对象
	 * @param saveFile
	 *            文件保存地址
	 * @param encode
	 *            文件编码格式(如"utf-8","gbk","gb2312","big-5")
	 * @return boolean
	 */
	public static boolean documentToFile(Document document, File saveFile,
			String encode) {
		try {
			if (!saveFile.getParentFile().exists()) {
				forceMkdir(saveFile.getParentFile());
			}
			FileOutputStream out1 = new FileOutputStream(saveFile);
			Format format = Format.getPrettyFormat();
			format.setEncoding(encode);
			format.setIndent("\t");
			XMLOutputter outputter = new XMLOutputter(format);
			outputter.output(document, out1);
			out1.flush();
			out1.close();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;

	}
	public static void forceMkdir(File directory) throws IOException {
        if (directory.exists()) {
            if (!directory.isDirectory()) {
                String message = "File " + directory + " exists and is "
                        + "not a directory. Unable to create directory.";
                throw new IOException(message);
            }
        }
        else {
            if (!directory.mkdirs()) {
                // Double-check that some other thread or process hasn't made
                // the directory in the background
                if (!directory.isDirectory()) {
                    String message = "Unable to create directory " + directory;
                    throw new IOException(message);
                }
            }
        }
    }
	/**
	 * 将表达式查出的节点全部删除，并保存。
	 * 
	 * <pre>
	 * XML.remove(new File(&quot;c://abc/a.xml&quot;), &quot;/xml&quot;);
	 * </pre>
	 * 
	 * </pre>
	 * 
	 * @param file
	 *            XML文件路径
	 * @param express
	 *            Xpath查询表达式
	 * @return boolean
	 * @throws JDOMException
	 */
	public static boolean remove(File file, String express)
			throws JDOMException {
		List eList = queryToElementList(file, express);
		for (int i = 0; eList != null && i < eList.size(); i++) {
			Element element = (Element) eList.get(i);
			element.removeContent();
		}
		return documentToFile(document, file);
	}
}
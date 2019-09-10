package core;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import servlet.Servlet;

/**
 * 服务器相关配置信息定义
 * @author Administrator
 */
public class ServerContext {
	/**
	 * 请求与对应Servler的关系
	 * key:请求路径
	 * value:处理该请求的Servlet实例
	 */
	private static Map<String,Servlet> servletMapping = new HashMap<>();

	static {
		initServlet();
	}
	/*
	 * 初始化Servlet
	 */
	@SuppressWarnings("deprecation")
	private static void initServlet() {

		//		servletMapping.put("/myweb/reg", new RegServlet());
		//		servletMapping.put("/myweb/login", new LoginServlet());
		//		servletMapping.put("/myweb/update", new UpdateServet());
		/**
		 * 解析conf/servlets.xml文件
		 * 将根标签下所有的<servlet>标签解析出来
		 * 并用其属性path的值作为Key,
		 * className属性的值使用反射方式加载对应的Servlet类并进行
		 * 实例化，然后将对应的实例作为value
		 * 保存到servletMapping这个Map中。
		 *
		 */
		//创建
		try {
			SAXReader reader = new SAXReader();//1创建一个实例化对象
			Element root = reader.read(new File("conf/servlets.xml")).getRootElement();
			List<Element> list = root.elements();//获取当前标签下的所有子标签

			for (Element servletEle : list) {//遍历
				String key = servletEle.attributeValue("path");
				String className = servletEle.attributeValue("className");
				@SuppressWarnings("rawtypes")
				Class cls = Class.forName(className);//通过Class的静态方法forName得到要反射的类名
				Servlet servlet=(Servlet)cls.newInstance();//快速实例化，必须无惨构造
				servletMapping.put(key, servlet);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 根据请求路径获取对应的Servlet实例
	 * @param path
	 * @return
	 */
	public static Servlet getServlet(String path) {
		return servletMapping.get(path);
	}
}
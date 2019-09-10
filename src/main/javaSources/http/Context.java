package http;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Element;
import org.dom4j.io.SAXReader;

public class Context {//HTTP协议规定的相关内容
	/**
	 * 资源后缀名与Content-Type响应头对应值的映射关系
	 * key:资源后缀名
	 * value:Content-Type对应的值
	 */
	private static Map<String,String> mimeMapping = new HashMap<>();

	public static final int CR = 13;//回车符CR，ASC编码对应值：13
	public static final int LF = 10;//换行符LF，ASC编码对应值：10

	static {//初始化所有静态资源
		initmimeMapping();
	}

	private static void initmimeMapping() {//静态方法
		try {
			SAXReader reader = new SAXReader();//1创建一个实例化对象
			Element root = reader.read(new File("conf/web.xml")).getRootElement();

			@SuppressWarnings("unchecked")
			List<Element>list =root.elements("mime-mapping");//获取当前标签下的所有子标签
			System.out.println(list.size());
			for(Element empEle:list) {//遍历
				//<extension>中间的文本作为key;
				//<mime-type>中间的文本作为value;
				String key = empEle.element("extension").getTextTrim();
				String value = empEle.element("mime-type").getTextTrim();
				mimeMapping.put(key, value);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		System.out.println(mimeMapping.size());
	}
	/**
	 * 根据给定的资源后缀名获取对应的Content-Type值
	 * @param ext
	 * @return
	 */
	public static String getMimeType(String ext) {
		return mimeMapping.get(ext);
	}
}

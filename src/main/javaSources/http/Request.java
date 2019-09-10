package http;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class Request {//解析请求类
	//该类的每一个实例用于表示浏览器发送过来的一个请求内容,每个请求由三部分构成(请求行,消息头,消息正文)
	private String method;//请求方法
	private String url;//网址
	private String protocol;//使用的协议
	private String requestURL;//保存url中?左侧的请求部分
	private String queryString;//保存url中?右侧的参数部分，查询的地址
	private InputStream input;//声明一个字节输入流的实例
	private Map<String,String> headers = new HashMap<>();//创建一个集合来保存接受的数据
	private Map<String,String> parameters = new HashMap<>();//保存解析出来的每一个参数

	private byte[] data;//消息正文的数据

	public Request(Socket socket) throws EmptyRequestException{//构造本类的方法
		try {
			this.input = socket.getInputStream();//获取接受的接口输入流信息
			//解析三步走，功能方法细分
			parseRequestLine();//解析请求行
			parseHeaders();//解析消息头
			parseContent();//解析消息正文
		} catch (EmptyRequestException e){
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	private String readLine() throws IOException {//创建一个超类用来读取输入流
		//单线程操作大量数据使用builder
		StringBuilder builder = new StringBuilder();
		int c1=-1,c2=-1;
		while((c2=input.read())!=-1) {
			if(c1==Context.CR&&c2==Context.LF) {//13:CR回车  10:LF换行符
				break;//如果是换行符直接跳出本次循环
			}
			builder.append((char)c2);
			//本方法作用将指定的字符串追加到此字符序列,拼接连接
			c1 = c2;
		}
		return builder.toString().trim();//返回读取的数据
		//当读完一行后正好是CRLF,跳出循环
	}


	private void parseRequestLine() throws EmptyRequestException {//解析请求行
		try {
			String line = readLine();//接受保存超类返回的String字符串
			if("".equals(line)) {//如果是一个空的请求则创建一个异常
				throw new EmptyRequestException();//空请求异常
			}
			String[] data = line.split("\\s");
			//匹配到空白字符，空格，自动拆分到数组之中
			this.method = data[0];//请求的方法
			this.url = data[1];//如果是个空请求这里会出现下标越界异常
			this.protocol = data[2];//协议
			parseURL();//详细解析地址
		} catch (EmptyRequestException e) {
			throw e;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void parseURL() {//进一步详细解析url抽象路径地址
		//首先判断抽象路径部分是否含有?,有则开始细分，没有直接else
		if(url.contains("?")) {//包含则true
			String[] data = url.split("\\?");//分割？前的元素赋值给请求地址
			requestURL=data[0];
			if(data.length>1) {//数组中的长度必须大于1，否则为空则下标越界
				queryString=data[1];//余下的所有参数赋值给查询地址
				parseParameters(queryString);//调用方法简写代码
			}
		}else {//直接将url赋值给请求地址
			requestURL=url;
		}
		System.out.println("请求行解析完毕!"+requestURL);
	}

	private void parseHeaders() {//解析消息头
		try {
			String line = null;
			while (true) {
				line = readLine();//循环调用readLine方法读取每一个消息头
				if("".equals(line)) {
					//如果调用readLine方法返回的是一个空字符串，则说明本次段杜读取到了CRLF，那么就可以停止解析消息头了
					break;
				}
				String[] data = line.split(": ");//将消息头按照": "拆分，并将消息头的名字作为key
				headers.put(data[0], data[1]);//消息头的值作为value保存到属性headers这个Map中
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	private void parseContent() throws IOException {//解析消息正文
		System.out.println("开始解析消息正文");
		if("POST".equals(method.toUpperCase())) {
			//查看是否含有Content-Length
			if(headers.containsKey("Content-Length")) {
				int len = Integer.parseInt(headers.get("Content-Length"));
				data = new byte[len];
				//读取消息正文数据
				input.read(data);
				//判断消息正文类型
				String type = headers.get("Content-Type");
				if("application/x-www-form-urlencoded".equals(type)) {
					String line = new String(data,"ISO8859-1");
					parseParameters(line);
				}
			}
		}
		System.out.println("消息正文解析完毕!");
	}


	private void parseParameters(String line) {//简写代码方便调用
		//解析参数，格式为:name=value&name=value&```
		String[] data = line.split("&");//使用&分给查询地址
		for(String para:data) {//遍历
			String[] arr = para.split("=");//遍历的所有元素使用=分割
			if(arr.length>1) {
				parameters.put(arr[0], arr[1]);//写入Map集合中
			}else {
				parameters.put(arr[0], null);
			}
		}
	}

	public String getMethod() {//可以调用请求方法
		return method;
	}
	public String getUrl() {//地址
		return url;
	}
	public String getRequestURL() {//获取重新解析后地址的方法
		return requestURL;
	}
	public String getProtocol() {//使用的协议
		return protocol;
	}
	public String getQueryString() {
		return queryString;
	}
	public String getParameter(String name){//根据给定的参数名获取对应的参数值
		return this.parameters.get(name);
	}

}

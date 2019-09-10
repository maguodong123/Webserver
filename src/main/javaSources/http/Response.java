package http;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

public class Response {//响应请求类
	private String statusReason = "OK";//状态描述，默认值OK
	private int statusCode = 200;//状态代码，默认值200
	private OutputStream out;//获取输出流W
	private File entity;//响应正文的实体文件
	private byte[] data;//字节数据作为正文内容(作为响应动态数据使用)

	private Map<String,String> headers = new HashMap<>();

	public Response(Socket socket) {//初始化本类
		try {
			this.out = socket.getOutputStream();//输出流赋值
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void flush() {//将当前响应对象内容以一个标准HTTP响应格式发送给客户端
		try {
			//一个响应包含三部分：状态行，响应头，响应正文
			sendStatusLine();//1发送状态行
			sendHeaders();//2发送响应头
			sendContent();//3发送响应正文
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void sendStatusLine() throws IOException{//1发送状态行
		System.out.println("Response:开始发送状态行·····");
		String line ="HTTP/1.1"+" "+statusCode+" "+statusReason;
		println(line);//调用法法简写代码
	}

	private void sendHeaders() throws IOException{//2发送响应头
		System.out.println("Response:开始发送响应头");
		//告知浏览器响应正文的数据类型
		Set<Entry<String,String>> entrySet = headers.entrySet();
		for(Entry<String,String>header:entrySet) {//遍历
			String name = header.getKey();
			String value = header.getValue();
			String line = name+": "+value;
			println(line);//调用法法简写代码
		}
		println("");//这个空字符串的意义就是，不在用line这个方法，直接写入空的等于什么也不写，但是能能写null，否则报错
	}

	private void sendContent() throws IOException{//3发送响应正文
		//将用户请求的文件数据作为正文发送给客户端
		System.out.println("Response:开始发送正文···");
		//如果实体文件存在，则作为正文发送
		//注：响应与请求一样，可以不含有正文
		if(entity!=null) {
			try(FileInputStream fis = new FileInputStream(entity);){
				byte[] data = new byte[1024*10];
				int len =-1;
				while((len=fis.read(data))!=-1) {
					out.write(data,0,len);
				}
			}catch(IOException e) {
				throw e;
			}
		}
		if(data!=null) {//如果正文字节数组存在，则将这组字节作为响应正文发送
			out.write(data);
		}
		System.out.println("发送响应正文完毕·····");
	}


	public void setEntity(File entity) {//可以设置修改响应正文的实体文件
		this.entity = entity;
		this.data=null;//2选一，如果entity存在则data为null
		String fileName = entity.getName();//获取资源名

		//获取后缀名
		String ext = fileName.substring(fileName.lastIndexOf(".")+1);
		//添加响应头
		putHeader("Content-Type", Context.getMimeType(ext));//解析方式
		putHeader("Content-Length", entity.length()+"");//长度
	}
	public File getEntity() {//可以获取响应正文的实体文件
		return entity;
	}


	private void println(String line) throws UnsupportedEncodingException, IOException {//向客户端写出一行字符串(CRLF结尾)
		out.write(line.getBytes("ISO8859-1"));
		out.write(Context.CR);
		out.write(Context.LF);
	}

	public int getStatusCode() {
		return statusCode;
	}
	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}
	public String getStatusReason() {
		return statusReason;
	}
	public void setStatusReason(String statusReason) {
		this.statusReason = statusReason;
	}
	public void putHeader(String name,String value) {//向当前响应对象中添加一个响应头
		this.headers.put(name, value);//name  响应头   //value 响应头对应的值
	}

	public byte[] getContentData() {
		return data;
	}
	/**
	 * 设置响应正文数据
	 * @param data  该组字节会作为响应正文内容发送给客户端
	 */
	public void setContentData(byte[] data) {
		this.data = data;
		this.entity=null;//二选一

		//自动添加Content-Length头
		putHeader("Content-Length",data.length+"");
	}

}

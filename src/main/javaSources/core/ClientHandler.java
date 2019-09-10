package core;

import java.io.File;
import java.io.IOException;
import java.net.Socket;
import http.Request;
import http.Response;
import servlet.Servlet;
//客户端服务器，用来处理各项客户端发送过来的请求
public class ClientHandler implements Runnable {
	private Socket socket;//实例化一个套接字接口
	public ClientHandler(Socket socket) {//构造方法
		this.socket=socket;//接受服务器传送的接口；
	}
	public void run() {//客户端服务器线程启动
		System.out.println("客户端服务器启动");
		//第一步：准备工作
		try {
			Request request = new Request(socket);//实例化解析请求对象,传送接口
			Response response = new Response(socket);//实例化响应对象，传送接口
			//第二步处理请求
			/**
			 *1：首先通过request获取requestURI,用来得知用户请求的资源的路径
			 *2：然后从webapps目录下根据该资源路径找到对应资源
			 *3：判断该资源是否真实存在
			 *4：存在则响应该资源，若不存在则响应404
			 */
			String path = request.getRequestURL();//详细解析后的URL地址

			//判断该请求是否为请求业务处理
			Servlet servlet = ServerContext.getServlet(path);

			if(servlet!=null){//判断是否为请求业务处理
				servlet.service(request, response);
			}else {
				File file = new File("webapps"+path);//通过路径找到webapps目录下对应资源
				if(file.exists()){//判断用户请求的资源是否真实存在,如果存在的话
					//将要响应的资源设置到response的entity属性上
					//将该资源以标准的HTTP响应格式发送给客户端
					response.setEntity(file);
				}else{//如果不存在的话
					response.setStatusCode(404);//设置状态代码和描述
					response.setStatusReason("NOT FOUND");
					//设置响应正文为404页面
					response.setEntity(new File("webapps/root/404.html"));
				}
			}
			response.flush();//3 发送响应,标准的响应格式
			System.out.println("处理完毕!");
		}catch (http.EmptyRequestException e){
			System.out.println("警告-空的请求！");
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			//处理完毕后与客户端断开连接
			try {
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}

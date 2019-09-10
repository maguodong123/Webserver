package servlet;

import java.io.RandomAccessFile;
import http.Request;
import http.Response;

public class LoginServlet extends Servlet{
	public void service(Request request,Response response){
		System.out.println("LoginServlet:开始登录...");
		//获取用户登录信息
		String username = request.getParameter("username");
		String password = request.getParameter("password");

		try (RandomAccessFile raf
					 = new RandomAccessFile("user.dat","r");){
			for(int i=0;i<raf.length()/100;i++){
				raf.seek(i*100);
				byte[] data = new byte[32];
				raf.read(data);
				String name = new String(data,"UTF-8").trim();
				if(name.equals(username)){
					raf.read(data);
					String pwd = new String(data,"UTF-8").trim();
					if(pwd.equals(password)){
						//跳转登录成功页面
						forward("/myweb/login_success.html", request, response);
						return;
					}
					break;
				}
			}
			//跳转登录失败页面
			forward("/myweb/login_fail.html", request, response);
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("LoginServlet:登录完毕!");
	}
}

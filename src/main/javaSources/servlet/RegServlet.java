package servlet;

import java.io.RandomAccessFile;
import java.util.Arrays;

import http.Request;
import http.Response;

public class RegServlet extends Servlet{//处理注册业务类

	public void service(Request request,Response response){
		System.out.println("开始处理注册业务");
		/*
		 * 1:通过request获取用户在页面上输入的注册信息
		 * 2:将该用户的注册信息写入到文件user.dat中
		 * 3:设置response响应注册结果页面
		 */
		String username = request.getParameter("username");
		String password = request.getParameter("password");
		String nickname = request.getParameter("nickname");
		int age = Integer.parseInt(request.getParameter("age"));

		try(RandomAccessFile raf
					= new RandomAccessFile("user.dat","rw");){
			/*
			 * 首先读取user.dat文件中的每个用户信息
			 * 比对用户名与当前注册用户的名字是否一致，
			 * 若一致则说明该用户名已经被使用，这时设置
			 * response响应用户提示页面:该用户已存在
			 * 否则执行下面原有的注册操作。
			 *
			 * 用户提示页面:reg_fail.html
			 */
			for(int i=0;i<raf.length()/100;i++){
				raf.seek(i*100);
				byte[] data = new byte[32];
				raf.read(data);
				String name = new String(data,"UTF-8").trim();
				if(name.equals(username)){
					//已注册用户
					forward("/myweb/reg_fail.html", request, response);
					return;
				}
			}
			//现将指针移动到文件末尾以便追加一条新记录
			raf.seek(raf.length());

			//写用户名
			byte[] data = username.getBytes("UTF-8");
			data = Arrays.copyOf(data, 32);//扩容到32字节
			//将32字节一次性写出
			raf.write(data);

			//写密码
			data = password.getBytes("UTF-8");
			data = Arrays.copyOf(data, 32);
			raf.write(data);

			//写昵称
			data = nickname.getBytes("UTF-8");
			data = Arrays.copyOf(data, 32);
			raf.write(data);

			//写年龄
			raf.writeInt(age);
		} catch (Exception e) {
			e.printStackTrace();
		}
		//3
		forward("/myweb/reg_success.html", request, response);
		System.out.println("RegServlet:处理注册业务完毕!");
	}
}

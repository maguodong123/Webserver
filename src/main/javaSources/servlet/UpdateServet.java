package servlet;

import java.io.RandomAccessFile;
import java.util.Arrays;
import http.Request;
import http.Response;

public class UpdateServet extends Servlet{//用于修改密码
	public void service(Request request,Response response){
		System.out.println("开始处理修改密码操作!");
		String username = request.getParameter("username");//用户名
		String password = request.getParameter("password");//原来的密码
		String pass = request.getParameter("pass");//新的密码
		//进入修改工作
		try (RandomAccessFile raf
					 = new RandomAccessFile("user.dat","rw");){//用于读写文件操作
			for(int i=0;i<raf.length()/100;i++){//遍历文件,一次读一行（一行为100个字节）
				raf.seek(i*100);//挪到开始位置
				byte[] data = new byte[32];//创建一个32位的byte数组
				raf.read(data);//首先读取user.dat文件中的每个用户信息
				String name = new String(data,"UTF-8").trim();
				if(name.equals(username)){//找到此用户,对比是否存在
					raf.read(data);
					String pwd = new String(data,"UTF-8").trim();
					if(pwd.equals(password)){//原密码与输入的密码相同，可以修改

						raf.seek(i*100+32);//指针移动到密码位置
						data = pass.getBytes("UTF-8");
						data = Arrays.copyOf(data, 32);
						raf.write(data);
						//跳转登录成功页面
						forward("/myweb/update_success.html", request, response);
						return;
					}
					break;
				}
			}
			//跳转登录失败页面
			forward("/myweb/update_fail.html", request, response);
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("修改密码成功!");
	}
}

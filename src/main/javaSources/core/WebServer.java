package core;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class WebServer {//网络服务器,主程序开启
	private ServerSocket server;//创建绑定到特定端口的服务器套接字。
	private ExecutorService threadPoll;//线程池
	public static void main(String[] args) {
		WebServer server = new WebServer();//创建一个服务器对象
		server.start();//对象启动启动服务器
	}
	public WebServer() {//初始化服务器，构造函数
		System.out.println("服务器正在重置中····");
		try {
			server = new ServerSocket(8888);//创建一个服务端口
			threadPoll = Executors.newFixedThreadPool(50);//懒汉模式，用一个创建一个，创建一个线程池最多50个线程
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("服务器已开机!");
	}
	public void start() {//启动服务器的方法
		try {
			while (true) {//死循环，让服务器不会停止
				Socket socket = server.accept();//侦听并接受到此套接字的连接。
				//创建一个客户端处理服务器的对象，并且把侦听到的套接字传送过去
				ClientHandler handler = new ClientHandler(socket);
				//创建一个线程用来运行客户端服务器
//				new Thread(handler).start();
				threadPoll.execute(handler);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("一个客户端连接了");
	}
}

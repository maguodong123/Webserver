package servlet;
import java.io.File;
import http.Request;
import http.Response;
public abstract class Servlet {//创造一个抽象方法
	public abstract void service(Request request,Response response);

	public void forward(String path,Request request,Response response) {
		response.setEntity(new File("./webapps"+path));
		//跳转指定路径(TomCat的该方法实际上是通过request获取的转发器:dispatcher对应的方法)

	}
}

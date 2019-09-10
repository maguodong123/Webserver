package servlet;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Random;
import javax.imageio.ImageIO;

import http.Request;
import http.Response;

/**
 * 随机验证码
 * @author Administrator
 */
public class RandomImageServlet extends Servlet {
	private static char[] chars =  "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789".toCharArray();

	private static int IMAGE_WIDTH=70;//宽度
	private static int IMAGE_HEIGHT=30;//高度

	public void service(Request request, Response response) {
		System.out.print("RandomImageServlet:开始生成随机验证码图片····");
		BufferedImage image = new BufferedImage(IMAGE_WIDTH,IMAGE_HEIGHT, BufferedImage.TYPE_INT_RGB);//缓冲图片

		Graphics g = image.getGraphics();//获取对这张图的画笔,用于往这张图上画内容

		Color color = new Color(200,200,255);//画笔的颜色,三原色

		g.setColor(color);//设置上画笔的颜色

		g.fillRect(0, 0,IMAGE_WIDTH, IMAGE_HEIGHT);//使用画笔填充一个矩形，颜色为当前画笔指定颜色,背景颜色

		Random random = new Random();//随机生成

		g.setFont(new Font("楷体",Font.ITALIC,35));//设置使用什么字体,字体风格，字号大小

		for (int i = 0; i <=5; i++) {//写i个字符
			g.setColor(new Color(random.nextInt(256), random.nextInt(256), random.nextInt(256)));
			g.drawString(chars[random.nextInt(chars.length)] + "", 10*i+3, 28);//画，写上字符
		}

		for(int i=0;i<7;i++) {//随机画i条线
			g.setColor(new Color(random.nextInt(256), random.nextInt(256), random.nextInt(256)));
			g.drawLine(random.nextInt(IMAGE_WIDTH), random.nextInt(IMAGE_HEIGHT), random.nextInt(IMAGE_WIDTH),random.nextInt(IMAGE_HEIGHT));//画线,xy,xy两个点的坐标1——2；
		}
		try {
			/**
			 * ByteArrayOutputStream写入字节数组流,是一个低级流
			 * 通过这个流写出的字节会保存到它内部的一个字节数组中。
			 */
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			ImageIO.write(image, "jpg",out);//写到内部字节数组中
			byte[] data = out.toByteArray();//通过输出流的内容，转成一个字节数组
			//获取输出流里已经写出来的字节(图片的所有字节上)
			response.setContentData(data);//将图片数据作为正文设置到响应对象中去
			response.putHeader("Content-Type", "image/jpeg");//自己设置响应头
		} catch (FileNotFoundException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		} catch (IOException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}//写出来,图片格式jpg,输出流：

		System.out.println("图片生成完毕!");
	}
}

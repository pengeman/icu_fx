
package org.peng.icu.videorecorder;

import java.awt.Robot;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import javax.imageio.ImageIO;

public class ClientPicture {

    public static void main(String[] args) throws Exception {
        Robot robot = new Robot();
//1.连接诶服务器 
        Socket s = new Socket("127.0.0.1", 9999);
        System.out.println("已连接到服务器9999端口，准备传送图片...");
//获取截屏字节流 
        //FileInputStream fis = new FileInputStream("/tmp/a.jpg");
        
        BufferedImage image = null;
        image = CaptureScreen.getImage();
        
        
        
        
//获取输出流 
//OutputStream out = s.getOutputStream(); 
        BufferedOutputStream bos = new BufferedOutputStream(s.getOutputStream()); 
        ImageIO.write(image, "png", s.getOutputStream());
        
//2.往输出流里面投放数据 
        
//通知服务端，数据发送完毕 
        s.shutdownOutput();
//3.获取输出流，接受服务器传送过来的消息“上传成功” 
//InputStream in = s.getInputStream(); 
        BufferedInputStream bis = new BufferedInputStream(s.getInputStream());
        byte[] bufIn = new byte[1024];
        int num = bis.read(bufIn);
        System.out.println(new String(bufIn, 0, num));
//关闭资源 
        
        bos.close();
        bis.close();
        s.close();
    }
}

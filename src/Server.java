import java.io.*;
import java.util.*;
import java.net.*;
public class Server extends Thread{
	private ServerSocket serverSocket;
	public Server(int port)throws IOException{
		serverSocket = new ServerSocket(port);
	}
	
	//从报文获取文件相对路径
	protected String getUrl(String str) {
		String url=str.substring(5, str.indexOf("HTTP") - 1);
		if(url.equals(""))
			 url = "index.html";
		return url;
	}
	
	//获取文件类型
	protected String getContentType(String URI) {
        String contentType;
        if (URI.indexOf("html") != -1 || URI.indexOf("htm") != -1)
            contentType = "text/html";
        else if (URI.indexOf("jpg") != -1 || URI.indexOf("jpeg") != -1)
            contentType = "image/jpeg";
        else if (URI.indexOf("png") != -1)
            contentType = "image/png";
        else if (URI.indexOf("gif") != -1)
            contentType = "image/gif";
        else if (URI.indexOf("css") != -1)
            contentType = "text/css";
        else if (URI.indexOf("js") != -1)
            contentType = "application/javascript";
        else
            contentType = "application/octet-stream";
        return contentType;
    }
	
	//返回成功报文
	protected String successResponseHead(String url,String Type,long len) {
		String FirstLine = "HTTP/1.1 200 OK\r\n";
		String responsetype = "Content-Type:" + Type + "\r\n";
		String responsedate="Date:"+new Date()+ "\r\n";
		String responselen="Content-Length:" + len+"\r\n";
        return FirstLine + responsetype+responsedate+responselen+"\r\n";
	}
	
	public void run() {
		while(true) {
			try {
				//日志文件
				File logt=new File("log/log.txt");
				 FileOutputStream log=new FileOutputStream(logt,true);
				 OutputStreamWriter logwriter=new OutputStreamWriter(log,"UTF-8");
				 
				 String base_url="www/";
		         Socket s = serverSocket.accept();  
		          InputStream request = s.getInputStream();   
		          BufferedReader reader = new BufferedReader(new InputStreamReader(request));  
		           String line=reader.readLine();     
		           String url = getUrl(line); //获得路径
		           String path=base_url+url;
		           File file = new File(path);
		            if(file.exists()){
		            	   InputStream out = new FileInputStream(file);//读文件
				           OutputStream outputStream = s.getOutputStream(); //写文件
				           PrintStream writer = new PrintStream(outputStream);
			                String type=getContentType(url);
			                //返回报文
			                outputStream.write(successResponseHead(url,type,file.length()).getBytes());
				           byte[] buffer = new byte[4 * 1024];  
				           int len = 0;   
				           while ((len = out.read(buffer)) != -1) {   
				                   outputStream.write(buffer, 0, len);   
				           }   
				           outputStream.flush();
				           System.out.println("IP:"+s.getRemoteSocketAddress()+"获取"+url+"成功");
				           //log文件
				           logwriter.append("time:"+(new Date()).toString()+"IP:"+s.getRemoteSocketAddress()+"获取"+url+"成功");
				           logwriter.append("\r\n");
				           logwriter.close();
				           
				           s.close();
		            }else {
				           OutputStream outputStream = s.getOutputStream(); 
				           PrintStream writer = new PrintStream(outputStream);
			                writer.println("HTTP/1.1 404 Not Found");// 返回应答消息,并结束应答
			                writer.println();//空行 
				           outputStream.flush();
		            	System.out.println("IP:"+s.getRemoteSocketAddress()+"获取"+"文件"+url+"不存在");
		            	logwriter.append("time:"+(new Date()).toString()+"IP:"+s.getRemoteSocketAddress()+"获取"+"文件"+url+"不存在");
				           logwriter.append("\r\n");
				           logwriter.close();
		                s.close();
		            }
		     }catch (IOException e) {
	         e.printStackTrace();
	            break;
	      }
		}
	}
	public static void main(String[] args) {
		int port = 8888;
	      try
	      {
	         Thread t = new Server(port);
	         System.out.println("服务器运行中....");
	         t.run();
	         
	      }catch(IOException e)
	      {
	         e.printStackTrace();
	      }     
	}
}

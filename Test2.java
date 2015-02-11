import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStream;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;


public class Test {
	public static class Result{
		public Boolean TC1;
		public Boolean TC2;
		public Boolean TC3;
	}
	
	public static class Infor {
		public String username;
		public String password;
		public String hostAddress;
	}
	
	//Find subString in String
	public static boolean contains( String haystack, String needle ) {
		  haystack = haystack == null ? "" : haystack;
		  needle = needle == null ? "" : needle;
		  return haystack.toLowerCase().contains( needle.toLowerCase() );
	}
	
	//Parsing xml file and store to struct Infor
	public static Infor ParseXmlFile(String xmlFile){
		Infor infor = new Infor();
		try {
			File fXmlFile = new File(xmlFile);
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(fXmlFile);
			doc.getDocumentElement().normalize();
			Element eTarget = (Element) doc.getElementsByTagName("target").item(0);
			infor.username = eTarget.getElementsByTagName("user").item(0).getTextContent();
			infor.password = eTarget.getElementsByTagName("pwd").item(0).getTextContent();
			Element eIpAddress = (Element) eTarget.getElementsByTagName("ipAddress").item(0);
			Element eCtrl = (Element) eIpAddress.getElementsByTagName("ctrl").item(0);
			infor.hostAddress = eCtrl.getElementsByTagName("ctrl1").item(0).getTextContent();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return infor;
	}
	
	//TestCase1: Reboot node PL-3
	public static boolean TestCase1(String username, String passwd, String host){
		System.out.println("TestCase1: Reboot node PL-3");
		
		boolean res = false;
        String command1 = "cluster reboot -n 3";
        
        try{
             
            java.util.Properties config = new java.util.Properties(); 
            config.put("StrictHostKeyChecking", "no");
            JSch jsch = new JSch();
            Session session=jsch.getSession(username, host, 22);
            session.setPassword(passwd);
            session.setConfig(config);
            session.connect();
            System.out.println("Connected");
            Channel channel=session.openChannel("exec");
            ((ChannelExec)channel).setCommand(command1);
            channel.setInputStream(null);
            ((ChannelExec)channel).setErrStream(System.err);
            InputStream in=channel.getInputStream();
            channel.connect();
            byte[] tmp=new byte[1024];
            while(true){
              while(in.available()>0){
                int i=in.read(tmp, 0, 1024);
                if(i<0)break;
                System.out.print(new String(tmp, 0, i));
              }
              if(channel.isClosed()){
                System.out.println("exit-status: "+channel.getExitStatus());
                break;
              }
              try{Thread.sleep(1000);}catch(Exception ee){}
              if(channel.getExitStatus() == 0){
            	  System.out.println("TC1: PASSED");
            	  res = true;
              }
              else{
            	  System.out.println("TC1: FAILED");
            	  res = false;
              }
            }
            channel.disconnect();
            session.disconnect();       
        }catch(Exception e){
            e.printStackTrace();
        }
		return res;
	}
	
	//TestCase2:Copy a file to the server
	public static boolean TestCase2(String username, String passwd, String host, String pathFile, String workingDir){
		System.out.println("TestCase2: Copy a file to the server");
		boolean res = false;
        String SFTPHOST = host;
        int    SFTPPORT = 22;
        String SFTPUSER = username;
        String SFTPPASS = passwd;
        String SFTPWORKINGDIR = workingDir; // /home/java
         
        Session     session     = null;
        Channel     channel     = null;
        ChannelSftp channelSftp = null;
         
        try{
	        JSch jsch = new JSch();
	        session = jsch.getSession(SFTPUSER,SFTPHOST,SFTPPORT);
	        session.setPassword(SFTPPASS);
	        java.util.Properties config = new java.util.Properties();
	        config.put("StrictHostKeyChecking", "no");
	        session.setConfig(config);
	        session.connect();
	        channel = session.openChannel("sftp");
	        channel.connect();
	        channelSftp = (ChannelSftp)channel;
	        channelSftp.cd(SFTPWORKINGDIR);
	        File f = new File("D:/CoreMW/java/Jtest-master/result.html");
	        channelSftp.put(new FileInputStream(f), f.getName());
	        res = true;
	        System.out.println("TC2: PASSED");
        }catch(Exception ex){
        	res = false;
        	System.out.println("TC2: FAILED");
        ex.printStackTrace();
        }
        return res;
	}
	
    //TestCase3: Get every WARN log messages	
	public static boolean TestCase3(String username, String passwd, String host){
		System.out.println("TestCase3: Get every WARN log messages");
		
		boolean res = false;
        String command = "cat /var/log/messages";
        try{
            java.util.Properties config = new java.util.Properties(); 
            config.put("StrictHostKeyChecking", "no");
            JSch jsch = new JSch();
            Session session = jsch.getSession(username, host, 22);
            session.setPassword(passwd);
            session.setConfig(config);
            session.connect();
            System.out.println("Connected");
            Channel channel = session.openChannel("exec");
            ((ChannelExec)channel).setCommand(command);
            channel.setInputStream(null);
            ((ChannelExec)channel).setErrStream(System.err);
            InputStream in=channel.getInputStream();
            channel.connect();
            byte[] tmp=new byte[1024];
            int countWARN = 0;
            while(true){
              while(in.available()>0){
                int i=in.read(tmp, 0, 1024);
                if(i<0)break;
                String temp = new String(tmp,0,i);
                System.out.println(temp);
                if(contains(temp,"WARN")){
                	countWARN++;
                }
              }
              if(channel.isClosed()){
                System.out.println("exit-status: "+channel.getExitStatus());
                break;
              }
              try{Thread.sleep(1000);}catch(Exception ee){}
            }
            System.out.println(countWARN);
            if(countWARN > 2){
            	res = false;
            	System.out.println("TC3: FAILED");
            }
            else{
            	res = true;
            	System.out.println("TC3: PASSED");
            }
            channel.disconnect();
            session.disconnect();       
        }catch(Exception e){
            e.printStackTrace();
        }
        return res;
	}

	//Write the result into an html file
	public static void WriteHtmlFile(boolean res1, boolean res2, boolean res3){
		FileWriter fWriter = null;
        BufferedWriter writer = null;
        try {
            //Creating a new FileWriter object with the file location
            fWriter = new FileWriter("D:/CoreMW/java/Jtest-master/result.html");
            //creating a buffered writer for the file object
            writer = new BufferedWriter(fWriter);
            //Adding the initial HTML tags
            writer.write("<html><head><title>Jtest</title><h1> TEST RESULT </h1></head><body>");
            //Adding the data to be displayed in body 
            writer.write("<div class='testId' >Use Case 1</div>");
            if(res1 == true){
            	writer.write("<div class='result'>passed</div>");
            }else{
            	writer.write("<div class='result failed'>failed</div>");
            }
            writer.write("------------------------------------------------------------------------------------");
            writer.write("<div class='testId' >Use Case 2</div>");
            if(res2 == true){
            	writer.write("<div class='result'>passed</div>");
            }else{
            	writer.write("<div class='result failed'>failed</div>");
            }
            writer.write("------------------------------------------------------------------------------------");
            writer.write("<div class='testId' >Use Case 3</div>");
            if(res3 == true){
            	writer.write("<div class='result'>passed</div>");
            }else{
            	writer.write("<div class='result failed'>failed</div>");
            } 
            //Closing the tags
            writer.write("</body></html>");
            writer.write("<style type='text/css'>h1{ background: #f1c40f; width: 50%;} .testId{ background:#2ecc71; font-weight: bold; color: white; width: 50%; border-right: solid;} .result{ background: #3498db; font-weight: bold; color: white; width: 50%; margin-top: 4px;} .failed{ background: #d35400;}</style>");
            //closing the writer object
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
	}
	
	public static void main(String argv[]) {
		Result result = new Result();
		Infor infor = ParseXmlFile("D:/CoreMW/java/Jtest-master/my_target.xml");
		result.TC1 = TestCase1(infor.username, infor.password, infor.hostAddress);
		result.TC2 = TestCase2(infor.username, infor.password, infor.hostAddress, "D:/CoreMW/java/Jtest-master/result.html", "/home/java");
		result.TC3 = TestCase3(infor.username, infor.password, infor.hostAddress);
		WriteHtmlFile(result.TC1, result.TC2, result.TC3);
		
	}
}

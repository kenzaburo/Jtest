package LuanVuEx;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.swing.JOptionPane;

import com.jcraft.jsch.*;

public class Excercises {
	private Configure config;
	private int connectSC = 0;
	
	public Excercises() throws Exception
	{
		config = new Configure();
		System.out.println(config.toString());
	}
	
	/* Remote to Sc-1 node, execute cluster reboot command */
	public boolean Excercise1()
	{
		boolean rc = true;
	    try{
	        JSch jsch=new JSch();

	        if (config.getNoOfSC() == 0)
	        {
	        	return false;
	        }	       
	        
	        System.out.println("usr: " + config.getSCs().get(connectSC).getUsr() + " host: " + config.getSCs().get(connectSC).getIpAddr() + " pass: " +  config.getSCs().get(connectSC).getPwd());
	        Session session=jsch.getSession(config.getSCs().get(connectSC).getUsr(), config.getSCs().get(connectSC).getIpAddr(), 22);
	        session.setPassword(config.getSCs().get(connectSC).getPwd());
	        
	        UserInfo ui = new MyUserInfo();

	        session.setUserInfo(ui);

	        session.connect(30000);   // making a connection with timeout 30s.

	        rc = execCommand(session, "cluster reboot -a -q");
	        session.disconnect();

	      }
	      catch(Exception e){
	        System.out.println(e);
	        JOptionPane.showMessageDialog(null, e.toString(), "Connection failed", 0);
	        rc = false;
	      }

		return rc;
	}
	
	/* Copy a script file to remote host
	 * Execute that script
	 *  */
	public boolean Excercise2()
	{
		boolean rc = true;
	    try{

	      String lfile= "src/script";
	      String rfile= "script";

	      JSch jsch=new JSch();
	      
	      System.out.println("usr: " + config.getSCs().get(connectSC).getUsr() + " host: " + config.getSCs().get(connectSC).getIpAddr() + " pass: " +  config.getSCs().get(connectSC).getPwd());
	      Session session=jsch.getSession(config.getSCs().get(connectSC).getUsr(), config.getSCs().get(connectSC).getIpAddr(), 22);
	      session.setPassword(config.getSCs().get(connectSC).getPwd());

	      // username and password will be given via UserInfo interface.
	      UserInfo ui=new MyUserInfo();
	      session.setUserInfo(ui);
	      session.connect();

	      rc = execScpSendCmd(session, rfile, lfile);
	      
	      if (rc)
	      {
	    	  // Execute chmod command
	    	  String cmd = "chmod +x " + rfile;
	    	  rc = execCommand(session, cmd);
	      }
	      
	      if (rc)
	      {
	    	  // Execute script command
	    	  String cmd = "./" + rfile;
	    	  rc = execCommand(session, cmd);
	      }
	      
	      session.disconnect();

	    }
	    catch(Exception e){
	      System.out.println(e);
	      rc = false;
	    }		
		
		return rc;
	}
	
	/* Read /var/log/message file from remote node
	 * If it contains more than 1 WARN log then return false.
	 * Otherwise return true;
	 */
	public boolean Excercise3()
	{
		boolean rc = true;
		String keyWord = "WARN";
	    try{

	      String rfile= "/var/log/messages";

	      JSch jsch=new JSch();
	      
	      System.out.println("usr: " + config.getSCs().get(connectSC).getUsr() + " host: " + config.getSCs().get(connectSC).getIpAddr() + " pass: " +  config.getSCs().get(connectSC).getPwd());
	      Session session=jsch.getSession(config.getSCs().get(connectSC).getUsr(), config.getSCs().get(connectSC).getIpAddr(), 22);
	      session.setPassword(config.getSCs().get(connectSC).getPwd());

	      // username and password will be given via UserInfo interface.
	      UserInfo ui=new MyUserInfo();
	      session.setUserInfo(ui);
	      session.connect();

	      int count = countLine(session, rfile, keyWord);
	      if (count > 1)
	      {
	    	  rc = false;
	      }
	      System.out.println("Count: " + count);
//	      execCommand(session, "vi /var/log/messages");
	      
	      session.disconnect();

	    }
	    catch(Exception e){
	      System.out.println(e);
	      rc = false;
	    }		
		
		return rc;
	}
	
	static int checkAck(InputStream in) throws IOException{
	    int b=in.read();
	    // b may be 0 for success,
	    //          1 for error,
	    //          2 for fatal error,
	    //          -1
	    if(b==0) return b;
	    if(b==-1) return b;

	    if(b==1 || b==2){
	      StringBuffer sb=new StringBuffer();
	      int c;
	      do {
		c=in.read();
		sb.append((char)c);
	      }
	      while(c!='\n');
	      if(b==1){ // error
		System.out.print(sb.toString());
	      }
	      if(b==2){ // fatal error
		System.out.print(sb.toString());
	      }
	    }
	    return b;
	  }
	
	static boolean execCommand(Session session, String cmd)
	{
		boolean rc = true;
	    try{
	    	
        Channel channel=session.openChannel("exec");
        ((ChannelExec)channel).setCommand(cmd);

        channel.setInputStream(null);

        channel.setOutputStream(System.out);

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
            if(in.available()>0) continue;
            System.out.println("exit-status: "+channel.getExitStatus() +" 0 - Success");
            if (channel.getExitStatus() != 0)
            	rc = false;
            break;
          }
          try{Thread.sleep(1000);}catch(Exception ee){}
        }
        channel.disconnect();
	    }
	    catch(Exception e){
		        System.out.println(e);
		        JOptionPane.showMessageDialog(null, e.toString(), "Connection failed", 0);
		        rc = false;
		}  
	    
		return rc;
	}
	
	static boolean execScpSendCmd(Session session, String rfile, String lfile)
	{
		boolean rc = true;
		FileInputStream fis = null;
	    boolean ptimestamp = true;

	    String command="scp " + (ptimestamp ? "-p" :"") +" -t "+rfile;
	      
	    try{
	      Channel channel=session.openChannel("exec");
	      ((ChannelExec)channel).setCommand(command);

	      // get I/O streams for remote scp
	      OutputStream out=channel.getOutputStream();
	      InputStream in=channel.getInputStream();

	      channel.connect();

	      if(checkAck(in)!=0){
		       return false;
	      }

	      File _lfile = new File(lfile);

	      if(ptimestamp){
	        command="T "+(_lfile.lastModified()/1000)+" 0";
	        // The access time should be sent here,
	        // but it is not accessible with JavaAPI ;-<
	        command+=(" "+(_lfile.lastModified()/1000)+" 0\n"); 
	        out.write(command.getBytes()); out.flush();
	        if(checkAck(in)!=0){
	  	  		rc = false;
	        }
	      }

	      // send "C0644 filesize filename", where filename should not include '/'
	      long filesize=_lfile.length();
	      command="C0644 "+filesize+" ";
	      if(lfile.lastIndexOf('/')>0){
	        command+=lfile.substring(lfile.lastIndexOf('/')+1);
	      }
	      else{
	        command+=lfile;
	      }
	      command+="\n";
	      out.write(command.getBytes()); out.flush();
	      if(checkAck(in)!=0){
			rc = false;
	      }

	      // send a content of lfile
	      fis=new FileInputStream(lfile);
	      byte[] buf=new byte[1024];
	      while(true){
	        int len=fis.read(buf, 0, buf.length);
		if(len<=0) break;
	        out.write(buf, 0, len); //out.flush();
	      }
	      fis.close();
	      fis=null;
	      // send '\0'
	      buf[0]=0; out.write(buf, 0, 1); out.flush();
	      if(checkAck(in)!=0){
	    	  rc = false;
	      }
	      out.close();

	      channel.disconnect();
		 }
		 catch(Exception e){
			        System.out.println(e);
			        JOptionPane.showMessageDialog(null, e.toString(), "Connection failed", 0);
			        rc = false;
			        try{if(fis!=null)fis.close();}catch(Exception ee){}
		 } 	      
		return rc;
	}
	
	static int countLine(Session session, String rFile, String keyWord)
	{
		int count = 0;
		String tmpFile = "D:\\temp.txt";
	    try{
	    		    	
	        String command="scp -f "+ rFile;
	        Channel channel=session.openChannel("exec");
	        ((ChannelExec)channel).setCommand(command);

	        // get I/O streams for remote scp
	        OutputStream out=channel.getOutputStream();
	        InputStream in=channel.getInputStream();

	        channel.connect();

	        byte[] buf=new byte[1024];

	        // send '\0'
	        buf[0]=0; out.write(buf, 0, 1); out.flush();

	        while(true){
	  	int c=checkAck(in);
	          if(c!='C'){
	  	  break;
	  	}

	          // read '0644 '
	          in.read(buf, 0, 5);

	          long filesize=0L;
	          while(true){
	            if(in.read(buf, 0, 1)<0){
	              // error
	              break; 
	            }
	            if(buf[0]==' ')break;
	            filesize=filesize*10L+(long)(buf[0]-'0');
	          }

	          String file=null;
	          for(int i=0;;i++){
	            in.read(buf, i, 1);
	            if(buf[i]==(byte)0x0a){
	              file=new String(buf, 0, i);
	              break;
	    	  }
	          }

	          buf[0]=0; out.write(buf, 0, 1); out.flush();

	          // read a content of lfile
	          FileOutputStream fos =new FileOutputStream(tmpFile);
	          int foo;
	          while(true){
	            if(buf.length<filesize) foo=buf.length;
	  	  else foo=(int)filesize;
	            foo=in.read(buf, 0, foo);
	            if(foo<0){
	              // error 
	              break;
	            }
	            fos.write(buf, 0, foo);
	            filesize-=foo;
	            if(filesize==0L) break;
	          }
	          fos.close();
	          fos=null;
	          
	          BufferedReader bIn = new BufferedReader(new FileReader(tmpFile));
	          String line = null;
	          while ((line = bIn.readLine()) != null)
	          {
	        	  if (line.contains(keyWord))
	        		  count++;
	          }	          
	          bIn.close();

	          /* Delete temporary file */
	          File delFile = new File(tmpFile);
	          
	    	if(delFile.delete()){
	    			System.out.println(delFile.getName() + " is deleted!");
	    	}else{
	    			System.out.println("Delete operation is failed.");
	    		}
	  	if(checkAck(in)!=0){
	  	  System.exit(0);
	  	}

	          // send '\0'
	          buf[0]=0; out.write(buf, 0, 1); out.flush();
	        }
		    }
		    catch(Exception e){
			        System.out.println(e);
			        JOptionPane.showMessageDialog(null, e.toString(), "Connection failed", 0);
			}  

		return count;
	}
}

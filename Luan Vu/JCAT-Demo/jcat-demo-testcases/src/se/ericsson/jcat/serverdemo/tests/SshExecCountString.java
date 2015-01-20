package se.ericsson.jcat.serverdemo.tests;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import se.ericsson.jcat.fw.utils.Ssh2Session;
import se.ericsson.jcat.serverdemo.fw.DemoTestCase;
import se.ericsson.jcat.serverdemo.properties.PropLibrary;

public class SshExecCountString extends DemoTestCase {

	public SshExecCountString(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}

	public void testCountString()
	{
		Ssh2Session ssh;
		
		setTestcase("Count WARN string", "Count string");
		setTestStep("Connecting to: " + PropLibrary.sshHost);
		setTestInfo("Using this username: " + PropLibrary.sshUser);
		setTestInfo("Using this password: " + PropLibrary.sshPass);
		
		ssh = new Ssh2Session(PropLibrary.sshHost, PropLibrary.sshUser, PropLibrary.sshPass);
		
		boolean connected = ssh.connect();				
		saveAssertTrue("Connect success", connected);
		
        /* Delete temporary file */
        File delFile = new File(PropLibrary.sshlcpy);
        
        if(delFile.delete()){
  			System.out.println(delFile.getName() + " is deleted!");
        }else{
  			System.out.println("Delete operation is failed.");
  			}
        
		setTestStep("Copy log file to local");		
		boolean received = ssh.sftpGet(PropLibrary.sshrcpy, PropLibrary.sshlcpy);
		saveAssertTrue("get file success", received);
		
		int count = 0;
        BufferedReader bIn;
		try {
			bIn = new BufferedReader(new FileReader(PropLibrary.sshlcpy));
		
	        String line = null;
	        while ((line = bIn.readLine()) != null)
	        {
	      	  if (line.contains(PropLibrary.keyword))
	      		  count++;
	        }	          
	        bIn.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}        
		
		saveAssertTrue("Check keyword pass", count < 2);
		
        /* Delete temporary file */
        delFile = new File(PropLibrary.sshlcpy);
        
        if(delFile.delete()){
  			System.out.println(delFile.getName() + " is deleted!");
        }else{
  			System.out.println("Delete operation is failed.");
  			}
	}
}

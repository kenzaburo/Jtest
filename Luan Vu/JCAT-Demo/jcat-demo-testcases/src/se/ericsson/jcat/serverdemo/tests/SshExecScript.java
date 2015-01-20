package se.ericsson.jcat.serverdemo.tests;

import java.io.IOException;

import se.ericsson.jcat.fw.utils.Ssh2Session;
import se.ericsson.jcat.serverdemo.fw.DemoTestCase;
import se.ericsson.jcat.serverdemo.properties.PropLibrary;


public class SshExecScript extends DemoTestCase {

	public SshExecScript(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}

	public void testExecScript()
	{
		Ssh2Session ssh;
		
		setTestcase("Copy script to remote", "Copy script");
		setTestStep("Connecting to: " + PropLibrary.sshHost);
		setTestInfo("Using this username: " + PropLibrary.sshUser);
		setTestInfo("Using this password: " + PropLibrary.sshPass);
		
		ssh = new Ssh2Session(PropLibrary.sshHost, PropLibrary.sshUser, PropLibrary.sshPass);
		
		boolean connected = ssh.connect();				
		saveAssertTrue("Connect success", connected);
		setTestStep("Copy script");
		
		try {
			// remove script if it is exist
			ssh.sendCommand("rm " + PropLibrary.sshDst);
			boolean sent = ssh.sftpPut(PropLibrary.sshSrc, PropLibrary.sshDst);
			saveAssertTrue("Sent file success", sent);
			
			setTestStep("Execute script");
			String result = ssh.sendCommand("chmod +x " + PropLibrary.sshDst);
			result = ssh.sendCommand(PropLibrary.sshDst);
			setTestInfo(result);
			saveAssertTrue("Execute script success", result.contains("Luan Vu"));
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

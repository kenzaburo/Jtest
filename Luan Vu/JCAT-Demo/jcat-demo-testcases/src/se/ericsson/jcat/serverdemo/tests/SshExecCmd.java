package se.ericsson.jcat.serverdemo.tests;

import java.io.IOException;

import se.ericsson.jcat.fw.utils.Ssh2Session;
import se.ericsson.jcat.serverdemo.fw.DemoTestCase;
import se.ericsson.jcat.serverdemo.properties.PropLibrary;

public class SshExecCmd extends DemoTestCase {

	public SshExecCmd(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}

	public void testExecCmd(){
		
		Ssh2Session ssh;
		
		setTestcase("Exec reboot command", "Reboot cluster");
		setTestStep("Connecting to: " + PropLibrary.sshHost);
		setTestInfo("Using this username: " + PropLibrary.sshUser);
		setTestInfo("Using this password: " + PropLibrary.sshPass);
		
		ssh = new Ssh2Session(PropLibrary.sshHost, PropLibrary.sshUser, PropLibrary.sshPass);
		
		ssh.connect();				
		
		setTestStep("Checking the output of the command");		
		
		try {
			String result = ssh.sendCommand("cluster reboot -a -q");
			setTestInfo(result);
			saveAssertTrue("Cluster reboot success", result.contains("Rebooting"));
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

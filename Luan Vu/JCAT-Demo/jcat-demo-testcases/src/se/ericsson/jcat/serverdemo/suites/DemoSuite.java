package se.ericsson.jcat.serverdemo.suites;

import se.ericsson.jcat.serverdemo.setup.DemoTestSetup;
import se.ericsson.jcat.serverdemo.tests.SshExecCmd;
import se.ericsson.jcat.serverdemo.tests.SshExecCountString;
import se.ericsson.jcat.serverdemo.tests.SshExecScript;
import junit.framework.Test;
import junit.framework.TestSuite;

public class DemoSuite extends TestSuite {

	public DemoSuite()
	{
		super("DemoSuite");
		
		addTest(new SshExecCmd("testExecCmd"));
		addTest(new SshExecScript("testExecScript"));
		addTest(new SshExecCountString("testCountString"));
	}
	
	public static Test suite(){
		return new DemoTestSetup(new DemoSuite());
	}
}
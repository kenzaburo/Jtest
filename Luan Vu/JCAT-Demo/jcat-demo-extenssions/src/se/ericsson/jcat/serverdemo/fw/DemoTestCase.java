package se.ericsson.jcat.serverdemo.fw;

import se.ericsson.jcat.fw.NonUnitTestCase;
import se.ericsson.jcat.serverdemo.properties.PropLibrary;

public class DemoTestCase extends NonUnitTestCase{

	@Override
	protected void setUp() throws Exception{
		super.setUp();
		this.setTestStep("Reading properties file");
		PropLibrary.getInstance();
	}
	
	public DemoTestCase(String name){
		super(name);
	}
}

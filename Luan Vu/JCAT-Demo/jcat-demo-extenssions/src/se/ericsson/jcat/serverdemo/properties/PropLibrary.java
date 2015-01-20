package se.ericsson.jcat.serverdemo.properties;

import java.util.Properties;
import org.apache.log4j.Logger;
import se.ericsson.jcat.fw.utils.Cat2Utils;

public class PropLibrary {
	private static Logger logger = Logger.getLogger("PropLibrary");
	private static PropLibrary propLib = null;

	public static String sshHost = null;
	public static String sshUser = null;
	public static String sshPass = null;
	public static String sshSrc = null;
	public static String sshDst = null;
	public static String sshlcpy = null;
	public static String sshrcpy = null;
	public static String keyword = null;

	private PropLibrary() {
		Properties props = Cat2Utils.getProperties();
		logger.info("Populating the property library");

		sshHost = getProperty(props, "ssh.host");
		sshUser = getProperty(props, "ssh.user");
		sshPass = getProperty(props, "ssh.pass");
		sshSrc = getProperty(props, "ssh.srcScript");
		sshDst = getProperty(props, "ssh.dstScript");
		sshlcpy = getProperty(props, "ssh.lcpyfile");
		sshrcpy = getProperty(props, "ssh.rcpyfile");
		keyword = getProperty(props, "keyword");
	}

	private String getProperty(Properties props, String key) {
		String value = null;
		value = props.getProperty(key);
		if (value == null) {
			logger.error("Property " + "\"" + key + "\"" + " could not be found");
		}
		
		logger.info("Read properties " + "\"" + key + "\"" + " value: " + "\"" + value + "\"");
		
		return value;
		
	}

	public static PropLibrary getInstance() {
		if (propLib == null) {
			propLib = new PropLibrary();
		}
		return propLib;
	}

}

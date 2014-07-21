package ssh;

import com.jcraft.jsch.*;

import java.awt.*;

import javax.swing.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import ssh.Shell.MyUserInfo;

import java.io.*;

class MyInfo {
	String user;
	String host;
	String password;
};

public class Exec {
	static boolean status = false;
	static String result = "";

	public static void main(String[] arg) {
		try {
			JSch jsch = new JSch();
			String xmlPath = "D:\\Document\\OverviewCBA\\Jtest-master\\my_target.xml";
			String htmlPath = "D:\\Document\\OverviewCBA\\Jtest-master\\result.html";
			MyInfo myInfo = new MyInfo();
			if (parseXML(xmlPath, myInfo) != true) {
				System.out.print("Read XML error");
				return;
			}
			System.out.print("usr:" + myInfo.user + " host:" + myInfo.host
					+ " password:" + myInfo.password);
			Session session = jsch.getSession(myInfo.user, myInfo.host, 22);
			session.setPassword(myInfo.password);

			// username and password will be given via UserInfo interface.
			// UserInfo ui=new MyUserInfo();

			UserInfo ui = new MyUserInfo() {
				public void showMessage(String message) {
					JOptionPane.showMessageDialog(null, message);
				}

				public boolean promptYesNo(String message) {
					Object[] options = { "yes", "no" };
					int foo = JOptionPane.showOptionDialog(null, message,
							"Warning", JOptionPane.DEFAULT_OPTION,
							JOptionPane.WARNING_MESSAGE, null, options,
							options[0]);
					return foo == 0;
				}

				// If password is not given before the invocation of
				// Session#connect(),
				// implement also following methods,
				// * UserInfo#getPassword(),
				// * UserInfo#promptPassword(String message) and
				// * UIKeyboardInteractive#promptKeyboardInteractive()

			};
			session.setUserInfo(ui);
			session.connect();
			switch (arg[0]) {
			case "1":

				if (case1(session))
					writeHTML(htmlPath, "Use Case 1", "passed");
				else
					writeHTML(htmlPath, "Use Case 1", "failed");
				break;
			case "2":
				if (case2(session))
					writeHTML(htmlPath, "Use Case 2", "passed");
				else
					writeHTML(htmlPath, "Use Case 2", "failed");
				break;
			case "3":
				if (case3(session))
					writeHTML(htmlPath, "Use Case 3", "passed");
				else
					writeHTML(htmlPath, "Use Case 3", "failed");
				break;
			default:
				case3(session);
				break;
			}
			System.exit(0);
		} catch (Exception e) {
			System.out.println(e);
		}
	}
	// Excute command: cluster reboot -a -q;
	public static boolean case1(Session session) {
		String command = "cluster reboot -a -q";
		try {
			Channel channel = session.openChannel("exec");
			((ChannelExec) channel).setCommand(command);
			channel.connect();
			channel.setInputStream(null);

			// channel.setOutputStream(System.out);

			// FileOutputStream fos=new FileOutputStream("/tmp/stderr");
			// ((ChannelExec)channel).setErrStream(fos);
			((ChannelExec) channel).setErrStream(System.err);

			InputStream in = channel.getInputStream();

			channel.connect();

			byte[] tmp = new byte[1024];
			while (true) {
				while (in.available() > 0) {
					int i = in.read(tmp, 0, 1024);
					if (i < 0)
						break;
					System.out.print(new String(tmp, 0, i));
				}
				if (channel.isClosed()) {
					if (in.available() > 0)
						continue;
					System.out.println("exit-status: "
							+ channel.getExitStatus());
					break;
				}
				try {
					Thread.sleep(1000);
				} catch (Exception ee) {
				}
			}
			channel.disconnect();
			session.disconnect();
			return true;
		} catch (Exception e) {
			System.out.print("ERROR");
		}
		return false;
	}
	// put testfile.txt to SC-1:/root/
	public static boolean case2(Session session) {
		FileInputStream fis = null;
		try {
			String file1 = "testfile.txt";
			String file2 = "/root/";
			boolean ptimestamp = true;
			String command = "scp " + (ptimestamp ? "-p" : "") + " -t " + file2;
			Channel channel = session.openChannel("exec");
			((ChannelExec) channel).setCommand(command);

			// get I/O streams for remote scp
			OutputStream out = channel.getOutputStream();
			InputStream in = channel.getInputStream();

			channel.connect();

			if (checkAck(in) != 0) {
				System.exit(0);
			}

			File _lfile = new File(file1);

			if (ptimestamp) {
				command = "T " + (_lfile.lastModified() / 1000) + " 0";
				// The access time should be sent here,
				// but it is not accessible with JavaAPI ;-<
				command += (" " + (_lfile.lastModified() / 1000) + " 0\n");
				out.write(command.getBytes());
				out.flush();
				if (checkAck(in) != 0) {
					System.exit(0);
				}
			}

			// send "C0644 filesize filename", where filename should not include
			// '/'
			long filesize = _lfile.length();
			command = "C0644 " + filesize + " ";
			if (file1.lastIndexOf('/') > 0) {
				command += file1.substring(file1.lastIndexOf('/') + 1);
			} else {
				command += file1;
			}
			command += "\n";
			out.write(command.getBytes());
			out.flush();
			if (checkAck(in) != 0) {
				System.exit(0);
			}

			// send a content of lfile
			fis = new FileInputStream(file1);
			byte[] buf = new byte[1024];
			while (true) {
				int len = fis.read(buf, 0, buf.length);
				if (len <= 0)
					break;
				out.write(buf, 0, len); // out.flush();
			}
			fis.close();
			fis = null;
			// send '\0'
			buf[0] = 0;
			out.write(buf, 0, 1);
			out.flush();
			if (checkAck(in) != 0) {
				System.exit(0);
			}
			out.close();

			channel.disconnect();
			session.disconnect();
			return true;

		} catch (Exception e) {
			System.out.println(e);
			try {
				if (fis != null)
					fis.close();
			} catch (Exception ee) {
			}
		}
		return false;
	}

	static int checkAck(InputStream in) throws IOException {
		int b = in.read();
		// b may be 0 for success,
		// 1 for error,
		// 2 for fatal error,
		// -1
		if (b == 0)
			return b;
		if (b == -1)
			return b;

		if (b == 1 || b == 2) {
			StringBuffer sb = new StringBuffer();
			int c;
			do {
				c = in.read();
				sb.append((char) c);
			} while (c != '\n');
			if (b == 1) { // error
				System.out.print(sb.toString());
			}
			if (b == 2) { // fatal error
				System.out.print(sb.toString());
			}
		}
		return b;
	}

	public static boolean case3(Session session) {
		String command = "cat /var/log/messages";
		try {
			Channel channel = session.openChannel("exec");
			((ChannelExec) channel).setCommand(command);

			// X Forwarding
			// channel.setXForwarding(true);

			// channel.setInputStream(System.in);
			channel.setInputStream(null);

			// channel.setOutputStream(System.out);

			// FileOutputStream fos=new FileOutputStream("/tmp/stderr");
			// ((ChannelExec)channel).setErrStream(fos);
			((ChannelExec) channel).setErrStream(System.err);

			InputStream in = channel.getInputStream();
			InputStreamReader isr = new InputStreamReader(in);
			BufferedReader bfr = new BufferedReader(isr);
			channel.connect();
			int numWarn = 0;
			while (true) {
				while (bfr.ready()) {
					String line = bfr.readLine();
					if (line.contains("WARNING")) {
						result = result + line + "\n";
						numWarn++;
					}

				}

				if (channel.isClosed()) {
					// if(in.available()>0) continue;
					System.out.println("exit-status: "
							+ channel.getExitStatus());
					break;
				}

				try {
					Thread.sleep(1000);
				} catch (Exception ee) {
				}
			}
			channel.disconnect();
			session.disconnect();
			System.out.print(result);
			if (numWarn > 1)
				return false;
			else
				return true;

		} catch (Exception e) {
			System.out.println("ERROR");
		}
		return false;
	}

	public static boolean parseXML(String path, MyInfo myInfo) {
		try {

			File fXmlFile = new File(path);
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(fXmlFile);

			doc.getDocumentElement().normalize();

			// System.out.println("Root element :"
			// + doc.getDocumentElement().getNodeName());

			Element eTarget = (Element) doc.getElementsByTagName("target")
					.item(0);
			// System.out.println("noOfSCs : "
			// + eTarget.getElementsByTagName("noOfSCs").item(0)
			// .getTextContent());
			// System.out.println("user : "
			// + eTarget.getElementsByTagName("user").item(0)
			// .getTextContent());
			myInfo.user = eTarget.getElementsByTagName("user").item(0)
					.getTextContent();
			// System.out.println("pwd : "
			// + eTarget.getElementsByTagName("pwd").item(0)
			// .getTextContent());
			myInfo.password = eTarget.getElementsByTagName("pwd").item(0)
					.getTextContent();
			Element eIpAddress = (Element) eTarget.getElementsByTagName(
					"ipAddress").item(0);
			Element eCtrl = (Element) eIpAddress.getElementsByTagName("ctrl")
					.item(0);
			// System.out.println("ctrl1 : "
			// + eCtrl.getElementsByTagName("ctrl1").item(0)
			// .getTextContent());
			myInfo.host = eCtrl.getElementsByTagName("ctrl1").item(0)
					.getTextContent();
			// System.out.println("ctrl2 : "
			// + eCtrl.getElementsByTagName("ctrl2").item(0)
			// .getTextContent());
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

	}

	public static void writeHTML(String path, String useCase, String value) {
		// File f = new File(
		// "D:\\Document\\OverviewCBA\\Jtest-master\\result.html");
		File f = new File(path);
		FileInputStream fs = null;
		InputStreamReader in = null;
		BufferedReader br = null;

		StringBuffer sb = new StringBuffer();
		String textinLine;

		try {
			fs = new FileInputStream(f);
			in = new InputStreamReader(fs);
			br = new BufferedReader(in);

			while (true) {
				textinLine = br.readLine();
				if (textinLine == null)
					break;
				sb.append(textinLine);
			}
			String nUseCase = useCase;
			String sReplace = "result\">";
			String eReplace = "</div>";
			int iUseCase = sb.indexOf(nUseCase);
			int isReplace = sb.indexOf(sReplace, iUseCase);
			int ieReplace = sb.indexOf(eReplace, isReplace);
			sb.replace(isReplace + sReplace.length(), ieReplace, value);

			fs.close();
			in.close();
			br.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			FileWriter fstream = new FileWriter(f);
			BufferedWriter outobj = new BufferedWriter(fstream);
			outobj.write(sb.toString());
			outobj.close();

		} catch (Exception e) {
			System.err.println("Error: " + e.getMessage());
		}
	}

	public static class MyUserInfo implements UserInfo, UIKeyboardInteractive {
		public String getPassword() {
			return passwd;
		}

		public boolean promptYesNo(String str) {
			Object[] options = { "yes", "no" };
			int foo = JOptionPane.showOptionDialog(null, str, "Warning",
					JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE,
					null, options, options[0]);
			return foo == 0;
		}

		String passwd;
		JTextField passwordField = (JTextField) new JPasswordField(20);

		public String getPassphrase() {
			return null;
		}

		public boolean promptPassphrase(String message) {
			return true;
		}

		public boolean promptPassword(String message) {
			return true;
			// Object[] ob={passwordField};
			// int result=
			// JOptionPane.showConfirmDialog(null, ob, message,
			// JOptionPane.OK_CANCEL_OPTION);
			// if(result==JOptionPane.OK_OPTION){
			// passwd=passwordField.getText();
			// return true;
			// }
			// else{
			// return false;
			// }
		}

		public void showMessage(String message) {
			JOptionPane.showMessageDialog(null, message);
		}

		final GridBagConstraints gbc = new GridBagConstraints(0, 0, 1, 1, 1, 1,
				GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
				new Insets(0, 0, 0, 0), 0, 0);
		private Container panel;

		public String[] promptKeyboardInteractive(String destination,
				String name, String instruction, String[] prompt, boolean[] echo) {
			panel = new JPanel();
			panel.setLayout(new GridBagLayout());

			gbc.weightx = 1.0;
			gbc.gridwidth = GridBagConstraints.REMAINDER;
			gbc.gridx = 0;
			panel.add(new JLabel(instruction), gbc);
			gbc.gridy++;

			gbc.gridwidth = GridBagConstraints.RELATIVE;

			JTextField[] texts = new JTextField[prompt.length];
			for (int i = 0; i < prompt.length; i++) {
				gbc.fill = GridBagConstraints.NONE;
				gbc.gridx = 0;
				gbc.weightx = 1;
				panel.add(new JLabel(prompt[i]), gbc);

				gbc.gridx = 1;
				gbc.fill = GridBagConstraints.HORIZONTAL;
				gbc.weighty = 1;
				if (echo[i]) {
					texts[i] = new JTextField(20);
				} else {
					texts[i] = new JPasswordField(20);
				}
				panel.add(texts[i], gbc);
				gbc.gridy++;
			}

			if (JOptionPane.showConfirmDialog(null, panel, destination + ": "
					+ name, JOptionPane.OK_CANCEL_OPTION,
					JOptionPane.QUESTION_MESSAGE) == JOptionPane.OK_OPTION) {
				String[] response = new String[prompt.length];
				for (int i = 0; i < prompt.length; i++) {
					response[i] = texts[i].getText();
				}
				return response;
			} else {
				return null; // cancel
			}
		}
	}
}

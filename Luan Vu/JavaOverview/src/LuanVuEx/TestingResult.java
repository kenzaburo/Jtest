package LuanVuEx;

import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileWriter;

public class TestingResult {
	private boolean tc1 = false;
	private boolean tc2 = false;
	private boolean tc3 = false;
	
	public boolean isTc1() {
		return tc1;
	}
	public void setTc1(boolean tc1) {
		this.tc1 = tc1;
	}
	public boolean isTc2() {
		return tc2;
	}
	public void setTc2(boolean tc2) {
		this.tc2 = tc2;
	}
	public boolean isTc3() {
		return tc3;
	}
	public void setTc3(boolean tc3) {
		this.tc3 = tc3;
	}
	
	public void generateHtml()
	{
		String fileName = "LVTestingResult.html";
		String inHeader = "src/HtmlHeader";
		String inFooter = "src/HtmlFooter";
		FileInputStream fis;
		
		try{
			
			BufferedWriter br = new BufferedWriter(new FileWriter(fileName));
			
			fis = new FileInputStream(inHeader);
			
			int bufLen = 50;
			byte[] buf = new byte[bufLen];
			while(fis.available() > 0)
			{
				
				int i = fis.read(buf, 0, bufLen);
				if(i < 0)
					break;
				
				//fos.write(buf,0, buf.length);
				br.append(new String(buf).trim());
				for (int j = 0; j < bufLen; j++)
					buf[j] = 0;
			}
			fis.close();
			

			String tmp;
			tmp = " -----------------------------------------------------\n";
			
			br.append(tmp);
			tmp = " 	<div class=\"testId\" >Use Case 1</div>\n";
			br.append(tmp);
			
			if (tc1)
				tmp = " 	<div class=\"result\">[passed]</div>\n";
			else
				tmp = " 	<div class=\"result, failed\">[failed]</div>\n";
			br.append(tmp);
			
			tmp = " 	<div class=\"testId\" >Use Case 2</div>\n";
			br.append(tmp);
			
			if (tc2)
				tmp = " 	<div class=\"result\">[passed]</div>\n";
			else
				tmp = " 	<div class=\"result, failed\">[failed]</div>\n";
			br.append(tmp);		
			
			tmp = " 	<div class=\"testId\" >Use Case 3</div>\n";
			br.append(tmp);
			
			if (tc3)
				tmp = " 	<div class=\"result\">[passed]</div>\n";
			else
				tmp = " 	<div class=\"result, failed\">[failed]</div>\n";
			br.append(tmp);
			
			tmp = " -----------------------------------------------------\n";
			br.append(tmp);
			
			fis = new FileInputStream(inFooter);
			
			while(fis.available() > 0)
			{
				int i = fis.read(buf, 0, bufLen);
				if(i < 0)
					break;
				
				//fos.write(buf,0, buf.length);
				br.append(new String(buf).trim());
				for (int j = 0; j < bufLen; j++)
					buf[j] = 0;
			}
			fis.close();
			br.close();
			//fos.close();
		}catch(Exception e)
		{
			System.out.print(e);
		}
	}
	
}

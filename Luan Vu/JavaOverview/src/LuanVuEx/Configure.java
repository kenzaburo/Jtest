package LuanVuEx;

import java.util.List;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


public class Configure {
	int noOfSC = 0;	
	List<NodeInfo> SCs;
	
	public Configure() throws Exception
	{
		int scCount = 0;
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

		//Get the DOM Builder
		DocumentBuilder builder = factory.newDocumentBuilder();
	
		//Load and Parse the XML document
		//document contains the complete XML as a Tree.
		 Document document = builder.parse(ClassLoader.getSystemResourceAsStream("target.xml"));
	 
		SCs = new ArrayList<NodeInfo>();
		
		NodeList nodeList = document.getDocumentElement().getChildNodes();

		/* SCs level */
		for (int f = 0; f < nodeList.getLength(); f++)
		{
			Node node = nodeList.item(f);
			
			if (node instanceof Element) {
			       
				   this.noOfSC = Integer.parseInt(node.getAttributes().getNamedItem("noOfSC").getNodeValue());
				   if (noOfSC == 0)
				   {
					   return;
				   }
				   
				   // Hit SCx tag
			       NodeList childNodes = node.getChildNodes();
			       
			       for (int j = 0; scCount < noOfSC; j++) {
			    	   Node childNode = childNodes.item(j);
			    	   
			    	   if (childNode instanceof Element)
			    	   {			    	   
				    	   NodeList grNodes = childNode.getChildNodes();
				    	   NodeInfo SC = new NodeInfo();
				    	   // Hit ipAddr, usr, pwd elements
				    	   for (int k = 0; k < grNodes.getLength(); k++)
				    	   {
				    		   Node grNode = grNodes.item(k);
				    		   if (grNode instanceof Element)
				    		   {
					    		   String content = grNode.getLastChild().getTextContent().trim();
					    		   
					    		   if (grNode.getNodeName().equalsIgnoreCase("ipAddr"))
					    			    	 SC.setIpAddr(content);		    			        
					    		   else if (grNode.getNodeName().equalsIgnoreCase("usr"))
					    			         SC.setUsr(content);
					    		   else if (grNode.getNodeName().equalsIgnoreCase("pwd"))
					    			         SC.setPwd(content);
				    		   }		
				    	   }
				    	   SCs.add(SC);
				    	   scCount++;
			    	   }
			       }
			 }
		}
	}
	
	public int getNoOfSC() {
		return noOfSC;
	}
	public void setNoOfSC(int noOfSC) {
		this.noOfSC = noOfSC;
	}
	public List<NodeInfo> getSCs() {
		return SCs;
	}
	public void setSCs(List<NodeInfo> sCs) {
		SCs = sCs;
	}	
	
	@Override
	public	String toString()
	{
		String text = "" + noOfSC;
		for (int i = 0; i < noOfSC; i++)
			text += "\n ipAddr " + SCs.get(i).getIpAddr() + " usr " + SCs.get(i).getUsr() 
			        + " Pwd " + SCs.get(i).getPwd();
		
		return text;
	}
}

package doom;

import java.io.*;
import java.net.*;
  
public class clientSide extends Thread {
    private Socket socket = null;
    private DataOutputStream out = null;
    
	private PlayerInformation m_playerInfo = new PlayerInformation();
	private boolean running=true;
	private byte[] bytes;
	private String output;
  
    public clientSide(String address, int port)
    {
  
        try {
  
            socket = new Socket(address, port);
  
            System.out.println("Connected");
            out = new DataOutputStream(
                socket.getOutputStream());
        }
  
        catch (UnknownHostException u) {
  
            System.out.println(u);
        }
  
        catch (IOException i) {
  
            System.out.println(i);
        }
        }
    
  
    public void run()
    {
    	output="";
        while (running) {
        	  
            try {
            	try {
            	//serialize object
            	ByteArrayOutputStream bos = new ByteArrayOutputStream();
            	ObjectOutputStream out = null;
            	
            	out = new ObjectOutputStream(bos);   
            	out.writeObject(m_playerInfo);
            	out.flush();
            	bytes = bos.toByteArray();
            	output=new String(bytes);
          	  	try {
          		  bos.close();
          	  	} catch (IOException ex) {
          	  		System.out.println(ex);
          	  	}
            	}finally {}
            	//serialize object
            	
            	out.writeUTF(output);
            	
            	
            }
  
            catch (IOException i) {
  
                System.out.println(i);
            }
        }

        try {
            out.close();
            socket.close();
        }
  
        catch (IOException i) {
  
            System.out.println(i);
        }
    }
    
    public void setPlayerInfo(PlayerInformation playerInfo) {
    	m_playerInfo=playerInfo;
    }
}
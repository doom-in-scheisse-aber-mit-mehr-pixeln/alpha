package doom;

import java.io.*;
import java.net.*;
  
public class clientSide extends Thread {
    private Socket socket = null;
    private DataOutputStream out = null;
    
	private Transform m_playerTransform = new Transform();
	private boolean running=true;
	private byte[] output;
  
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
        while (running) {
        	  
            try {
            	
            	//serialize object
            	ByteArrayOutputStream bos = new ByteArrayOutputStream();
            	ObjectOutputStream out = null;
            	try {
            	  out = new ObjectOutputStream(bos);   
            	  out.writeObject(m_playerTransform);
            	  out.flush();
            	  output = bos.toByteArray();
            	  
            	  //write Player Transform
            	  out.write(output);
            	  
            	} finally {
            	  try {
            	    bos.close();
            	  } catch (IOException ex) {
            	    System.out.println(ex);
            	  }
            	}
            	//serialize object
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
    
    public void setPlayerTransform(Transform playerTransform) {
    	m_playerTransform=playerTransform;
    }
}
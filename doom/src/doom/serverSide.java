package doom;

import java.io.*;
import java.net.*;

public class serverSide extends Thread{
	private Socket socket = null;
	private ServerSocket server = null;
	private DataInputStream in = null;
	
	private int m_port;
	private boolean running=true;
	private byte[] bytes;
	private String input="";
	private Transform m_playerTransform=new Transform();


	public serverSide(int port)
	{
		try {
			System.out.println(InetAddress.getLocalHost().getHostAddress());
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		m_port=port;
	}
	public void run() {
		try {
            server = new ServerSocket(m_port);
            
            System.out.println("Server started");
  
            System.out.println("Waiting for a client ...");
  
            socket = server.accept();
  
            System.out.println("Client accepted");
  
            // takes input from the client socket
            in=new DataInputStream(new BufferedInputStream(socket.getInputStream()));
            
            input="a";
			while (running) {
				System.out.println(input);
				try {
                    input=in.readUTF();
					System.out.println(input);
					bytes=input.getBytes();
					
					
					//deseralize object from byte array
					ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
					ObjectInput d = null;
					try {
					  d = new ObjectInputStream(bis);
					  try {
						m_playerTransform = (Transform) d.readObject();
					} catch (ClassNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					} finally {
					  try {
					    if (d != null) {
					      d.close();
					    }
					  } catch (IOException ex) {
					    System.out.println(ex);
					  }
					}
					//deseralize object from byte array
					
					
				}
				catch (IOException i) {

					System.out.println(i);
				}
			}

				System.out.println("Closing connection");

				socket.close();

				in.close();
			}

			catch (IOException i) {

				System.out.println(i);
			}
		}
	
	public Transform getPlayerTransform() {
		return m_playerTransform;
	}
}

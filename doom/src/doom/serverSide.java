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
	private PlayerInformation m_playerInfo=new PlayerInformation();
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
				try {
                    input=in.readUTF();
					bytes=input.getBytes();
					
					
					//deseralize object from byte array
					ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
					ObjectInput d = null;
					try {
					  d = new ObjectInputStream(bis);
					  try {
						m_playerInfo = (PlayerInformation) d.readObject();
					} catch (ClassNotFoundException e) {
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
	
	public PlayerInformation getPlayerInfo() {
		return m_playerInfo;
	}
	public Transform getPlayerTransform() {
		m_playerTransform.SetPos(m_playerInfo.getPos());
		m_playerTransform.SetRot(m_playerInfo.getRot());
		return new Transform(m_playerInfo.getPos(), m_playerInfo.getRot());
	}
}

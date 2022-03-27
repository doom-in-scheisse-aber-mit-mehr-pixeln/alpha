package doom;

import java.io.*;
import java.net.*;

public class serverSide extends Thread{
	private Socket socket = null;
	private ServerSocket server = null;
	private DataInputStream in = null;
	
	private int m_port;
	private Transform playerTransform = new Transform();
	private boolean running=true;
	private byte[] input;

	public serverSide(int port)
	{
		m_port=port;
	}
	public void run() {

		try {
			System.out.println(InetAddress.getLocalHost().getHostAddress());
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		try {
			server = new ServerSocket(m_port);

			System.out.println("Server started");

			System.out.println("Waiting for a client ...");

			socket = server.accept();

			System.out.println("Client accepted");
			
			in = new DataInputStream(
				new BufferedInputStream(
					socket.getInputStream()));
		
			while (running) {

				try {

					input = in.readAllBytes();
					
					//deseralize object from byte array
					ByteArrayInputStream bis = new ByteArrayInputStream(input);
					ObjectInput in = null;
					try {
					  in = new ObjectInputStream(bis);
					  try {
						playerTransform = (Transform) in.readObject();
					} catch (ClassNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					} finally {
					  try {
					    if (in != null) {
					      in.close();
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
		return playerTransform;
	}
}

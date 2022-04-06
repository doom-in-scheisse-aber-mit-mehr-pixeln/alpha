package doom;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class ServerFeedback extends Thread{

    private Socket socket = null;
    private DataOutputStream out = null;
	PlayerInformation m_playerInfo=new PlayerInformation();
	clientSide client;
	
	int m_port;
	
	public ServerFeedback(int port) {
		m_port=port;
	}
	
	public void setPlayerInfos(PlayerInformation playerInfo) {
		m_playerInfo=playerInfo;
	}
	
	public void run() {
		while(m_playerInfo.getIP()=="") {
		if(m_playerInfo.getIP()!="") {
			client=new clientSide(m_playerInfo.getIP(), m_port);
			client.start();
		}
		}
		while(true) {
			client.setPlayerInfo(m_playerInfo);
		}
	}
}

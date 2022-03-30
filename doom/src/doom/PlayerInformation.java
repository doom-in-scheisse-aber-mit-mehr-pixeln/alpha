package doom;

public class PlayerInformation {
	private String m_ipAdress;
	private byte m_team;
	
	
	public PlayerInformation(String ipAdress, byte team, Vector4f pos, Quaternion rot, Vector4f velocity) {
		m_ipAdress=ipAdress;
	}

}

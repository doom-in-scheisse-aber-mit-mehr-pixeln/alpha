package doom;

import java.io.Serializable;

public class PlayerInformation implements Serializable{
	private String m_ipAdress;
	private byte m_team=0;
	private Vector4f m_pos=new Vector4f(0.0f,0.0f,0.0f,0.0f);
	private Vector4f m_velocity=new Vector4f(0.0f,0.0f,0.0f,0.0f);
	private Quaternion m_rot=new Quaternion(0.0f,0.0f,0.0f,0.0f);
	
	
	public PlayerInformation() { 
		m_ipAdress="";
	}
	
	public PlayerInformation(String ipAdress, byte team, Vector4f pos, Vector4f velocity, Quaternion rot) {
		m_ipAdress=ipAdress;
		m_team=team;
		m_pos=pos;
		m_velocity=velocity;
		m_rot=rot;
	}
	
	public String getIP() {return m_ipAdress;}
	public byte getTeam() { return m_team;}
	public Vector4f getPos() {return m_pos;}
	public Vector4f getVelocity() {return m_velocity;}
	public Quaternion getRot() {return m_rot;}
	
	public void setTeam(byte team) {m_team=team;}
	public void setPos(Vector4f pos) {m_pos=pos;}
	public void setVelocity(Vector4f velocity) {m_velocity=velocity;}
	public void setRot(Quaternion rot) {m_rot=rot;}
}

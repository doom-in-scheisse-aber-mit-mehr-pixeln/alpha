package doom;

import java.io.Serializable;

public class PlayerInformation implements Serializable{
	private String m_ipAddress;
	private byte m_team=0;
	private Vector4f m_pos=new Vector4f(0.0f,0.0f,0.0f,0.0f);
	private Vector4f m_bullet=new Vector4f(0.0f,0.0f,0.0f,0.0f);
	private Quaternion m_rot=new Quaternion(0.0f,0.0f,0.0f,0.0f);
	private byte m_health=100;
	
	
	public PlayerInformation() { 
		m_ipAddress="";
	}
	
	public PlayerInformation(String ipAddress) { 
		m_ipAddress=ipAddress;
	}
	
	public PlayerInformation(String ipAddress, byte team, Vector4f pos, Vector4f bullet, Quaternion rot) {
		m_ipAddress=ipAddress;
		m_team=team;
		m_pos=pos;
		m_bullet=bullet;
		m_rot=rot;
	}
	
	public String getIP() {return m_ipAddress;}
	public byte getTeam() { return m_team;}
	public Vector4f getPos() {return m_pos;}
	public Vector4f getBullet() {return m_bullet;}
	public Quaternion getRot() {return m_rot;}
	public byte getHealth() {return m_health;}
	public boolean isLiving() {return ((m_health>0)?true:false);}
	
	public void setTeam(byte team) {m_team=team;}
	public void setPos(Vector4f pos) {m_pos=pos;}
	public void setBullet(Vector4f bullet) {m_bullet=bullet;}
	public void setRot(Quaternion rot) {m_rot=rot;}
	public void setHealth(byte health) {m_health=health;}
	public void damage(int damage) {m_health-=damage;}
}

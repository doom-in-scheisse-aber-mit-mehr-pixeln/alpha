package doom;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.KeyEvent;

public class Player extends Thread
{
	private static final Vector4f Y_AXIS = new Vector4f(0,1,0);

	private Transform m_transform;
	private Vector4f m_cameraPos;
	private Matrix4f m_projection;
	private Vector4f m_playerPos;
	private Matrix4f m_playerRotation;
	private Vector4f m_currentVelocity=new Vector4f(0.0f, 0.0f, 0.0f, 0.0f);
	
	
	public int team=0;
	public int weapon=0;
	public int health=100;
	public int ammo=42;
	private float shootingTime=0f;
	private boolean reload=false;
	
	private Display m_display;
	
	private float movementSpeedFactor=1.0f;;
	
	private int m_xMouseStartPoint;
	private int m_yMouseStartPoint;
	
	private byte m_player_tilt=0;
	private boolean crouching=false;
	private boolean jump=false;
	private float jumpTime=0;
	
	private Input input;
	private Robot robot;

	public Transform GetTransform()
	{
		return m_transform;
	}

	public Player(Matrix4f projection)
	{
		this.m_projection = projection;
		this.m_transform = new Transform();
		this.m_cameraPos = new Vector4f(0.0f, 1.725f, 0.0f, 0.0f);
}
	
	public Matrix4f GetProjection(){ return m_projection; }
	public Vector4f GetPosition(){ return m_playerPos;}
	public Matrix4f GetRotation(){ return m_playerRotation; }
	
	public void SetProjection(Matrix4f projection){ m_projection=projection; }
	public void SetPosition(Vector4f playerPos){ m_playerPos=playerPos; }
	public void SetRotation(Matrix4f playerRotation){ m_playerRotation=playerRotation; }

	public Matrix4f GetViewProjection()
	{
		m_playerRotation = GetTransform().GetTransformedRot().Conjugate().ToRotationMatrix();
		m_playerPos = GetTransform().GetTransformedPos().Mul(-1);
		Matrix4f cameraTranslation = new Matrix4f().InitTranslation(m_playerPos.GetX()+m_cameraPos.GetX()*-1, m_playerPos.GetY()+m_cameraPos.GetY()*-1, m_playerPos.GetZ()+m_cameraPos.GetZ()*-1);
		
		return m_projection.Mul(m_playerRotation.Mul(cameraTranslation));
	}
	
	public void Update(Display display)
	{
		m_display=display;
	}

	public void run()
	{
		long previousTime = System.nanoTime();
		while(true) {
			long currentTime = System.nanoTime();
			float delta = (float)((currentTime - previousTime)/1000000000.0);
			if (delta<0.01)
				continue;
				
			previousTime = currentTime;
			
			input= m_display.GetInput();
			// Speed and rotation amounts are hardcoded here.
			// In a more general system, you might want to have them as variables.
			final float keySensitivityX = 2.66f * delta;
			final float keySensitivityY = 2.0f * delta;
		
			final float SensitivityX = 2.66f * delta /50.0f;
			final float SensitivityY = 2.0f * delta /50.0f;
		
			final float movAmt = 7.0f * delta;

			Quaternion quat = GetTransform().GetRot();
			float[] ypr = quat.getEulerAngles();
			
			if(input.GetKey(KeyEvent.VK_W))
				Move(GetTransform().GetRot().GetFPForward(), movAmt*movementSpeedFactor);
			if(input.GetKey(KeyEvent.VK_S))
				Move(GetTransform().GetRot().GetFPBack(), movAmt*movementSpeedFactor);
			if(input.GetKey(KeyEvent.VK_A))
				Move(GetTransform().GetRot().GetFPLeft(), movAmt*movementSpeedFactor);
			if(input.GetKey(KeyEvent.VK_D))
				Move(GetTransform().GetRot().GetFPRight(), movAmt*movementSpeedFactor);
		
		
			if(input.GetKey(KeyEvent.VK_SHIFT)&&!crouching)
			{
				movementSpeedFactor=0.3f;
				crouching=true;
				MoveCamera(new Vector4f(0.0f, -0.5f, 0.0f, 0.0f), 1.0f);
			}
			else if(input.GetKey(KeyEvent.VK_CONTROL))
				movementSpeedFactor=0.5f;
			else if(!crouching)
				movementSpeedFactor=1.0f;
		
			if(!input.GetKey(KeyEvent.VK_SHIFT)&&crouching)
			{
				crouching=false;
				MoveCamera(new Vector4f(0.0f, 0.5f, 0.0f, 0.0f), 1.0f);
			}
		

			if(input.GetKey(KeyEvent.VK_SPACE)&&!jump)
			{
				jump=true;
			}
			if(jump)
			{
				jump=true;
				jumpTime+=delta;
				Move(new Vector4f(0.0f,-9.81f*jumpTime*jumpTime+4.5f*jumpTime-m_transform.GetPos().GetY(),0.0f,0.0f), 1.0f);
			}
			if(GetTransform().GetPos().GetY()<=0.0f)
			{
				jump=false;
				setGround();
				jumpTime=0.0f;
			}
			
			if(input.GetKey(KeyEvent.VK_M))
				team=(team+1)%2;
			
			shootingTime-=delta;
			if(input.GetKey(KeyEvent.VK_1)) {
				weapon=0; shootingTime=0.3f; reload=false;
			}
			if(input.GetKey(KeyEvent.VK_3)) {
				weapon=1; shootingTime=0.3f; reload=false;
			}

			if((ammo<=0||input.GetKey(KeyEvent.VK_R))&&weapon==0&&!reload) {
				reload=true; shootingTime=2.4f;
			}
			else if(shootingTime<=0f&&reload) {
				reload=false; ammo=42;
			}
			if(ammo>0&&shootingTime<=0&&input.GetMouse(1)&&weapon==0) {
				ammo--; shootingTime=0.1f;
				Rotate(Y_AXIS, 0.02f*(float)(Math.random()-0.5)*movementSpeedFactor);
				Rotate(GetTransform().GetRot().GetRight(), -0.02f*movementSpeedFactor);
			}
		
		
			//Rainbow
			quat = GetTransform().GetRot();
			ypr = quat.getEulerAngles();
			if(input.GetKey(KeyEvent.VK_Q)&&m_player_tilt!=-1)
			{
				m_player_tilt--;
				ypr[1]+=0.25;
				MoveCamera(GetTransform().GetRot().GetFPLeft(), 0.2f);
			}
			else if(!input.GetKey(KeyEvent.VK_Q)&&m_player_tilt==-1)
			{
				m_player_tilt++;
				ypr[1]-=0.25;
				MoveCamera(GetTransform().GetRot().GetFPRight(), 0.0f);
			}
			if(input.GetKey(KeyEvent.VK_E)&&m_player_tilt!=1)
			{
				m_player_tilt++;
				ypr[1]-=0.25;
				MoveCamera(GetTransform().GetRot().GetFPRight(), 0.2f);
			}
			else if(!input.GetKey(KeyEvent.VK_E)&&m_player_tilt==1)
			{
				m_player_tilt--;
				ypr[1]+=0.25;
				MoveCamera(GetTransform().GetRot().GetFPLeft(), 0.0f);
			}

			Quaternion newQuat = Quaternion.GetQuaternionFromEulerAngles(ypr[0], ypr[1], ypr[2]);
			GetTransform().GetRot().SetW(newQuat.GetW());
			GetTransform().GetRot().SetX(newQuat.GetX());
			GetTransform().GetRot().SetY(newQuat.GetY());
			GetTransform().GetRot().SetZ(newQuat.GetZ());
			
			
		
			if (input.inFocus) {
				Rotate(Y_AXIS, SensitivityX*(input.GetMouseXOnScreen()-(m_display.getWidth()+m_display.getLocationOnScreen().x) / 2));
				Rotate(GetTransform().GetRot().GetRight(), SensitivityY*(input.GetMouseYOnScreen()-(m_display.getHeight()+m_display.getLocationOnScreen().y) / 2));
				quat = GetTransform().GetRot();
				ypr = quat.getEulerAngles();
				if(ypr[2] > 1.5f) {
					ypr[2] = 1.5f;
					newQuat = Quaternion.GetQuaternionFromEulerAngles(ypr[0], ypr[1], ypr[2]);
					GetTransform().GetRot().SetW(newQuat.GetW());
					GetTransform().GetRot().SetX(newQuat.GetX());
					GetTransform().GetRot().SetY(newQuat.GetY());
					GetTransform().GetRot().SetZ(newQuat.GetZ());
				}
				if(ypr[2] < -1.5f) {
					ypr[2] = -1.5f;
					newQuat = Quaternion.GetQuaternionFromEulerAngles(ypr[0], ypr[1], ypr[2]);
					GetTransform().GetRot().SetW(newQuat.GetW());
					GetTransform().GetRot().SetX(newQuat.GetX());
					GetTransform().GetRot().SetY(newQuat.GetY());
					GetTransform().GetRot().SetZ(newQuat.GetZ());
				}
				
				try {
					robot = new Robot();
					robot.mouseMove((m_display.getWidth()+m_display.getLocationOnScreen().x) / 2 , (m_display.getHeight()+m_display.getLocationOnScreen().y) / 2);
				} catch (AWTException e) {
					e.printStackTrace();
				}
			}
		
			if(input.GetKey(KeyEvent.VK_RIGHT))
				Rotate(Y_AXIS, keySensitivityX);
			if(input.GetKey(KeyEvent.VK_LEFT))
				Rotate(Y_AXIS, -keySensitivityX);
			if(input.GetKey(KeyEvent.VK_DOWN))
				Rotate(GetTransform().GetRot().GetRight(), keySensitivityY);
			if(input.GetKey(KeyEvent.VK_UP))
				Rotate(GetTransform().GetRot().GetRight(), -keySensitivityY);
		}
	}
	
	private void MoveCamera(Vector4f dir, float amt) {
		m_cameraPos=new Vector4f(dir.GetX()*amt, m_cameraPos.GetY(), dir.GetZ()*amt, 1.0f);
	}

	private void Move(Vector4f dir, float amt)
	{
		m_transform = GetTransform().SetPos(GetTransform().GetPos().Add(dir.Mul(amt)));
	}

	private void Rotate(Vector4f axis, float angle)
	{
		m_transform = GetTransform().Rotate(new Quaternion(axis, angle));
	}
	
	private void setGround()
	{
		if(crouching)
			Move(new Vector4f(0f,-GetTransform().GetPos().GetY()-0.5f,0f,0f),1.0f);
		else
			Move(new Vector4f(0f,-GetTransform().GetPos().GetY(),0f,0f),1.0f);
	}
}
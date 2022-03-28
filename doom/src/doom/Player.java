package doom;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.KeyEvent;

public class Player
{
	private static final Vector4f Y_AXIS = new Vector4f(0,1,0);

	private Transform m_transform;
	private Matrix4f m_projection;
	private Vector4f m_cameraPos;
	private Matrix4f m_cameraRotation;
	private Vector4f m_currentVelocity=new Vector4f(0.0f, 0.0f, 0.0f, 0.0f);
	
	private float movementSpeedFactor=1.0f;;
	
	private int m_xMouseStartPoint;
	private int m_yMouseStartPoint;
	
	private byte m_player_tilt=0;
	private boolean crouching=false;
	private boolean jump=false;
	
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
}
	
	public Matrix4f GetProjection(){ return m_projection; }
	public Vector4f GetPosition(){ return m_cameraPos; }
	public Matrix4f GetRotation(){ return m_cameraRotation; }

	public Matrix4f GetViewProjection()
	{
		m_cameraRotation = GetTransform().GetTransformedRot().Conjugate().ToRotationMatrix();
		m_cameraPos = GetTransform().GetTransformedPos().Mul(-1);
		Matrix4f cameraTranslation = new Matrix4f().InitTranslation(m_cameraPos.GetX(), m_cameraPos.GetY(), m_cameraPos.GetZ());

		return m_projection.Mul(m_cameraRotation.Mul(cameraTranslation));
	}

	public void Update(Display display, float delta)
	{
		input= display.GetInput();
		// Speed and rotation amounts are hardcoded here.
		// In a more general system, you might want to have them as variables.
		final float keySensitivityX = 2.66f * delta;
		final float keySensitivityY = 2.0f * delta;
		
		final float SensitivityX = 2.66f * delta /50.0f;
		final float SensitivityY = 2.0f * delta /50.0f;
		
		final float movAmt = 7.0f * delta;

		
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
			movementSpeedFactor=0.5f;
			crouching=true;
			Move(new Vector4f(0.0f, -1.0f, 0.0f, 0.0f), 0.5f);
		}
		else if(input.GetKey(KeyEvent.VK_CONTROL))
			movementSpeedFactor=0.75f;
		else if(!crouching)
			movementSpeedFactor=1.0f;
		
		if(!input.GetKey(KeyEvent.VK_SHIFT)&&crouching)
		{
			crouching=false;
			Move(new Vector4f(0.0f, 1.0f, 0.0f, 0.0f), 0.5f);
		}
		
		/*if(GetTransform().GetPos().GetY()>0.0f)
		{
			m_currentVelocity.Set(1, (float)(m_currentVelocity.GetY()-9.81*delta*0.4));
			System.out.println(GetTransform().GetPos().GetY());}
		else if(jump)
		{
			jump=false;
			m_currentVelocity.Set(1, 0.0f);
		}
		if(input.GetKey(KeyEvent.VK_SPACE)&&!jump)
		{
			jump=true;
			m_currentVelocity.Set(1, 1.0f*movementSpeedFactor);
		}*/
		
		
		Move(m_currentVelocity, 1.0f);
		if(GetTransform().GetPos().GetY()<0.0f)
			setGround();
		
		
		//Rainbow
		if(input.GetKey(KeyEvent.VK_Q)&&m_player_tilt!=-1)
		{
			m_player_tilt--;
			Rotate(GetTransform().GetRot().GetForward(), 0.4f);
		}
		else if(!input.GetKey(KeyEvent.VK_Q)&&m_player_tilt==-1)
		{
			m_player_tilt++;
			Rotate(GetTransform().GetRot().GetForward(), -0.4f);
		}
		if(input.GetKey(KeyEvent.VK_E)&&m_player_tilt!=1)
		{
			Rotate(GetTransform().GetRot().GetForward(), -0.4f);
			m_player_tilt++;
		}
		else if(!input.GetKey(KeyEvent.VK_E)&&m_player_tilt==1)
		{
			m_player_tilt--;
			Rotate(GetTransform().GetRot().GetForward(), 0.4f);
		}
		
		
		Rotate(Y_AXIS, SensitivityX*(input.GetMouseXOnScreen()-display.getWidth() / 2));
		Rotate(GetTransform().GetRot().GetRight(), SensitivityY*(input.GetMouseYOnScreen()-display.getHeight() / 2));
		try {
            robot = new Robot();
            robot.mouseMove(display.getWidth() / 2 , display.getHeight() / 2);
        } catch (AWTException e) {
            e.printStackTrace();
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
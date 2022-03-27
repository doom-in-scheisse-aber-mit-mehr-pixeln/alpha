package doom;

import java.awt.event.KeyEvent;

public class Camera
{
	private static final Vector4f Y_AXIS = new Vector4f(0,1,0);

	private Transform m_transform;
	private Matrix4f m_projection;
	private Vector4f m_cameraPos;
	private Matrix4f m_cameraRotation;
	
	private int m_xMouseStartPoint;
	private int m_yMouseStartPoint;

	private Transform GetTransform()
	{
		return m_transform;
	}

	public Camera(Matrix4f projection)
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

	public void Update(Input input, float delta)
	{
		// Speed and rotation amounts are hardcoded here.
		// In a more general system, you might want to have them as variables.
		final float keySensitivityX = 2.66f * delta;
		final float keySensitivityY = 2.0f * delta;
		
		final float SensitivityX = 2.66f * delta /50.0f;
		final float SensitivityY = 2.0f * delta /50.0f;
		
		final float movAmt = 5.0f * delta;

		// Similarly, input keys are hardcoded here.
		// As before, in a more general system, you might want to have these as variables.
		if(input.GetKey(KeyEvent.VK_W))
			Move(GetTransform().GetRot().GetForward(), movAmt);
		if(input.GetKey(KeyEvent.VK_S))
			Move(GetTransform().GetRot().GetBack(), movAmt);
		if(input.GetKey(KeyEvent.VK_A))
			Move(GetTransform().GetRot().GetLeft(), movAmt);
		if(input.GetKey(KeyEvent.VK_D))
			Move(GetTransform().GetRot().GetRight(), movAmt);
		
		if (input.GetMouse(2)) {
		Rotate(Y_AXIS, SensitivityX*(input.GetMouseX()-m_xMouseStartPoint));
		Rotate(GetTransform().GetRot().GetRight(), SensitivityY*(input.GetMouseY()-m_yMouseStartPoint));
		}
		m_xMouseStartPoint=input.GetMouseX();
		m_yMouseStartPoint=input.GetMouseY();
		
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
}
package doom;

public class light {
	private Vector4f m_light_direction;
	private Vector4f m_light_color;
	private Transform m_transform;
	
	public light(Transform light_transform, Vector4f light_color) {
		m_transform=light_transform;
		m_light_color=light_color;
	}

	public light(Vector4f light_direction, Vector4f light_color) {
		m_light_direction=light_direction;
		m_light_color=light_color;
	}
	
	public Vector4f GetLightDirection() {
		return m_light_direction;
	}
	public Vector4f GetLightColor() {
		return m_light_color;
	}
	
	public void SetLightDirection(Vector4f light_direction) {
		m_light_direction=light_direction;
	}
	public void SetLightColor(Vector4f light_color) { //a random Color is like a thunder
		m_light_color=light_color;
	}
	public void ChangeLightDirection(Vector4f light_direction_change, float delta) {
		m_light_direction=m_light_direction.Add(light_direction_change.Mul(delta));
	}
	public void ChangeLightColor(Vector4f light_color_change, float delta) {
		m_light_color=m_light_color.Add(light_color_change.Mul(delta));
	}
	
	public void LimitDirectionIntensity() { //please use it!
		if(m_light_direction.Get(0)>1) {
			m_light_direction.Set(0, 1);
		}
		if(m_light_direction.Get(1)>1) {
			m_light_direction.Set(1, 1);
		}
		if(m_light_direction.Get(2)>1) {
			m_light_direction.Set(2, 1);
		}
	}
	
	public void LimitColorIntensity(float max_limit, float min_limit) { //please use it! (default(1f,0f)
		if(m_light_color.Get(0)>max_limit) {
			m_light_color.Set(0, max_limit);
		}
		else if(m_light_color.Get(0)<min_limit) {
			m_light_color.Set(0, min_limit);
		}
		
		
		if(m_light_color.Get(1)>max_limit) {
			m_light_color.Set(1, max_limit);
		}
		else if(m_light_color.Get(1)<min_limit) {
			m_light_color.Set(1, min_limit);
		}
		
		
		if(m_light_color.Get(2)>max_limit) {
			m_light_color.Set(2, max_limit);
		}
		else if(m_light_color.Get(2)<min_limit) {
			m_light_color.Set(2, min_limit);
		}
		
		
		if(m_light_color.Get(3)>max_limit) {
			m_light_color.Set(3, max_limit);
		}
		else if(m_light_color.Get(3)<min_limit) {
			m_light_color.Set(3, min_limit);
		}
	}

}

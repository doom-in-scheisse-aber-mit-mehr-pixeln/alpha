package doom;

public class Vertex {
	private Vector4f m_pos;
	private Vector4f m_texCoords;
	private Vector4f m_normal;
	
	public Vector4f GetPosition() {return m_pos;}
	public Vector4f GetTexCoords() {return m_texCoords;}
	public Vector4f GetNormal() {return m_normal;}
	
	public Vertex(Vector4f pos, Vector4f textCoords, Vector4f normal) {
		m_pos=pos;
		m_texCoords=textCoords;
		m_normal=normal;
	}
	
	public Vertex Transform(Matrix4f transform, Matrix4f normalTransform)
	{
		return new Vertex(transform.Transform(m_pos), m_texCoords, 
				normalTransform.Transform(m_normal).Normalized());
	}
	
	public Vertex PerspectiveDivide() {
		return new Vertex(new Vector4f(m_pos.GetX()/m_pos.GetW(), m_pos.GetY()/m_pos.GetW(), m_pos.GetZ()/m_pos.GetW(), m_pos.GetW()), m_texCoords, m_normal);
	}
	
	public float TriangleAreaTimesTwo(Vertex b, Vertex c) {
		float x1=b.Get(0)-m_pos.GetX();
		float y1=b.Get(1)-m_pos.GetY();
		
		float x2=c.Get(0)-m_pos.GetX();
		float y2=c.Get(1)-m_pos.GetY();
		
		return (x1 * y2 - x2 * y1);
	}
	
	public Vertex Lerp(Vertex other, float lerpAmt)
	{
		return new Vertex(
				m_pos.Lerp(other.GetPosition(), lerpAmt),
				m_texCoords.Lerp(other.GetTexCoords(), lerpAmt),
				m_normal.Lerp(other.GetNormal(), lerpAmt));
	}

	public boolean IsInsideViewFrustum()
	{
		return 
			Math.abs(m_pos.GetX()) <= Math.abs(m_pos.GetW()) &&
			Math.abs(m_pos.GetY()) <= Math.abs(m_pos.GetW()) &&
			Math.abs(m_pos.GetZ()) <= Math.abs(m_pos.GetW());
	}

	
	public float GetX() { return m_pos.GetX(); }
	public float GetY() { return m_pos.GetY(); }
	
	public float Get(int index)
	{
		switch(index)
		{
			case 0:
				return m_pos.GetX();
			case 1:
				return m_pos.GetY();
			case 2:
				return m_pos.GetZ();
			case 3:
				return m_pos.GetW();
			default:
				throw new IndexOutOfBoundsException();
		}
	}
}
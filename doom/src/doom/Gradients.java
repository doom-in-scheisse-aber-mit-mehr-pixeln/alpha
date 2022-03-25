package doom;

public class Gradients
{
	private float[] m_texCoordX;
	private float[] m_texCoordY;
	private float[] m_oneOverZ;
	private float[] m_depth;
	
	
	private float[] m_lightAmt;

	private float m_texCoordXXStep;
	private float m_texCoordXYStep;
	private float m_texCoordYXStep;
	private float m_texCoordYYStep;
	private float m_oneOverZXStep;
	private float m_oneOverZYStep;
	private float m_depthXStep;
	private float m_depthYStep;
	
	
	private float m_lightAmtBXStep;
	private float m_lightAmtBYStep;

	public float GetTexCoordX(int loc) { return m_texCoordX[loc]; }
	public float GetTexCoordY(int loc) { return m_texCoordY[loc]; }
	public float GetOneOverZ(int loc) { return m_oneOverZ[loc]; }
	public float GetDepth(int loc) { return m_depth[loc]; }
	public float GetLightAmt(int loc) { return m_lightAmt[loc]; }

	public float GetTexCoordXXStep() { return m_texCoordXXStep; }
	public float GetTexCoordXYStep() { return m_texCoordXYStep; }
	public float GetTexCoordYXStep() { return m_texCoordYXStep; }
	public float GetTexCoordYYStep() { return m_texCoordYYStep; }
	public float GetOneOverZXStep() { return m_oneOverZXStep; }
	public float GetOneOverZYStep() { return m_oneOverZYStep; }
	public float GetDepthXStep() { return m_depthXStep; }
	public float GetDepthYStep() { return m_depthYStep; }
	public float GetLightAmtXStep() { return m_lightAmtBXStep; }
	public float GetLightAmtYStep() { return m_lightAmtBYStep; }

	private float CalcXStep(float[] values, Vertex minYVert, Vertex midYVert,
			Vertex maxYVert, float oneOverdX)
	{
		return
			(((values[1] - values[2]) *
			(minYVert.Get(1) - maxYVert.Get(1))) -
			((values[0] - values[2]) *
			(midYVert.Get(1) - maxYVert.Get(1)))) * oneOverdX;
	}

	private float CalcYStep(float[] values, Vertex minYVert, Vertex midYVert,
			Vertex maxYVert, float oneOverdY)
	{
		return
			(((values[1] - values[2]) *
			(minYVert.Get(0) - maxYVert.Get(0))) -
			((values[0] - values[2]) *
			(midYVert.Get(0) - maxYVert.Get(0)))) * oneOverdY;
	}
	
	private float Saturate(float val) {
		if (val<0.0f) {
			return 0.0f;
		}
		else if (val>1.0f) {
			return 1.0f;
		}
		return val;
	}

	public Gradients(Vertex minYVert, Vertex midYVert, Vertex maxYVert, light sun)
	{
		float oneOverdX = 1.0f /
			(((midYVert.Get(0) - maxYVert.Get(0)) *
			(minYVert.Get(1) - maxYVert.Get(1))) -
			((minYVert.Get(0) - maxYVert.Get(0)) *
			(midYVert.Get(1) - maxYVert.Get(1))));

		float oneOverdY = -oneOverdX;

		m_oneOverZ = new float[3];
		m_texCoordX = new float[3];
		m_texCoordY = new float[3];
		m_depth = new float[3];
		m_lightAmt = new float[3];

		m_depth[0] = minYVert.GetPosition().GetZ();
		m_depth[1] = midYVert.GetPosition().GetZ();
		m_depth[2] = maxYVert.GetPosition().GetZ();
	
		
		Vector4f lightDir=sun.GetLightDirection(); //That is the lamp
		
		
		float sunColor=sun.GetLightColor().Get(0);
		m_lightAmt[0] = Saturate(minYVert.GetNormal().Dot(lightDir))*0.9f*sunColor+0.1f;	//plus ambient lighting
		m_lightAmt[1] = Saturate(midYVert.GetNormal().Dot(lightDir))*0.9f*sunColor+0.1f;
		m_lightAmt[2] = Saturate(maxYVert.GetNormal().Dot(lightDir))*0.9f*sunColor+0.1f;

		
		
		// W component is the perspective Z value;
		// The Z component is the occlusion Z value
		m_oneOverZ[0] = 1.0f/minYVert.GetPosition().GetW();
		m_oneOverZ[1] = 1.0f/midYVert.GetPosition().GetW();
		m_oneOverZ[2] = 1.0f/maxYVert.GetPosition().GetW();

		m_texCoordX[0] = minYVert.GetTexCoords().GetX() * m_oneOverZ[0];
		m_texCoordX[1] = midYVert.GetTexCoords().GetX() * m_oneOverZ[1];
		m_texCoordX[2] = maxYVert.GetTexCoords().GetX() * m_oneOverZ[2];

		m_texCoordY[0] = minYVert.GetTexCoords().GetY() * m_oneOverZ[0];
		m_texCoordY[1] = midYVert.GetTexCoords().GetY() * m_oneOverZ[1];
		m_texCoordY[2] = maxYVert.GetTexCoords().GetY() * m_oneOverZ[2];

		m_texCoordXXStep = CalcXStep(m_texCoordX, minYVert, midYVert, maxYVert, oneOverdX);
		m_texCoordXYStep = CalcYStep(m_texCoordX, minYVert, midYVert, maxYVert, oneOverdY);
		m_texCoordYXStep = CalcXStep(m_texCoordY, minYVert, midYVert, maxYVert, oneOverdX);
		m_texCoordYYStep = CalcYStep(m_texCoordY, minYVert, midYVert, maxYVert, oneOverdY);
		m_oneOverZXStep = CalcXStep(m_oneOverZ, minYVert, midYVert, maxYVert, oneOverdX);
		m_oneOverZYStep = CalcYStep(m_oneOverZ, minYVert, midYVert, maxYVert, oneOverdY);
		m_depthXStep = CalcXStep(m_depth, minYVert, midYVert, maxYVert, oneOverdX);
		m_depthYStep = CalcYStep(m_depth, minYVert, midYVert, maxYVert, oneOverdY);
		m_lightAmtBXStep = CalcXStep(m_lightAmt, minYVert, midYVert, maxYVert, oneOverdX);
		m_lightAmtBYStep = CalcYStep(m_lightAmt, minYVert, midYVert, maxYVert, oneOverdY);
	}
}
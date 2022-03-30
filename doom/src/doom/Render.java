package doom;

public class Render extends Thread{
	private Mesh[] m_meshes;
	private RenderContext m_target;
	private Matrix4f u_vp;
	private Transform[] m_objectTransform;
	private Bitmap[] m_textures;
	private light m_sun;
	private light m_light_point;
	private Display m_display;
	
	private Matrix4f m_vp;

	public Render(Display display, Mesh[] meshes, RenderContext target, Matrix4f vp, Transform[] transform, Bitmap[] textures, light sun, light light_point) {
		m_meshes=meshes;
		m_target=target;
		u_vp=vp;
		m_objectTransform=transform;
		m_textures=textures;
		m_sun=sun;
		m_light_point=light_point;
		m_display=display;
	}

	public void set4Render(Display display, Mesh[] meshes, RenderContext target, Matrix4f vp, Transform[] transform, light sun, light light_point) {
		m_meshes=meshes;
		m_target=target;
		u_vp=vp;
		m_objectTransform=transform;
		m_sun=sun;
		m_light_point=light_point;
		m_display=display;
	}
	
	public void run() {
		for (int i=0; i<m_meshes.length; i++) {
			m_meshes[i].setTexture(m_textures[i]);
		}
		long previousTime = System.nanoTime();
		while (true) {
			long currentTime = System.nanoTime();
			float delta = (float)((currentTime - previousTime)/1000000000.0);
			previousTime = currentTime;
			//System.out.println("Render: "+1/delta);
			
			m_vp=u_vp;
			
			m_target.Clear((byte)0x11);
			m_target.ClearDepthBuffer();

			for (int i=0; i<m_meshes.length; i++) {
				m_meshes[i].set4Render(m_target, m_vp, m_objectTransform[i].GetTransformation(), m_sun, m_light_point);
				m_meshes[i].run();
			}
			m_display.SwapBuffers();
		}
	}

}

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
	private PlayerInformation[] m_player;
	
	private Matrix4f m_vp;
	
	public String ip="";

	public Render(Display display, Mesh[] meshes, RenderContext target, Matrix4f vp, Transform[] transform, Bitmap[] textures, light sun, light light_point, PlayerInformation[] player) {
		m_meshes=meshes;
		m_target=target;
		u_vp=vp;
		m_objectTransform=transform;
		m_textures=textures;
		m_sun=sun;
		m_light_point=light_point;
		m_display=display;
		m_player=player;
	}

	public void set4Render(Display display, Mesh[] meshes, RenderContext target, Matrix4f vp, Transform[] transform, light sun, light light_point, PlayerInformation[] player) {
		m_meshes=meshes;
		m_target=target;
		u_vp=vp;
		m_objectTransform=transform;
		m_sun=sun;
		m_light_point=light_point;
		m_display=display;
		m_player=player;
	}
	
	public void run() {
		for (int i=0; i<m_meshes.length; i++) {
			m_meshes[i].setTexture(m_textures[i]);
		}
		long previousTime = System.nanoTime();
		while (true) {
			//System.out.println("Render: "+1/delta);
			
			m_vp=u_vp;
			
			m_target.Clear((byte)0x55);
			m_target.ClearDepthBuffer();

			for (int i=0; i<m_meshes.length-3; i++) {
				m_meshes[i].set4Render(m_target, m_vp, m_objectTransform[i].GetTransformation(), m_sun, m_light_point);
				m_meshes[i].run();
			}
			
			
			for (int i=0; i<m_player.length; i++) {
				if(m_player[i].isLiving()&&ip!=m_player[i].getIP()) {
					Transform transform=new Transform(m_player[i].getPos(), m_player[i].getRot());
					for(int j=m_meshes.length-3; j<m_meshes.length; j++) {
						m_meshes[j].set4Render(m_target, m_vp, transform.GetTransformation(), m_sun, m_light_point);
						m_meshes[j].run();
					}
				}
			}
			
			long currentTime = System.nanoTime();
			float delta = (float)((currentTime - previousTime)/1000000000.0);
			previousTime = currentTime;
			m_display.fps=1/delta;
			m_display.SwapBuffers();
		}
	}

}

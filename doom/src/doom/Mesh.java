package doom;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Mesh extends Thread{
	
	private List<Vertex> m_vertices;
	private List<Integer> m_indices;
	
	private IndexedModel model;
	
	
	RenderContext m_context;
	Matrix4f m_viewProjection;
	Matrix4f m_transform;
	Bitmap m_texture;
	light m_sun;
	light m_light_point;
	
	
	public Vertex GetVertex(int i) {return m_vertices.get(i);}
	public int GetIndex(int i) {return m_indices.get(i);}
	public int GetNumIndices() {return m_indices.size();}

	public Mesh(String fileName) throws IOException {
	IndexedModel model = new OBJModel(fileName).ToIndexedModel();
	
	m_vertices=new ArrayList<Vertex>();
	for(int i=0; i<model.GetPositions().size();i++) {
		m_vertices.add(new Vertex(
				model.GetPositions().get(i),
				model.GetTexCoords().get(i),
				model.GetNormals().get(i)));
	}
	
	m_indices=model.GetIndices();
	}
	
	public void Draw(RenderContext context, Matrix4f viewProjection, Matrix4f transform, Bitmap texture, light sun, light light_point)
	{

		Matrix4f mvp=viewProjection.Mul(transform);
		for(int i = 0; i < m_indices.size(); i += 3)
		{
			context.DrawTriangle(
					m_vertices.get(m_indices.get(i)).Transform(mvp, transform),
					m_vertices.get(m_indices.get(i + 1)).Transform(mvp, transform),
					m_vertices.get(m_indices.get(i + 2)).Transform(mvp, transform),
					texture, sun, light_point);
		}
	}
	
	public void set4Render(RenderContext context, Matrix4f viewProjection, Matrix4f transform, light sun, light light_point)
	{
		m_context=context;
		m_viewProjection=viewProjection;
		m_transform=transform;
		m_sun=sun;
		m_light_point=light_point;
	}
	
	public void setTexture(Bitmap texture) {
		m_texture=texture;
	}
	public Bitmap getTexture() {
		return m_texture;
	}
	
	public void run()
	{

		long previousTime = System.nanoTime();

		Matrix4f mvp=m_viewProjection.Mul(m_transform);
		for(int i = 0; i < m_indices.size(); i += 3)
		{
			m_context.DrawTriangle(
					m_vertices.get(m_indices.get(i)).Transform(mvp, m_transform),
					m_vertices.get(m_indices.get(i + 1)).Transform(mvp, m_transform),
					m_vertices.get(m_indices.get(i + 2)).Transform(mvp, m_transform),
					m_texture, m_sun, m_light_point);
		}
		long currentTime = System.nanoTime();
		float delta = (float)((currentTime - previousTime)/1000000000.0);
		previousTime = currentTime;
		//System.out.println("Mesh: "+1/delta);
	}
}

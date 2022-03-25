package doom;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class RenderContext extends Bitmap {
	
	private float[] m_zBuffer;

	public RenderContext(int width, int height) {
		super(width, height);
		m_zBuffer= new float[width*height];
	}
	
	public void ClearDepthBuffer() {
		for(int i=0; i<m_zBuffer.length;i++) {
			m_zBuffer[i]=Float.MAX_VALUE;
		}
	}
	
	private boolean ClipPolygonAxis(List<Vertex> vertices, List<Vertex> auxillaryList,
			int componentIndex)
	{
		ClipPolygonComponent(vertices, componentIndex, 1.0f, auxillaryList);
		vertices.clear();

		if(auxillaryList.isEmpty())
		{
			return false;
		}

		ClipPolygonComponent(auxillaryList, componentIndex, -1.0f, vertices);
		auxillaryList.clear();

		return !vertices.isEmpty(); //if it is Empty it will returns false or otherwise it will return true
	}
	

	private void ClipPolygonComponent(List<Vertex> vertices, int componentIndex, 
			float componentFactor, List<Vertex> result)
	{
		Vertex previousVertex = vertices.get(vertices.size() - 1);
		float previousComponent = previousVertex.Get(componentIndex) * componentFactor;
		boolean previousInside = previousComponent <= previousVertex.GetPosition().GetW();

		Iterator<Vertex> it = vertices.iterator();
		while(it.hasNext())
		{
			Vertex currentVertex = it.next();
			float currentComponent = currentVertex.Get(componentIndex) * componentFactor;
			boolean currentInside = currentComponent <= currentVertex.GetPosition().GetW();

			if(currentInside ^ previousInside)
			{
				float lerpAmt = (previousVertex.GetPosition().GetW() - previousComponent) /
					((previousVertex.GetPosition().GetW() - previousComponent) - 
					 (currentVertex.GetPosition().GetW() - currentComponent));

				result.add(previousVertex.Lerp(currentVertex, lerpAmt));
			}

			if(currentInside)
			{
				result.add(currentVertex);
			}

			previousVertex = currentVertex;
			previousComponent = currentComponent;
			previousInside = currentInside;
		}
	}
	
	public void DrawTriangle(Vertex v1, Vertex v2, Vertex v3, Bitmap texture, light sun, light light_point)
	{
		if(v1.IsInsideViewFrustum() && v2.IsInsideViewFrustum() && v3.IsInsideViewFrustum())
		{
			FillTriangle(v1, v2, v3, texture, sun, light_point);
			return;
		}

		List<Vertex> vertices = new ArrayList<>();
		List<Vertex> auxillaryList = new ArrayList<>();
		
		vertices.add(v1);
		vertices.add(v2);
		vertices.add(v3);

		if(ClipPolygonAxis(vertices, auxillaryList, 0) &&
				ClipPolygonAxis(vertices, auxillaryList, 1) &&
				ClipPolygonAxis(vertices, auxillaryList, 2))
		{
			Vertex initialVertex = vertices.get(0);

			for(int i = 1; i < vertices.size() - 1; i++)
			{
				FillTriangle(initialVertex, vertices.get(i), vertices.get(i + 1), texture, sun, light_point);		//Triangles: ABC, ACD, ADE, AEF, AFG, ... (a general Triangle-Function(it makes a simple Polygon to an Triangle))
			}
		}
	}
	
	private void FillTriangle(Vertex v1, Vertex v2, Vertex v3, Bitmap texture, light sun, light light_point) {
		Matrix4f screenSpaceTransform= new Matrix4f().InitScreenSpaceTransform(GetWidth()/2, GetHeight()/2);
		
		Matrix4f identity= new Matrix4f().InitIdentity();
		Vertex minYVert=v1.Transform(screenSpaceTransform, identity).PerspectiveDivide();
		Vertex midYVert=v2.Transform(screenSpaceTransform, identity).PerspectiveDivide();
		Vertex maxYVert=v3.Transform(screenSpaceTransform, identity).PerspectiveDivide();
		
		if(minYVert.TriangleAreaTimesTwo(maxYVert, midYVert)>=0) {
			return;
		}
		
		if (maxYVert.Get(1)<midYVert.Get(1)) {
			Vertex temp=maxYVert;
			maxYVert=midYVert;
			midYVert=temp;
		}
		if (midYVert.Get(1)<minYVert.Get(1)) {
			Vertex temp=midYVert;
			midYVert=minYVert;
			minYVert=temp;
		}
		if (maxYVert.Get(1)<midYVert.Get(1)) {
			Vertex temp=maxYVert;
			maxYVert=midYVert;
			midYVert=temp;
		}
		
		
		ScanTriangle(minYVert, midYVert, maxYVert, minYVert.TriangleAreaTimesTwo(maxYVert, midYVert)>=0, texture, sun, light_point); 	//if the statement is true it gives the function an true boolean or otherwise it gives it an false boolean
//		float area=minYVert.TriangleAreaTimesTwo(maxYVert, midYVert);
//		int handedness= area >=0 ? 1 : 0;	//if area>=0 handness=1 if it isn't handness=0
//		
//		ScanConvertTriangle(minYVert, midYVert, maxYVert, handedness);
//		FillShape(((int)Math.ceil(minYVert.GetY())), ((int)Math.ceil(maxYVert.GetY())));
	}
	
	private void ScanTriangle(Vertex minYVert, Vertex midYVert, Vertex maxYVert, boolean handedness, Bitmap texture, light sun, light light_point) {
		Gradients gradients=new Gradients(minYVert, midYVert, maxYVert, sun);
		Edge topToBottom= new Edge(gradients, minYVert, maxYVert, 0);
		Edge topToMiddle= new Edge(gradients, minYVert, midYVert, 0);
		Edge middleToBottom= new Edge(gradients, midYVert, maxYVert, 1);
		
		Edge left=topToBottom;
		Edge right= topToMiddle;
		if (handedness) {
			Edge temp=left;
			left=right;
			right=temp;
		}
		
		int yStart=topToMiddle.GetYStart();
		int yEnd=topToMiddle.GetYEnd();
		
		for(int j=yStart; j<yEnd;j++) {
			DrawScanLine(gradients, left, right, j, texture, sun, light_point);
			left.Step();
			right.Step();
		}
		
		left=topToBottom;
		right= middleToBottom;
		if (handedness) {
			Edge temp=left;
			left=right;
			right=temp;
		}
		
		yStart=middleToBottom.GetYStart();
		yEnd=middleToBottom.GetYEnd();
		
		for(int j=yStart; j<yEnd;j++) {
			DrawScanLine(gradients, left, right, j, texture, sun, light_point);
			left.Step();
			right.Step();
		}
	}
	
	private void DrawScanLine(Gradients gradients, Edge left, Edge right, int j, Bitmap texture, light sun, light light_point)
	{
		int xMin = (int)Math.ceil(left.GetX());
		int xMax = (int)Math.ceil(right.GetX());
		float xPrestep = xMin - left.GetX();
		
		float xDist = right.GetX() - left.GetX();
		float texCoordXXStep = (right.GetTexCoordX() - left.GetTexCoordX())/xDist;
		float texCoordYXStep = (right.GetTexCoordY() - left.GetTexCoordY())/xDist;
		float oneOverZXStep = (right.GetOneOverZ() - left.GetOneOverZ())/xDist;
		float depthXStep = (right.GetDepth() - left.GetDepth())/xDist;
		float lightAmtXStep = gradients.GetLightAmtXStep();

		float texCoordX = left.GetTexCoordX() + texCoordXXStep * xPrestep;
		float texCoordY = left.GetTexCoordY() + texCoordYXStep * xPrestep;
		float oneOverZ = left.GetOneOverZ() + oneOverZXStep * xPrestep;
		float depth = left.GetDepth() + depthXStep * xPrestep;
		float lightAmt= left.GetLightAmt() +lightAmtXStep* xPrestep;

		for(int i = xMin; i < xMax; i++)
		{
			int index= i+j*GetWidth();
			
			if (depth<m_zBuffer[index]) {
				m_zBuffer[index]=depth;
				float z=1.0f/oneOverZ;
				int srcX = (int)(texCoordX * z * (texture.GetWidth() - 1) + 0.5f);
				int srcY = (int)(texCoordY * z * (texture.GetHeight() - 1) + 0.5f);
				
				
				//byte light =(byte)(lightAmt*255.0f + 0.5); DrawPixel(i, j, light, light, light, light); //only the lighting
				CopyPixel(i, j, srcX, srcY, texture, lightAmt, sun, light_point, depth);
			}
			
			//DrawPixel(i, j, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF);
			oneOverZ += oneOverZXStep;
			texCoordX+=gradients.GetTexCoordXXStep();
			texCoordY+=gradients.GetTexCoordYXStep();
			depth+=depthXStep;
			lightAmt+=lightAmtXStep;
		}
	}
}

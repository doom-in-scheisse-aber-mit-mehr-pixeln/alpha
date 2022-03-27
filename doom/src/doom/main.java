package doom;

import java.io.IOException;

//https://www.youtube.com/watch?v=Y_vvC2G7vRo&list=PLEETnX-uPtBUbVOok816vTl1K9vV1GgH5

public class main
{
	public static void main(String[] args) throws IOException
	{
		Display display = new Display(0, 0, "42");
		RenderContext target = display.GetFrameBuffer();

		Bitmap texture = new Bitmap("./res/bricks.jpg");
		Bitmap texture2 = new Bitmap("./res/bricks2.jpg");
		
		
		Mesh monkeyMesh = new Mesh("./res/smoothMonkey0.obj");
		Transform monkeyTransform = new Transform(new Vector4f(0.0f,0.0f,3.0f));

		Mesh terrainMesh = new Mesh("./res/terrain2.obj");
		Transform terrainTransform = new Transform(new Vector4f(0.0f,-3.0f,0.0f));
		
		Mesh testCubeMesh = new Mesh("./res/Cube with big UV texture.obj");
		Transform testCubeTransform = new Transform(new Vector4f(3.0f,0.0f,0.0f));
		
		

		Player player = new Player(new Matrix4f().InitPerspective((float)Math.toRadians(70.0f),
					   	(float)target.GetWidth()/(float)target.GetHeight(), 0.1f, 1000.0f));
		
		light sun=new light(new Vector4f(0,0,1), new Vector4f(1f,1.0f,1.0f,1.0f));
		light light_point= new light(new Vector4f(3f,1f,0f), new Vector4f(1f,1f,1f,1f));
	 
		float [] own_pos= {0.0f,0.0f,0.0f};
		float rotCounter = 0.0f;
		long previousTime = System.nanoTime();
		
		int period=1;
		while(true)
		{
			long currentTime = System.nanoTime();
			float delta = (float)((currentTime - previousTime)/1000000000.0);
			previousTime = currentTime;

			player.Update(display, delta);
			Matrix4f vp = player.GetViewProjection();

			monkeyTransform = monkeyTransform.Rotate(new Quaternion(new Vector4f(0,1,0), delta));
			
			
			target.Clear((byte)0x11);
			target.ClearDepthBuffer();
			
			Vector4f playerPos = player.GetPosition();
			
			monkeyMesh.Draw(target, vp, monkeyTransform.GetTransformation(), texture2, sun, light_point);
			terrainMesh.Draw(target, vp, terrainTransform.GetTransformation(), texture, sun, light_point);
			testCubeMesh.Draw(target, vp, testCubeTransform.GetTransformation(), texture, sun, light_point);

			display.SwapBuffers();
		}
	}

}

package doom;

import java.io.IOException;

//https://www.youtube.com/watch?v=Y_vvC2G7vRo&list=PLEETnX-uPtBUbVOok816vTl1K9vV1GgH5

public class main
{
	public static void main(String[] args) throws IOException
	{
		Display display = new Display(0, 0, "Client-42");
		RenderContext target = display.GetFrameBuffer();

		Bitmap texture = new Bitmap("./res/bricks.jpg");
		Bitmap texture2 = new Bitmap("./res/bricks2.jpg");
		
		Mesh monkeyMesh = new Mesh("./res/smoothMonkey0.obj");
		Transform monkeyTransform = new Transform(new Vector4f(0.0f,0.0f,3.0f));

		Mesh terrainMesh = new Mesh("./res/terrain2.obj");
		Transform terrainTransform = new Transform(new Vector4f(0.0f,-3.0f,0.0f));
		
		Mesh testCubeMesh = new Mesh("./res/Cube with big UV texture.obj");
		Transform testCubeTransform = new Transform(new Vector4f(3.0f,0.0f,0.0f));
		
		Mesh[] meshes=new Mesh[3];
		Transform[] transform=new Transform[3];
		Bitmap[] textures=new Bitmap[3];
		
		meshes[0]=monkeyMesh;
		meshes[1]=terrainMesh;
		meshes[2]=testCubeMesh;
		
		transform[0]=monkeyTransform;
		transform[1]=terrainTransform;
		transform[2]=testCubeTransform;
		
		textures[0]=texture2;
		textures[1]=texture;
		textures[2]=texture;
		

		Player player = new Player(new Matrix4f().InitPerspective((float)Math.toRadians(70.0f),
					   	(float)target.GetWidth()/(float)target.GetHeight(), 0.1f, 1000.0f));
		
		light sun=new light(new Vector4f(0,0,1), new Vector4f(1f,1.0f,1.0f,1.0f));
		light light_point= new light(new Vector4f(3f,1f,0f), new Vector4f(1f,1f,1f,1f));
	 
		float [] own_pos= {0.0f,0.0f,0.0f};
		float rotCounter = 0.0f;
		long previousTime = System.nanoTime();
		
		int period=1;
		
		
		
		//serverSide server = new serverSide(5000);
		//server.start();
		
		clientSide networkTalker=new clientSide("192.168.0.41", 5000);
		networkTalker.start();
		
		
		Matrix4f vp = player.GetViewProjection();
		target.Clear((byte)0x11);
		target.ClearDepthBuffer();
		Render render=new Render(display, meshes, target, vp, transform, textures, sun, light_point);
		render.start();
		
		player.Update(display);
		player.start();
		
		while(true)
		{
			long currentTime = System.nanoTime();
			float delta = (float)((currentTime - previousTime)/1000000000.0);
			previousTime = currentTime;

			player.Update(display);
			vp = player.GetViewProjection();
			
			//transform[0]=server.getPlayerTransform();
			networkTalker.setPlayerTransform(player.GetTransform());
			
			
			render.set4Render(display, meshes, target, vp, transform, textures, sun, light_point);
		}
	}

}
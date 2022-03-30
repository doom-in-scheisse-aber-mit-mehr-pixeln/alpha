package doom;

import java.io.IOException;

//https://www.youtube.com/watch?v=Y_vvC2G7vRo&list=PLEETnX-uPtBUbVOok816vTl1K9vV1GgH5

public class main
{
	public static void main(String[] args) throws IOException
	{
		Display display = new Display(0, 0, "Server-42");
		RenderContext target = display.GetFrameBuffer();
		

		Bitmap texture = new Bitmap("./res/bricks.jpg");
		Bitmap soldierHeadTexture = new Bitmap("./res/SoldierType01/Head.png");
		Bitmap soldierBodyTexture = new Bitmap("./res/SoldierType01/Body.png");
		Bitmap soldierLowerTexture = new Bitmap("./res/SoldierType01/Lower.png");

		Mesh terrainMesh = new Mesh("./res/cs_map.obj");
		Transform terrainTransform = new Transform(new Vector4f(0.0f,0.0f,0.0f));
		
		Mesh testCubeMesh = new Mesh("./res/Cube with big UV texture.obj");
		Transform testCubeTransform = new Transform(new Vector4f(3.0f,0.0f,0.0f));
		
		Mesh soldierHead = new Mesh("./res/SoldierHead.obj");
		Mesh soldierBody = new Mesh("./res/SoldierBody.obj");
		Mesh soldierLower = new Mesh("./res/SoldierLower.obj");
		Transform soldierTransform = new Transform(new Vector4f(0.0f,0.0f,0.0f));
		
		Mesh[] meshes=new Mesh[5];
		Transform[] transform=new Transform[5];
		Bitmap[] textures=new Bitmap[5];
		
		meshes[0]=terrainMesh;
		meshes[1]=testCubeMesh;
		meshes[2]=soldierHead;
		meshes[3]=soldierBody;
		meshes[4]=soldierLower;
		
		transform[0]=terrainTransform;
		transform[1]=testCubeTransform;
		transform[2]=soldierTransform;
		transform[3]=soldierTransform;
		transform[4]=soldierTransform;
		
		textures[0]=texture;
		textures[1]=texture;
		textures[2]=soldierHeadTexture;
		textures[3]=soldierBodyTexture;
		textures[4]=soldierLowerTexture;
		

		Player player = new Player(new Matrix4f().InitPerspective((float)Math.toRadians(70.0f),
					   	(float)target.GetWidth()/(float)target.GetHeight(), 0.1f, 1000.0f));
		
		light sun=new light(new Vector4f(0.7f, 0.2f, 1.0f, 1), new Vector4f(1f,1.0f,1.0f,1.0f));
		light light_point= new light(new Vector4f(3f,1f,0f), new Vector4f(1f,1f,1f,1f));
	 
		float [] own_pos= {0.0f,0.0f,0.0f};
		float rotCounter = 0.0f;
		long previousTime = System.nanoTime();
		
		int period=1;
		
		
		
		//serverSide server = new serverSide(5000);
		//server.start();
		
		clientSide networkTalker=new clientSide("192.168.56.1", 5000);
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
			
			//transform[2]=server.getPlayerTransform();
			//networkTalker.setPlayerTransform(player.GetTransform());
			
			vp = player.GetViewProjection();
			render.set4Render(display, meshes, target, vp, transform, sun, light_point);
		}
	}

}

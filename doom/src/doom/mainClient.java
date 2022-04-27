package doom;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class mainClient
{
	public static void main(String[] args) throws IOException
	{
		Display display = new Display(800, 600, "Doom Strike 6 Siege");
		RenderContext target = display.GetFrameBuffer();
		display.loadCrosshair("./res/crosshairs/crosshair_type0.png"); //URLs sind auch möglich

		Bitmap textureFloor = new Bitmap("./res/TextureFloor.png");
		Bitmap textureWalls = new Bitmap("./res/TextureBricks.jpg");
		Bitmap soldierHeadTexture = new Bitmap("./res/SoldierType01/Head.png");
		Bitmap soldierBodyTexture = new Bitmap("./res/SoldierType01/Body.png");
		Bitmap soldierLowerTexture = new Bitmap("./res/SoldierType01/Lower.png");

		Mesh mapFloor = new Mesh("./res/map_floor.obj");
		Mesh mapWalls = new Mesh("./res/map_walls.obj");
		Transform mapTransform = new Transform(new Vector4f(0.0f,0.0f,0.0f));
		
		Mesh soldierHead = new Mesh("./res/SoldierHead.obj");
		Mesh soldierBody = new Mesh("./res/SoldierBody.obj");
		Mesh soldierLower = new Mesh("./res/SoldierLower.obj");
		Transform soldierTransform = new Transform(new Vector4f(0.0f,0.0f,0.0f));
		
		Mesh[] meshes=new Mesh[5];
		Transform[] transform=new Transform[5];
		Transform[] playerTransform=new Transform[1];
		Bitmap[] textures=new Bitmap[5];
		
		meshes[0]=mapFloor;
		meshes[1]=mapWalls;
		meshes[2]=soldierHead;
		meshes[3]=soldierBody;
		meshes[4]=soldierLower;
		
		transform[0]=mapTransform;
		transform[1]=mapTransform;
		transform[2]=soldierTransform;
		transform[3]=soldierTransform;
		transform[4]=soldierTransform;
		
		textures[0]=textureFloor;
		textures[1]=textureWalls;
		textures[2]=soldierHeadTexture;
		textures[3]=soldierBodyTexture;
		textures[4]=soldierLowerTexture;
		

		Player player = new Player(new Matrix4f().InitPerspective((float)Math.toRadians(70.0f),
					   	(float)target.GetWidth()/(float)target.GetHeight(), 0.1f, 1000.0f));
		
		String ip_address="";
		try {
			ip_address=InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		PlayerInformation playerInfo=new PlayerInformation(ip_address);

		PlayerInformation[] onlinePlayers=new PlayerInformation[2];
		
		light sun=new light(new Vector4f(0.7f, 0.2f, 1.0f, 1), new Vector4f(1f,1.0f,1.0f,1.0f));
		light light_point= new light(new Vector4f(3f,1f,0f), new Vector4f(1f,1f,1f,1f));
	 
		float [] own_pos= {0.0f,0.0f,0.0f};
		float rotCounter = 0.0f;
		long previousTime = System.nanoTime();
		
		int period=1;
		
		
		
		//serverSide server = new serverSide(5000);
		//server.start();
		ServerFeedback feedback=new ServerFeedback(5000);
		feedback.start();
		
		clientSide networkTalker=new clientSide("192.168.56.1", 5000);
		networkTalker.start();
		
		ServerFeedback serverFeedback=new ServerFeedback(5000);
		
		
		Matrix4f vp = player.GetViewProjection();
		target.Clear((byte)0x11);
		target.ClearDepthBuffer();
		Render render=new Render(display, meshes, target, vp, transform, textures, sun, light_point, onlinePlayers);
		render.ip=ip_address;
		render.start();
		
		player.Update(display);
		player.start();
		
		Collision collision=new Collision();
		collision.setMapFloor(mapFloor);
		collision.setMapWalls(mapWalls);
		collision.ip=ip_address;
		
		while(true)
		{
			long currentTime = System.nanoTime();
			float delta = (float)((currentTime - previousTime)/1000000000.0);
			previousTime = currentTime;
			
			//display.loadCrosshair("./res/crosshairs/crosshair_type"+(int)(Math.random()*13)+".png");//beste Zeile!!!

			player.Update(display);
			
			boolean shooting=player.getShooting();
			display.setShooting(shooting);
			display.setWeapon(player.weapon*4+player.team*2+(shooting?1:0));
			display.health=player.health;
			display.ammo=player.ammo;
			

			
			/*playerInfo.setHealth((byte)player.health);
			playerInfo.setPos(player.GetPosition().Mul(-1));
			playerInfo.setBullet(player.getBullet(shooting));
			playerInfo.setRot(player.GetTransform().GetRot());*/
			playerInfo=player.getPlayerInfo(playerInfo, shooting);
			//feedback.setPlayerInfos(playerInfo);
			
			
			onlinePlayers[0]=playerInfo;
			//onlinePlayers[1]=server.getPlayerInfo();
			//onlinePlayers[1].damage(10*(shooting?1:0));
			onlinePlayers=collision.hitCollision(onlinePlayers);
			//playerTransform[0]=server.getPlayerTransform();
			
			player.health=onlinePlayers[0].getHealth();
			
			networkTalker.setPlayerInfo(playerInfo);
			
			player.SetPosition(collision.playerCollision(onlinePlayers, player.GetTransform()));
			//player.SetPosition(collision.worldCollision(player.GetTransform()));
			player.SetPosition(collision.wallCollision(player.GetTransform()));
			
			vp = player.GetViewProjection();
			render.set4Render(display, meshes, target, vp, transform, sun, light_point, onlinePlayers);
		}
	}

}

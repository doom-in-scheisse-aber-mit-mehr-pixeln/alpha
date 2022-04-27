package doom;

public class Collision {
	private Vector4f pos=new Vector4f(0.0f,0.0f,0.0f,0.0f);
	private Vector4f old_pos=new Vector4f(-1.0f,0.0f,-1.0f,0.0f);
	
	private float distance2Player;
	private Mesh mapFloor;
	private Mesh mapWalls;
	private Vector4f[] vertices=new Vector4f[3];
	private Vector4f[] verticesMinMidMax=new Vector4f[4];	//used to save min, mid, max, temp Z value
	
	private float playerHitboxRadius=0.3f;
	
	public String ip="";
	
	
	public Collision() {
	}
	
	public void setMapFloor(Mesh map) {
		mapFloor=map;
	}
	
	public void setMapWalls(Mesh map) {
		mapWalls=map;
	}
	
	public Vector4f playerCollision(PlayerInformation[] playerInformation, Transform transform) {
		pos=transform.GetPos();
		for(int i=0; i<playerInformation.length; i++) {
			if(playerInformation[i].isLiving()&&playerInformation[i].getIP()!=ip) {
				float a=(pos.GetX()-playerInformation[i].getPos().GetX());
				float b=(pos.GetZ()-playerInformation[i].getPos().GetZ());
				distance2Player=(float) Math.sqrt((a*a)+(b*b));
				if (distance2Player<playerHitboxRadius*2) {
					if (distance2Player!=0) {
						pos.Set(0, playerInformation[i].getPos().GetX()+(playerHitboxRadius*2/distance2Player)*(pos.GetX()-playerInformation[i].getPos().GetX()));
						pos.Set(2, playerInformation[i].getPos().GetZ()+(playerHitboxRadius*2/distance2Player)*(pos.GetZ()-playerInformation[i].getPos().GetZ()));
					}
				}    
		}
		}
		return pos;
		
	}
	
	public Vector4f worldCollision(Transform transform) {
		pos=transform.GetPos();
		
		for(int i = 0; i < mapFloor.GetNumIndices(); i += 3)
		{
				vertices[0]=mapFloor.GetVertex(mapFloor.GetIndex(i)).GetPosition();
				vertices[1]=mapFloor.GetVertex(mapFloor.GetIndex(i+1)).GetPosition();
				vertices[2]=mapFloor.GetVertex(mapFloor.GetIndex(i+2)).GetPosition();
				if(worldCollisionFloorBox()) {
					if(worldCollisionFloorInTriangle()) {
						old_pos.Set(0, pos.GetX());
						old_pos.Set(2, pos.GetZ());
						return pos;
					}
				}

		}

		pos.Set(0, old_pos.GetX());
		pos.Set(2, old_pos.GetZ());
		return pos;
	}
	
	public boolean worldCollisionFloorBox() {
		verticesMinMidMax[0]=vertices[0];
		verticesMinMidMax[1]=vertices[1];
		verticesMinMidMax[2]=vertices[2];
		for(int j=0; j<2; j++) {
			if(verticesMinMidMax[2].Get(j*2)<verticesMinMidMax[1].Get(j*2)) {
				verticesMinMidMax[3]=verticesMinMidMax[1];
				verticesMinMidMax[1]=verticesMinMidMax[2];
				verticesMinMidMax[2]=verticesMinMidMax[3];
			}
			if(verticesMinMidMax[1].Get(j*2)<verticesMinMidMax[0].Get(j*2)) {
				verticesMinMidMax[3]=verticesMinMidMax[0];
				verticesMinMidMax[0]=verticesMinMidMax[1];
				verticesMinMidMax[1]=verticesMinMidMax[3];
			}
			if(verticesMinMidMax[2].Get(j*2)<verticesMinMidMax[1].Get(j*2)) {
				verticesMinMidMax[3]=verticesMinMidMax[1];
				verticesMinMidMax[1]=verticesMinMidMax[2];
				verticesMinMidMax[2]=verticesMinMidMax[3];
			}
		
		if(!(verticesMinMidMax[2].Get(j*2)>pos.Get(j*2)&&verticesMinMidMax[0].Get(j*2)<pos.Get(j*2))) {
			break;
		}
		else if(j>0) {
			return true;
		}
		}
		return false;
	}
	
	public boolean worldCollisionFloorInTriangle() {
		/*verticesMinMidMax[0]=vertices[0];
		verticesMinMidMax[1]=vertices[1];
		verticesMinMidMax[2]=vertices[2];
		
		if(verticesMinMidMax[2].GetZ()<verticesMinMidMax[1].GetZ()) {
			verticesMinMidMax[3]=verticesMinMidMax[1];
			verticesMinMidMax[1]=verticesMinMidMax[2];
			verticesMinMidMax[2]=verticesMinMidMax[3];
		}
		if(verticesMinMidMax[1].GetZ()<verticesMinMidMax[0].GetZ()) {
			verticesMinMidMax[3]=verticesMinMidMax[0];
			verticesMinMidMax[0]=verticesMinMidMax[1];
			verticesMinMidMax[1]=verticesMinMidMax[3];
		}
		if(verticesMinMidMax[2].GetZ()<verticesMinMidMax[1].GetZ()) {
			verticesMinMidMax[3]=verticesMinMidMax[1];
			verticesMinMidMax[1]=verticesMinMidMax[2];
			verticesMinMidMax[2]=verticesMinMidMax[3];
		}*/
		
		float a=(verticesMinMidMax[1].GetX()-verticesMinMidMax[0].GetX());
		if(a!=0) {
			if((pos.GetZ()-verticesMinMidMax[0].GetZ()<((verticesMinMidMax[1].GetZ()-verticesMinMidMax[0].GetZ())*(pos.GetX()-verticesMinMidMax[0].GetX())/a))) {
				return false;
			}
		
		}
		
		float value1, value2;
		a=(verticesMinMidMax[2].GetZ()-verticesMinMidMax[0].GetZ());
		if(a!=0) {
			value1=verticesMinMidMax[0].GetX()+(verticesMinMidMax[2].GetX()-verticesMinMidMax[0].GetX())*((pos.GetZ()-verticesMinMidMax[0].GetZ())/a);
		}
		else {
			return true;
		}
		a=(verticesMinMidMax[2].GetZ()-verticesMinMidMax[1].GetZ());
		if(a!=0) {
			value2=verticesMinMidMax[1].GetX()+(verticesMinMidMax[2].GetX()-verticesMinMidMax[1].GetX())*((pos.GetZ()-verticesMinMidMax[1].GetZ())/a);
		}
		else {
			value2=verticesMinMidMax[1].GetX();
		}
		
		if((value1<=pos.GetX())&&(value2>=pos.GetX())) {
			return true;
		}
		else if((value1>=pos.GetX())&&(value2<=pos.GetX())) {
			return true;
		}
		
		//Working Collision with the Ground (true or false)
		/*float b;
		float c;
		float k;
		a=(verticesMinMidMax[2].GetZ()-verticesMinMidMax[0].GetZ());
		if(a!=0) {
			b=(verticesMinMidMax[2].GetX()-verticesMinMidMax[0].GetX());
			c=pos.GetZ()-verticesMinMidMax[0].GetZ();
			k=c/a;
			value1=verticesMinMidMax[0].GetX()+b*k;
		}
		else {
			return true;
		}
		a=(verticesMinMidMax[2].GetZ()-verticesMinMidMax[1].GetZ());
		if(a!=0) {
			b=(verticesMinMidMax[2].GetX()-verticesMinMidMax[1].GetX());
			c=pos.GetZ()-verticesMinMidMax[1].GetZ();
			k=c/a;
			value2=verticesMinMidMax[1].GetX()+b*k;
		}
		else {
			return true;
		}
		
		if((value1<=pos.GetX())&&(value2>=pos.GetX())) {
			return true;
		}
		else if((value1>=pos.GetX())&&(value2<=pos.GetX())) {
			return true;
		}*/
		
		//System.out.println(value1+"   "+pos.GetX()+"   "+value2);
		return false;
	}
	
	public boolean wallBoxCollision() {
		verticesMinMidMax[0]=vertices[0];
		verticesMinMidMax[1]=vertices[1];
		verticesMinMidMax[2]=vertices[2];
		for(int j=0; j<3; j++) {
			if(verticesMinMidMax[2].Get(j)<verticesMinMidMax[1].Get(j)) {
				verticesMinMidMax[3]=verticesMinMidMax[1];
				verticesMinMidMax[1]=verticesMinMidMax[2];
				verticesMinMidMax[2]=verticesMinMidMax[3];
			}
			if(verticesMinMidMax[1].Get(j)<verticesMinMidMax[0].Get(j)) {
				verticesMinMidMax[3]=verticesMinMidMax[0];
				verticesMinMidMax[0]=verticesMinMidMax[1];
				verticesMinMidMax[1]=verticesMinMidMax[3];
			}
			if(verticesMinMidMax[2].Get(j)<verticesMinMidMax[1].Get(j)) {
				verticesMinMidMax[3]=verticesMinMidMax[1];
				verticesMinMidMax[1]=verticesMinMidMax[2];
				verticesMinMidMax[2]=verticesMinMidMax[3];
			}
		
		if(!(verticesMinMidMax[2].Get(j)+playerHitboxRadius+1.8f*(j%2)>pos.Get(j)&&verticesMinMidMax[0].Get(j)-playerHitboxRadius-0.5f*(j%2)<pos.Get(j))) {
			break;
		}
		else if(j==2) {
			return true;
		}
		}
		return false;
	}
	
	public boolean inWall() {
		for (int i=0; i<3; i++) {
			float a=vertices[i].GetX()-vertices[(i+1)%3].GetX();
			float b=vertices[i].GetZ()-vertices[(i+1)%3].GetZ();
			if (a==0&&b==0)
				break;
			float k;
			if((a==0&&b!=0)) {
				k=(a/b)*(pos.GetZ()-vertices[i].GetZ());
			}
			else {
				k=(a/b)*(pos.GetZ()-vertices[i].GetZ());
			}
			if(pos.GetX()<k+vertices[i].GetX()&&pos.GetX()+playerHitboxRadius>k+vertices[i].GetX()) {
				pos.Set(0, k+vertices[i].GetX()-playerHitboxRadius);
			}
			else if (pos.GetX()>k+vertices[i].GetX()&&pos.GetX()-playerHitboxRadius<k+vertices[i].GetX()) {
				pos.Set(0, k+vertices[i].GetX()+playerHitboxRadius);
			}
			
			
			if((b==0&&a!=0)) {
				k=0;
			}
			else {
				k=(b/a)*(pos.GetX()-vertices[i].GetX());
			}
			if(pos.GetZ()<k+vertices[i].GetZ()&&pos.GetZ()+playerHitboxRadius>k+vertices[i].GetZ()) {
				pos.Set(2, k+vertices[i].GetZ()-playerHitboxRadius);
			}
			else if(pos.GetZ()>k+vertices[i].GetZ()&&pos.GetZ()-playerHitboxRadius<k+vertices[i].GetZ()){
				pos.Set(2, k+vertices[i].GetZ()+playerHitboxRadius);
			}
		}
		return false;
	}

	public Vector4f wallCollision(Transform transform) {
		pos=transform.GetPos();
		
		for(int i = 0; i < mapWalls.GetNumIndices(); i += 3)
		{
				vertices[0]=mapWalls.GetVertex(mapWalls.GetIndex(i)).GetPosition();
				vertices[1]=mapWalls.GetVertex(mapWalls.GetIndex(i+1)).GetPosition();
				vertices[2]=mapWalls.GetVertex(mapWalls.GetIndex(i+2)).GetPosition();
				if(wallBoxCollision()) {
					inWall();	
				}

		}
		

		old_pos.Set(0, pos.GetX());
		old_pos.Set(1, pos.GetY());
		old_pos.Set(2, pos.GetZ());
		return pos;
	}
	
	public PlayerInformation[] hitCollision(PlayerInformation[] onlinePlayer) {
		for(int i=0; i<onlinePlayer.length; i++) {
			if(onlinePlayer[i].getPos().equals(onlinePlayer[i].getBullet()))
				continue;
			for(int j=0; j<onlinePlayer.length;j++) {
				if(j==i)
					continue;
				double a=onlinePlayer[i].getBullet().GetX()-onlinePlayer[i].getPos().GetX();
				double b=onlinePlayer[i].getBullet().GetZ()-onlinePlayer[i].getPos().GetZ();
				double k;
				
				
				if(b!=0) {
					k=(a/b)*(onlinePlayer[j].getPos().GetZ()-onlinePlayer[i].getPos().GetZ());
					
					
					float k1=(float) Math.sqrt(k*k+(onlinePlayer[j].getPos().GetZ()-onlinePlayer[i].getPos().GetZ())*(onlinePlayer[j].getPos().GetZ()-onlinePlayer[i].getPos().GetZ()));
					float k2=(float) Math.sqrt(a*a+b*b+(onlinePlayer[i].getPos().GetY()-onlinePlayer[i].getBullet().GetY())*(onlinePlayer[i].getPos().GetY()-onlinePlayer[i].getBullet().GetY()));
					float z=(k1/k2)*(onlinePlayer[i].getPos().GetY()-onlinePlayer[i].getBullet().GetY());
					System.out.println(z);
					
					if(Math.abs(k-onlinePlayer[j].getPos().GetX())<=Math.sqrt(playerHitboxRadius*playerHitboxRadius+playerHitboxRadius*playerHitboxRadius*(a/b)*(a/b))) {
						onlinePlayer=playerHit(onlinePlayer, j, false, 0);
						continue;
					}
				}
				if(a!=0) {
					k=(b/a)*(onlinePlayer[j].getPos().GetX()-onlinePlayer[i].getPos().GetX());
					
					
					float k1=(float) Math.sqrt(k*k+(onlinePlayer[j].getPos().GetX()-onlinePlayer[i].getPos().GetX())*(onlinePlayer[j].getPos().GetX()-onlinePlayer[i].getPos().GetX()));
					float k2=(float) Math.sqrt(a*a+b*b+(onlinePlayer[i].getPos().GetY()-onlinePlayer[i].getBullet().GetY())*(onlinePlayer[i].getPos().GetY()-onlinePlayer[i].getBullet().GetY()));
					float z=(k1/k2)*(onlinePlayer[i].getPos().GetY()-onlinePlayer[i].getBullet().GetY());
					System.out.println(z);
					
					if(Math.abs(k-onlinePlayer[j].getPos().GetZ())<=Math.sqrt(playerHitboxRadius*playerHitboxRadius+playerHitboxRadius*playerHitboxRadius*(b/a)*(b/a))) {
						onlinePlayer=playerHit(onlinePlayer, j, false, 0);
						continue;
					}
				}
				
			}
		}
		return onlinePlayer;
	}
	
	private PlayerInformation[] playerHit(PlayerInformation[] onlinePlayer, int player, boolean headshot, int weapon) {
		onlinePlayer[player].damage(headshot?90:20);
		System.out.println("hit: "+(headshot?90:20));
		return onlinePlayer;
	}
}

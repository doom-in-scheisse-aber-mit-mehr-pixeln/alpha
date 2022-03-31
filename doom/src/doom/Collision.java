package doom;

public class Collision {
	private Vector4f pos=new Vector4f(0.0f,0.0f,0.0f,0.0f);
	private Vector4f old_pos=new Vector4f(-1.0f,0.0f,-1.0f,0.0f);
	
	private float distance2Player;
	private float playerHitBoxRadius=0.6f;
	private Mesh mapFloor;
	private Vector4f[] vertices=new Vector4f[3];
	private Vector4f[] verticesMinMidMax=new Vector4f[4];	//used to save min, mid, max, temp Z value
	

	private float max=0.0f;
	private float min=0.0f;
	
	public Collision() {
	}
	
	public void setMapFloor(Mesh map) {
		mapFloor=map;
	}
	
	public Vector4f playerCollision(Transform[] playerTransform, Transform transform) {
		pos=transform.GetPos();
		for(int i=0; i<playerTransform.length; i++) {
			float a=(pos.GetX()-playerTransform[i].GetPos().GetX());
			float b=(pos.GetZ()-playerTransform[i].GetPos().GetZ());
			distance2Player=(float) Math.sqrt((a*a)+(b*b));
			if (distance2Player<playerHitBoxRadius) {
				if (distance2Player!=0) {
					pos.Set(0, playerTransform[i].GetPos().GetX()+(playerHitBoxRadius/distance2Player)*(pos.GetX()-playerTransform[i].GetPos().GetX()));
					pos.Set(2, playerTransform[i].GetPos().GetZ()+(playerHitBoxRadius/distance2Player)*(pos.GetZ()-playerTransform[i].GetPos().GetZ()));
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
		for(int j=0; j<2; j++) {
		max=vertices[0].Get(2*j);
		min=vertices[0].Get(2*j);
		if(vertices[0].Get(2*j)>vertices[1].Get(2*j)) {
			min=vertices[1].Get(2*j);
		}
		else {
			max=vertices[1].Get(2*j);
		}
		if(vertices[2].Get(2*j)>max) {
			max=vertices[2].Get(2*j);
		}
		else if(min>vertices[2].Get(2*j)) {
			min=vertices[2].Get(2*j);
		}
		
		if(!(max>pos.Get(j*2)&&min<pos.Get(j*2))) {
			break;
		}
		else if(j>0) {
			return true;
		}
		}
		return false;
	}
	
	public boolean worldCollisionFloorInTriangle() {
		verticesMinMidMax[0]=vertices[0];
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
		}
		
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
		
		System.out.println(value1+"   "+pos.GetX()+"   "+value2);
		return false;
	}
}

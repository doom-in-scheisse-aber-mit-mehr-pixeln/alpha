package doom;

public class Collision {
	private Vector4f pos=new Vector4f(0.0f,0.0f,0.0f,0.0f);
	private float playerPosX;
	private float playerPosY;
	private float playerPosZ;
	
	private float distance2Player;
	private float playerHitBoxRadius=0.6f;
	private Mesh mapFloor;
	private Vector4f[] vertices=new Vector4f[3];
	
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
		playerPosX=pos.GetX();
		playerPosZ=pos.GetZ();
		for(int i = 0; i < mapFloor.GetNumIndices(); i += 3)
		{
				vertices[0]=mapFloor.GetVertex(mapFloor.GetIndex(i)).GetPosition();
				vertices[1]=mapFloor.GetVertex(mapFloor.GetIndex(i+1)).GetPosition();
				vertices[2]=mapFloor.GetVertex(mapFloor.GetIndex(i+2)).GetPosition();
				
		}
		
		return pos;
	}
}

package doom;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import javax.imageio.ImageIO;

public class Bitmap {
	private final int m_width;
	private final int m_height;
	private final byte m_components[];
	
	public Bitmap(int width, int height) {
		m_width =width;
		m_height=height;
		m_components= new byte[width*height*4];
	}
	
	public Bitmap(String fileName) throws IOException
	{
		int width = 0;
		int height = 0;
		byte[] components = null;

		BufferedImage image = ImageIO.read(new File(fileName));

		width = image.getWidth();
		height = image.getHeight();

		int imgPixels[] = new int[width * height];
		image.getRGB(0, 0, width, height, imgPixels, 0, width);
		components = new byte[width * height * 4];

		for(int i = 0; i < width * height; i++)
		{
			int pixel = imgPixels[i];

			components[i * 4]     = (byte)((pixel >> 24) & 0xFF); // A
			components[i * 4 + 1] = (byte)((pixel      ) & 0xFF); // B
			components[i * 4 + 2] = (byte)((pixel >> 8 ) & 0xFF); // G
			components[i * 4 + 3] = (byte)((pixel >> 16) & 0xFF); // R
		}

		m_width = width;
		m_height = height;
		m_components = components;
	}

	
	public void Clear(byte shade) {
		Arrays.fill(m_components,  shade);
	}
	
	public void DrawPixel(int x, int y, byte a, byte b, byte g, byte r) {
		int index= (x+y*m_width)*4;
		m_components[index    ]=a;
		m_components[index + 1]=b;
		m_components[index + 2]=g;
		m_components[index + 3]=r;
	}
	
	private float limit(float a){
		if (a>255) {
			a= 255;
		}
		return a;
	}
	
	public void CopyPixel(int destX, int destY, int srcX, int srcY, Bitmap src, float lightAmt, light sun, light light_point, float depth)
	{
		//it is a funny thing to use the depth buffor for lighting (1/depth), because then everything near it lights up
		//float a_lightAmt=(1-depth);
		float general_lightAmt=0.1f;
		
		float a_lightAmt=general_lightAmt+lightAmt*sun.GetLightColor().GetX();
		float b_lightAmt=general_lightAmt+lightAmt*sun.GetLightColor().GetY();
		float g_lightAmt=general_lightAmt+lightAmt*sun.GetLightColor().GetZ();
		float r_lightAmt=general_lightAmt+lightAmt*sun.GetLightColor().GetW();
		
		
		//r_lightAmt+=(1-depth);
		
		
		int destIndex = (destX + destY * m_width) * 4;
		srcX=srcX%src.GetHeight();
		srcY=srcY%src.GetWidth();
		if(srcX<0) {
			srcX+=src.GetHeight();
		}
		if(srcY<0) {
			srcY+=src.GetWidth();
		}
		int srcIndex = (srcX + srcY * src.GetWidth()) * 4;
		m_components[destIndex    ] = (byte)limit(((src.GetComponent(srcIndex    )&0xFF)*a_lightAmt));	//weird stuff we are doing to solve a weird problem
		m_components[destIndex + 1] = (byte)limit(((src.GetComponent(srcIndex + 1)&0xFF)*b_lightAmt));
		m_components[destIndex + 2] = (byte)limit(((src.GetComponent(srcIndex + 2)&0xFF)*g_lightAmt));
		m_components[destIndex + 3] = (byte)limit(((src.GetComponent(srcIndex + 3)&0xFF)*r_lightAmt));
	}
	
	public void CopyToByteArray(byte[] dest) { //bit manipulation (yeahhhhhhhhh!)
	for(int i=0; i< m_width*m_height; i++) {
		dest[i*3    ]= m_components[i*4 + 1];
		dest[i*3 + 1]= m_components[i*4 + 2];
		dest[i*3 + 2]= m_components[i*4 + 3];
	}
	
}
	public int GetWidth() {return m_width;}
	public int GetHeight() {return m_height;}
	public byte GetComponent(int index) {return m_components[index];}
	
	
//	public void CopyToIntArray(int[] dest) { //bit manipulation (yeahhhhhhhhh!)
//		for(int i=0; i< m_width*m_height; i++) {
//			int a= ((int) m_components[i*4    ]) << 24;
//			int r= ((int) m_components[i*4 + 1]) << 16;
//			int g= ((int) m_components[i*4 + 2]) << 8;    //shifing g 8 bits (one byte), because an integer is 4 bytes long and a char ist only one byte (4 chars are equals to 1 int)
//			int b= ((int) m_components[i*4 + 3]);
//			
//			dest[i]= a | r | g | b; //adding the integers to one, so that we have one integers with 4 smaller integers in it (aRGB)
//		}
//	}
}

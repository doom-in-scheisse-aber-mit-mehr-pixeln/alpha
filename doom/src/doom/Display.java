package doom;


import java.awt.Canvas;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.image.BufferedImage;
import java.awt.image.BufferStrategy;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageInputStream;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import doom.Bitmap;


public class Display extends Canvas{
	private final JFrame m_frame;
	private RenderContext m_frameBuffer;
	private final BufferedImage m_displayImage;
	private final byte[] m_displayComponents;
	private final BufferStrategy m_bufferStrategy;
	private final Graphics m_graphics;
	private Graphics UI;
	private final Input m_input;

	private BufferedImage crosshairImg=new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
	private BufferedImage healthIconImg=new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
	private BufferedImage ammoIconImg=new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
	private BufferedImage weapon=new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
	private BufferedImage[] weapons=new BufferedImage[8];
	
	private float crosshairSize=1.0f;
	public int ammo=42;
	public int health=100;
	public float fps=0f;
	private boolean shooting=false;
	
	
	public Input GetInput() { return m_input; }
	
	public Display(int width, int height, String title) throws IOException {
		
		Dimension size= new Dimension(width, height);
		setPreferredSize(size);
		setMinimumSize(size);
		setMaximumSize(size);
		
		JFrame.setDefaultLookAndFeelDecorated(true);
		
		m_frame=new JFrame();
		if(width==0 && height==0)
			m_frame.setUndecorated(true);
		m_frame.add(this);
		m_frame.pack();
		m_frame.setResizable(false);
		m_frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		m_frame.setLocationRelativeTo(null);
		m_frame.setTitle(title);
		
	    Image icon = Toolkit.getDefaultToolkit().getImage("./res/ds6s_logo.png");  
	    m_frame.setIconImage(icon);  
		m_frame.setVisible(true);
		m_frame.setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
		
		m_input = new Input();
		addKeyListener(m_input);
		addFocusListener(m_input);
		addMouseListener(m_input);
		addMouseMotionListener(m_input);
		
		if(width==0 && height==0) {
			m_frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
			//m_frame.setUndecorated(true);
			width=m_frame.getWidth();
			height=m_frame.getHeight();
		}
		
				//invisible Cursor
				BufferedImage cursorImg= new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
				Cursor blankCursor=Toolkit.getDefaultToolkit().createCustomCursor(cursorImg, new Point(0,0), "blank cursor");
				m_frame.getContentPane().setCursor(blankCursor);
				
		
		m_frameBuffer= new RenderContext (width, height);
		m_displayImage= new BufferedImage (width, height, BufferedImage.TYPE_3BYTE_BGR);
		m_displayComponents = ((DataBufferByte) m_displayImage.getRaster().getDataBuffer()).getData();
		UI=m_displayImage.getGraphics();
		
		loadUI();
		loadWeapons();
		
		m_frameBuffer.Clear((byte)0x00);
	
		
		createBufferStrategy(1);
		m_bufferStrategy =getBufferStrategy();
		m_graphics = m_bufferStrategy.getDrawGraphics();
	}
	
	public RenderContext GetFrameBuffer() { return m_frameBuffer; }
	public void loadCrosshair(String file) {try {crosshairImg = ImageIO.read(new URL(file));} catch (IOException e) {try {crosshairImg = ImageIO.read(new File(file));} catch (IOException e2) {}}}
	public void loadUI() {
		try {healthIconImg = ImageIO.read(new File("./res/UI/health_icon.png"));} catch (IOException e) {}
		try {ammoIconImg = ImageIO.read(new File("./res/UI/ammo_icon.png"));} catch (IOException e) {}
	}
	public void loadWeapons() {
		try {weapons[0] = ImageIO.read(new File("./res/weapons/AK47.png"));} catch (IOException e) {}
		try {weapons[1] = ImageIO.read(new File("./res/weapons/AK47_shooting.png"));} catch (IOException e) {}
		try {weapons[2] = ImageIO.read(new File("./res/weapons/M4A4.png"));} catch (IOException e) {}
		try {weapons[3] = ImageIO.read(new File("./res/weapons/M4A4_shooting.png"));} catch (IOException e) {}
		try {weapons[4] = ImageIO.read(new File("./res/weapons/T_Knife.png"));} catch (IOException e) {}
		try {weapons[5] = ImageIO.read(new File("./res/weapons/T_Knife.png"));} catch (IOException e) {}
		try {weapons[6] = ImageIO.read(new File("./res/weapons/CT_Knife.png"));} catch (IOException e) {}
		try {weapons[7] = ImageIO.read(new File("./res/weapons/CT_Knife.png"));} catch (IOException e) {}
	}
	public void setWeapon(int weaponNumber) {if(!(weaponNumber%2==0&&shooting)) {weapon=weapons[weaponNumber];}}
	public float getCrosshairSize() {return crosshairSize;}
	public void setCrosshairSize(float size) {crosshairSize=size;}
	
	public void SwapBuffers() {
		if(GetInput().GetKey(KeyEvent.VK_ESCAPE))
			System.exit(0);
		m_frameBuffer.CopyToByteArray(m_displayComponents);
		UI=m_displayImage.getGraphics();
		drawUI();
		drawDebug();
		m_graphics.drawImage(m_displayImage, 0, 0, m_frameBuffer.GetWidth(), m_frameBuffer.GetHeight(), null);
		m_bufferStrategy.show();
	}
	
	public void drawUI() {
		UI.drawImage(weapon, 0, 0, m_frameBuffer.GetWidth(), m_frameBuffer.GetHeight(), null);
		shooting=false;
		UI.drawImage(crosshairImg, (int)(m_frameBuffer.GetWidth()/2-crosshairImg.getWidth()*crosshairSize/2), (int)(m_frameBuffer.GetHeight()/2-crosshairImg.getHeight()*crosshairSize/2), (int)(crosshairImg.getWidth()*crosshairSize), (int)(crosshairImg.getHeight()*crosshairSize), null);
		UI.setFont(new Font("Bauhaus 93", Font.PLAIN, 96));
		UI.drawString(Integer.toString(health), 80, m_frameBuffer.GetHeight()-50);
		UI.drawImage(healthIconImg, 10, m_frameBuffer.GetHeight()-115, 65, 65, null);
		UI.drawString(Integer.toString(ammo), m_frameBuffer.GetWidth()-125, m_frameBuffer.GetHeight()-50);
		UI.drawImage(ammoIconImg, m_frameBuffer.GetWidth()-175, m_frameBuffer.GetHeight()-115, 50, 70, null);
	}
	
	public void drawDebug() {
		UI.setFont(new Font("Consolas", Font.PLAIN, 23));
		UI.drawString(Float.toString(fps), 10, 20);
	}
	
	public void setShooting(boolean a) {
		if (!shooting)
			shooting=a;
	}
}

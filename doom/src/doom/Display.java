package doom;


import java.awt.Canvas;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.awt.image.BufferStrategy;
import java.awt.image.DataBufferByte;
import javax.swing.JFrame;

import doom.Bitmap;


public class Display extends Canvas{
	private final JFrame m_frame;
	private RenderContext m_frameBuffer;
	private final BufferedImage m_displayImage;
	private final byte[] m_displayComponents;
	private final BufferStrategy m_bufferStrategy;
	private final Graphics m_graphics;
	private final Input m_input;
	
	
	public Input GetInput() { return m_input; }
	
	public Display(int width, int height, String title) {

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
		
	    Image icon = Toolkit.getDefaultToolkit().getImage("res/ds6s_logo.png");  
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
		
		m_frameBuffer.Clear((byte)0x00);
	
		
		createBufferStrategy(1);
		m_bufferStrategy =getBufferStrategy();
		m_graphics = m_bufferStrategy.getDrawGraphics();
		
	}
	
	public RenderContext GetFrameBuffer() { return m_frameBuffer; }
	
	public void SwapBuffers() {
		if(GetInput().GetKey(KeyEvent.VK_ESCAPE))
			System.exit(0);
		m_frameBuffer.CopyToByteArray(m_displayComponents);
		m_graphics.drawImage(m_displayImage, 0, 0, m_frameBuffer.GetWidth(), m_frameBuffer.GetHeight(), null);
		m_bufferStrategy.show();
	}
}

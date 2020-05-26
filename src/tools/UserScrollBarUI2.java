package tools;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JScrollBar;
import javax.swing.plaf.basic.BasicScrollBarUI;

@SuppressWarnings("serial")
public class UserScrollBarUI2 extends BasicScrollBarUI 
{
	/*
	 * 스크롭바의 UI 이미지 입니다.
	 * 클래스 내에 존재하는 모든 연산들은 이미지를 그리는 작업으로 이루어져 있습니다.
	 */
	
	private final static Color TRACK_COLOR = Color.getColor("bright gray", 0XFFEFEFEF);
	
	private final static Color BAR_PRESS = Color.getColor("WHITE", 0X55FFFFFF),
			BAR_PUT = Color.getColor("WHITE little", 0X22eeeeee),
			BAR_NOMAL = Color.getColor("soft gray", 0X00DDDDDD),
			BAR_BORDER = Color.WHITE;
	
	private final static int BAR_WINDING = 10;
	
	private final Dimension d = new Dimension();


	protected JButton createDecreaseButton(int orientation) //끝 구석의 버튼 하나를 숨깁니다.
	{
	  return new JButton() 
	  {
	 	public Dimension getPreferredSize()//크기 0,0 이므로 버튼이 사라지는 효과 
	 	{return d;}
	  };
	}


	protected JButton createIncreaseButton(int orientation)//반대쪽 끝 구석의 버튼 하나를 숨깁니다.
	{
	  return new JButton() 
	  {
		public Dimension getPreferredSize() //크기 0,0 이므로 버튼이 사라지는 효과 
	    {return d;}
	  };
	}

	@Override
	protected void paintTrack(Graphics g, JComponent c, Rectangle r) //스크롤바의 배경화면을 지정합니다.
	{
		Graphics2D g2 = (Graphics2D) g.create();
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
		RenderingHints.VALUE_ANTIALIAS_ON);
		    
		g2.setPaint(TRACK_COLOR);
		g2.fillRoundRect(r.x, r.y, r.width, r.height, 0, 0);
	}

	protected void paintThumb(Graphics g, JComponent c, Rectangle r) //바의 모양을 지정합니다.
	{
	    Graphics2D g2 = (Graphics2D) g.create();
	    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
	        RenderingHints.VALUE_ANTIALIAS_ON);
	    Color color = null;
	    JScrollBar sb = (JScrollBar) c;
	    
	    if (!sb.isEnabled() || r.width > r.height) 
	    {return;} 
	    else if (isDragging) 
	    {color = BAR_PRESS;} 
	    else if (isThumbRollover()) 
	    {color = BAR_PUT;} 
	    else 
	    {color = BAR_NOMAL;}
	    
	    g2.setPaint(color);
	    g2.fillRoundRect(r.x, r.y, r.width, r.height, BAR_WINDING, BAR_WINDING);
	    g2.setPaint(BAR_BORDER);
	    g2.drawRoundRect(r.x, r.y, r.width, r.height, BAR_WINDING, BAR_WINDING);
	    g2.dispose();
	}

	@Override
	protected void setThumbBounds(int x, int y, int width, int height) 
	{
	    super.setThumbBounds(x, y, width, height);
	    scrollbar.repaint();
	}
}
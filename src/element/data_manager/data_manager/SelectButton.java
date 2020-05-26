package element.data_manager.data_manager;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class SelectButton extends JButton
{
	/*
	 * 이 클래스는 디렉토리 매니저의 레이아웃 구성요소중 하나입니다.
	 * 디렉토리 매니저 측면에 위치하여 디렉토리 매니저와 데이터베이스 매니저를 교체 출력하는 버튼 두개입니다.
	 * 이 클래스는 버튼 과 기능만으로 구성된 간단한 클래스입니다.
	 * Button 객체와 크게 다르지 않으므로 객체를 상속합니다.
	 */
	
	public static final String DIRECTORY = "Directory", DATABASE = "DataBase";
	
	private static final int WIDTH = 35, HEIGHT = 100;
	
	private static final int LABEL_LOCATION_X = 8, LABEL_LOCATION_Y = -7; //그림이 1/4돌아갔기 때문에 X 와 Y의 기준 수치도 변화했음.
	
	private static final ImageIcon BUTON_NOMAL_IMAGE = new ImageIcon("images/selector_nomal.png"),
			BUTTON_MOUSE_PUT_IMAGE = new ImageIcon("images/selector_put.png"),
			BUTTON_PRESSED_IMAGE = new ImageIcon("images/selector_press.png");
			
	
	//---------------store instance
	
	private final String string;
	
	//-----------------------------
	//creator
	//-----------------------------
	
	/**
	 * It can change screen of Component in JPanel
	 * It just change CENTER location Component.
	 * @param text a Label of Vertical button
	 * @param parent JPanel what put in a Component
	 * @param component of will be change Screen output
	 */
	public SelectButton(final String text, JPanel parent, Component component)//버튼의 레이아웃을 설정
	{
		super(BUTON_NOMAL_IMAGE);//하단 설명 참조(1)
		setRolloverIcon(BUTTON_MOUSE_PUT_IMAGE);
		setPressedIcon(BUTTON_PRESSED_IMAGE);
		setPreferredSize(new Dimension(WIDTH, HEIGHT));
		setMaximumSize(new Dimension(WIDTH, HEIGHT));
		setBorderPainted(false); 
		setFocusPainted(false); 
		setContentAreaFilled(false);
		string = text;
		
		addActionListener(getScreenChangeWork(text,parent,component));
	}
		
		private ActionListener getScreenChangeWork(final String text,final JPanel parent,final Component component)//버튼을 누르면 컴포턴를 인자로 받은 페널 화면으로 교체하는 리스너를 보내줌
		{
			return
						
			new ActionListener() 
			{
				@Override
				public void actionPerformed(ActionEvent e) 
				{
					parent.remove(((BorderLayout)parent.getLayout()).getLayoutComponent(BorderLayout.CENTER));
					parent.add(component,BorderLayout.CENTER);
					parent.revalidate();
				}
			};
		}
	
	//-----------------------------
	//Override only
	//-----------------------------
	
	@Override
    public void paintComponent(Graphics g)//버튼의 라벨을 세로로 90도 돌려셔서 출력한다.
	{
        super.paintComponent(g);
        
        final Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        
        AffineTransform at = AffineTransform.getQuadrantRotateInstance(1);//그림을 1/4 돌리겠다고 예약함.
        g2.setTransform(at);
        g2.setFont(new Font("Bookman Old Style", Font.PLAIN, 18));
        g2.drawString(string, LABEL_LOCATION_X, LABEL_LOCATION_Y);
        repaint();
	}
}
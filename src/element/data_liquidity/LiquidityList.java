package element.data_liquidity;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.LineBorder;

import tools.UserScrollBarUI;

public class LiquidityList {
	
	/*
	 * 이 메소드는 가장 나중에 만들어 질 예정.. 지금은 아무것도 없음.
	 */
	
	private final static int LIST_WIDTH = 240, LIST_HEIGHT = 500;
	
	private JPanel list = new JPanel();
	
	public LiquidityList()
	{
		setLayout();
	}
	
	public JScrollPane get()
	{
		final JScrollPane ListSc 
		= new JScrollPane(list,
				JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		ListSc.getVerticalScrollBar().setUnitIncrement(16);
		ListSc.getVerticalScrollBar().setUI(new UserScrollBarUI());
		
		return ListSc;
	}
	
	private void setLayout()
	{
		list.setBackground(Color.white);
		list.setLayout(new BoxLayout(list,BoxLayout.Y_AXIS));
		list.setMaximumSize(new Dimension(LIST_WIDTH, LIST_HEIGHT));
		list.setPreferredSize(new Dimension(LIST_WIDTH, LIST_HEIGHT));
		list.setBorder(new LineBorder(Color.getColor("darkness",0XFFd3d3d3)));
	}
	

}

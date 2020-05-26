package tools;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.JLabel;
import javax.swing.border.LineBorder;

public class Title {
	
	/*
	 * 사용 빈도가 높아서 따로 만들어둔 라벨:Text 쌍입니다.
	 */
	
	private int TITLE_WIDTH;
	private int TITLE_HEIGHT;
	
	private final GradationPanel title = new GradationPanel(Color.getColor("lightgray", 0XFFdddddd),Color.getColor("lightergray", 0XFFeeeeee));
	
	public Title(int width,int height)//checked
	{
		TITLE_WIDTH = width;
		TITLE_HEIGHT = height;
		
		title.setLayout(new BorderLayout());
		title.setPreferredSize(new Dimension(TITLE_WIDTH, TITLE_HEIGHT));
		title.setMaximumSize(new Dimension(TITLE_WIDTH, TITLE_HEIGHT));
		title.setBorder(new LineBorder(Color.getColor("darkness",0XFFdddddd)));
	}
	
	public GradationPanel get()
	{return title;}
	
	public void setText(final String text)
	{
		title.setLayout(new FlowLayout());
		title.add(new JLabel(text));
	}
}


package window;

import java.awt.BorderLayout;

import javax.swing.BoxLayout;
import javax.swing.JPanel;

import element.layout_list.LayoutList;
import element.tags.BigTag;
import element.tags.SmallTag;

public class Tags extends JPanel
{
	/*
	 * 버전에 + 는 0.?를 뜻힘
	 * 
	 * 기능...
	 * 1.비율에 따른 레이아웃의 위치를 직접 이동시켜줌
	 * 2.레이아웃 개개인에 동기화 되어 그 레이아웃으로 작업을 바로 이동시켜줌
	 * 3.레이아웃의 위치를 변동함
	 * 4.레이아웃의 이름울 표기
	 */
	private static final long serialVersionUID = 1L + 0;
	private final JPanel settingButons = new JPanel();
	
	public Tags(LayoutList list)
	{
		super();
		setLayout(new BorderLayout());
		settingButons.setLayout(new BoxLayout(settingButons,BoxLayout.Y_AXIS));
		add(new BigTag(list).get(),BorderLayout.NORTH);
		add(new SmallTag(list).get(),BorderLayout.CENTER);
		add(settingButons,BorderLayout.SOUTH);
	}
	
}

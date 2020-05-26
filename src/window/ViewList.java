package window;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JPanel;
import javax.swing.border.LineBorder;

import element.layout_list.LayoutList;
import element.view_list.ViewStructure;

public class ViewList extends JPanel
{
	/*
	 * 버전에 + 는 0.?를 뜻힘
	 * 
	 * 기능...
	 * 1.화면의 정보를 담아 뷰를 만든뒤 화면단위 의 작업 이동을 가능하게함 Done
	 * 2.뷰의 렌더링 상황을 표기 Done
	 * 3.뷰를 추가 Done
	 * 4.뷰를 삭제 Done
	 * 5.뷰의 위치를 이동 Done
	 * 6.뷰의 이름을 지정 및 표기 Done
	 * 7.이뷰에 링크 되어있는 다른 뷰 리스트를 표기
	 */
	private static final long serialVersionUID = 0L + 8;
	public static final int HEIGHT = 300;
	
	public ViewList(LayoutList list)
	{
		super();
		
		setLayout(new BorderLayout());
		setBackground(Color.getColor("whiteless",0XFFEAEAEA));
		setBorder(new LineBorder(Color.WHITE,1));
		setPreferredSize(new Dimension(Integer.MAX_VALUE,HEIGHT));
		setMaximumSize(new Dimension(Integer.MAX_VALUE,HEIGHT));
		add(new ViewStructure(list).get());
		
	}
}

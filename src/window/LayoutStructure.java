package window;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JPanel;

import element.layout_list.LayoutList;


public class LayoutStructure extends JPanel
{
	/*
	 * 버전에 + 는 0.?를 뜻힘
	 * 
	 * 기능...
	 * 1.파일의 정보를 추출하여 레이아웃을 추가
	 * 2.레이아웃의 정보 리스트를 표기
	 * 3.레이아웃의 결합관리
	 * 4.레이아웃의 삭제
	 * 5.레이아웃의 이름 수정
	 */
	private static final long serialVersionUID = 0L + 8;
	
	private static final int WIDTH = 250;

	public LayoutStructure(LayoutList list)
	{
		super();
		setPreferredSize(new Dimension(WIDTH,Integer.MAX_VALUE));
		setMaximumSize(new Dimension(WIDTH,Integer.MAX_VALUE));
		setLayout(new BorderLayout());
		add(list.get());
	}

}

package element.layout_list;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JPanel;

import logic.ReferenceList;
import tools.ImageButton;

public class Binder extends ImageButton implements ActionListener{
	
	/*
	 * 이 클래스는 LayoutInfo 와 함께 리트스 출력 내부에 들어가있는 버튼입니다.
	 * 버튼을 클릭하면 상단(리스트의 전 포인트)의 레이아웃과 이 버튼이 들어있는 레이아웃을 합칩니다.
	 * LayoutList 내부에서만 만들수 있습니다.
	 */
	
	private static final long serialVersionUID = 1L;
	
	public final static int WIDTH = 250 , HEIGHT = 40;
	
	final private LayoutInfo target;
	final private LayoutList list;
	
	//-----------------------------
	//creator
	//-----------------------------
	
	/**
	 * this is a button of LayoutInfo Object for Bind two Info.
	 */
	public Binder(final LayoutInfo info, final LayoutList list) //설명생략
	{
		super(new String[]{"images/binder_nomal.png","images/binder_press.png","images/binder_up.png"}, WIDTH, HEIGHT);
		target = info;
		this.list = list;
		addActionListener(this);
	}
	/*
	 * Other Why
	 * (E)사실상 private
	 * 이 객체는 LayoutInfo 객체를 필요로 하지만 외부에서 LayoutInfo 객체를 얻어올수 있는 방법은 없다.
	 * 즉 이 객체는 LayoutList 내에서만 사용하는 객체가 된다.
	 */
	
	//-----------------------------
	//actions
	//-----------------------------

	@Override
	public void actionPerformed(ActionEvent e)//현재 정보를 이전 list 객체로 보내고 삭제함
	{
		final LayoutList viewer = list;
		final ReferenceList layout = (ReferenceList)target.getParent().getParent();//LayoutInfo > LayoutInfo를 지닌 출력버튼 > 출력버튼을 가리기는 레퍼런스
		final LayoutInfo bindWith = (LayoutInfo)((JPanel)layout.getNextList().getDesignated()).getComponent(1);//하단 설명 참조 (1)
		
		bindWith.bindInfo(target);//현재정보를 이전 리스트의 정보로 보냄
		viewer.changeFocus(bindWith);//포커스를 정보를 합친 객체로 바꿈.
		viewer.tools.remove(layout);
	}
	/*
	 * Other Why
	 * (1)해석
	 * layoutInfo는 list상에서 사용자의 편의를 위해서 역순으로 정렬되어있다.
	 * 즉 리스트인 ReferenceList의 다음 타겟은 이전값이 된다.
	 * layout.getNextList() : 이전 레퍼런스
	 * .getDesignated() : 레퍼런스가 가리키는 LayoutInfo를 지닌 출력버튼
	 * .getComponent(1) : 출력버튼내에있는 LayoutInfo 객체 (index 0번은 Binder)
	 */

}
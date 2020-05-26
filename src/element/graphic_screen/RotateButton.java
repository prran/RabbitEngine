package element.graphic_screen;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JPanel;

import element.layout_list.LayoutInfo;
import logic.ReferenceList;
import tools.DimensionF;
import tools.ImageButton;


@SuppressWarnings("serial")
public class RotateButton extends ImageButton implements Screen.ScreenComponent
{	
	/*
	 * 이 클래스는 그래픽 스크린 레이아웃 구성요소중 하나입니다.
	 * 버튼을 클릭하면 스크린의 화면을 가로모드, 세로모드로 변환합니다.
	 * 이 클래스는 버튼 과 기능만으로 구성된 간단한 클래스입니다.
	 * Button 객체와 크게 다르지 않으므로 객체를 상속합니다.
	 */
	
	private static final int WIDTH = 25 , HEIGHT = 25;

	private static final String BUTON_NOMAL_IMAGE = "images/rotate_nomal.png",
			BUTTON_MOUSE_PUT_IMAGE = "images/rotate_put.png",
			BUTTON_PRESSED_IMAGE = "images/rotate_pressed.png";
	
	private static final String KEY = "2k3yee2!p%";
	
	//-----------------------------
	//creator
	//-----------------------------	
	
	/**
	 * 
	 * @param scr Screen object that change Landscape or Portrait
	 */
	public RotateButton(Screen scr)//버튼이미지 설정, 레이아웃 설정, 버튼기능 추가
	{
		super(new String[] {BUTON_NOMAL_IMAGE,BUTTON_PRESSED_IMAGE,BUTTON_MOUSE_PUT_IMAGE},WIDTH,HEIGHT);
		scr.addSynComponent(this);
		setFocusPainted(false);
		setContentAreaFilled(false);
		addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				rotateScreen(scr);
			}	
		});
	}
	
	/**
	 * it use for change Landscape or Portrait by non-graphical UI
	 */
	public RotateButton()
	{
		super(new String[] {BUTON_NOMAL_IMAGE,BUTTON_PRESSED_IMAGE,BUTTON_MOUSE_PUT_IMAGE},WIDTH,HEIGHT);
		setFocusPainted(false);
		setContentAreaFilled(false);
	}
	
	//-----------------------------
	//actions
	//-----------------------------	
	
	/**
	 * @param scr Screen object that change Landscape or Portrait
	 */
	@SuppressWarnings("unchecked")
	public void rotateScreen(Screen scr)//하단 설명 참조
	{
		scr.getRotation().unlock(KEY).set(!scr.getRotation().get());//스크린의 가로모드/세로모드를 변환
		
		final DimensionF size = scr.getScrSize();
		size.setSize(size.height, size.width);//하단 설명 참조(2)
		
		final DimensionF renderSize = scr.getRenderSize();
		renderSize.setSize(renderSize.height, renderSize.width);
		
		scr.get().setPreferredSize(renderSize.getSize());
		scr.get().revalidate();
		
		final List<SizeSet> sizeSets = scr.getSynObject("SizeSet");
		
		for(SizeSet sizeSet : sizeSets)//하단 설명 참조(1)
		{
			if(sizeSet.getDirection() == SizeSet.X)
				sizeSet.setValueNoAction(size.getWidth());
			if(sizeSet.getDirection() == SizeSet.Y)
				sizeSet.setValueNoAction(size.getHeight());
		}
		
		JPanel list = scr.getLayoutList().getList();

		for(int i = 0; i < list.getComponentCount();i++)
		{
			final JPanel comp = (JPanel) ((ReferenceList)list.getComponent(i)).getDesignated();//하단 설명 참조 (3)
			final LayoutInfo info = (LayoutInfo) comp.getComponent(comp.getComponentCount()-1);
			info.updateDataByScrSize();
		}
		scr.getLayoutList().reFocus();

	}
	
	/*
	 * 기능:화면의 가로모드/세로모드를 변경
	 * 1.가로모드/세로모드 스위치를 변환
	 * 2.실제 화면 크기 값과 프로그램내의 출력 화면 크기 값을 반전한다.
	 * 3.출력 화면의 크기를 바꾼 값으로 적용한다
	 * 4.화면의 크기를 출력해주는 텍스트창을 업데이트 한다.
	 * 5.LayoutInfo 값들을 회전한 값에 맞춰 수정한다.
	 * 
	 * Other Why
	 * (1) 2개인데도 for문을 사용하는 이유..
	 * >> 비록 프론트엔드를 작성자가 구성하긴 했지만 이 클래스들은 다른 사람들도 사용할 수 있다는 전재 하에 만들어 지고 있다.
	 * >> 즉, 화면의 크기를 텍스트로 출력하는것을 원하지 않을 수도 있고 역으로 렌더링되는 화면의 실제 크기 까지 전부 출력하길 원할 수도 있다는 것이다.
	 * >> 유지 보수를 하면서 출력 텍스트 창의 갯수가 변화가 있을 것으로 추정되어서 for문을 사용했다.
	 * >> 또한 for(:)는 null이 리턴 되면 아무 작동도 하지 않기 때문에
	 * >> 실제로 출력 텍스트가 없더라도 NullPointerException 예외처리와 속도가 향상되는 효과 까지 있다.
	 * >> 물론. 가장 빠른 방법은 레퍼런스 대입 연산을 반복하지 않는 하드코딩이 최고다. 특히 2개 밖에 안된다면 더더욱 그렇다.
	 * 
	 * (2) size 객체가 scr 객체를 바꾸는 현상
	 * >> size는 레퍼런스다.
	 * >> scr 과 size가 가리키고있는 데이터의 해시가 같기 때문에 값을 수정하면 scr에서 읽어 들이는 값도 변하게된다.
	 * 
	 * (3)해석
	 * >> 1.layoutList의 레이아웃들은 ReferenceList라는 리스트에 의해 감싸진 상태로 layoutList 내에 존재한다.
	 * >> 2.getDesignated() 는 ReferenceList가 가리키고 있는 본래 값을 가져온다.
	 * >> 3.레이아웃은 Binder와 LayoutInfo로 구성 되어있으며 LayoutInfo는 마지막 index 순번을 가지므로 comp.getComponentCount()-1번지 값을 가져와 LayoutInfo를 가져온다.
	 * 
	 * (E) 이 메소드는 불합리적으로 짜여져있음.
	 * >> 메소드를 다시보면 scr객체에서 부터 각종 요소들을 가져와서 처리한뒤 scr에 적용하는 형식을 가지고 있다.
	 * >> 다시 말하자면 scr내부에서 처리한다면 굳이 데이터를 주고받는 연산들을 할 필요가 없다는 뜻이다.
	 * >> sizeSet 또한 scr 내부에 sizeSet을 등록해서 가져오는 형식이라니라 매개변수로 직접 가져올 수 있다.
	 * >> 그럼에도 이런식으로 코드를 짠 이유는 두가지 이유가 있다.
	 * >> 첫째로, 확실한 역활의 선그음이 이유다.
	 * >> Rotation 버튼 기능에 문제가 생겼다고 가정했을때 유지보수 개발자는 Screen 클래스와 Rotation 클래스중에 어느 클래스를 들여다 볼것 같은가의 문제이다.
	 * >> 만약 효율을 위해서 수 많은 메소드들이 다른 Class 들에 분산 되어진다고 생각해보면 유지보수할때 일일히 어디어디의 무엇이 있는지 전부 찾아야 된다.
	 * >> 만약에 메소드를 분산한 클래스를 수정했는데 문제가 발생한다면 메소트를 줬던 엉뚱한 클래스가 같이 죽을수 있다는 문제도 있다.
	 * >> 두번째로, 이용자 중심으로 짜여져있기 때문이다.
	 * >> 화면 회전기능은 화면할 회전이 있어야 회전이 가능하므로 Screen 객체는 반드시 받아야 하지만 화면의 크기를 Text로 출력하는 SizeSet은 필수 사항이 아니다.
	 * >> 이용자는 화면만 돌리고 싶을 수도 있다.
	 * >> null 을 매개변수로 입력시키고 NullPointerException 처리를 할 수는 있지만 메소드를 부를때마다 null을 적는것이 Image.getWidth(null)처럼 불편을 야기한다.
	 * >> 오버로딩하면 되긴 하는데 사용 안하는 메소드를 하나 추가하는게 그냥 마음에 걸려서 그냥 처음부터 필요없게 만들었다.
	 */
	
	public final static String getKey()
	{return KEY;}

	@Override
	public void destoryer() 
	{}
}

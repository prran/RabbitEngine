package element.tags;

import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;

import javax.swing.BoxLayout;
import javax.swing.JPanel;

import element.layout_list.LayoutList;
import logic.FileConnection;
import tools.ImageButton;

public class BigTag
{
	/*
	 * 버튼을 통해서 LayoutList의 스크롤과 포커스의 위치를 처음/중간/끝으로 이동합니다.
	 */
	
	public final static String HEAD = "HEAD", BODY = "BODY", TAIL = "TAIL";
	
	private final static int TAG_WIDTH = 37, TAG_HEIGHT = 100;
	
	private final static String IMAGE_NOMAL = "images/big_tag_nomal.png",
			IMAGE_PRESSED = "images/big_tag_pressed.png"
			,IMAGE_PUT = "images/big_tag_put.png";
	
	public final JPanel result = new JPanel();
	public LayoutList list;
	
	//-----------------------------
	//creator
	//-----------------------------
	
	/**
	 * It Offer buttons what can move scroll to start,middle,end point.
	 */
	public BigTag(LayoutList list)//설명 생략
	{
		FileConnection.setObject("big tag", result);
		this.list = list;
		list.setBigTag(this);
		result.setLayout(new BoxLayout(result,BoxLayout.X_AXIS));
		result.add(new Tag(TAIL));
		result.add(new Tag(BODY));
		result.add(new Tag(HEAD));
		result.setVisible(false);//레이아웃의 양이 3개 미만일경우 BigTag가 필요하지 않으므로 기본적으로 숨김 처리 되어있다.
	}
	
	//-----------------------------
	//getter
	//-----------------------------
	
	public JPanel get()
	{return result;}
	
	//-----------------------------
	//setter
	//-----------------------------
	
	@SuppressWarnings("serial")
	private class Tag extends ImageButton
	{
		private String label;
		
		public Tag(final String location)//설명 생략
		{
			super(new String[]{IMAGE_NOMAL,IMAGE_PRESSED,IMAGE_PUT}, TAG_WIDTH, TAG_HEIGHT);
			label = location;
			
			this.addActionListener(new ActionListener() 
			{
				public void actionPerformed(ActionEvent e) 
				{list.tools.setScrollByOrder(location);}//하단 설명 참조(1)
			});
		}
		
		@Override
		public void paintComponent(Graphics g)//버튼 이미지를 그린뒤 화면을 90도 돌려서 Label을 버튼위에 출력함.
		{
			super.paintComponent(g);
			
			final Graphics2D g2 = (Graphics2D) g;
	        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
	                RenderingHints.VALUE_ANTIALIAS_ON);
	        
	        AffineTransform at = AffineTransform.getQuadrantRotateInstance(1);
	        g2.setTransform(at);
	        g2.setFont(new Font("Bookman Old Style", Font.PLAIN, 18));
	        g2.drawString(label, 8, -10);
	        repaint();
		}

	}
	/*
	 * Other Why
	 * (1)이 논리는 RotateButton과 정 반대로 짜여져 있음.
	 * >>rotateButton에서는 유지보수를 위해 이 클래스만의 작동에 관련된 메소드는 최대한 그 안에다가 구성하는것이 맞다고 했다.
	 * >>다만 이번엔 예외로 두는 이유가 있는데, 정보은닉을 위해서다.
	 * >>LayoutList 안에있는 LayoutInfo 는 내부적으로 데이터를 public으로 공유하여 사용한다.
	 * >>그래서, LayoutInfo는 LayoutList 내부에서만 객체가 생성되며
	 * >>사용자가 클래스를 뜯어서 고치는게 아니거나 내부 요소를 파악하고있는것이 아니라면
	 * >>객체 사용자가 외부에서 LayoutInfo에 접근할 수 없게 막아두었다.
	 * >>만약 이 메소드 연산을 BigTag 내부에서 처리하려면 LayoutInfo를 직접적으로 getter로 받아야 한다.
	 * >>그렇다고 getter가 생기면 LayoutInfo가 밖으로 노출되는 문제가 생겨서 이를 방지하기 위해서 이 안에서 메소드 처리를 않고 있다.
	 * >>다른 클래스들의 LayoutInfo를 인자로 받는 메소드들은 focus된 데이터 한정으로 LayoutList 에서 레이아웃 내부데이터를 뜯어서 받고있다.
	 * >>이 경우도 데이터 구조를 알고 있는 사람만 받을수 있는 것이이며 focus 되어있는(될 예정인) Info 한정이다.
	 * 
	 * (E)Tag가 패키지로 따로 빠져있는 이유
	 * >>tag는 LayoutList의 내부 요소는 아니고 외부 요소이다.
	 * >>그러나 LayoutList 와 직접적으로 연동되어 작동하는 기능이므로 tag 는 layout_list 패키지 안에 들어갈 수도 있다.
	 * >>그리고 이러한 이유로 같은 패키지 안에 들어간 예시가 graphic_screen 의 RotateButton,SizeSet이다.
	 * >>그럼에도 이번엔 tag를 따로 패키지로 꺼내준 이유는 특점 기준점 때문인데,
	 * >>Screen, ImageView LayoutEvent, ViewStructer, DataLiquildity 모두 LayoutList와 외부연동되는 기능이다.
	 * >>그렇다고 이들을 모두 같은 패키지 안에 넣자니 Screen/DataLiquildity쌍 처럼 서로 연관성이 무관한 클래스들이 같은 패키지에 묶이에 되고
	 * >>이는 유지보수에 좋지 못하다고 판단이 되었다.
	 * >>반대로 생각해서 모든 외부기능등을 세부적으로 모두 패키지로 나누는 것은 패키지의 의미를 상실하게 한다.
	 * >>그래서 이에 대한 기준점을 두고 클래스들을 나누고 있으며 그 기준에 따라 tag는 따로 나눠진 것이다.
	 * >>기준점은 다음과 같다.
	 * >>기준 1.
	 * >>A class 가 B클래스에 종속됨 (패키지 고려)
	 * >>C class 가 B클래스에 종속됨 (패키지 고려)
	 * >>A class 와 C class가  B class 에 종속되고 A 와 B 간에는 연관성이 없음 (개별 패키지)
	 * >>아래는 예시이다.
	 * >>RotateButton은 Screen 에 종속됨 (패키지 고려)
	 * >>SizeSet 은 Screen에 종속됨 (패키지 고려)
	 * >>RotateButton 과 SizeSet은 서로 연관성 있음(패키지 고려)
	 * >>기준 2.
	 * >>UISizeChanger 는 Screen 내부에 들어가는 내부요소 (패키지 확정)
	 * >>이경우엔 내부요소는 Screen 자체의 내부 클래스로 보는 편이 맞다. 그렇다면 UISizeChanger는 Screen으로 취급하여
	 * >>RotateButton은 UISizeChanger 에 종속 되어 있음 으로 판단한다.
	 * >>LayoutList 로 이를 정리해 보자면
	 * >>Binder,LayoutInfo 는 LayoutList의 내부요소,
	 * >>ImageManager는 LayoutList에 종속됨(패키지 고려)
	 * >>Screen은 LayoutList에 종속됨(패키지 고려)
	 * >>ImageManager는 Screen과 연관있음(패키지 고려)
	 * >>BigTag 는 LayoutList에 종속됨(패키지 고려)
	 * >>ImageManager는 BigTag와 연관 없음(개별 패키지)
	 * >>이에 따라 Screen,ImageMager,BigTag 개별 패키지를 지닌다.
	 * >>여기에서 주의할 점은, 종속과 연관은 다르다. 
	 * >>종속은 해당 클래스가 없으면 작동할 수 없다는 것이고
	 * >>연관은 해당 클래스가 없어도 작동할 수 있다는 것이다.
	 */
}

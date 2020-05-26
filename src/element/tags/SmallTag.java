package element.tags;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.LineBorder;

import element.layout_list.LayoutInfo;
import element.layout_list.LayoutList;
import logic.FileConnection;
import logic.ReferenceList;
import tools.ImageButton;
import tools.UserScrollBarUI2;

public class SmallTag{
	
	/*
	 * LayoutList의 Layout들과 매치되어 스크롤 이동 버튼 기능을 제공합니다.
	 * LayoutList내의 Layout간의 순서 변경의 기능을 제공합니다.
	 */
	private final static int SCROLL_WIDTH = 9;
	
	private final static int TAG_WIDTH = 111 , TAG_HEIGHT = 23;
	
	private final static String IMAGE_NOMAL = "images/small_tag_nomal.png",
			IMAGE_PRESSED = "images/small_tag_pressed.png",
			IMAGE_PUT = "images/small_tag_put.png";
	
	//---------------layout instance
	
	private JScrollPane tagLayout;
	private JPanel tagFrame = new JPanel();
	
	//---------------store instance
	
	private LayoutList list;
	private ReferenceList rList = new ReferenceList();
	
	//---------------store for logic
	
	private String dragFocus;
	public Tools tools = new Tools();
	
	//---------------switch
	
	private boolean isChangeMode = false;
	
	//-----------------------------
	//creator
	//-----------------------------
	
	/**
	 * It Offer buttons what can move scroll to matched Layout in LayoutList.
	 * And It can be change the position of Layouts
	 * @param list LayoutList object
	 */
	public SmallTag(LayoutList list)//스크롤바를 추가함
	{
		FileConnection.setObject("small tag", this);
		this.list = list;
		
		tagFrame.setLayout(new BoxLayout(tagFrame,BoxLayout.Y_AXIS));
		
		tagLayout = new JScrollPane(tagFrame,
				JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);//세로로 스크롤바 추가
		tagLayout.getVerticalScrollBar().setUnitIncrement(16);//마우스 휠로 움직이는 스크롤 되는 속도를 조정
		tagLayout.getVerticalScrollBar().setUI(new UserScrollBarUI2());//스크롤바 이미지 조정
		tagLayout.setPreferredSize(new Dimension(Tag.WIDTH,Integer.MAX_VALUE));//바를 지니고 있는 외부 프레임의 크기
		tagLayout.setBorder(null);
		tagLayout.getVerticalScrollBar().setPreferredSize(new Dimension(SCROLL_WIDTH,tagLayout.getVerticalScrollBar().getHeight()));//스크롤의 크기
	}
	
	//-----------------------------
	//getter
	//-----------------------------
	
	/**
	 * @return JScrollPane if it added any Component you can see tags List.
	 */
	public JScrollPane get()//tag 화면 Panel을 내보낸다.
	{return tagLayout;}
	
	//public JPanel get()//tag 화면 Panel을 내보낸다.
	//{return tagFrame;}
	
	//-----------------------------
	//inner Class
	//-----------------------------
	
	@SuppressWarnings("serial")
	public class Tag extends ImageButton
	{
		/*
		 * TagList 안에 존재하는 각각의 Tag 객체입니다.
		 * Tag를 클릭하면 연동되어있는 LayoutInfo 의 위치로 이동합니다.
		 * Tag를 위아래로 드래그하면 LayoutList 내의 Info 와 함께 위치를 변경 할 수 있습니다.
		 */
		private final JLabel label;
		private final LayoutInfo synedInfo;
		
		private Tag(final LayoutInfo info)//테그의 레이아웃을 구성하고 리스너 기능을 추가 합니다.
		{	
			super(new String[]{IMAGE_NOMAL,IMAGE_PRESSED,IMAGE_PUT},TAG_WIDTH,TAG_HEIGHT);
			synedInfo = info;
			info.setSynTag(this);//서로 동기화
			
			setLayout(new BorderLayout());
			
			label = new JLabel(synedInfo.getLayoutName());
			label.setForeground(Color.LIGHT_GRAY);
			
			add(this.label,BorderLayout.CENTER);
			
			final MouseAdapter mouseListener = getTagActions(info);//마우스 드래그시 위치변경 작업
			addMouseListener(mouseListener);
			addMouseMotionListener(mouseListener);
			
			this.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent e) //마우스로 클릭 했을때
				{
					if(!isChangeMode)//순서를 바꾸는 모드(드레그 중이)가 아닐경우
					{
						list.changeFocus(synedInfo);//연동된 Info를 선택모드로 하고
						list.setScrollLocationByInfo(info);//그 위치로 이동한다. 하단설명 참조(1)
					}
				}
			});
		}
		/*
		 * Other Why
		 * (1)이 논리는 RotateButton과 정 반대로 짜여져 있음.
		 * >>rotateButton에서는 유지보수를 위해 이 클래스만의 작동에 관련된 메소드는 최대한 그 작동하는 클래스 내에서 구성하는것이 맞다고 했다.
		 * >>다만 이번엔 예외로 두는 이유가 있는데, 정보은닉을 위해서다.
		 * >>LayoutList 안에있는 LayoutInfo 는 내부적으로 데이터를 public으로 공유하여 사용한다.
		 * >>그래서,LayoutInfo는 LayoutList 내부에서만 객체가 생성되며
		 * >>사용자가 클래스를 뜯어서 고치는게 아니거나 내부 요소를 파악하고있는것이 아니라면
		 * >>객체 사용자가 외부에서 LayoutInfo에 접근할 수 없게 막아두었다.
		 * >>만약 이 메소드 연산을 BigTag 내부에서 처리하려면 LayoutInfo를 직접적으로 getter로 받아야 하고
		 * >>getter가 생기면 LayoutInfo가 밖으로 노출되는 문제가 생겨서 이 클래스에서 연산을 처리를 할수가 없었다.
		 * >>다른 클래스들의 LayoutInfo를 인자로 받는 메소드들은 focus된 데이터 한정으로 LayoutList 에서 레이아웃 내부데이터를 뜯어서 받고있다.
		 * >>이 경우도 데이터 구조를 알고 있는 사람만 받을수 있는 것이이며 focus 되어있는 Info 한정이다.
		 */
		//=======>linked procedure
			private MouseAdapter getTagActions(final LayoutInfo info)//하단 설명 참조
			{
				return
						
				new MouseAdapter() 
				{
					public void mouseReleased(MouseEvent e) 
					{
						if(isChangeMode)//만일 드래그 중에 버튼을 떼었다면
						{
							isChangeMode = false;//모드 해제 하고
							rList.resetChangeTarget();//교환 대상 지정을(잡고있던 테그 정보를) 해제하고
							((JPanel)e.getComponent().getParent()).setBorder(null);//테두리선을 복귀한뒤
							list.changeFocus(synedInfo);//LayoutList 에서 연동된 info를 선택모드로 지정하고 
							list.setScrollLocationByInfo(info);//스크롤을 그 위치로 이동한다.
						}
					}
					public void mouseEntered(MouseEvent e) 
					{
						if(!isChangeMode)//드래그 모드가 아니라면
						{
							tagLayout.getVerticalScrollBar().setPreferredSize(new Dimension(SCROLL_WIDTH,tagLayout.getVerticalScrollBar().getHeight()));
							tagLayout.revalidate();
						}//마우스가 tag에 있는동안에 스크롤 바를 보여준다.
						else//드래그 모드라면
						{
							if(dragFocus != synedInfo.getLayoutName())//하단 설명 참조 (1)
							{
								rList.changeLocation((ReferenceList) e.getComponent().getParent());//지정했던 태그와 위치를 바꾸고
								list.changeLocation(((ReferenceList)synedInfo.getParent().getParent()));//LayoutList 내의 info의 위치도 바꾼다.
								((JPanel)e.getComponent().getParent()).setBorder(new LineBorder(null));//하단 설명 참조 (1)
								
							}
						}
					}
					public void mouseExited(MouseEvent e) 
					{
						if(!isChangeMode)//드래그 모드가 아니라면
						{
							tagLayout.getVerticalScrollBar().setPreferredSize(new Dimension(0,tagLayout.getVerticalScrollBar().getHeight()));
							tagLayout.revalidate();
						}//마우스가 tag위에 없다면 스크롤바를 숨긴다.
						if(isChangeMode)//드래그 모드라면
						{
							((JPanel)e.getComponent().getParent()).setBorder(null);//테두리선을 복구하고
							((JPanel)e.getComponent().getParent()).repaint();//적용한다.
						}
					}
					
					public void mouseDragged(MouseEvent e) 
					{
						isChangeMode = true;//드래그모드로 락을 걸고
						rList.setChangeTarget((ReferenceList) e.getComponent().getParent());//현재 잡고있는 태그를 교환의 중점 태그로 지정한다.
						((JPanel)e.getComponent().getParent()).setBorder(new LineBorder(Color.WHITE,1));//드래그로 잡힌 태그는 테두리선이 생기며
						list.setLocationChangeTarget((ReferenceList) synedInfo.getParent().getParent());//연동된 Info도 같이 중점 처리를 한다.
						dragFocus = info.getLayoutName();//하단 설명 참조 (1)
					}
					
				};
			}
			/*
			 * 기능: 드래그로 LayoutList 내의 LayoutInfo 와 Tag의 위치를 변경하는 기능을 내보낸다.
			 * 1.마우스를 놓으면 드래그 작업을 끝내고 LayoutList내의 포커스를 연동된 LayoutInfo로 이동 시킨다.
			 * 2.마우스를 올려두면 스크롤바가 보이게하고, 드래그 중이엿다면 위치를 서로 교환한다.
			 * 3.마우스가 벗어나면 스크롤바를 숨기고, 드래그 중이였다면 테두리선 표시를 복구한다.
			 * 3.드래그를 시작하면 드래그 중점 타겟을 설정하고 연동값에도 적용한다.
			 */
		
		/**
		 * reName tag label
		 * @param newName any String
		 */
		public void reName(String newName)//태그의 이름을 변경할 수 있다.
		{
			label.setText(newName);
		}
	}
	
	public class Tools
	{
		private Tools() {}
		

		/**
		 * @deprecated 
		 */
		public void addTag(LayoutInfo info)//LayoutList에서 새로운 LayutInfo를 만들때 태그 만들고 동기화를 하기위해 사용.
		{
			tagFrame.add(((ReferenceList)rList.getBoxing(new Tag(info))).setViewMode(),0);
			tagLayout.revalidate();
		}//사실 사용하고 싶어도 외부의 객체 사용자는 LayoutInfo를 구할수 없을 것이다.
		
		/**
		 * it return tageList and clear all tag Component.
		 */
		public ReferenceList expertTagList()//LayoutList 에서 View 사용시에 작업 환경을 저장하고 보내기 위해서 사용
		{
			ReferenceList returnlist = rList;
			rList = new ReferenceList();
			tagLayout.revalidate();
			return returnlist;
		}
		//=======>linked method
			/**
			 * get tagList and Apply in this.
			 */
			public void importTagList(ReferenceList list)//LayoutList 에서 View 사용시에 작업 환경을 불러오기 위해서 사용
			{
				try
				{
					rList = list;
					final int count = rList.getCount();
					tagFrame.removeAll();
					for(int i = 0; i<count;i++)
					{
						tagFrame.add(rList.getReferenceList(i));
					}
					tagLayout.repaint();
					tagLayout.revalidate();
				}
				catch(Exception e)//null 을 인자로 받았다면
				{
					rList = new ReferenceList();
					tagFrame.removeAll();
					tagLayout.repaint();
					tagLayout.revalidate();
				}
			}
	}

}

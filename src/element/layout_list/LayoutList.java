package element.layout_list;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.LineBorder;

import element.graphic_screen.Screen;
import element.lmage_manager.ImageManager;
import element.tags.BigTag;
import element.tags.SmallTag;
import logic.FileConnection;
import logic.ReferenceList;
import tools.GradationPanel;
import tools.RemovePopup;
import tools.UserScrollBarUI2;

public class LayoutList{
	
	/*
	 * 이 클래스는 지니고있는 LayoutInfo 정보를 출력하는 리스트입니다.
	 * LayoutInfo와 각종 객체의 연결다리 역활을 합니다.
	 */
	
	
	private static final Color BACKGROUND_COLOR = Color.getColor("gray", 0XFFF2F2F2);
	private static final Color BORDER_COLOR = Color.WHITE;
	
	private static final Color LIMIT_SEPARATE_COLOR1 = Color.getColor("graygray", 0XFFAAAAAA),
			LIMIT_SEPARATE_COLOR2 = Color.WHITE;
	
	private final static int SCROLL_WIDTH = 10;
	
	private final static String KEY = "K#FFQGA%1h";
	
	//---------------layout instance
	
	private final GradationPanel head = new GradationPanel(LIMIT_SEPARATE_COLOR1,LIMIT_SEPARATE_COLOR2);
	private final GradationPanel tail = new GradationPanel(LIMIT_SEPARATE_COLOR2,LIMIT_SEPARATE_COLOR1);
	
	private final JPanel list = new JPanel();
	private RemovePopup popup;
	private JScrollPane layoutListSc;
	
	//---------------store instance
	
	private LayoutInfo focus;
	private LayoutInfo clickedInfo;
	private SmallTag smallTag;
	private BigTag bigTag;
	private Screen scr;
	private ImageManager im;
	private ReferenceList rList = new ReferenceList();
	
	//---------------store for logic
	
	private static Logic logic;
	public final Tools tools = new Tools();
	
	//---------------variable
	
	private int priority = 1;
	
	//-----------------------------
	//creator
	//-----------------------------
	
	public LayoutList()
	{
		FileConnection.setObject("layout viewer",this);
		
		list.setLayout(new BoxLayout(list,BoxLayout.Y_AXIS));
		list.setBackground(BACKGROUND_COLOR);
		setScroll();
	}
	//=======>linked procedure
		private void setScroll()//Panel에 스크롤을 추가함
		{
			layoutListSc 
			= new JScrollPane(setLimit(list),
					JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
					JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
			
			layoutListSc.getVerticalScrollBar().setUnitIncrement(16);//마우스 휠 속도. Default는 너무 느려서 수정함.
			layoutListSc.getVerticalScrollBar().setUI(new UserScrollBarUI2());//스크롤바 모양
			layoutListSc.setBorder(new LineBorder(BORDER_COLOR,1));
			layoutListSc.getVerticalScrollBar().setPreferredSize(new Dimension(SCROLL_WIDTH,layoutListSc.getVerticalScrollBar().getHeight()));
			layoutListSc.addMouseListener(new MouseAdapter() 
			{
				public void mouseEntered(MouseEvent e)//마우스가 리스트안에 있으면 바가 보이고 없으면 사라짐.
				{
					layoutListSc.getVerticalScrollBar().setPreferredSize(new Dimension(SCROLL_WIDTH,layoutListSc.getVerticalScrollBar().getHeight()));
					layoutListSc.revalidate();
				}
				public void mouseExited(MouseEvent e) 
				{
					layoutListSc.getVerticalScrollBar().setPreferredSize(new Dimension(0,layoutListSc.getVerticalScrollBar().getHeight()));
					layoutListSc.revalidate();
				}
			});
		}
		//=======>linked procedure
			private JPanel setLimit(JPanel panel)//화면의 시작과 끝에 리스트의 끝지점을 알리는 구분자를 넣는다.
			{
				final JPanel result = new JPanel();
				result.setLayout(new BorderLayout());
				result.add(head,BorderLayout.NORTH);
				result.add(panel,BorderLayout.CENTER);
				result.add(tail,BorderLayout.SOUTH);
				
				return result;
			}
	
		
	//-----------------------------
	//setter
	//-----------------------------
	
	/**@deprecated
	 */
	public static void setLogic(Logic e)
	{logic = e;}
	
	/**
	 * if you have multiple LayoutList or Tag, it must have calling
	 */
	public void setSmallTag(SmallTag smallTag)//만일 LayoutList를 여럿 사용하거나 Tag를 여럿 사용 하고자 할 경우 사용해야한다.
	{this.smallTag = smallTag;}
	
	/**
	 * if you have multiple LayoutList or Tag, it must have calling
	 */
	public void setBigTag(BigTag bigTag)//만일 LayoutList를 여럿 사용하거나 Tag를 여럿 사용 하고자 할 경우 사용해야한다.
	{
		this.bigTag = bigTag;
	}
	
	public void setScreen(Screen scr)//스크린을 여러개 사용할때 대체할 수있다.
	{this.scr = scr;}
	
	public void setImageManager(ImageManager im)//ImageManager를 여러개 사용하고자 한다면 대체할수 있다.
	{this.im = im;}
		
	//-----------------------------
	//getter
	//-----------------------------
	
	/**
	 * @return JScrollPane component of Graphical Layout list.
	 */
	public JScrollPane get()
	{return layoutListSc;}
	
	/**
	 * it can be use if you want content of Info List
	 * @return JPanel
	 */
	public JPanel getList()//scroll을 지닌 외부 화면 값이 아니라 내부의 info 데이터 목록을 가져오고자 할때 쓸수 있다.
	{return list;}
	
	/**
	 * expert Layouts and Synchronized Objects data. 
	 * it can be readable layoutListOb.importData(data)
	 */
	public LayoutListData expertData()//LayoutList의 모든 정보를 내보낸다.
	{
		return new LayoutListData(rList,smallTag,focus,KEY);
	}
	//=======>linked method
		/**
		 * set LayoutList Data and Apply it.
		 * you can use it Graphical button by ViewStrcture.
		 * @param data LayoutListData where Obtained layoutListOb.expertData()
		 */
		public void importData(LayoutListData data)//LayoutListData 에서 정보를 읽어와서 적용한다.
		{
			try
			{
				data.unlock(KEY);//데이터 암호를 해제
				final ReferenceList tags = data.getSmallTagList();
				
				tools.getScreen().newScreenImage();
				
				rList = data.getList();//내부 Layout 데이터를 받아옴
				if(rList.getCount() == 0)
					rList.get(-1);
				final int count = rList.getCount();
				list.removeAll();
				for(int i = 1; i<=count;i++)//화면상의 출력 요소들을 받아옴
				{list.add(rList.getReferenceList(count-i));}
			
				try 
				{smallTag.tools.importTagList(tags);}
				catch(Exception e) 
				{
					try
					{(smallTag = (SmallTag)FileConnection.getObject("small tag")).tools.importTagList(tags);}//하단 설명 참조 (1)
					catch(Exception ex) {}
				}
			
				updateBigTag();
				
				list.repaint();
			
				try
				{(focus = data.getFocus()).inFocus();}
				catch(Exception e)
				{focus = null;}
				tools.getScreen().repaint();
			}
			catch(Exception e)//data로 null 이 왔다면...
			{
				try
				{tools.getScreen().newScreenImage();}
				catch(Exception ex) {}
				
				rList = new ReferenceList();
				list.removeAll();
				
				try 
				{smallTag.tools.importTagList(null);}
				catch(Exception ex) 
				{
					try
					{(smallTag = (SmallTag)FileConnection.getObject("small tag")).tools.importTagList(null);}
					catch(Exception exc) {}
				}
				
				updateBigTag();
				list.repaint();
				try
				{
					tools.getScreen().getUISizeChanger().get().setVisible(false);
					tools.getScreen().repaint();
				}
				catch(Exception ex) {}
			}
		}
		/*
		 * Other Why
		 * (1)tag의 try catch
		 * >>등록되어있는 Tag가 존재 한다면 그 태그에 우선적으로 TagList를 업데이트하고
		 * >>만일 등록한 Tag가 없다면 Tag 객체를 선언했는지 확인해서 그 태그에 적용한다.
		 * >>Tag 객체 자체가 없다면 아무일도 하지 않는다.
		 */
	
	
	//-----------------------------
	//actions
	//-----------------------------
	
	/**
	 * set Scroll bar location where Info is set.
	 * @param info LayoutInfo Button in LayoutList.
	 */
	public void setScrollLocationByInfo(final LayoutInfo info)//해당 LayoutInfo 를 지닌 버튼의 위치로 스크롤을 이동함.
	{
		final float maxValue = layoutListSc.getVerticalScrollBar().getMaximum();
		final float locationRate = (float)(rList.getPriority((ReferenceList) info.getParent().getParent())+1)/(float)rList.getCount();//하단 설명 참조(1)
		final float location = maxValue - locationRate * maxValue;
		layoutListSc.getVerticalScrollBar().setValue((int)location);
	}
	/*
	 * Other Why
	 * (1)위치조정
	 * >>LayoutListSc는 Component 화 되어있는 rList를 의 요소들을 지니고 있다.
	 * >>rList 와 locationRate 가 가리키는 객체는 같으므로 상대적으로 객체의 순서 정보를 지니고 있는 rList에서 버튼의 위치를 파악하고 그 위치로 이동한다.
	 * >>+1이 되는 이유는 
	 * >>이렇게 가져온값으로 바로 이동하면 
	 * >>해당버튼이 화면의 최상단에 보이는 위치값을 지니기 때문에 
	 * >>그 다음 번지를 가리켜 위치를 좀더 아래로 배치하게 하여 상단에 여유를 주기 위함이다.(사용상 편의 문제)
	 * >>info.getParent().getParent() = info > info를 지닌 버튼 > 버튼을 가리키는 레퍼런스.
	 */
	
	/**
	 * Change focus where the Focused info to new info
	 */
	public void changeFocus(LayoutInfo newFocus)//설명 생략
	{
		focus.outFocus();//버튼 색상을 원래값으로 복구
		focus = newFocus;
		focus.inFocus();//버튼색상을 포커스로 변경, 각종 연관 객체들 포커스 변경사항을 전달
	}
	
	/**
	 * set target what doing change location in list.
	 * @param target LayoutInfo Button in LayoutList.
	 */
	public void setLocationChangeTarget(ReferenceList target)//changeLocation 의 중심이 되는 Info를 지정한다.
	{rList.setChangeTarget(target);}
	/*
	 * Other Why
	 * (E)changeLocation(target,target2) 말고 이게 별도로 필요한가?
	 * >> 변경할 타겟을 정하는 것과 실제 변경 작업을 하는 작업을 별도의 작업에서 처리하기 위해서 사용된다.
	 * >> 스레드 1에는 target 만 있고 스레드 2 에는 target2만 있는데 둘을 교환 한다고 가정해 보면 쉽다.
	 */
	//=======>linked method
		/**
		 * change button location where targeted Button is set
		 * it need prepare to call method setLocationChangeTarget(ReferenceList target)
		 */
		public void changeLocation(ReferenceList target)//Info 목록의 순서를 교환한다.
		{
			Binder binder = null;
			ReferenceList changeWith = rList.getChangeTarget();
			if(rList.getPriority(changeWith)>rList.getCount()-2)//하단 설명 참조(1)
			{
				binder = (Binder) ((JPanel)changeWith.getDesignated()).getComponent(0);//하단 설명 참조(2)
				binder.setVisible(true);
				binder = (Binder) ((JPanel)target.getDesignated()).getComponent(0);
				binder.setVisible(false);
				list.revalidate();
			}
			else if(rList.getPriority(target)>rList.getCount()-2)
			{
				binder = (Binder) ((JPanel)target.getDesignated()).getComponent(0);
				binder.setVisible(true);
				binder = (Binder) ((JPanel)changeWith.getDesignated()).getComponent(0);
				binder.setVisible(false);
				list.revalidate();
			}
			rList.changeLocation(target);
		}
		/*
		 * Other Why
		 * (1)바꾸고자 하는 상대쪽이 리스트의 첫번째 라면..
		 * >>reList.getCount()는 현재 버튼의 갯수
		 * >>-1 은 인덱스값 -2는 마지막 인덱스의 이전값
		 * >>마지막 인덱스의 이전값 보다 크다 = 마지막 값이다.
		 * >>이 리스트는 사용자 편의를 위해서 역순으로 정렬된다.
		 * >>하므로 이는 리스트의 첫번째 값을 의미.
		 * (2)해석
		 * >>changeWith 은 레퍼런스이므로
		 * >>getDesignated()는 가리키는 값을 가져옴.
		 * >>이 값은 Binder 와 LayoutInfo 를 지니는 한 JPanel의 값이므로 getComponent(0)는 0번지의 Binder를 가져온다.
		 */
	
	/**
	 * it can be use to refresh Layout Information to linked Object
	 */
	public void reFocus()//설명 생략
	{
		try{focus.inFocus();}
		catch(Exception e) {}
	}
	
	public void noFocus()//설명 생략
	{
		focus.noFocus();
		focus = null;
	}
		
	@SuppressWarnings("deprecation")
	public void addLayoutInfo(final File file)//새로운 Layout을 추가하고 Tag와 동기화 한뒤  list에 등록한다.
	{
		final JPanel layout = new JPanel();
		layout.setLayout(new BorderLayout());
		layout.setMaximumSize(new Dimension(LayoutInfo.WIDTH,LayoutInfo.HEIGHT+Binder.HEIGHT));
		
		final LayoutInfo info = logic.newInfo();
		info.scr = scr;
		info.im = im;
		info.setNewData(priority++, file);
		try 
		{smallTag.tools.addTag(info);}//하단 설명 참조(1)
		catch(Exception e) 
		{
			try
			{(smallTag = (SmallTag)FileConnection.getObject("small tag")).tools.addTag(info);}
			catch(Exception ex)
			{}
		}
		
		try 
		{focus.outFocus();}
		catch(Exception e){}//첫실행으로 Focus 되어있는 Layout 이 존재하지 않음.
		(focus = info).inFocus();
		
		setClickListener(info);
		
		final Binder binder = new Binder(info,this);
		binder.setVisible(false);
		
		layout.add(binder,BorderLayout.NORTH);
		layout.add(info,BorderLayout.CENTER);
		list.add(rList.getBoxing(layout).setViewMode(),0);//하단 설명 참조(2)
		
		try
		{((JPanel)((ReferenceList)list.getComponent(1)).getDesignated()).getComponent(0).setVisible(true);}//하단 설명 참조(3)
		catch(Exception e) {}//첫실행으로 Layout 이 존재하지 않음.
		
		updateBigTag();

	}
	/*
	 * Other Why
	 * (1)tag의 try catch
	 * >>등록되어있는 Tag가 존재 한다면 그 태그를 우선으로 info와 동기화 하고
	 * >>만일 등록한 Tag가 없다면 Tag 객체를 선언했는지 확인해서 그 태그에 적용한다.
	 * >>Tag 객체 자체가 없다면 아무일도 하지 않는다.
	 * (2)해석
	 * >>rList.getBoxing : 레퍼런스리스트에서 새로운 리스트를 추가하고 레퍼런스가 Box를 가리키게함.
	 * >>.setViewMode : 리스트가 가리키고있는 컴포넌트를 투영하여 자기 자신으로 보이게함
	 * (3)binder를 재 설정한다.
	 * >>LayoutList의 첫번째 레이아웃은 레이아웃을 bind 작업을 할 이전 Layout이 없으므로 Binder가 비활성화 되어있다.
	 * >>하므로 이 Layout의 Binder를 활성화 하고 이번에 새롭게 만들어진 Layout을 첫번째 값으로 추가한다.
	 * (E)Layout을 클래스로 따로 두지 않는 이유
	 * >>Layout 객체는 만든다면 LayoutInfo 와 Binder로 구성된다.
	 * >>코드를 짜다보니 그중에서 LayoutInfo의 비율이 압도적으로 높아서 클래스의 정체성을 알수 없었다.
	 * >>유지보수 적으로 부대찌개를 예로 든다면 부대찌개의 맛을 변형 하고자 각종 식재료들을 냉장고에서 찾는데.
	 * >>뚜껑을 열어보니 그곳엔 햄이 잔뜩있고 야채와 육수가 안쪽에 박혀있어서 식재료를 꺼내기 힘들었다는 느낌이라보면 쉽다.
	 * >>그래서 LayoutInfo를 따로 꺼내서 작성하게 되었고, Layout 내부에는 Binder 만이 남게 되었으나
	 * >>이 또한 문제가 있다 판단하여 Layout을 없애고 LayoutInfo 와 Binder를 따로 두게 되었다
	 */
	//=======>linked procedure
		private void setClickListener(final LayoutInfo info)//마우스 좌 클릭시 포커스설정, 우클릭시 삭제팝업 띄움.
		{
			info.addMouseListener(new MouseAdapter() 
			{
				@Override
				public void mouseClicked(MouseEvent e) 
				{
					if(e.getButton()==MouseEvent.BUTTON1)//LayouyList내에서 Layout하나를 클릭하면 중점을 바꾼다.
					{
						focus.outFocus();
						(focus = info).inFocus();
					}
				else if(e.getButton()==MouseEvent.BUTTON3)//우클릭시엔 레이아웃 삭제 팝업 메뉴를 띄운다.
				{	
					clickedInfo = (LayoutInfo)e.getComponent();
					try
					{popup.show(e.getComponent(),e.getX(),e.getY());}
					catch(Exception ex)
					{(popup = new RemovePopup(getLayoutRemoveAction())).show(e.getComponent(),e.getX(),e.getY());}
				}
				
			}
			public void mouseEntered(MouseEvent e)//하단 설명 참조 (1)
			{
				layoutListSc.getVerticalScrollBar().setPreferredSize(new Dimension(SCROLL_WIDTH,layoutListSc.getVerticalScrollBar().getHeight()));
				layoutListSc.revalidate();
			}
			public void mouseExited(MouseEvent e) 
			{
				layoutListSc.getVerticalScrollBar().setPreferredSize(new Dimension(0,layoutListSc.getVerticalScrollBar().getHeight()));
				layoutListSc.revalidate();
			}
			});
		}
		/*
		 * (1) 각각의 Layout에 스크롤바 작업을 넣는이유
		 * >> LayoutList는 Layout의 뒤에 존재하기 때문에 Layout이 있을경우 마우스를 올리는 작업을 해도 LayoutList에서 마우스 리스너가 블러지진 않는다.
		 * >> 그래서 레이아웃마다 마우스를 올리면 스크롤바를 숨기는 작용을 추가했다.
		 */
		//=======>linked method	
			private Runnable getLayoutRemoveAction()//하단 설명 참조
			{
				return
				
				new Runnable()
				{
					@Override
					public void run()
					{
						clickedInfo.outFocus();
						focus.outFocus();
							
						if(list.getComponentCount()<=1)//리스트의 첫번째만 있을때
						{
							clickedInfo.noFocus();
						}//관련된 모든 객체의 집중을 해제한다.
						else if(clickedInfo.getParent().equals(((JPanel) list.getComponent(0)).getComponent(0)))//리스트 목록이 2개이고 그중 첫번째를 지울때
						{
							JPanel fristLayout = (JPanel) ((JPanel) list.getComponent(list.getComponentCount()-1)).getComponent(0);
							JPanel rmBinder = (JPanel)((JPanel) list.getComponent(1)).getComponent(0);
							rmBinder.getComponent(0).setVisible(false);//두번째 레이아웃의 Binder 버튼을 제거하고
							rmBinder.revalidate();
							focus = ((LayoutInfo)fristLayout.getComponent(fristLayout.getComponentCount()-1));//그 버튼을 제거한 Layout을 선택모드로 변환한다.
						}
						else//기타 2번째 이상 번지의 레이아웃을 지우고자 할때
						{
							if(focus.equals(clickedInfo))//만약 지우고자하는 레이아웃이 선택 모드였다면
							{
								JPanel beforeLayout = (JPanel)((ReferenceList)clickedInfo.getParent().getParent()).getNextList().getDesignated();
								focus = ((LayoutInfo)beforeLayout.getComponent(beforeLayout.getComponentCount()-1));//이전 번지에있는 Layout를 선택모드로 변환한다.
							}
						}
						
						clickedInfo.removeWithSyn();//연동된 값들 제거
						list.remove(clickedInfo.getParent().getParent());//LayoutList 화면 상의 제거
						rList.removeList(clickedInfo.getParent().getParent());//LayoutList 내부 값 제거
						--priority;
						list.revalidate();
						scr.repaint();
						

						updateBigTag();
						
						try
						{focus.inFocus();}
						catch(Exception e){}//Layout을 모두 삭제하여 선택 요소가 존재하지 않음
					}
				};
			}
			/*
			 * 기능: 레이아웃을 삭제하는 리스너를 건네준다.
			 * >>1.선택모드를 해제함
			 * >>2.삭제대상을 재외한 적절한 대상을 선택모드로 지정함
			 * >>3.삭제작업
			 * >>4.선택모드로 설정함
			 */
			
	//-----------------------------
	//Other functions
	//-----------------------------
			
	private void updateBigTag() //importData(),addLayoutInfo(),getLayoutRemoveAction()에서 사용. 하단 설명 참조
	{
		if(list.getComponentCount()>2)//레이아웃이 3개 이상이면
			try
			{bigTag.get().setVisible(true);}//bigTag를 출력
			catch(Exception e) 
			{
				try
				{(bigTag = (BigTag)FileConnection.getObject("big tag")).get().setVisible(true);}//등록된 BigTag가없다면 선언된 객체를 찾아서 출력
				catch(Exception ex) {}//선언된 객체가 없다면 아무일도 하지 않음.
			}
		else
			try
			{bigTag.get().setVisible(false);}
			catch(Exception e) 
			{
				try
				{(bigTag = (BigTag)FileConnection.getObject("big tag")).get().setVisible(false);}
				catch(Exception ex) {}
			}
	}
	/*
	 * 기능: 레이아웃 갯수가 3개 이상 미만일때 BigTag를 출력,숨김
	 * 
	 * Other Why
	 * (E)원래 이 기능은 BigTag Class 에 존재해야한다
	 * >>이 구절이 자주 사용되기 때문에 따로 메소드를 만들었지만 이 메소드는 엄연히 BigTag의 기능이다.
	 * >>그럼에도 LayoutList에 정의한 이유는 한계성 때문이다.
	 * >>BigTag에 이 메소드를 정의한다 하더라도 환경상 이 클래스의 사용자가 BigTag를 사용하는지 안하는지 불확실하다.
	 * >>즉 bigTag.updateBigTag() 메소드를 사용 할때 null이 발생할수 있으며 이를 !=null로 처리 해야한다.
	 * >>그러나 본인은 BigTag 객체가 선언 되어있다면 다시 찾아서 연산을 수행하고 있고
	 * >>이를 적용하기위해서 try Exception을 사용한다. 
	 * >>그리고 이를 적용하면 외부에서 정의한 내용과 BigTag안에 정의한 내용이 결국 똑같아져서 내부에 정의한 내용들이 정말 무의미해진다.
	 */
			
	//-----------------------------
	//inner Class
	//-----------------------------
	
	public interface Logic//이 인터페이스는 LayoutInfo를 LayoutList에서만 만들수 있게하기 위해 사용된다.
	{public LayoutInfo newInfo();}
	
	public class Tools//이 클래스는 LayoutList의 순수 기능은 아니지만 다른 관련 객체에서 필요로 하는 메소드를 모아놓음.
	{
		private Tools() {}
		
		/**
		 * @deprecated
		 */
		public Screen.ScreenListener getHandler()//Screen에서 사용하는 메소드.
		{
			return
					
			new Screen.ScreenAdapter()
			{
				public void droppedInScreen(File file){addLayoutInfo(file);}
				public void screenSizeChanged() {updateInfos();}
			};
		}
		//=======>linked procedure
			private void updateInfos()//LayoutInfo들의 정보를 갱신한다.
			{
				for(ReferenceList rf:rList.getList())
				{
					JPanel layout = ((JPanel)rf.getDesignated());
					((LayoutInfo)layout.getComponent(layout.getComponentCount()-1)).updateDataByScrSize();
				}
				
				reFocus();
			}
		/**
		 * remove one Layout in List
		 */
		public void remove(ReferenceList c)//Binder.actionPerformed()에서 사용하는 메소드. 내부의 Info 하나를 제거한다.
		{
			final JPanel layout = (JPanel)c.getDesignated();
			try
			{((LayoutInfo)layout.getComponent(layout.getComponentCount()-1)).removeWithSyn();}//연동된 값들 제거
			catch(Exception e)//
			{
				System.err.println("LayoutList.remove(): can't found Compoent in LayoutList(-6)");
				return;
			}
			list.remove(c);//LayoutList 화면 상의 제거
			rList.removeList(c);//LayoutList 내부 값 제거
			--priority;
			list.revalidate();
			
			try
			{
				if(list.getComponentCount()<3)
				try
				{bigTag.get().setVisible(false);}
				catch(Exception e) 
				{
					try
					{(bigTag = (BigTag)FileConnection.getObject("big tag")).get().setVisible(false);}
					catch(Exception ex) {}
				}
				
			}
			catch(Exception e){}
		}
		/**
		 * move scroll and focus to Start,Middle,End.
		 * @param staticOrder Inputs.HEAD,Inputs.BODY,Inputs.TAIL
		 */
		public void setScrollByOrder(final String staticOrder)
		{
			int location = 0;
			focus.outFocus();
			switch(staticOrder)
			{
			case BigTag.HEAD:
				focus = (LayoutInfo) ((JPanel)rList.get(rList.getCount()-1)).getComponent(1);
				break;
			case BigTag.BODY:
				final int maxValue = layoutListSc.getVerticalScrollBar().getMaximum();
				final float locationRate = (rList.getCount()-1)/2/(float)rList.getCount();
				location = (int) (locationRate * (float)maxValue);
				focus = (LayoutInfo) ((JPanel)rList.get((rList.getCount()-1)/2)).getComponent(1);
				break;
			case BigTag.TAIL:
				location = layoutListSc.getVerticalScrollBar().getMaximum();
				focus = (LayoutInfo) ((JPanel)rList.get(0)).getComponent(1);
				break;
			}
			focus.inFocus();
			layoutListSc.getVerticalScrollBar().setValue(location);
		}
		
		public Screen getScreen()//ViewInfo에서 이미지 동기화를 위해서 사용
		{
			if(scr == null)
				scr = (Screen)FileConnection.getObject("screen");
			return scr;
		}
	}
	
	public class LayoutListData//DTO.. LayoutList의 정보를 한 객체에 압축 해서 전달함.
	{
		private final String key;
		private final ReferenceList list;
		private final ReferenceList sTagList;
		private final LayoutInfo focus;
		private boolean lock = true;
		private LayoutList layoutList = LayoutList.this;
		
		private LayoutListData(ReferenceList list, SmallTag sTag, LayoutInfo focus, String key)
		{
			ReferenceList tagsData = null;
			if(sTag != null)
				tagsData = sTag.tools.expertTagList();
			this.list = list;
			this.sTagList = tagsData;
			this.key = key;
			this.focus = focus;
		}
		
		public void unlock(String key)
		{
			if(this.key == key)
				lock = false;
		}
		
		public ReferenceList getList()
		{
			if(!lock)
				return list;
			else
				return null;
		}
		
		public ReferenceList getSmallTagList()
		{
			if(!lock)
				return sTagList;
			else
				return null;
		}
		
		public LayoutInfo getFocus()
		{
			if(!lock)
				return focus;
			else
				return null;
		}
		
		public LayoutList getFetcher()
		{return layoutList;}
	}
}

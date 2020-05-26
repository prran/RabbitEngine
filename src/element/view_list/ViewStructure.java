package element.view_list;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.LineBorder;

import element.graphic_screen.Screen;
import element.graphic_screen.Screen.ScreenAdapter;
import element.layout_list.LayoutList;
import logic.FileConnection;
import logic.ReferenceList;
import tools.ImageButton;
import tools.RemovePopup;

public class ViewStructure 
{
	/*
	 * 이 클래스는 LayoutList의 작업 환경을 여럿 저장할수 있게합니다.
	 * 내부 요소인 Add button 과 RemovePopup이 있습니다.
	 * Add button 으로 새로운 작업 환경을 추가 할수 있고
	 * 마우스 우클릭으로 팝업 표시되는 제거 버튼을 통해서 작업 환경을 지울 수 있습니다.
	 */
	
	private static final int SCROLL_HEIGHT = 10;
	private static final int HEIGHT = 300;
	private static final int ADD_VIEW_BUTTON_WIDTH = 250;
	
	private static final String ADD_VIEW_NOMAL_IMAGE = "images/view_add_nomal.png",
			ADD_VIEW_PRESSED_IMAGE = "images/view_add_press.png",
			ADD_VIEW_PUT_IMAGE = "images/view_add_put.png";
	
	//---------------layout instance
	
	private final JPanel list = new JPanel();
	private final RemovePopup popup;
	private JScrollPane layoutListSc;
	private JPanel frame = new JPanel();
	
	//---------------store instance
	
	private final ReferenceList rList = new ReferenceList();
	private final LayoutList layoutStructure;
	private ViewInfo focus;
	private Screen scr;
	
	//---------------store for logic
	public final Tools tools = new Tools();
	private static Logic logic;
	
	//-----------------------------
	//creator
	//-----------------------------
	/**
	 * It can store LayoutList work state, and exchange works
	 * @param layoutStruecture LayoutList what want store work state
	 */
	public ViewStructure (final LayoutList layoutStruecture)//초기화, ViewInfo 를 한개 생성후 기능추가,리스트에 Info등록, 다른객체에 작업을 보냄, 레이아웃 설정
	{
		FileConnection.setObject("View", this);
		ViewInfo.StaReadyer();
		this.layoutStructure = layoutStruecture;

		list.setLayout(new FlowLayout(FlowLayout.LEFT,0,0));
		
		
		frame.setLayout(new BorderLayout());
		frame.add(getAddViewButton(),BorderLayout.EAST);
		frame.add(setScroll(list),BorderLayout.CENTER);
		
		tools.setHandler();//screen객체 의 크기가 변하면 화면 이미지를 받는 작업을 보냄
		
		popup = new RemovePopup(new RemoveRunnable(addView()));//뷰를 한개 추가하고 제거 팝업창을 초기화 한다.
	}
	//=======>linked procedure
		private ImageButton getAddViewButton()//클릭하면 작업환경을 저장하는 View 를 하나 추가한다.
		{
			ImageButton addButton = new ImageButton(new String[] {ADD_VIEW_NOMAL_IMAGE,ADD_VIEW_PRESSED_IMAGE,ADD_VIEW_PUT_IMAGE},ADD_VIEW_BUTTON_WIDTH,HEIGHT);
			addButton.addActionListener(new ActionListener() 
			{
				public void actionPerformed(ActionEvent e) 
				{
					addView();
					frame.revalidate();
				}
			});
		
			return addButton;
		}
	//=======>linked procedure2
		private ReferenceList addView()//현재 스크린 이미지를 투영하여 뷰를 하나 만들고 스크롤 위치를 조정한다.
		{
			try {focus.outFocus(layoutStructure);}
			catch(Exception e) {}
			
			Image scrImage = null;
			try
			{scrImage = layoutStructure.tools.getScreen().getScreenImage();}
			catch(Exception e) {}
			
			focus = logic.newInfo(scrImage);
			focus.addMouseListener(getViewClickedAction());
			focus.inFocus(layoutStructure);
			
			final ReferenceList result = ((ReferenceList)rList.getBoxing(focus)).setViewMode();
			list.add(result);
			list.repaint();
			new Thread(new Runnable()
			{
				public void run() 
				{
					try 
					{
						Thread.sleep(100);
						final int maxValue = layoutListSc.getHorizontalScrollBar().getMaximum();//layoutListSc가 null
						layoutListSc.getHorizontalScrollBar().setValue(maxValue);
						layoutListSc.repaint();
					}
					catch(Exception e) {}
				}
			}).start();
			
			return result;
		}
		//=======>linked procedure
			private MouseAdapter getViewClickedAction()//좌클릭시 LayoutList의 작업환경을 이동, 우클릭시 삭제 팝업을 보임.
			{
				return
						
				new MouseAdapter()
				{
					@Override
					public void mouseClicked(MouseEvent ev) 
					{
						if(ev.getButton() == MouseEvent.BUTTON1)
						{
							focus.outFocus(layoutStructure);
							(focus = (ViewInfo)ev.getComponent()).inFocus(layoutStructure);
							setScrollByInfo(focus);
						}
						else if(ev.getButton() == MouseEvent.BUTTON3 && list.getComponentCount()>1)
						{
							ReferenceList layout = (ReferenceList)ev.getComponent().getParent();
							((RemoveRunnable)popup.getRunnable()).setView(layout);//우클릭한 view 정보를 제거 팝업에 넘김.
							popup.show(ev.getComponent(),ev.getX(),ev.getY());
						}
					}
				};
			}
	//=======>linked procedure3
		public JScrollPane setScroll(JPanel panel)//View 들을 보여주는 리스트 판넬에 가로 스크롤 바를 추가함.
		{
			layoutListSc
			= new JScrollPane(panel,
					JScrollPane.VERTICAL_SCROLLBAR_NEVER,
					JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);//가로 스크롤 모드.
			layoutListSc.getHorizontalScrollBar().setUnitIncrement(16);//마우스 휠의 속도 조정
			layoutListSc.setBorder(new LineBorder(Color.WHITE,1));
			layoutListSc.getHorizontalScrollBar().setPreferredSize(new Dimension(layoutListSc.getHorizontalScrollBar().getWidth(),SCROLL_HEIGHT));
			layoutListSc.addMouseListener(new MouseAdapter() 
			{
				public void mouseEntered(MouseEvent e)//마우스가 들어오면 스크롤바를 보임.
				{
					layoutListSc.getHorizontalScrollBar().setPreferredSize(new Dimension(layoutListSc.getHorizontalScrollBar().getWidth(),SCROLL_HEIGHT));
					layoutListSc.revalidate();
				}
				public void mouseExited(MouseEvent e) //마우스가 나가면 스크롤바를 숨김.
				{
					layoutListSc.getHorizontalScrollBar().setPreferredSize(new Dimension(layoutListSc.getHorizontalScrollBar().getWidth(),0));
					layoutListSc.revalidate();
				}
			});

			return layoutListSc;
		}
		
	//-----------------------------
	//getter
	//-----------------------------
	
	/**
	 * @return JPanel of ViewStructure content screen
	 */
	public JPanel get()//설명 생략.
	{return frame;}
	
	//-----------------------------
	//setter
	//-----------------------------
	
	/**
	 * move scroll where a View is exist.
	 */
	public void setScrollByInfo(final ViewInfo info)//스크롤의 위치를 포커스 되어있는 View 의 위치로 이동함.
	{
		final float maxValue = layoutListSc.getHorizontalScrollBar().getMaximum();
		float locationRate = 0f;
		try 
		{
			locationRate = (float)(rList.getPriority(((ReferenceList) info.getParent()).getBeforeList().getBeforeList()))/(float)(rList.getCount()-1);// 포커스 View의 이전 View의 index/전체 View의 수
			if(locationRate<0) locationRate = 0f;
		}
		catch(Exception e) {}//이전 View 가 없음.
		final float location = locationRate * maxValue;//스크롤 내에서의 View의 위치값을 구해서
		layoutListSc.getHorizontalScrollBar().setValue((int)location);//적용
	}
	
	/**@deprecated
	 */
	public static void setLogic(Logic e)
	{logic = e;}
	
	//-----------------------------
	//inner Class
	//-----------------------------
	
	public interface Logic//이 인터페이스는 ViewInfo를 ViewStructure에서만 만들수 있게하기 위해 사용된다.
	{public ViewInfo newInfo(Image img);}
	
	private class RemoveRunnable implements Runnable
	{
		/*
		 * View를 하나 제거하는 기능입니다.
		 * 생성자에서 제거 팝업에 기능을 부여하기 위해서 쓰입니다.
		 * 제거하는 대상을 클릭에 맞춰서 변경할 필요가 인해서 getter를 쓰기 위해 클래스로 남겨둡니다.
		 */
		private ReferenceList view;
		
		public RemoveRunnable(ReferenceList view)
		{this.view = view;}
		
		public void setView(ReferenceList view)
		{this.view = view;}
		
		@Override
		public void run()//설명 생략
		{	
			focus.outFocus(layoutStructure);
			try
			{
				focus = (ViewInfo) view.getNextList().getDesignated();//view가 삭제되면 이전 View값이 focus를 지닌다.이전 값이 없다면 두개 있는것중에 첫번째를 삭제한것이므로 다음 값에 focus를 준다.
				focus.inFocus(layoutStructure);
			}
			catch(Exception e)
			{
				focus = (ViewInfo) view.getBeforeList().getDesignated();//view가 삭제되면 이전 View값이 focus를 지닌다.
				focus.inFocus(layoutStructure);
			}
			
			rList.removeList(view);
			list.remove(view);
			list.revalidate();
			list.repaint();
			
			setScrollByInfo(focus);
		}
	}
	
	public class Tools//외부에서 객체의 사용자에게는 필요하지 않은 메소드들이 모여 있습니다.
	{
		private Tools() {}
		
		/**
		 * synchronize with Screen Object. if you have multiple Screen try to use scrObject.synchronizeViewStructure(ViewStructure view)
		 */
		public ScreenAdapter setHandler()//만약 Screen 객체의 크기가 변동 됬을 경우 새로운 화면 Image 받아와서 적용한다.
		{
			scr =  layoutStructure.tools.getScreen();
			if(scr != null)
			{
				final ScreenAdapter handler = 
						new ScreenAdapter()
						{
							public void screenSizeChanged()
							{focus.setViewImage(scr.getScreenImage());}
							public void whenScrDrawed()
							{focus.repaintImage();}
						};//화면을 바꾸는 연산을 하면 새롭게 화면 이미지를 가져옴.
						
				scr.setHandler(handler);
				
				return handler;
			}
			else 
				return new ScreenAdapter();
		}
	}


}

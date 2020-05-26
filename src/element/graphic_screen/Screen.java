package element.graphic_screen;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.datatransfer.DataFlavor;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetAdapter;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;

import element.layout_list.LayoutInfo;
import element.layout_list.LayoutList;
import element.view_list.ViewStructure;
import logic.FileConnection;
import logic.LockBoolean;
import logic.ReferenceList;
import tools.DimensionF;

public class Screen{
	
	/*
	 * 이 클래스는 그래픽 스크린의 레이아웃 구성요소중 하나입니다.
	 * LayoutList 의 내용을 입출력합니다.
	 * 출력 화면의 정보를 수정 할 수 있습니다.
	 */
	
	private final static float MIN_WIDTH = 200f, MIN_HEIGHT = 200f;
	
	public final static float MAX_WIDTH = 600f, MAX_HEIGHT = 600f;
	
	public final static boolean VERTICAL = false, HORIZONTAL = true;

	public final static int DEFAULT_WIDTH = 1440, DEFAULT_HEIGHT = 2560;
	
	public final static String GROUP_SIZE_SET = "SizeSet"
		,GROUP_UI_SIZE_CHANGER = "UISizeChanger"
		,GROUP_ROTATE_BUTTON = "RotateButton";
	
	public final static Color DRAG_SCREEN_COLOR = Color.getColor("blue", 0XFF7b7bFF);
	private final static Color SCREEN_BACKGROUND_COLOR = Color.WHITE;
	
	//---------------layout instance
	
	private final JPanel result = new RenderScreen();
	
	private final Border defaultBorder = result.getBorder();
	private UISizeChanger uiSizeChanger;
	
	//---------------store instance
	
	private final LayoutList list;
	private final ArrayList<ScreenComponent> synComp = new ArrayList<>();
	private BufferedImage screenImage;
	private HashSet<ScreenListener> handlers = new HashSet<>();
	private ScreenListener viewWorks;
	
	//---------------variable
	
	private DimensionF scrSize = new DimensionF(DEFAULT_WIDTH,DEFAULT_HEIGHT);
	private DimensionF renderSize = new DimensionF();
	private LockBoolean deviceRotation = new LockBoolean(RotateButton.getKey());
	
	//---------------for logic
	
	private Graphics2D painter;
	
	//-----------------------------
	//creator
	//-----------------------------
	
	/**
	 * Screen Panel Component of LayoutViewer that Use to Output and Input in Layout Information.
	 */
	@SuppressWarnings("deprecation")
	public Screen(final LayoutList layoutList)//초기화, 화면크기조정, 드래그 삽입처리
	{
		FileConnection.setObject("screen", this);
		
		setHandler(layoutList.tools.getHandler());
		
		list = layoutList;
		scrSize.width = DEFAULT_WIDTH;
		scrSize.height = DEFAULT_HEIGHT;
		renderSize.width = MAX_WIDTH * scrSize.width/scrSize.height;
		renderSize.height = MAX_HEIGHT;
		
		result.setPreferredSize(renderSize.getSize());
		
		result.setMaximumSize(new Dimension((int)MAX_WIDTH, (int)MAX_HEIGHT));
		result.setMinimumSize(new Dimension((int)MIN_WIDTH, (int)MIN_HEIGHT));
		result.setBackground(SCREEN_BACKGROUND_COLOR);
		result.setLayout(null);
		
		result.revalidate();
		
		setDropsAfter();
		
		try 
		{viewWorks = ((ViewStructure)FileConnection.getObject("View")).tools.setHandler();}//만약 ViewStructure 객체가 존재 한다면 연동한다.
		catch(Exception e) {}
	}
	//=======>linked procedure
		private void setDropsAfter()//하단 설명 참조
		{
			result.setDropTarget(new DropTarget(result, new DropTargetAdapter() 
			{
				@Override
				public void dragEnter(DropTargetDragEvent dtde) 
				{result.setBorder(new LineBorder(Screen.DRAG_SCREEN_COLOR,3));}
            
				@Override
				public void dragExit(DropTargetEvent dte) 
				{result.setBorder(defaultBorder);}

				@Override
				public void drop(DropTargetDropEvent dtde) 
				{
					result.setBorder(defaultBorder);
            	
					try
					{
						for(ScreenListener r:handlers)
							r.droppedInScreen((File) dtde.getTransferable().getTransferData(DataFlavor.javaFileListFlavor));
					}
					catch(Exception e) 
					{e.printStackTrace();}
					dtde.dropComplete(true);
                
				}
			}));
		
		}
		/*
		 * 기능: 파일을 드로그 해 왔을 때의 후 처리들을 설정함
		 * 1.파일이 스크린으로 들어오면 스크린의 테두리 색을 변경함
		 * 2.파일이 스크린에서 벗어나면 본래 태두리 색으로 복구함
		 * 3.파일이 드래그 확정이 되면 본래 태두리 색으로 복구함
		 * 4.LayoutList 에서 레이아웃을 업데이트 함
		 */
	
	//----------------------------
	//destroyer
	//----------------------------
		
	public void hideUISizeChanger()//이미지 조절 창을 숨긴다.
	{
		result.remove(uiSizeChanger.get());
		uiSizeChanger = null;
		new Thread(
		new Runnable() 
		{
			public void run()
			{
				try
				{Thread.sleep(10);}
				catch(Exception e) {};
				addUISizeChanger(new UISizeChanger());
			}
		}).start();//하단 설명 참조 (1)
	}
	/*
	 * Other Why
	 * (1)스레드로 텀을 둔 이유..
	 * setVisible(false)가 안먹히는 문제가 발생 했으나 이유를 찾을수 없었다.
	 * 하므로 기존의 UC를 제거하고 새로운 UC를 대입한다.
	 * 10ms의 텀을 두고 실행하는 이유는 focus가 있을 경우
	 * addSizeChanger 메소드에서 현재 집중 되어있는 포커스를 UISizeChanger에 대입하게되고
	 * 이에따라서 focus가 UISizeChanger에서 인식되면 화면에 다시 표기 되어버리기 때문이다.
	 * 즉, hideUISizeChanger(); 이후에
	 * focus = null;를통해서 focus가 초기화된뒤에
	 * addUISizeChanger()를 실행한다.
	 * 이런 구조를 가지는 이유는 지금 이 메소드를 사용하고 있는 focus로 쓰는 객체 또한 이 메소드를 사용하기 때문이다.
	 * 
	 */
	
	//----------------------------
	//getter
	//----------------------------
	
	/**
	 * 
	 * @return screen JPanel
	 */
	public final JPanel get()
	{return result;}
		
	/**
	 * true: Screen.HORIZONTAL, false: Screen.VERTICAL
	 * must use to getRotation().get()
	 * 
	 * @return device rotation state 
	 */
	public final LockBoolean getRotation()//하단 설명 참조
	{return deviceRotation;}
	/*
	 * deviceRotation의 값을 외부 사용자가 수정하지 못하도록 lockBoolean을 사용했다.
	 */
		
	public DimensionF getScrSize()//설명생략
	{return scrSize;}
		
	public DimensionF getRenderSize()//설명생략
	{return renderSize;}
	
	public Image getScreenImage()//설명생략
	{return screenImage;}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })//하단 설명 참조(1)
	public List getSynObject(String ClassName)//하단 설명 참조
	{
		ArrayList list = new ArrayList();
		
		for(ScreenComponent c : synComp)
		{
			String getClass[] = c.getClass().toString().split("\\.");
			if(ClassName.equals(getClass[getClass.length-1]))
				list.add(c);
		}
		return list;
	}
	/*
	 * 기능: 등록되어 동기화된 객체들중 매개변수로 적은 클래스 종류의 객체가 있다면 모두 찾아내서 list에 담아서 건네줌
	 * 1.리스트 객체 생성
	 * 2.동기화된 목록들을 하나씩 받아와서 클래스명을 검사
	 * 3.클래스명이 원하는 내용일 경우 리스트에 담고 마지막엔 건네줌
	 * 
	 * Other Why
	 * (1)"rawtypes", "unchecked"..?
	 * 위의 두가지는 각각 Object 와 제너릭에대한 경고이나 의도한바 이므로 무시한다.
	 * 여러종류의 클래스 객체를 한 배열에 담기위한 방법은 Object로 저장하는 방법이지만
	 * 이 방법에는 한가지 번거로운 점이 있다.
	 * 받아올때 Object ob = (형변환)exam.method() 을 해줘야 한다는것...
	 * 그래서 저 형변환을 없애기 위해서 머리를 굴려 설계한 구조가 저것이다.
	 * 제너릭이 선언 되지 않은 List를 List<Class> 레퍼런스로 받는다.
	 */
	
	public UISizeChanger getUISizeChanger()//하단 설명 참고
	{return uiSizeChanger;}
	/*
	 * 기능 설명은 생략.
	 * 
	 * Other Why?
	 * (E) 기능 중복
	 * 이 기능은 getSynObject() 와 중복되는 기능이다. 
	 * 현재 UISizeChanger는 여러가지 이미지중에 선택된 하나의 크기를 조정하는 방식을 취하고 있으며
	 * 그래서 여러개를 리스트로 받을 이유도 생성할 이유도 없는 상황이다.
	 * 그래서 UISizeChanger는 따로 한개의 객체를 받을 수 있도록 해놓았다.
	 * 단, UISizeChanger를 여러개로 사용해야만 하는 가능성을 염두해서
	 * UISizeChanger 또한 addSynComponent 하여 복수 갯수를 가지고 있을 수는 있게했다.
	 * 만일 여러 이미지의 크기를 동시에 조정 해야한다면 이미지를 묶은 bind 버튼 기능이 있다.
	 * 
	 */
	
	/**
	 * @return value of Screen size x/y or y/x
	 */
	public float getRenderRate()//설명 생략
	{
		if(!deviceRotation.get()) return renderSize.width/scrSize.width;
		else return renderSize.height/scrSize.height;
	}
	
	public LayoutList getLayoutList()
	{return list;}
	
	//----------------------------
	//setter
	//----------------------------
	
	/**
	 * @param handler you can insert multiple Interface method when Dropped ImageFile and Resized screenSize
	 */
	public void setHandler(ScreenListener handler) // LayoutViewer, ViewStructure로부터 작업을 가져옴.
	{handlers.add(handler);}
	
	/**
	 * if you added Component about Screen, you can use the group method.
	 * for example, screen.groupVisible(true);
	 * It will be automatic call when Object about Screen is created 
	 * @param c it support RotateButton,SizeSet,UISizeChanger object
	 */
	public void addSynComponent(ScreenComponent c) //그룹으로 함께 움직일 객체들을 지정함
	{synComp.add(c);}
	
	
	/**
	 * add Image Size Editor by mouse
	 */
	public void addUISizeChanger(UISizeChanger uic)
	{
		uiSizeChanger = uic;
		uiSizeChanger.setScrInformation(this);
		result.add(uiSizeChanger.get());
		result.revalidate();
	}
	
	/**
	 * It can be use for multiple ViewStructure
	 */
	public void synchronizeViewStructure(ViewStructure view)
	{
		handlers.remove(viewWorks);
		view.tools.setHandler();
	}
	
	
	//----------------------------
	//actions
	//----------------------------
	
	/**
	 * hiding and appearing Screen components when synchronized
	 */
	public void groupVisible(boolean flag) // 설명 생략
	{
		for(ScreenComponent c : synComp)
			((Component)c).setVisible(flag);
		
		result.setVisible(flag);
	}
	
	public void repaint()
	{result.repaint();}
	
	public void newScreenImage()
	{result.setPreferredSize(result.getPreferredSize());}
	
	//----------------------------
	//support
	//----------------------------
	
	/**
	 * If you need Landscape and Portrait, use RotateButton class.
	 */
	public void howCanUseHorizontal(RotateButton rotateButton){}
	
	/**
	 * It can resize Screen to get().setPreferredSize..
	 * but you can do it also by sizeSet graphical interface
	 */
	public void howCanReSizeScreen(SizeSet sizeSet){}
	
	//----------------------------
	//inner Class/interface
	//----------------------------
	
	public static interface ScreenListener
	{
		public abstract void droppedInScreen(File file);
		public abstract void screenSizeChanged();
		public abstract void whenScrDrawed();
	}
	
	/**
	 *	method in {public void droppedInScreen(File file), public void screenSizeChanged()}
	 */
	public static class ScreenAdapter implements ScreenListener
	{
		public void droppedInScreen(File file) {};
		public void screenSizeChanged() {};
		public void whenScrDrawed() {};
	}
	
	/**
	 *	if a developer made class about this Screen, It can join group by this implement 
	 */
	public interface ScreenComponent
	{
		public abstract void destoryer();
	}
	
	@SuppressWarnings("serial")
	private class RenderScreen extends JPanel
	{
		@Override
		public void paintComponent(Graphics g)//레이아웃(LayoutList)의 요소들을 화면이 repaint 될때마다 순차적으로 읽어와서 화면에 출력, sreenImage를 갱신
		{
			painter = screenImage.createGraphics();
			super.paintComponent(g);
			super.paintComponent(painter);
			
			((Graphics2D)g).setRenderingHint(RenderingHints.KEY_ANTIALIASING,
		               RenderingHints.VALUE_ANTIALIAS_ON);
			painter.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
		               RenderingHints.VALUE_ANTIALIAS_ON);
			try
			{
				final int indexMax = list.getList().getComponentCount();//하단 설명 참조 (1)
				final JPanel layoutList = list.getList();
				for(int i = 1; indexMax - i >= 0;i++)
				{
					final JPanel comp = (JPanel) ((ReferenceList)layoutList.getComponent(indexMax - i)).getDesignated();//하단 설명 참조 (3)
					final LayoutInfo info = (LayoutInfo) comp.getComponent(comp.getComponentCount()-1);

					info.drawImage(g);
					info.drawImage(painter);
				}
				
				for(ScreenListener r:handlers)// ViewStructure 에서 화면을 새로고침.
					r.whenScrDrawed();
				
				painter.dispose();
				
			}
			catch(Exception e) {}
		}
		/*
		 *  Other Why
		 *  (1) 객체의 대입기준..
		 *  >> 위로 따로 빼둔 객체들은 반복해서 다시 대입을 할 필요가 없는 객체들이다.
		 *  >> for문에서 indexMax - i 를 list.getComponentCount() - i로 사용할 경우 매번 조건 확인을 위해서 getComponentCount() 를 호출하게 된다.
		 *  >> getComponentCount()는 간단한 getter가 아니며, 이 메소드의 경우 2중 stack을 지니므로 매번 호출하기 무겁다.
		 *  
		 *  (2) LayoutInfo를 찾는 과정..
		 *  1.JPanel layoutList = list.getList() 
		 *  :컴포넌트들을 가지는 리스트
		 *  2.ReferenceList componentRefer = (ReferenceList)JPanel.getComponent(indexMax - i) 
		 *  :리스트에서 역순으로 내용물을 하나씩 가져옴.
		 *  3.JPanel component = component.getDesignated() 
		 *  :안의 내용물은 레퍼런스(C기준으로 포인터)이므로 레퍼런스가 가리키는 내용물(컴포넌트)를 가져옴.
		 *  >>이런 구조의 이유는 ReferenceList 클래스를 참조.
		 *  4.LayoutInfo info = (LayoutInfo) comp.getComponent(comp.getComponentCount()-1)
		 *  :이렇게 가져온 Panel은 {기타 컴포넌트 ... ,LayoutInfo}의 구조를 가지고 있으므로 getComponentCount()-1 번지에 있는 LayoutInfo를 가져온다.
		 *  5.LayoutInfo 안의 정보들을 기준으로 이미지를 리스트의 역순으로 그리게 된다.
		 *  
		 *  (3)난잡한 코드
		 * 	>>위의 (2)번처럼 객체를 각각 대입해서 사용하면 알아보기 쉽고 편리하지만 작성자는 위의 형식을 좋아하지 않음.
		 *  >>객체마다 대입을 해도 안해도 결국엔 대입을 위해서 똑같은 양의 메소드를 호출하게 되는데
		 *  >>메소드의 호출에 레퍼런스를 매번 대입 하는 연산까지 가중되어 속도 측면에서 더 느리기 떄문임.
		 *  >>미미한 속도의 차이라고 는 하지만 문제는 이 연산이 for문이며 빠르고 가볍게 그려져야 하는 렌더링 관련 메소드라는 것이다.
		 *  
		 */
		
		@Override
		public void setPreferredSize(Dimension preferredSize)//화면크기 조정,Scr의 Image 틀을 생성, 내부 이미지 재구성
		{
			if(preferredSize.width<1)
				preferredSize.width = 1;
			if(preferredSize.height<1)
				preferredSize.height = 1;
			super.setPreferredSize(preferredSize);
			
			renderSize.setSize(preferredSize);
			
			screenImage = new BufferedImage(renderSize.getWidth(), renderSize.getHeight(), BufferedImage.TYPE_INT_RGB);//Image는 크기변경이 불가능해서 새로 만들어야 한다.
		
			for(ScreenListener r:handlers)//LayoutViewer 에서 이미지를 재정렬, ViewStructure 에서 화면을 다시 받아감.
				r.screenSizeChanged();
			
			repaint();
		}
	}
}

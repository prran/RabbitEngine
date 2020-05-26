package element.graphic_screen;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagLayout;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.concurrent.atomic.AtomicInteger;

import javax.swing.JPanel;
import javax.swing.border.LineBorder;

import element.layout_list.LayoutInfo;
import logic.FileConnection;
import tools.StaticTools;

public class UISizeChanger implements Screen.ScreenComponent {
	
	/*
	 * 이 클래스는 스크린의 내부에 들어가는 구성요소 입니다.
	 * 화면내의 이미지의 크기를 마우스로 조정하고 이동할수 있습니다.
	 */
	
	private final static Color BORDER_COLOR = Color.getColor("sizeset", 0XFFA4A4FF),
			BUTTON_COLOR = Color.getColor("sizeset", 0XFFFFFFFF);
	
	private final static Color TRANSPERANT = new Color(0,0,0,0);
	
	private final static int BUTTON_SIZE = 6;
	private final static int BUTTON_SIZE_HARP = BUTTON_SIZE/2;
	
	//---------------layout instance
	
	private final JPanel result = new LayoutFrame();
	private final JPanel imagePanel;
	
	//---------------store instance
	
	private LayoutInfo info;
	private Screen scr;
	
	//---------------variable
	
	private AtomicInteger x;
	private AtomicInteger y;
	private AtomicInteger w;
	private AtomicInteger h;
	
	private Point movePoint;
	
	//----------------switch
	
	private boolean isRun;
	
	//-----------------------------
	//creator
	//-----------------------------
	
	/**
	 * Edit Image Information by Mouse that owner LayoutList.
	 * this class is unit of Screen.
	 * it can use by screenOb.setUISizeChanger();
	 */
	public UISizeChanger()
	{
		this(null);
		result.setVisible(false);
	}
	public UISizeChanger(LayoutInfo info)//초기화, 레이아웃 설정, 기능 추가
	{
		FileConnection.setObject("UI size changer", this);
		
		try 
		{
			this.info = info;
			x = new AtomicInteger(info.totalLocation.x);		
			y = new AtomicInteger(info.totalLocation.y);
			w = new AtomicInteger(info.totalSize.width);
			h = new AtomicInteger(info.totalSize.height);
		}
		catch(Exception e)
		{
			x = new AtomicInteger();		
			y = new AtomicInteger();
			w = new AtomicInteger();
			h = new AtomicInteger();
		}
		
		result.setMaximumSize(new Dimension(Integer.MAX_VALUE,Integer.MAX_VALUE));
		result.setSize(new Dimension(w.get()+BUTTON_SIZE,h.get()+BUTTON_SIZE));
		result.setBackground(TRANSPERANT);
		result.setLayout(new GridBagLayout());
		result.setBounds(x.get()-BUTTON_SIZE_HARP , y.get()- BUTTON_SIZE_HARP, w.get() + BUTTON_SIZE, h.get() + BUTTON_SIZE);//하단 설명 참조 (1)
		
		imagePanel = new JPanel();
		imagePanel.setOpaque(false);//투명색으로 설정
		
		//키패드 기준으로 위치 설명
		//ㅁㅁㅁ
		//ㅁㅁㅁ
		//ㅁㅁㅁ
		
		result.add(imagePanel,StaticTools.setFullGrid(1, 1, 1, 1, 1f, 1f));//5번 위치
		
		result.add(new ResizeButton(x,y,w,h),StaticTools.setFullGrid(0, 0, 1, 1, 0f, 0f));//7번 위치, 하단설명 참조(2)
		result.add(new ResizeButton(null,y,null,h),StaticTools.setFullGrid(1, 0, 1, 1, 0f, 0f));//8번 위치
		result.add(new ResizeButton(null,y,w,h),StaticTools.setFullGrid(2, 0, 1, 1, 0f, 0f));//9번 위치
		result.add(new ResizeButton(x,null,w,null),StaticTools.setFullGrid(0, 1, 1, 1, 0f, 0f));//4번 위치
		result.add(new ResizeButton(null,null,w,null),StaticTools.setFullGrid(2, 1, 1, 1, 0f, 0f));//6번 위치
		result.add(new ResizeButton(x,null,w,h),StaticTools.setFullGrid(0, 2, 1, 1, 0f, 0f));//1번 위치
		result.add(new ResizeButton(null,null,null,h),StaticTools.setFullGrid(1, 2, 1, 1, 0f, 0f));//2번 위치
		result.add(new ResizeButton(null,null,w,h),StaticTools.setFullGrid(2, 2, 1, 1, 0f, 0f));//3번위치
		
		final MouseAdapter listener = getMouseListener();
		result.addMouseListener(listener);
		result.addMouseMotionListener(listener);
	}
	/*
	 * Other Why
	 * (1)이미지크기+버튼의 크기를 가지는 이유
	 * ㅁ--ㅁ--ㅁ
	 * ㅣ  	   ㅣ
	 * ㅁ 	   ㅁ
	 * ㅣ	   ㅣ 
	 * ㅁ--ㅁ--ㅁ
	 * (2)하드코딩
	 * 반복문을 써서 간편하게 줄이고 싶으나 안으로 들어가는 매개 변수가 문제가 되어 어려움.
	 */
	//=======>linked procedure
		private MouseAdapter getMouseListener()//이동 기능을 추가
		{
			return 
					
			new MouseAdapter()
			{
				@Override
				public void mousePressed(MouseEvent e)
				{
					movePoint = new Point(e.getX(),e.getY());
					isRun = true;//하단 설명 참조 (1)
				}
				@Override
				public void mouseReleased(MouseEvent e) 
				{
					movePoint = null;
					isRun = false;
				}
				@Override
				public void mouseDragged(MouseEvent e) 
				{
					try
					{
						final int movedX = e.getX() - movePoint.x;
						final int movedY = e.getY() - movePoint.y;
						x.set(x.get() + movedX);
						y.set(y.get() + movedY);
						updateSize();
					}
					catch(Exception ex)
					{}
				}
				
			};
		}
		/*
		 * Other Why
		 * (1) 동작의 시작과 끝을 기록하는 이유
		 * >> 이미지의 정보를 수정하는 방법은 마우스에 의한 입력, 키보드에 의한 입력으로 두가지가 존재한다.
		 * >> 이미지를 수정 하는 중에 키보드 에디터의 정보를 동기화 하는 과정이 있고, 이로인해 수정된 키보드 값이 이미지의 정보를 한번 더 수정한다.
		 * >> 하므로 이를 방지하기 위해서 UISizeChanger가 동작하는 동안 키보드 에디터를에 lock을 걸어두기 위해서 사용된다.
		 */
	
	//-----------------------------
	//getter
	//-----------------------------
	
	/**
	 * @return JPanel of image resize editor
	 */
	public JPanel get()//설명 생략
	{return result;}
	
	public boolean isRun()//설명 생략
	{return isRun;}
	
	//-----------------------------
	//setter
	//-----------------------------
	
	/**
	 * synchronize one image info in the LayoutList
	 */
	public void inFocus(LayoutInfo info)//이미지의 정보를 에디터와 동기화 한다.
	{
		this.info = info;
		
		x.set(info.totalLocation.x);
		y.set(info.totalLocation.y);
		w.set(info.totalSize.width);
		h.set(info.totalSize.height);
		
		result.setBounds(x.get()-BUTTON_SIZE_HARP , y.get()- BUTTON_SIZE_HARP, w.get() + BUTTON_SIZE, h.get() + BUTTON_SIZE);
		
		if(info.totalSize.width>scr.getRenderSize().width*1.5||info.totalSize.height>scr.getRenderSize().height*1.5)//크기조절창 크기가 화면을 넘어갈 경우 그래픽이 깨지는 오류 방지
			result.setVisible(false);
		else if(!result.isVisible())//화면에 아직 아무런 이미지도 없다가 처음 생길때, screen의  그룹행동 groupVisible에 의해 숨김 처리 되어 있을때.
			result.setVisible(true);
		
		result.revalidate();
		scr.get().repaint();
	}
	
	private void updateSize()//에디터의 정보를 이미지에 동기화 한다. getMouseListener(), WhenDraged.mouseDragged()에서 사용.
	{
		info.updateDataByUC(x.get(), y.get(), w.get(), h.get());
		
		result.setBounds(info.totalLocation.x-BUTTON_SIZE_HARP,info.totalLocation.y-BUTTON_SIZE_HARP, info.totalSize.width + BUTTON_SIZE, info.totalSize.height + BUTTON_SIZE);
		result.revalidate();
		scr.repaint();
	}
	
	/**
	 * this method is Use to Information Synchronize that on the screen.
	 * it is not change owner Screen
	 * if you want it, use screen.setUISizeChanger().
	 */
	void setScrInformation(Screen screen)//설명 생략
	{scr = screen;}
	
	
	//-----------------------------
	//override
	//-----------------------------
	
	@Override
	public void destoryer() {}
	
	//-----------------------------
	//inner Class
	//-----------------------------
	
	private class ResizeButton extends JPanel
	{
		/*
		 * UISizeChanger 내부에 존재하는 8개의 버튼 입니다.
		 * 버튼을 눌러서 드래그 하면 이미지의 크기가 변형됩니다.
		 */
		private static final long serialVersionUID = 1L;
		
		private final AtomicInteger x;//정수를 레퍼런스로 쓰고 이 클래스 에서 변수를 조종할수 있도록 설정.
		private final AtomicInteger y;
		private final AtomicInteger w;
		private final AtomicInteger h;
		private Point point;
		
		private ResizeButton(AtomicInteger x,AtomicInteger y, AtomicInteger w, AtomicInteger h)//초기화,레이아웃설정,기능추가
		{
			this.x = x;
			this.y = y;
			this.w = w;
			this.h = h;
			
			setPreferredSize(new Dimension(BUTTON_SIZE,BUTTON_SIZE));
			setMaximumSize(new Dimension(BUTTON_SIZE,BUTTON_SIZE));
			setBackground(BUTTON_COLOR);
			setBorder(new LineBorder(BORDER_COLOR,1));
			final MouseAdapter listener = getDragListener();
			this.addMouseListener(listener);
			this.addMouseMotionListener(listener);
		}
		//=======>linked procedure
			private MouseAdapter getDragListener()//드래그 x축 y축 위치에 따라서 값을 수정한뒤 이미지에 적용함
			{
				return 
				
				new MouseAdapter()
				{
					@Override
					public void mousePressed(MouseEvent e) 
					{
						point = MouseInfo.getPointerInfo().getLocation();//하단 설명 참조(1)
						System.out.println(point.x);
						isRun = true;// 하단 설명 참조 (2)
					}
					@Override
					public void mouseReleased(MouseEvent e) 
					{
						point = null;
						isRun = false;
					}
					@Override
					public void mouseDragged(MouseEvent e) 
					{
						Point mouseLocation = MouseInfo.getPointerInfo().getLocation();
						final int movedX = mouseLocation.x - point.x;
						final int movedY = mouseLocation.y - point.y;
							
						point = mouseLocation;
							
						try //x가 null 이면 w 값만 수정. 그마저 없다면 아무행동도 하지 않음
						{
							x.set(x.get() + movedX);
							w.set(w.get() - movedX);
						}
						catch(Exception ex)
						{
							try {w.set(w.get() + movedX);}
							catch(Exception ex2) {}
						}
						try //y가 null 이면 h 값만 수정. 그마저 없다면 아무행동도 하지 않음
						{
							y.set(y.get() + movedY);
							h.set(h.get() - movedY);
						}
						catch(Exception ex)
						{	
							try
							{h.set(h.get() + movedY);}
							catch(Exception ex2) {}
						}
						
						updateSize();

					}
				};
			}
		}
		/*
		 * Other Why
		 * (1)MouseEvent e.get() 는 사용하면 안됀다.
		 * 컴포넌트의 크기가 변동되면 각각의 버튼들의 위치가 조정되는 과정이 이루어 지게된다.
		 * 문제는 재구성이 이루어 지면서 일시적으로 버튼들의 위치가 본래와 다른 위치에 배치된 뒤에 재 위치하는 과정을 거치게 된다는 것이다.
		 * 즉 버튼을 중심으로 값을 전달해 주는 MouseEvent e 는 의도한 바와 다른 값을 순간순간 출력하기 때문에 버그를 일으킨다.
		 * (2)중복 연산 방지
		 * 이미지의 크기가 변동됨에 따라서 ImageManager에서 이 값을 연동하여 표기하고 있는 수치를 정정한다.
		 * 이때 수치를 변동 입력이 발생 하면서 ImageManager에서도 같은 연산을 다시 처리하므로 이 스위치는 이를 방치하기 위해서 사용된다.
		 */
	
	@SuppressWarnings("serial")
	private class LayoutFrame extends JPanel
	{
		/*
		 * 화면 출력을 이 클래스에 맞게 Overriding 한 판넬입니다.
		 * 상단 생성자에서 Overriding할수 있으나 보기에 난잡하여 따로 아래로 내렸습니다.
		 */
		@Override
		public void paintComponent(Graphics g)//안티 엘리어싱 적용하고 현재 동기화중인 이미지를 규격에 맞게 출력합니다.
		{	
			super.paintComponent(g);
			
			((Graphics2D)g).setRenderingHint(RenderingHints.KEY_ANTIALIASING,
	                RenderingHints.VALUE_ANTIALIAS_ON);
			g.drawImage(info.getImage(), BUTTON_SIZE_HARP, BUTTON_SIZE_HARP,w.get(),h.get(),null);
			g.setColor(BORDER_COLOR);
			g.drawRect(BUTTON_SIZE_HARP,BUTTON_SIZE_HARP,w.get(),h.get());
		}
	}

}

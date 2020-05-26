package element.lmage_manager;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;

import element.layout_list.LayoutInfo;
import logic.FileConnection;
import window.Core;
import tools.StaticTools;

public class ImageManager
{
	/*
	 * 버전에 + 는 0.?를 뜻힘
	 * 
	 * 기능...
	 * 1.움직이는 이미지를 표기
	 * 2.화면비의 이동을 제공
	 * 3.내용비의 이동을 제공
	 * 4.전체 배율 변경을 제공
	 * 5.화면비의 크기 변동을 제공
	 * 6.내용비의 크기 변동을 제공
	 * 7.현재 레이아웃의 이미지 정보를 표기
	 * 8.화면 회전에 대한 크기 기준 선택
	 * 9.화면 회전에 대한 위치 기준의 선택
	 * 10.x,y축의 시작지점에 대한 반전 선택
	 */
	
	/*
	 * 이 클래스는 별도의 Frame을 가지는 이미지 수정 화면 입니다.
	 * LayoutList 에서 Focus 처리된 이미지를 읽어오며,
	 * 이미지를 키보드로 수정하고자 할때 사용합니다.
	 */
	@SuppressWarnings("unused")
	private static final long serialVersionUID = 1L + 0;
	
	private final static int WIDTH = 320;
	private final static int HEIGHT = 470;
	private final static int LOCATIONX = 100;
	private final static int LOCATIONY = 100;
	
	private final static Image IMAGE_BACKGROUND =new ImageIcon("images/transparent.jpg").getImage();
	
	//---------------layout instance
	
	private final JFrame frame = new JFrame();
	private JPanel imageViewer;
	private final Inputs contentsX = new Inputs(Inputs.X_BY_CONTENT);//화면 비율비로 이미지를 조정하는 input
	private final Inputs contentsY = new Inputs(Inputs.Y_BY_CONTENT);
	private final Inputs scrByW = new Inputs(Inputs.W_BY_SCREEN);
	private final Inputs scrByH = new Inputs(Inputs.H_BY_SCREEN);
	private final Inputs scrByX = new Inputs(Inputs.X_BY_SCREEN);//원본 비율비로 이미지를 조정하는 input
	private final Inputs scrByY = new Inputs(Inputs.Y_BY_SCREEN);
	private final Inputs contentsW = new Inputs(Inputs.W_BY_CONTENT);
	private final Inputs contentsH = new Inputs(Inputs.H_BY_CONTENT);
	private final Inputs allScale = new Inputs(Inputs.ALL_SCALE);//수정된 비율에서 전체 비율을 조정하는 input
	private final PopupMenu popup = new PopupMenu();
	
	//---------------store instance
	
	private LayoutInfo focus;
	private final String name;
	
	//---------------store for logic
	
	public Tools tools = new Tools();
	
	//-----------------------------
	//creator
	//-----------------------------
	
	/**
	 * Revise image data to Keyboard in LayoutList.
	 * This Class has pop up Frame 
	 * 
	 * @param name if it use multiply, must need difference name each other.
	 */
	public ImageManager(String name)//설명 생략
	{
		FileConnection.setObject("image manager", this);
		this.name = name;
		setFrame();
		setComponents();
		frame.setAlwaysOnTop (true);
	}
	//======>linked procedure
		private void setFrame()
		{
			frame.setTitle("Image Manager");
			frame.setIconImage(new ImageIcon(Core.ICONIMAGE).getImage());
			frame.setSize(WIDTH,HEIGHT);
			final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
			frame.setLocation(screenSize.width-LOCATIONX - WIDTH, LOCATIONY);
		}
	//======>linked procedure2
		private void setComponents()
		{
			imageViewer = getImageView(popup);
			
			frame.setLayout(new GridBagLayout());
			frame.add(scrByX,StaticTools.setGridFill(StaticTools.setFullGrid(0, 0, 1, 1, 0.5f, 0f)));
			frame.add(scrByY,StaticTools.setGridFill(StaticTools.setFullGrid(1, 0, 1, 1, 0.5f, 0f)));
			frame.add(contentsX,StaticTools.setGridFill(StaticTools.setFullGrid(0, 1, 1, 1, 0.5f, 0f)));
			frame.add(contentsY,StaticTools.setGridFill(StaticTools.setFullGrid(1, 1, 1, 1, 0.5f, 0f)));
			frame.add(imageViewer,StaticTools.setGridFill(StaticTools.setFullGrid(0, 2, 2, 1, 1f, 1f)));
			frame.add(allScale,StaticTools.setGridFill(StaticTools.setFullGrid(0, 3, 2, 1, 1f, 0f)));
			frame.add(contentsW,StaticTools.setGridFill(StaticTools.setFullGrid(0, 4, 1, 1, 0.5f, 0f)));
			frame.add(contentsH,StaticTools.setGridFill(StaticTools.setFullGrid(1, 4, 1, 1, 0.5f, 0f)));
			frame.add(scrByW,StaticTools.setGridFill(StaticTools.setFullGrid(0, 5, 1, 1, 0.5f, 0f)));
			frame.add(scrByH,StaticTools.setGridFill(StaticTools.setFullGrid(1, 5, 1, 1, 0.5f, 0f)));
			
			contentsW.setSynInputs(scrByW,contentsX);//연동되어야하는 Input들간의 동기화를 한다.
			contentsH.setSynInputs(scrByH,contentsY);
			scrByX.setSynInputs(contentsX);
			scrByY.setSynInputs(contentsY);
			
			scrByW.setSynInputs(contentsW,contentsX);
			scrByH.setSynInputs(contentsH,contentsY);
			contentsX.setSynInputs(scrByX);
			contentsY.setSynInputs(scrByY);
			
			allScale.setSynInputs(contentsX,contentsY);
		}
	//======>linked procedure3(in instance declare place)
		@SuppressWarnings("serial")
		private JPanel getImageView(final PopupMenu popup)//현재 LayoutList에서 포커스 되어있는 Layout의 이미지를 출력해줌.
		{
			return
		
			new JPanel()
			{	
				{
					LayoutInfo.setGifDrawingTarget(name, this);//ImageManger에서는 GIF움직임을 제공받기 위해서 지역명의 새로운 Thread를 등록함. 
					addMouseListener(new MouseAdapter() 
					{
						public void mouseClicked(MouseEvent e) 
						{
							if(e.getButton() == MouseEvent.BUTTON3&&focus!=null)
							{
								popup.show(e.getComponent(),e.getX(),e.getY());
								repaint();
							}
						}
					});
				}
				
				@Override
			    public void paintComponent(Graphics g) 
				{
			        super.paintComponent(g);
			        ((Graphics2D)g).setRenderingHint(RenderingHints.KEY_ANTIALIASING,
			                RenderingHints.VALUE_ANTIALIAS_ON);
			        
			        for(int i = 0; i*IMAGE_BACKGROUND.getWidth(null)<getWidth(); i++)//화면의 가로 크기와
			        	for(int j = 0; j*IMAGE_BACKGROUND.getHeight(null)<getHeight(); j++)//화면의 세로 크기가 배경 이미지보다 크다면
			        g.drawImage(IMAGE_BACKGROUND, i*IMAGE_BACKGROUND.getWidth(null), j*IMAGE_BACKGROUND.getHeight(null),IMAGE_BACKGROUND.getWidth(null),IMAGE_BACKGROUND.getHeight(null),null);
			        //너백만큼의 이미지를 가로 세로로 그림.
			        try
			        {
			        	final int x = (int)(getWidth()/2f -(float)focus.totalSize.width/2);
			        	final int y = (int)(getHeight()/2f - (float)focus.totalSize.height/2);//화면의 중앙에
			        	focus.drawGif(g,x,y);//Layout 이미지를 그림
			        }
			        catch(Exception e){}
				}
				
			};
		}
		
	
	//-----------------------------
	//destroyer
	//-----------------------------
		
	public void destroyer()
	{LayoutInfo.endGif(name);}//스레드를 종료한다.
	
	//-----------------------------
	//getter
	//-----------------------------
	
	/**
	 * @return JFrame of ImageManager
	 */
	public JFrame get()//설명생략
	{return frame;}
	
	//-----------------------------
	//actions
	//-----------------------------
		
	/**
	 * when focused Info is revised externally
	 * it can be update Inputs Text.
	 */
	public void refreshTexts()//관리하는 이미지의 정보가 달라졌을때 그 정보를 ImageManager에 동기화 하기위해 쓰임.
	{
		scrByX.setValue(focus.rateX);
		scrByY.setValue(focus.rateY);
		contentsW.setValue(focus.widthScale);
		contentsH.setValue(focus.heightScale);
		allScale.setValue(focus.allScale);
		contentsX.setValue(focus.XScale);
		contentsY.setValue(focus.YScale);
		scrByH.setValue(focus.rateH);
		scrByW.setValue(focus.rateW);
	}
	
	/**
	 * it can be change text without Actions.
	 * @param InputID Inputs.?_BY_SCREEN or Inputs.?_BY_CONTENT or ALL_SCALE
	 */
	public void setTextNoAction(final String InputID,final float vlaue)//input화면의 출력되는 값을 변경할수 있다.
	{
		switch(InputID)
		{
		case Inputs.X_BY_SCREEN:
			scrByX.updateTextNoAction(vlaue);
			break;
		case Inputs.Y_BY_SCREEN:
			scrByY.updateTextNoAction(vlaue);
			break;
		case Inputs.W_BY_SCREEN:
			contentsW.updateTextNoAction(vlaue);
			break;
		case Inputs.H_BY_SCREEN:
			contentsH.updateTextNoAction(vlaue);
			break;
		case Inputs.X_BY_CONTENT:
			contentsX.updateTextNoAction(vlaue);
			break;
		case Inputs.Y_BY_CONTENT:
			contentsY.updateTextNoAction(vlaue);
			break;
		case Inputs.W_BY_CONTENT:
			scrByW.updateTextNoAction(vlaue);
			break;
		case Inputs.H_BY_CONTENT:
			scrByH.updateTextNoAction(vlaue);
			break;
		case Inputs.ALL_SCALE:
			allScale.updateTextNoAction(vlaue);
			break;
			
		}
	}
	
	public class Tools//ImageManger 자체의 기능은 아니지만 다른 연동된 객체에서 필요한 메소드들을 담아놓은 클래스.
	{
		private Tools(){}
		
		public void inFocus(LayoutInfo focus)//LayoutList에서 포커스가 바뀌었을때 LayoutInfo 내에서 호출. 정보를 읽어옴.
		{
			ImageManager.this.focus = focus;
			
			try
			{
				//imageViewer.inFocus(focus);
				scrByX.inFocus(focus,focus.rateX);
				scrByY.inFocus(focus,focus.rateY);
				contentsX.inFocus(focus,focus.XScale);
				contentsY.inFocus(focus,focus.YScale);
				scrByW.inFocus(focus,focus.rateW);
				scrByH.inFocus(focus,focus.rateH);
				allScale.inFocus(focus,focus.allScale);
				contentsW.inFocus(focus,focus.widthScale);
				contentsH.inFocus(focus,focus.heightScale);
				popup.inFocus(focus);
			}
			catch(Exception e)//첫실행시 혹은 noFocus에 의해 지정된 정보가 없음.
			{
				scrByX.inFocus(null,0);
				scrByY.inFocus(null,0);
				contentsX.inFocus(null,0);
				contentsY.inFocus(null,0);
				scrByW.inFocus(null,0);
				scrByH.inFocus(null,0);
				allScale.inFocus(null,0);
				contentsW.inFocus(null,0);
				contentsH.inFocus(null,0);
			}
			
			try 
			{
				if(focus.isGif())//레이아웃이 GIF이미지를 포함하고 있고
				{
					if(!LayoutInfo.isGifPlay(name))//만일 이전 레이아웃이 GIF가 아니라서 스레드가 정지되어있다면
						LayoutInfo.startGif(name);
				}
				else
					LayoutInfo.stopGif(name);
			}
			catch(Exception e)//만일 LayoutList 내에서 focus된 Layout이 없다면
			{
				if(LayoutInfo.isGifPlay(name))
				LayoutInfo.stopGif(name);//스레드를 정지 한다.
			}
			imageViewer.repaint();
		}
		
		/**
		 * refresh Image in ImageManager Viewer
		 */
		public void refreshImage()//UserSizeChanger에서 크리 변동이 있을때 LayoutInfo 내에서 값을 수정하면서 사용.
		{imageViewer.repaint();}
	}
	
}

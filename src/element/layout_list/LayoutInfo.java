package element.layout_list;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;

import org.junit.jupiter.api.Test;

import element.graphic_screen.Screen;
import element.graphic_screen.UISizeChanger;
import element.lmage_manager.ImageManager;
import element.lmage_manager.Inputs;
import element.tags.SmallTag.Tag;
import logic.FileConnection;
import logic.NullExceptionEscape;
import logic.ReferenceList;
import tools.DimensionF;
import tools.RenameLabel;
import tools.RenameLabel.LabelChangedListener;
import tools.StaticTools;

public final class LayoutInfo extends NullExceptionEscape
{
	/*
	 * LayoutInfo는 각종 행동작업과 출력작업의 데이터들을 지니고있는 프로그램의 핵심 코어 클래스 입니다.
	 * LayoutList에서 소유하고 있는 하나의 객체 단위이기 때문에 이 객체는 LayoutList 에서만 생성할 수 있습니다.
	 * 외부에서 이 클래스에 접근할 일은 없으므로 메소드 클릭시의 추가적인 설명은 없습니다.
	 * 
	 * 
	 * \Info\
	 * 
	 * >다른 클래스들은 서로 교류하는 특정 객체 끼리 
	 * >그 클래스가 존재해도, 존재하지 않아도 개별적으로 운용 가능하게 설계 되어있습니다.
	 * >하지만 이 클래스는 DTO의 역활 또한 존재하기 때문에 반드시 의존적인 객체입니다.
	 * >데이터를 수정하는 하는 측에서 데이터를 수정 작업을 하는게 맞을 수 있으나,
	 * >반드시 의존적인 객체라면 수정을 위해서 해당 객체로 정보를 보내고 받는 작업을 할 필요성을 못느꼈습니다.
	 * >하므로 각종 데이터 수정 작업이 이 객체에서 처리됩니다.
	 * >이는 물론 Screen의 정보를 RotateButton로 보낼때에 주장한 내용과 상반된 의견입니다.
	 * >RotateButton와 Screen은 있어도 없어도 돌아가지만,
	 * >LayoutInfo 는 반드시 존재 해야한다는 차이점이 있습니다.
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public final static boolean FOCUS_BY_SCREEN = true, FOCUS_BY_CONTENTS = false; 
	public final static int WIDTH = 250, HEIGHT = 250;
	
	private final static int IMAGE_PANEL_WIDTH = 200, IMAGE_PANEL_HEIGHT = 200;
	
	private final static Color FOCUS_COLOR = Color.getColor("green_white", 0XFFE5FFE5),
				NOMAL_COLOR = Color.WHITE;
	
	private final static Color BORDER_COLOR = Color.getColor("grayless",0XFFE9E9E9);
	private final static Color IMAGE_BACK_COLOR = Color.getColor("grayless",0XFFE9E9E9);
	private final static String DEFAULT_LABEL_NAME = "Layout";
	
	public final static boolean SCREEN = true, CONTEXT = false;
	
	
	//---------------layout instance
	
	private RenameLabel nameLabel;
	private JPanel imagePanel;
	
	//---------------store instance
	
	private final static HashMap<String,GifThread> gifThread = new HashMap<>();
	private FilesInfoStruct filesInfo;
	private Tag synTag;
	private Image staticImage;
	
	//---------------this Object need many calling
	
	private UISizeChanger uc;
	public Screen scr;
	public ImageManager im;
	
	//---------------store for logic
	
	@SuppressWarnings("unused")
	private final static Tools tools = new Tools();
	private Float[] setterKey;
	private final ArrayList<returnRunnable> setterVlaue = new ArrayList<>();
	private Object[] switchKey;
	private final ArrayList<returnRunnable> switchValue = new ArrayList<>();
	private float setValue;
	private boolean setSwitch;
	
	//-----------------------------
	//creator
	//-----------------------------
	
	private LayoutInfo()//설명 생략
	{	
		frameCreator();
		layoutNameLabelCreator();
		fileImagePanelCreator();
	}
	//======>linked procedure
		private void frameCreator()
		{
			setFocusable(true);
			setPreferredSize(new Dimension(WIDTH,HEIGHT));
			setMaximumSize(new Dimension(WIDTH,HEIGHT));
			setBackground(NOMAL_COLOR);
			setBorder(new LineBorder(BORDER_COLOR,1));
			setLayout(new GridBagLayout());
		}
	//=======>linked procedure2
		private void layoutNameLabelCreator()
		{
			final LabelChangedListener labelListener = new LabelChangedListener() 
			{
				@Override
				public void whenTextUpdated(String text) 
				{
					synTag.reName(text);
				}
			};
			nameLabel = new RenameLabel(this,labelListener);
			nameLabel.setLimit(15);
			add(nameLabel,StaticTools.setFullGrid(0, 0, 1, 1, 0f, 0f));
		}
	//========>linked procedure3
		private void fileImagePanelCreator()
		{
			imagePanel = new JPanel()
			{
				private static final long serialVersionUID = 1L;

				@Override
				public void paintComponent(Graphics g)
				{
					super.paintComponent(g);
					((Graphics2D)g).setRenderingHint(RenderingHints.KEY_ANTIALIASING,
							RenderingHints.VALUE_ANTIALIAS_ON);
					g.setColor(IMAGE_BACK_COLOR);
					g.drawRect(0, 0, this.getWidth(), this.getHeight());
					try 
					{
						g.drawImage(staticImage, 0, 0, getWidth(), getHeight(), null);
					}
					catch(Exception e) {System.out.println("LayoutInfo.fileImagePanelCreator() (1) : nullPointer Image in Creator");}
				}
			};
		
			imagePanel.setPreferredSize(new Dimension(IMAGE_PANEL_WIDTH,IMAGE_PANEL_HEIGHT));
			imagePanel.setMaximumSize(new Dimension(IMAGE_PANEL_WIDTH,IMAGE_PANEL_HEIGHT));
			add(imagePanel,StaticTools.setFullGrid(0,1,1,1,0f,0f));
		
		}
	
	public void setNewData(int priority,final File file)//설명 생략
	{
		Image image = null;
		
		try
		{image = ImageIO.read(file);}
		catch(IOException e)
		{
			e.printStackTrace();
			System.err.println("LayoutInfo.setNewData");
			JOptionPane.showMessageDialog((JFrame)FileConnection.getObject("main frame"), "(-1) File is unauthorized access!");
			return;
		}
		
		final String defaultName = DEFAULT_LABEL_NAME + priority;
		nameLabel.setText(defaultName);
		
		allScale = 100f;
		widthScale = 100f;
		heightScale = 100f;
		
		int w;
		int h;
		int x;
		int y;
		try
		{
			w = (int) (image.getWidth(null)* scr.getRenderRate());
			h = (int) (image.getHeight(null) * scr.getRenderRate());
			
			rateX = 50f;
			rateY = 50f;
			
			x = (int) (scr.getRenderSize().width * rateX / 100 - w/2);
			y = (int) (scr.getRenderSize().height * rateY / 100 - h/2);
			
			rateW = w/(float)scr.getRenderSize().width*100f;
			rateH = h/(float)scr.getRenderSize().height*100f;
		}
		catch(Exception e)//Screen 객체를 사용하지 않을경우
		{
			w = (int) (image.getWidth(null)* scr.getRenderRate());
			h = (int) (image.getHeight(null) * scr.getRenderRate());
			x = 0;
			y = 0;
		}
		orignalRenderSize.width = w;
		orignalRenderSize.height = h;
		totalLocation.x  = x;
		totalLocation.y = y;
		totalSize.width = w;
		totalSize.height = h;
		
		filesInfo = new FilesInfoStruct(file,new ImageIcon(file.getPath()),new Dimension(w,h));
		staticImage = filesInfo.getStaticImage();
		XScale = (float)x/w*100f;
		YScale = (float)y/h*100f;
		setterBoxing();
	}
	//========>linked procedure
			private void setterBoxing()//이에 대한 설명은 setValue 측에서 확인. 334라인.
			{
				setterKey= new Float[] {rateX,rateY,rateW,rateH,XScale,YScale,widthScale,heightScale,allScale};
				switchKey= new Object[] {sizeSw,locationSw,reverseXSw,reverseYSw};
				
				setterVlaue.add(setRateX());
				setterVlaue.add(setRateY());
				setterVlaue.add(setRateW());
				setterVlaue.add(setRateH());
				
				setterVlaue.add(setXScale());
				setterVlaue.add(setYScale());
				setterVlaue.add(setWidthScale());
				setterVlaue.add(setHeightScale());
				
				setterVlaue.add(setAllScale());
				
				switchValue.add(setSizeBy());
				switchValue.add(setLocationBy());
				switchValue.add(setIsReverseX());
				switchValue.add(setIsReverseY());
			}
			//========>linked procedures
				private returnRunnable setRateX()
				{return new returnRunnable(){public Float run() {return (rateX = setValue);}};}
				
				private returnRunnable setRateY()
				{return new returnRunnable(){public Float run() {return (rateY = setValue);}};}
				
				private returnRunnable setRateW()
				{return new returnRunnable(){public Float run() {return (rateW = setValue);}};}
				
				private returnRunnable setRateH()
				{return new returnRunnable(){public Float run() {return (rateH = setValue);}};}
				
				private returnRunnable setXScale()
				{return new returnRunnable(){public Float run() {return (XScale = setValue);}};}
				
				private returnRunnable setYScale()
				{return new returnRunnable(){public Float run() {return (YScale = setValue);}};}
				
				private returnRunnable setWidthScale()
				{return new returnRunnable(){public Float run() {return (widthScale = setValue);}};}
				
				private returnRunnable setHeightScale()
				{return new returnRunnable(){public Float run() {return (heightScale = setValue);}};}
				
				private returnRunnable setAllScale()
				{return new returnRunnable(){public Float run() {return (allScale = setValue);}};}
				
				private returnRunnable setSizeBy()
				{return new returnRunnable(){public Boolean runs() {return (sizeBy = setSwitch);}};}
		
				private returnRunnable setLocationBy()
				{return new returnRunnable(){public Boolean runs() {return (locationBy = setSwitch);}};}
				
				private returnRunnable setIsReverseX()
				{return new returnRunnable(){public Boolean runs() {return (isReverseX = setSwitch);}};}
		
				private returnRunnable setIsReverseY()
				{return new returnRunnable(){public Boolean runs() {return (isReverseY = setSwitch);}};}
	
	//----------------------------
	//destroyer
	//----------------------------
	
	public void removeWithSyn()//설명 생략
	{
		try 
		{
			JPanel tags = (JPanel)synTag.getParent().getParent();
			tags.remove((ReferenceList)synTag.getParent());
			tags.revalidate();
			tags.repaint();
		}
		catch(Exception e)//Tag를 사용하지 않을 경우 아무 동작도 하지 않음
		{}
	}
	//수정 요망
	
	//----------------------------
	//getter
	//----------------------------
	public File[] getFile()
	{return (File[]) filesInfo.file.toArray();}
	
	public String getLayoutName()
	{return nameLabel.getText();}
	
	public Dimension getOrignalRenderSize()
	{return orignalRenderSize;}
	
	public Image getImage()
	{return staticImage;}
	
	public FilesInfoStruct getFilesInfo()
	{return filesInfo;}
	
	//-----------------------------
	//setter
	//-----------------------------
	
	public Boolean setSwitch(Object publicSwitchVariable, boolean value)//하단 메소드의 설명 참조
	{
		setSwitch = value;
		
		int i = 0;
		for(i = 0; (switchKey[i]) != publicSwitchVariable; i++);//하단 설명 참조(1)

		returnRunnable runnable = switchValue.get(i);
		Boolean newVlaue = runnable.runs();//기존 Float에 새로운 값을 '새로 대입'함.
		
		return newVlaue;
	}
	/*
	 * Other Why
	 * (1)Boolean의 특수성...
	 * >>Boolean의 값은 true 혹은 false이다. 
	 * >>이런이유로 Boolean객체는 예외적으로.. 객체 생성할때 해쉬 코드가 같다.
	 * >>이게 무슨 뜻이냐면
	 * >>Float a = new Float(); Float b = new Float(); 일때
	 * >>a==b 의 값은 false고...
	 * >>Boolean a = new Boolean(); Boolean b = new Boolean(); 일때
	 * >>(Object)a == (Object)b 의 값은 TRUE 다.(이는 오토박싱에 의한 문제가 아니다.)
	 * >>하므로.. getter와 setter를 사용을 권장 하지만.... 상황상 getter와 setter의 사용이 더 난잡한관계로.
	 * >>작성자는 제 3자 Object를 사용하기로 했다. 즉 여기 안에 들어가는 내용물은 Boolean 이 아니다.
	 */
	
	public Float setValue(Float publicFloatVariable, float value)//하단 설명 참조
	{
		setValue = value;
		
		int i = 0;
		
		for(i = 0; setterKey[i] != publicFloatVariable; i++);
		
		returnRunnable runnable = setterVlaue.get(i);
		Float newKey = runnable.run();//기존 Float에 새로운 값을 '새로 대입'함.
		setterKey[i] = newKey;//새로 만들어진 Float를 키로 설정함.
		
		return newKey;
	}
	/*
	 * 기능: setter
	 * 
	 * Other Why
	 * (E)변수가 public인데 이 메소드가 왜 필요한가?
	 * >>
	 * >>설명이 매우 길고, 이유는 알아도 되며 몰라도 된다.
	 * >>
	 * >>다른 클래스 A 에서 이 클래스의 변수를 변동한다고 가정해보자.
	 * >>여기의 변수 값은 public 이므로 layoutInfo.rateX = 50 이란 값으로 쉽게 수정할수 있을것이다.
	 * >>그런데 예를 들어서
	 * >>do
	 * >>{
	 * >>	rateX += 1;
	 * >>	printImage(image,rateX,rateY);
	 * >>}while(layoutInfo.rateX <= layoutInfo.rateY);//대충 x와 y값이 같아 질때까지 1씩 이동함.
	 * >>do
	 * >>{
	 * >>	rateW += 1;
	 * >>	printImage(image,rateW,rateH);
	 * >>}while(layoutInfo.rateW <= layoutInfo.rateH);//대충 너비와 높이가 같아 질때까지 크기를 1씩 키움.
	 * >>
	 * >>이런 연산이 필요한 상황이라면? 어떻게 할것인가?
	 * >>
	 * >>보통이라면 
	 * >>public void valuePlusOne(Float value,Float ruler)
	 * >>{
	 * >>	do
	 * >>	{
	 * >>		value += 1;
	 * >>		printImage(image,value,ruler);
	 * >>	}while(value <= ruler);
	 * >>}
	 * >>이런 함수를 하나 만들어서 인자를 대입해서 처리하려 하지 않겠는가?
	 * >>하지만, float등의 기본형 데이터는 인자로 들어갈때 복사값을 지니게된다(java에는 포인터가 없으므로....)
	 * >>즉 인자의 값을 수정해도 원본값에 변동이 없으므로 무의미해 진다.
	 * >>다르게 생각해서 변수를 Float로 대입해서 참조형으로 집어 넣으라고 할수도 있으나.
	 * >>Float 에서  = 연산은 값의 수정이 아니라 대입이라는 문제가 생긴다.
	 * >>이게 무슨 뜻이냐면 인자의 레퍼런스가 참조하는 지점을 바꿀뿐이지 원본값을 수정하는것은 아니라는 뜻이다.
	 * >>Float 등의 Wrapper 클래스는 set() 메소드가 없으며, final 클래스 이므로 상속도 안된다.
	 * >>그러면 이런 방법을 생각할수도 있을것이다. 변수값들을 class안에 감싸거나 배열 객체로 전달해서 처리한다.
	 * >>..그건 지금있는 13개의 public 변수만큼 13개의 class를 전부 정의를 하는건 둘째치고
	 * >>수정 외적으로 변수를 사용할때마다 모든 연산에 .get()연산이나 ob.vlaue 혹은 array[RATE_X] 를 쳐서 쓰겠다는건데..
	 * >>object + object 를 구현하는 연산자 오버로딩이 java에 없으니 그냥 메소드 하나 하드코딩 되는편이 훨 나은 선택이다.
	 * >>하므로 이 메소드를 이렇게 활용한다.
	 * >>public void valuePlusOne(Float value,Float ruler)
	 * >>	do
	 * >>	{
	 * >>		setVlaue(value,value += 1);
	 * >>		printImage(image,value,ruler);
	 * >>	}while(value <= ruler);
	 * >>
	 */
	
	public void setSynTag(final Tag tag)
	{synTag = tag;}
	
	public static void setGifDrawingTarget(final String targetName,final Component c)//하단 설명 참조
	{gifThread.put(targetName,new GifThread(c));}
	/*
	 * 기능: LayoutInfo 에서 지닌 이미지가 GIF 일경우  실행전에 준비 작업을 한다.
	 * component를 지속적으로 repaint 하는 스레드를 생성하여 실행한다.
	 * 
	 * Other Why
	 * (E)GIF는 여러개일수 있다.
	 * targetName을 따로 두는 이유는 다른 LayoutInfo 측에서 동시에 GIF를 사용할 가능성이 있기 때문에 식별자ID를 준것이다.
	 */
	//========>linked method
		public static void startGif(final String targetName)
		{
			GifThread thread = gifThread.get(targetName);
			thread.isRun=true;
			try {gifThread.get(targetName).start();}
			catch(Exception e) {gifThread.notify();}
		}
	//========>linked method2
		public static boolean isGifPlay(final String targetName)
		{
			try {return gifThread.get(targetName).isAlive();}
			catch(NullPointerException e){return false;}
		}
	//========>linked method3
		public static void stopGif(final String targetName)
		{
			try {gifThread.get(targetName).wait();} 
			catch (InterruptedException e) {}
		}
	//========>linked method4
		public static void endGif(final String targetName)
		{gifThread.get(targetName).isRun = false;}
	
	//-----------------------------
	//actions
	//-----------------------------
		
	public boolean isGif()//이 레이아웃의 이미지중에 GIF가 있을 경우 true를 건네준다.
	{
		int images = 0;
	    final ImageReader is = ImageIO.getImageReadersBySuffix("GIF").next();  
	    try 
	    {
	        for(File file:filesInfo.file)
	        {
	        	ImageInputStream iis = ImageIO.createImageInputStream(file);
	            is.setInput(iis);  
	            images= is.getNumImages(true);
	                
	            if(images>1)
	            	break;
	        }
	    } 
	    catch (IOException e) 
	    {
	        e.printStackTrace();
	        System.err.println("LayoutInfo.isGIF() (-2): A file is Crashed!");
	    }
	    
	    return images>1;
	}
		
	
	public void updateDataByIM()//이 메소드는 ImageManeger에서 수치를 변동했을경우 호출된다.
	{
		try 
		{
			if(uc.isRun())//UISizeChanger로 값을 변동할 경우 ImageManager 수치가 변동되어 버리기 때문에 이때 이 메소드가 호출되면 곂쳐 버린 연산으로 인한 값 오차가 발생한다.
				return;
		}
		catch(Exception e)
		{
			try
			{
				if((uc = (UISizeChanger)FileConnection.getObject("UI size changer")).isRun())
					return;
			}
			catch(Exception ex)
			{}
		}
		
		if(widthScale!= 0)
		{
			totalSize.width = (int)((float)orignalRenderSize.width * widthScale/100f * allScale/100f);
			
			if(!isReverseX || scr==null)
			{
				if(XScale == 0)
					totalLocation.x = 0;
				else
				totalLocation.x = (int) ((float)totalSize.width * XScale / 100f);
			}
			else
			{
				if(XScale == 0)
					totalLocation.x = scr.getRenderSize().getWidth()-totalSize.width;
				else
					totalLocation.x = (int)(scr.getRenderSize().width - totalSize.width - ((float)totalSize.width * XScale / 100f));//
			}
		}
		if(heightScale!= 0)
		{
			totalSize.height = (int)((float)orignalRenderSize.height * heightScale/100f * allScale/100f);
			
			if(!isReverseY || scr==null)
			{
				if(YScale == 0)
					totalLocation.y = 0;
				else
				totalLocation.y = (int) ((float)totalSize.height * YScale / 100f);
			}
			else
			{
				if(YScale == 0)
					totalLocation.y = scr.getRenderSize().getHeight()-totalSize.height;
				else
					totalLocation.y = (int)(scr.getRenderSize().height - totalSize.height - ((float)totalSize.height * YScale / 100f));//
			}
		}
		
		filesInfo.updateData();
		
		try
		{im.get().repaint();}
		catch(Exception e)
		{(im = (ImageManager)FileConnection.getObject("Image Manager")).get().repaint();}//하단 설명 참조(1)
		try{uc.inFocus(this);}//ImageManager 에서 수정한 값을 UserSizeChanger 에 동기화 한다.
		catch(Exception e) {};
	}
	/*
	 * Other Why
	 * (1)ImageManager객체는 반드시 존재한다.
	 * >>LayoutInfo는 LayoutList에서만 만들수 있으며 LayoutInfo 자체가 외부로 노출되지 않게 설계되어있다.
	 * >>즉 이 메소드가 호출되는 시점은 반드시 ImageManager의 화면이 돌아갈때 밖에 없으며 이는 ImageManager가 반드시 존재 한다는 뜻이다.
	 */
	
	public void updateDataByUC(final int x, final int y, final int w, final int h)//이 메소드는 UISizeChanger에서 수치를 변동했을경우 호출된다.
	{
		totalLocation.x = x;
		totalLocation.y = y;
		totalSize.width = w;
		totalSize.height = h;
		
		updateRateData();
		
		filesInfo.updateData();
		
		try 
		{im.refreshTexts();}
		catch(Exception e)
		{
			im = (ImageManager)FileConnection.getObject("image manager");
			im.refreshTexts();
		}
		finally
		{
			try
			{im.tools.refreshImage();}
			catch(Exception e)
			{}
		}//UserSizeChanger 에서 수정한 값을 ImageManager에 동기화 한다.
	}
	//========>linked procedure
		private void updateRateData()//이미지의 비율 값들을 조정 한다.
		{
			DimensionF scrSize;
			
			try//이 메소드가 호출된 시점부터 Screen은 반드시 있으나 이를 따로 등록하지 않았을 경우 screen 값을 가져온다.
			{scrSize = scr.getRenderSize();}
			catch(Exception e)
			{scrSize = (scr = (Screen)FileConnection.getObject("screen")).getRenderSize();}//하단 설명 참조(2)
		
			setValue(widthScale , totalSize.width/(float)getOrignalRenderSize().width*100 / (allScale/100));
			setValue(heightScale , totalSize.height/(float)getOrignalRenderSize().height*100 / (allScale/100));
		
			if(!isReverseX)//하단 설명 참조(1)
				setValue(XScale , (float)totalLocation.x/totalSize.width*100);
			else
				setValue(XScale , (float)(scrSize.width - (totalLocation.x + totalSize.width))/totalSize.width*100);
		
			if(!isReverseY)
				setValue(YScale , (float)totalLocation.y/totalSize.height*100);
			else
				setValue(YScale , (float)(scrSize.height -(totalLocation.y + totalSize.height))/totalSize.height*100);
		
				setValue(rateX , (totalLocation.x+(float)totalSize.width/2)/scrSize.width*100);
				setValue(rateY , (totalLocation.y+(float)totalSize.height/2)/scrSize.height*100);
				setValue(rateW , totalSize.width/(float)scrSize.width*100);
				setValue(rateH , totalSize.height/(float)scrSize.height*100);
		}
		/*
		 * Other Why
		 * (1)실행 우선순위가 높음
		 * >>isReverseX 와 isReverseY 는 x와 y의 시작 기준점을 반대 방향으로 바꾸는 스위치이다.
		 * >>기존 Default 값은 리버스를 적용하지 않은 값이므로 !reverse가 먼저 실행될 가능성이 높다.
		 * (2)Screen객체는 반드시 존재한다.
		 * >>LayoutInfo는 LayoutList에서만 만들수 있으며 LayoutInfo 자체가 외부로 노출되지 않게 설계되어있다.
		 * >>즉 이 메소드가 호출되는 시점은 반드시 screen의 화면이 돌아갈때 밖에 없으며 이는 Screen이 반드시 존재 한다는 뜻이다.
		 */
	
	public void updateDataByScrSize()//Graphic screen 의 크기가 변동되었을때 LayoutList에서 호출된다.
	{
		DimensionF renderSize;
		
		try
		{renderSize = scr.getRenderSize();}
		catch(Exception e)
		{renderSize = (scr = (Screen)FileConnection.getObject("screen")).getRenderSize();}//하단 설명 참조 (1)
		
		float width = 0;
		float height = 0;
		
		if(sizeBy)//화면 회전시에 이미지의 크기를 화면비로 설정했다면..
		{
			width = renderSize.width * rateW/100f;
			height = renderSize.height * rateH/100f;
			totalSize.width = (int)(width * allScale/100f);
			totalSize.height = (int)(height * allScale/100f);
			setValue(widthScale,width/orignalRenderSize.width*100f);
			setValue(heightScale,height/orignalRenderSize.height*100f);
		}
		else//화면 회전시에 이미지의 크기를 이미지비율로 설정했다면..
		{
			width = (float)orignalRenderSize.width * widthScale/100f;
			height = (float)orignalRenderSize.height * heightScale/100f;
			totalSize.width = (int)(width * allScale/100f);
			totalSize.height = (int)(height * allScale/100f);
			setValue(rateW,width/(float)renderSize.width*100f);
			setValue(rateH,height/(float)renderSize.height*100f);
		}
		if(locationBy)//화면 회전시에 이미지의 위치를 화면비로 설정했다면..
		{
			totalLocation.x = (int) (renderSize.width * rateX / 100f - (float)totalSize.width/2);
			totalLocation.y = (int) (renderSize.height * rateY / 100f - (float)totalSize.height/2);
			if(!isReverseX) 
				setValue(XScale,(float)totalLocation.x/totalSize.width*100f);
			else
				setValue(XScale,(float)(renderSize.width - (totalLocation.x + totalSize.width)) /totalSize.width*100f);
			if(!isReverseY) 
				setValue(YScale,(float)totalLocation.y/totalSize.height*100f);
			else
				setValue(YScale,(float)(renderSize.height - (totalLocation.y + totalSize.height)) /totalSize.height*100f);
		}
		else//화면 회전시에 이미지의 위치를 이미지비율로 설정했다면..
		{
			if(!isReverseX)
				totalLocation.x = (int) (totalSize.width * XScale/100);
			else
				totalLocation.x = (int) (renderSize.width - totalSize.width * XScale/100 - totalSize.width);
			if(!isReverseY)
				totalLocation.y = (int) (totalSize.height * YScale/100);
			else
				totalLocation.y = (int) (renderSize.height - totalSize.height * YScale/100 - totalSize.height);
			setValue(rateX,(float)(totalLocation.x + totalSize.width/2f)/renderSize.width*100);
			setValue(rateY,(float)(totalLocation.y + totalSize.height/2f)/renderSize.height*100);
		}
		
		filesInfo.updateData();
	}
	/*
	 * Other Why
	 * (1)Screen객체는 반드시 존재한다.
	 * >>LayoutInfo는 LayoutList에서만 만들수 있으며 LayoutInfo 자체가 외부로 노출되지 않게 설계되어있다.
	 * >>즉 이 메소드가 호출되는 시점은 반드시 screen의 화면이 돌아갈때 밖에 없으며 이는 Screen이 반드시 존재 한다는 뜻이다.
	 */
	public void updateDataByReverse()//ImageManager에서 이미지의 시작기준점을 바꿧을 경우에 실행된다.
	{
		DimensionF scrSize;
		try {scrSize = scr.getRenderSize();}
		catch(Exception e) 
		{
			try
			{scrSize = (scr=(Screen) FileConnection.getObject("screen")).getRenderSize();}
			catch(Exception ex)
			{return;}
		}
		
		final float w = scrSize.width*(rateW/100)*(allScale/100);
		final float locationX = scrSize.width * rateX/100;
		
		
		if(!isReverseX)//하단 설명 참조 (1)
			setValue(XScale,(locationX - w/2)/w*100);
		else
			setValue(XScale,(scrSize.width - (locationX + w/2))/w*100);
		
		final float h = scrSize.height*(rateH/100)*(allScale/100);
		final float locationY = scrSize.height * rateY/100;
		if(!isReverseY)
			setValue(YScale,(locationY - h/2)/h*100);
		else
			setValue(YScale,(scrSize.height - (locationY + h/2))/h*100);
		
		try
		{
			im.setTextNoAction(Inputs.X_BY_CONTENT, XScale);
			im.setTextNoAction(Inputs.Y_BY_CONTENT, YScale);
		}
		catch(Exception e)
		{
			(im = (ImageManager)FileConnection.getObject("image manager")).setTextNoAction(Inputs.X_BY_CONTENT, XScale);
			im.setTextNoAction(Inputs.Y_BY_CONTENT, YScale);
		}
	}
	/*
	 * Other Why
	 * (1)실행 우선순위가 높음
	 * >>isReverseX 와 isReverseY 는 x와 y의 시작 기준점을 반대 방향으로 바꾸는 스위치이다.
	 * >>기존 Default 값은 리버스를 적용하지 않은 값이므로 !reverse가 먼저 실행될 가능성이 높다.
	 */
	
	public static WorkingRunnable getUpdateWorkByLocation(Float locationVar, Inputs input,NullExceptionEscape info)//Inputs 에서 위치변수 값이 수정됬을때 연동된 수정할 값들을 업데이트 한다.
	{
		WorkingRunnable result;
		
		if(locationVar==info.rateX||locationVar==info.rateY)//screen 비율비가 수정 되었을 경우
		{		
			result = 
					
			new WorkingRunnable()
			{
				public void run()
				{
					try 
					{
						float scrSize = 0;
						float sizeRate = 0;
						Float updatedLocation = 0f;
						Float synedLocation = 0f;
						boolean reverse = false;
						
						if(holder == 2)//높이 값들을 초기화한다.
						{
							scrSize = info.scr.getRenderSize().height;
							updatedLocation = info.rateY;
							synedLocation = info.YScale;
							sizeRate = info.rateH;
							reverse = info.isReverseY;
						}
						else if(holder == 1)//너비 값들을 초기화한다.
						{
							scrSize = info.scr.getRenderSize().width;
							updatedLocation = info.rateX;
							synedLocation = info.XScale;
							sizeRate = info.rateW;
							reverse = info.isReverseX;
						}
						updatedLocation = info.setValue(updatedLocation,input.getValueF());//info를 uiSizer에서 업데이트 하기전에 그 uiSizer에서 바뀐 값을 미리 키/벨류 업데이트 해야한다.
						float size = scrSize*(sizeRate/100)*(info.allScale/100);
						float location = scrSize * (updatedLocation/100);
						if(!reverse)//위치의 시작점이 화면의 처음부터일 경우
							synValue[0] = info.setValue(synedLocation,(location - size/2)/size*100);
						else//위치의 시작점이 화면의 끝부터 일경우
							synValue[0] = info.setValue(synedLocation,(scrSize - (location + size/2))/size*100);
					}
					catch(Exception e) {}//하단설명 참조(2)
				}
			};
			
			if(locationVar==info.rateX)
				result.holder =1;//하단 설명참조(1)
			else if(locationVar==info.rateY)
				result.holder =2;
			
			return result;
		}
		else if(locationVar==info.XScale||locationVar==info.YScale)//이미지 원본 비율비가 수정 되었을 경우
		{
			result = 
					
			new WorkingRunnable()
			{	
				public void run()
				{
					try 
					{
						float scrSize = 0f;
						float imgSize = 0f;
						float sizeRate = 0;
						Float updatedLocation = 0f;
						Float synedLocation = 0f;
						float location = 0f;
						boolean reverse = false;
						
						if(holder == 1)//너비 값들을 초기화한다.
						{
							scrSize = info.scr.getRenderSize().width;
							imgSize = info.orignalRenderSize.width;
							updatedLocation = info.XScale;
							sizeRate = info.widthScale;
							synedLocation = info.rateX;
							reverse = info.isReverseX;
						}
						else if(holder == 2)//높이 값들을 초기화한다.
						{
							scrSize = info.scr.getRenderSize().height;
							imgSize = info.orignalRenderSize.height;
							updatedLocation = info.YScale;
							sizeRate = info.heightScale;
							synedLocation = info.rateY;
							reverse = info.isReverseY;
						}
						
						updatedLocation = info.setValue(updatedLocation,input.getValueF());
						
						float size = imgSize*(sizeRate/100)*(info.allScale/100);
						if(!reverse)
							location = size * updatedLocation/100 + size/2;
						else
							location = scrSize - size * updatedLocation/100 - size/2;
						
						synValue[0] = info.setValue(synedLocation,location/scrSize*100);
					}
					catch(Exception e){}
				}
			};
			
			if(locationVar==info.XScale)
				result.holder =1;//하단 설명참조(1)
			else if(locationVar==info.YScale)
				result.holder =2;
			
			return result;
		}
		else
			return new WorkingRunnable() {public void run() {}};
	}
	/*
	 * 기능:위치변수 값이 수정됬을때 연동된 수정할 값들을 업데이트하는 객체를 건네준다.
	 * 1.screen객체가 있는지 확인하고 있다면 초기화 하고 없다면 빈연산을 보내서 끝낸다.
	 * 2.인자로 입력된 값이 화면비일경우 X인지 Y인지 가려낸뒤 이미지 원본비율 값을 변경하는 객체를 보낸다.
	 * 3.인자로 입력된 값이 원본비일경우 X인지 Y인지 가려낸뒤 이미지 화면비율 값을 변경하는 객체를 보낸다.
	 * 4.인자로 입력된 값이 알수 없는 경우 연산이 없는 객체를 보낸다.
	 * 
	 * Other Why
	 * (1)고정값과 변동값
	 * >>인자로 받은 Float locationVar는 수정이 불가능한 데이터이다.
	 * >>고로 이 값을 수정하고자 한다면 기존 객체를 버리고 새로운 객체를 만든뒤 레퍼런스 지정을 변경하는 방법을 쓰게되는데.
	 * >>이렇게 수정된 레퍼런스의 값을 지속적으로 인자로 넘겨 줄수 있으면 좋겠지만 
	 * >>연산을 객체화하여 넘기는 작업을 하는 이 메소드에 인자를 지속적으로 넘긴다는 것은 지속적으로 인자가 바뀔때마다 객체를 만드는 결과를 낳게한다.
	 * >>하므로 객체는 그대로 두고 내부의 데이터만 밖의 데이터에 맞게 최신화를 할 필요가 있으므로 인자를 더이상 받지 않고 전역변수의 값을 대입하여 최신화 하는 것이다.
	 * >>이 과정에서 값이 바뀌면서 locationVar != XScale 같은 현상이 나타나게 되므로 if 구절에서 작용할 새로운 기준의 역활을 하기 위해서 holder가 쓰인다.
	 * 
	 * (2)Listener의 생성시기
	 * >>여기에서 받을수있는 Runnable은 하나의 기능을 하는 역활을한다.
	 * >>즉 이 기능을 Listener에 집어넣어서 run() 메소드를 실행 시킬수 있지만
	 * >>여기에서 한가지 문제점이 생긴다. 물론 사용하기 나름이지만 LayoutInfo는 프로그램실행시에
	 * >>객체가 존재하지 않고 나중에 만들어 진다는 것이다.
	 * >>이에 따라서 이 기능을 리스너로써 사용하고 싶어도 외부에서 객체를 받을수 없는 이 객체의 특성상
	 * >>레퍼런스에 임시값을 대입해서 이 메소트를 호출하는것도 불가능하며,
	 * >>이러한 문제를 해결하기 위해서 이 메소드를 Static으로 정의 하였다.
	 * >>인자로 들어가는 LayoutInfo가 존재하게 되면 메소드는 정상작동 할것이다.
	 */
	
	public static WorkingRunnable getUpdateWorkBySize(Float sizeVar, Inputs input, NullExceptionEscape info)//Inputs 에서 크기변수 값이 수정됬을때 연동된 수정할 값들을 업데이트 한다.
	{
		WorkingRunnable result;
		
		if(sizeVar==info.rateW||sizeVar==info.rateH)//screen 비율비가 수정 되었을 경우
		{
			result =
					
			new WorkingRunnable()
			{
				public void run()
				{
					try 
					{
						float scrSize = 0;
						float locationScrRate = 0;
						float imgSize = 0f;
						Float updatedSize = 0f;
						Float synedSize = 0f;
						Float synedLocation = 0f;
						boolean reverse = false;
						
						if(holder == 1||sizeVar==info.rateW)//너비 값들을 초기화한다.
						{
							scrSize = info.scr.getRenderSize().width;
							updatedSize = info.rateW;
							synedSize = info.widthScale;
							locationScrRate = info.rateX;
							synedLocation = info.XScale;
							imgSize = info.orignalRenderSize.width;
							reverse = info.isReverseX;
						}
						else if(holder == 2||sizeVar==info.rateH)//높이 값들을 초기화한다.
						{
							scrSize = info.scr.getRenderSize().height;
							updatedSize = info.rateH;
							synedSize = info.heightScale;
							locationScrRate = info.rateY;
							synedLocation = info.YScale;
							imgSize = info.orignalRenderSize.height;
							reverse = info.isReverseY;
						}
						
						updatedSize = info.setValue(updatedSize,input.getValueF());
						synedSize = info.setValue(synedSize,scrSize*updatedSize/100f/(float)imgSize*100);
						
						final float size = scrSize*(updatedSize/100)*(info.allScale/100);
						final float location = (float)scrSize * locationScrRate/100;
						
						if(!reverse)//위치의 시작점이 화면의 처음부터일 경우
							synedLocation = info.setValue(synedLocation,(location - size/2)/size*100);
						else//위치의 시작점이 화면의 끝부터 일경우
							synedLocation = info.setValue(synedLocation,(scrSize - (location + size/2))/size*100);
						
						synValue[0] = synedSize;
						synValue[1] = synedLocation;
					}
					catch(Exception e) {} // 하단 설명참조(2)
				}
			};
			
			if(sizeVar==info.rateW)
				result.holder =1;
			else if(sizeVar==info.rateH)
				result.holder =2;
			
			return result;
		}
		else if(sizeVar==info.widthScale||sizeVar==info.heightScale)//이미지 원본 비율비가 수정 되었을 경우
		{
			result = 
			
			new WorkingRunnable()
			{
				
				public void run()
				{
					try 
					{
						Float scrSize = null;
						float imgSize = 0f;
						float locationScrRate = 0;
						Float updatedSize = 0f;
						Float synedSize = 0f;
						Float synedLocation = 0f;
						boolean reverse = false;
						
						if(holder == 1||sizeVar.equals(info.widthScale))//너비 값들을 초기화한다.
						{
							scrSize = info.scr.getRenderSize().width;
							imgSize = info.orignalRenderSize.width;
							updatedSize = info.widthScale;
							
							locationScrRate = info.rateX;
							synedSize = info.rateW;
							synedLocation = info.XScale;
							reverse = info.isReverseX;
						}
						else if(holder == 2||sizeVar.equals(info.heightScale))//높이 값들을 초기화한다.
						{
							scrSize = info.scr.getRenderSize().height;
							imgSize = info.orignalRenderSize.height;
							updatedSize = info.heightScale;
							
							locationScrRate = info.rateY;
							synedSize = info.rateH;
							synedLocation = info.YScale;
							reverse = info.isReverseY;
						}
						updatedSize = info.setValue(updatedSize,input.getValueF());
						
						synedSize = info.setValue(synedSize,(int)(imgSize * updatedSize/100)/(float)scrSize*100f);
						
						final float size = imgSize*(updatedSize/100)*(info.allScale/100);
						final float location = scrSize * locationScrRate/100;
						
						if(!reverse)
							synedLocation = info.setValue(synedLocation,(location - size/2)/size*100);
						else
							synedLocation = info.setValue(synedLocation,(scrSize - (location + size/2))/size*100);
						
						synValue[0] = synedSize;
						synValue[1] = synedLocation;
					}
					catch(Exception e) {}
				}
			};
			
			if(sizeVar==info.widthScale)
				result.holder =1;
			else if(sizeVar==info.heightScale)
				result.holder =2;
			
			return result;
		}
		else if(sizeVar==info.allScale)
		{
			return
			
			new WorkingRunnable()
			{
				public void run()
				{
					try
					{
						info.setValue(info.allScale,input.getValueF());
						
						float sizeW = info.orignalRenderSize.width*(info.widthScale/100)*(info.allScale/100);
						float locationX = (float)info.scr.getRenderSize().width * info.rateX/100;
						
						if(!info.isReverseX)
							info.setValue(info.XScale,(locationX - sizeW/2)/sizeW*100);
						else
							info.setValue(info.XScale,((float)info.scr.getRenderSize().width - (locationX + sizeW/2))/sizeW*100);
						
						final float sizeH = info.orignalRenderSize.height*(info.heightScale/100)*(info.allScale/100);
						final float locationY = (float)info.scr.getRenderSize().height * info.rateY/100;
						
						if(!info.isReverseY)
							info.setValue(info.YScale,(locationY - sizeH/2)/sizeH*100);
						else
							info.setValue(info.YScale,((float)info.scr.getRenderSize().height - (locationY + sizeH/2))/sizeH*100);
						
						synValue[0] = info.XScale;
						synValue[1] = info.YScale;
					}
					catch(Exception e){}
				}
			};
		}
			
		return new WorkingRunnable() {public void run() {}};
	}
	
	/*
	 * 기능:크기변수 값이 수정됬을때 연동된 수정할 값들을 업데이트하는 객체를 건네준다.
	 * 1.screen객체가 있는지 확인하고 있다면 초기화 하고 없다면 빈연산을 보내서 끝낸다.
	 * 2.인자로 입력된 값이 화면비일경우 W인지 H인지 가려낸뒤 이미지 원본비율 값과 원본위치 비율을 변경하는 객체를 보낸다.
	 * 3.인자로 입력된 값이 원본비일경우 W인지 H인지 가려낸뒤 이미지 화면비율 값을 원본위치 비율을 변경하는 객체를 보낸다.
	 * 4.인자로 입력된 값이 전체 크기 변경 비율일 경우 원본 위치비율 X,Y를 수정한다.
	 * 5.인자로 입력된 값이 알수 없는 경우 연산이 없는 객체를 보낸다.
	 * 
	 * Other Why
	 * (1)고정값과 변동값
	 * >>인자로 받은 Float locationVar는 수정이 불가능한 데이터이다.
	 * >>고로 이 값을 수정하고자 한다면 기존 객체를 버리고 새로운 객체를 만든뒤 레퍼런스 지정을 변경하는 방법을 쓰게되는데.
	 * >>이렇게 수정된 레퍼런스의 값을 지속적으로 인자로 넘겨 줄수 있으면 좋겠지만 
	 * >>연산을 객체화하여 넘기는 작업을 하는 이 메소드에 인자를 지속적으로 넘긴다는 것은 지속적으로 인자가 바뀔때마다 객체를 만드는 결과를 낳게한다.
	 * >>하므로 객체는 그대로 두고 내부의 데이터만 밖의 데이터에 맞게 최신화를 할 필요가 있으므로 인자를 더이상 받지 않고 전역변수의 값을 대입하여 최신화 하는 것이다.
	 * >>이 과정에서 값이 바뀌면서 locationVar != XScale 같은 현상이 나타나게 되므로 if 구절에서 작용할 새로운 기준의 역활을 하기 위해서 holder가 쓰인다.
	 * 
	 * (2)Listener의 생성시기
	 * >>여기에서 받을수있는 Runnable은 하나의 기능을 하는 역활을한다.
	 * >>즉 이 기능을 Listener에 집어넣어서 run() 메소드를 실행 시킬수 있지만
	 * >>여기에서 한가지 문제점이 생긴다. 물론 사용하기 나름이지만 LayoutInfo는 프로그램실행시에
	 * >>객체가 존재하지 않고 나중에 만들어 진다는 것이다.
	 * >>이에 따라서 이 기능을 리스너로써 사용하고 싶어도 외부에서 객체를 받을수 없는 이 객체의 특성상
	 * >>레퍼런스에 임시값을 대입해서 이 메소트를 호출하는것도 불가능하며,
	 * >>이러한 문제를 해결하기 위해서 이 메소드를 Static으로 정의 하였다.
	 * >>인자로 들어가는 LayoutInfo가 존재하게 되면 메소드는 정상작동 할것이다.
	 */
	
	public void outFocus()//LayoutList에서 선택 해제 되었을때 호출된다.
	{
		setBackground(NOMAL_COLOR);
	}
	
	public void inFocus()//LayoutList에서 선택되었을때 이 LayoutInfo 정보들을 각 연계된 객체들에 전달한다.
	{
		setBackground(FOCUS_COLOR);
		
		UISizeChanger uc = null;
		try 
		{uc = scr.getUISizeChanger();}
		catch(Exception e) 
		{
			try
			{
				uc = (scr = (Screen)FileConnection.getObject("screen")).getUISizeChanger();
			}
			catch(Exception ex) {}// Screen 객체를 사용하지 않음.
		}
		try 
		{uc.inFocus(this);}
		catch(Exception e) {}// 등록된 UserSizeChanger가 존재하지 않음
		
		try
		{im.tools.inFocus(this);}
		catch(Exception e)
		{
			try
			{(im = (ImageManager)FileConnection.getObject("image manager")).tools.inFocus(this);}
			catch(Exception ex)
			{}//ImageManager를 사용하지 않음
		}
	}
	/**
	 * Focus out selection with linked Object.
	 */
	public void noFocus()//모든 선택을 해제하고 연관된 객체들에 전달한다.
	{
		setBackground(NOMAL_COLOR);
		
		try 
		{scr.hideUISizeChanger();}
		catch(Exception e) 
		{}// Screen 객체를 사용하지 않음. 혹은 등록된 UISizeChanger 가 존재 하지 않음.
		try
		{im.tools.inFocus(null);}
		catch(Exception e)
		{
			try
			{(im = (ImageManager)FileConnection.getObject("image manager")).tools.inFocus(null);}
			catch(Exception ex)
			{}//ImageManager를 사용하지 않음
		}
	}

	@Test
	public void log()
	{
		System.out.println("===============FILES INFO===============");
		System.out.println("---------------File List----------------");
		for(File file:filesInfo.file)
		{
			System.out.println("> " + file.getPath());
		}
		System.out.println("-------------renderSize data------------");
		System.out.println("> X: " + totalLocation.x);
		System.out.println("> Y: " + totalLocation.y);
		System.out.println("> W: " + totalSize.width);
		System.out.println("> H: " + totalSize.height);
		System.out.println("--------------realSize data-------------");
		System.out.println("> X: " + (int)(totalLocation.x/scr.getRenderRate()));
		System.out.println("> Y: " + (int)(totalLocation.y/scr.getRenderRate()));
		System.out.println("> W: " + (int)(totalSize.width/scr.getRenderRate()));
		System.out.println("> H: " + (int)(totalSize.height/scr.getRenderRate()));
		System.out.println("---------------reSize data--------------");
		System.out.println("*Rate by screen");
		System.out.println("> X: " + rateX);
		System.out.println("> Y: " + rateY);
		System.out.println("> W: " + rateW);
		System.out.println("> H: " + rateH);
		System.out.println("*Rate by context");
		System.out.println("> X: " + XScale);
		System.out.println("> Y: " + YScale);
		System.out.println("> W: " + widthScale);
		System.out.println("> H: " + heightScale);
		System.out.println("*All Scale");
		System.out.println("> X: " + allScale);
		System.out.println("=========================================");
		
	}
	
	public void drawGif(Graphics g)//이미지가 gif일경우 연속해서 그리면 이미지가 움직이게 된다.
	{
		for(int i = 0;i<filesInfo.size();i++)//하나의 레이아웃은 여러 이미지를 지닌다.
		g.drawImage(filesInfo.image.get(i).getImage(), 
				totalLocation.x + filesInfo.location.get(i).x, 
					totalLocation.y + filesInfo.location.get(i).y, 
						filesInfo.size.get(i).width, 
							filesInfo.size.get(i).height, 
								null);
	}
	//========>Overload
		public void drawGif(Graphics g,final int x, final int y)
		{
			for(int i = 0;i<filesInfo.size();i++)
				g.drawImage(filesInfo.image.get(i).getImage(), 
						x + filesInfo.location.get(i).x,
						y + filesInfo.location.get(i).y,
							filesInfo.size.get(i).width,
								filesInfo.size.get(i).height,
									null);
		}
	
	public void drawImage(Graphics g)
	{g.drawImage(staticImage, totalLocation.x, totalLocation.y, totalSize.width, totalSize.height, null);}
	
	public void bindInfo(LayoutInfo info)//두개의 레이아웃을 하나로 합칠때 호출된다
	{
		FilesInfoStruct filesInfo = info.getFilesInfo();
		
		this.filesInfo.bindFilesInfo(filesInfo.file, filesInfo.image, filesInfo.size, filesInfo.location, info.totalLocation, info.totalSize);
		restructVlaues();
		changeImagePanel();
		getParent().getParent().getParent().repaint();
	}
	//=======>linked procedure
		private void restructVlaues()//합쳐진 이미지들의 위치와 크기 전부 포함한 총 크기/비율/위치값을 다시 재구성한다.
		{
			orignalRenderSize.width = totalSize.width;
			orignalRenderSize.height = totalSize.height;
		
			//rate 는 스크린 기준비
			setValue(rateX,(totalLocation.x + (float)totalSize.width/2)/scr.getRenderSize().width*100);
			setValue(rateY,(totalLocation.y + (float)totalSize.height/2)/scr.getRenderSize().height*100);
			setValue(rateW,(float)orignalRenderSize.width/scr.getRenderSize().width*100);
			setValue(rateH,(float)totalSize.height/scr.getRenderSize().height*100);
		
			setValue(allScale,100f);
		
			//scale은 이미지 기준비
			setValue(XScale,(float)totalLocation.x/orignalRenderSize.width*100);
			setValue(YScale,(float)totalLocation.y/orignalRenderSize.height*100);
			setValue(widthScale,100f);
			setValue(heightScale,100f);
		}
	//=======>linked procedure2
		@SuppressWarnings("serial")
		private void changeImagePanel()
		{
			staticImage = filesInfo.getStaticImage();
			final int w = imagePanel.getWidth();
			final int h = imagePanel.getHeight();
			imagePanel = new JPanel()
			{
				{setSize(w,h);}//판넬의 paintComponent가 호출되기전 미리 판넬의 size를 정해둠.

				@Override
				public void paintComponent(Graphics g)
				{
					super.paintComponent(g);
					g.setColor(IMAGE_BACK_COLOR);
					g.drawRect(0, 0, w, h);
					g.drawImage(staticImage, 0, 0, w, h, null);
				}
			};
			repaint();
		}
	
	//============================================================================need reCreate
		/*
		public void setDBData()//actions
		{
			
		}
		
		데이터를 save load 할때  csv 형식을 줄 생각. 다만 아직 save load 가  아직 미구현이라서 남겨져있다.
		*/
	//============================================================================need reCreate
	
	//-----------------------------
	//inner class
	//-----------------------------
	
	private static class GifThread extends Thread//component를 지속적으로 repaint() 하여 GIF 그림이 움직이게한다. 
	{
		public boolean isRun = true;
		private Component target;
		
		private GifThread(Component target)
		{
			super();
			this.target = target;
		}
		@Override
		public void run()
		{
			while(isRun)
			{target.repaint();}
		}
	}
	
		
	private class FilesInfoStruct
	{
		/*
		 * 레이아웃이 가지는 여러 이미지들의 정보들을 담는 내부 이미지들의 DTO 입니다.
		 * 각종 작업에 의한 변수값의 조정을 담당합니다.
		 */
		public ArrayList<File> file = new ArrayList<>();
		public ArrayList<ImageIcon> image = new ArrayList<>();
		
		public ArrayList<Dimension> size = new ArrayList<>();
		public ArrayList<Point> location = new ArrayList<>();
		
		public ArrayList<Dimension> baseSize = new ArrayList<>();
		public ArrayList<Point> baseLocation = new ArrayList<>();
		
		private FilesInfoStruct(final File file, final ImageIcon image, final Dimension size)//하단 설명 참조
		{	
			this.file.add(file);
			this.image.add(image);
			this.size.add(size);
			this.location.add(new Point(0,0));
			this.baseSize.add((Dimension) size.clone());
			this.baseLocation.add(new Point(0,0));
		}
		/*
		 * Other Why
		 * (E)이 객체가 처음 생성 된다면 이미지는 한개다.
		 * >>이 객체는 레이아웃이 여러이미지들을 가질 수 있게 하기 위해 만들어진다.
		 * >>다만 처음에 생성된다면 이미지는 구조 상 무조건 한개가 된다.
		 * >>여기에서 location 값은
		 * >>현재 이미지가 
		 * >>이미지 집합이 들어있는 최소 크기의 액자 속에서 가지는
		 * >>이 이미지 하나의 위치값을 뜻한다.
		 * >>하므로 전체 이미지가 하나뿐인 지금은 location의 값이 무조건 0,0을 가진다.
		 */
		
		private int size()//지니고 있는 이미지의 갯수를 건네준다
		{return file.size();}
		
		public void bindFilesInfo(final ArrayList<File> files, final ArrayList<ImageIcon> images, final ArrayList<Dimension> sizes, final ArrayList<Point> locations, final Point getLocation, final Dimension getSize)
		{//하단 설명 참조
			
			files.addAll(this.file);//하단 설명 참조 (1)
			images.addAll(this.image);
			sizes.addAll(this.size);
			
			final ArrayList<Point> newLocations = new ArrayList<>();
			
			this.file = files;
			this.image = images;
			this.size = sizes;
			
			int pestLocationX = totalLocation.x;
			int pestLocationY = totalLocation.y;
			
			totalLocation.x = totalLocation.x<getLocation.x?totalLocation.x:getLocation.x;
			totalLocation.y = totalLocation.y<getLocation.y?totalLocation.y:getLocation.y;
			
			int widthB = getSize.width + getLocation.x - totalLocation.x;
			int heightB = getSize.height + getLocation.y - totalLocation.y;
			int widthA = totalSize.width + pestLocationX - totalLocation.x;
			int heightA = totalSize.height + pestLocationY - totalLocation.y;
			totalSize.width =  widthA>widthB? widthA:widthB;
			totalSize.height = heightA>heightB?heightA:heightB;
			
			
			int valueRulerX = getLocation.x - totalLocation.x;
			int valueRulerY = getLocation.y - totalLocation.y;
			for(Point location: locations)
			{newLocations.add(new Point(location.x + valueRulerX,location.y + valueRulerY));}
			
			
			valueRulerX = pestLocationX - totalLocation.x;
			valueRulerY = pestLocationY - totalLocation.y;
			for(Point location: this.location)
			{newLocations.add(new Point(location.x + valueRulerX,location.y + valueRulerY));}
			
			this.location = newLocations;
			
			
			baseLocation = new ArrayList<>();
			baseSize = new ArrayList<>();
			
			Point point;
			Dimension dimension;
			
			for(int i = 0; i<size();i++)
			{
				point = location.get(i);
				dimension = size.get(i);
				baseLocation.add(new Point(point.x,point.y));
				baseSize.add(new Dimension(dimension.width,dimension.height));
			}
			
			getStaticImage();	
		}
		/*
		 * 기능:레이아웃이 합쳐지면서 얻어오는 이미지들과 현재 이미지들을 조합하여 변수값들을 재구성한다.
		 * 1.매개변수로 전달받아온 레이아웃 이미지들의 정보를 현재 정보와 합친다.
		 * 2.이미지값들중 최소 x값과 최소 y값을 전체이미지 액자의 x,y값으로 지정한다.
		 * 3.수정된 값을 토대로 최대 w+x값과 최대 h+y 값을 토대로 액자의 크기 값을 계산하여 지정한다.
		 * 4.각 이미지들의 위치값을 조정한다.
		 * 5.새롭게 완성된 값을 시작하는 새로운 원본값으로 지정한다.
		 * 상세:
		 * >>이 메소드는 각 이미지들과 액자의 location 과 size를 다시 구한다.
		 * >>하나의 액자에 이미지들이 들어가있고
		 * >>그 이미지들을 담을려면 액자의 크기가 어떻게 되어야 하는지 비율비로 조정 한다고 생각 하는 거다.
		 * >>이미지들 또한 이전에 ImageManager나 UserSizeChanger를 통해서 크기와 위치가 변현된 값들이 합쳐지므로
		 * >>이때 변형된 이미지를 합치기 전에 원본 이미지로 취급할 필요가있다.
		 * >>이 메소드는 그 작업을 실행한다.
		 */
		
		@SuppressWarnings("serial")
		public Image getStaticImage()//GIF라고 해도 고정된 이미지를 건네줌
		{
			float rate;
			try
			{rate = scr.getRenderRate();}
			catch(Exception e)
			{rate = 1;}
			final float renderRate = rate;//JPanel 에서 사용해야 하므로 final 로 다시 치환해줌.
			final Point originalSize = new Point((int)(totalSize.width/renderRate), (int)(totalSize.height/renderRate));
			final BufferedImage result = new BufferedImage(originalSize.x,originalSize.y, BufferedImage.TYPE_INT_ARGB);//빈 이미지를 준비
			
			JPanel panel;
			
			panel = new JPanel()//투명 판넬에 Layout의 이미지를 그린뒤
			{
				{
					setBackground(new Color(0,0,0,0));
					setSize(originalSize.x,originalSize.y);
				}
				
				public void paintComponent(Graphics g)
				{
					super.paintComponent(g);
					((Graphics2D)g).setRenderingHint(RenderingHints.KEY_ANTIALIASING,
			                RenderingHints.VALUE_ANTIALIAS_ON);
					
					for(int i=0;i<file.size();i++)
					{
						try
						{g.drawImage(ImageIO.read(file.get(i)), (int)(location.get(i).x/renderRate), (int)(location.get(i).y/renderRate), (int)(size.get(i).width/renderRate), (int)(size.get(i).height/renderRate), null);}
						catch(Exception e)
						{System.err.println("LayoutInfo.FilesInfoStruct.getStaticImage() (-99): Unknown image error");}
						
					}
					
				}
			};
			
			panel.print(result.createGraphics());//그 이미지를 빈 이미지에 복사해서 내보냄.
				
			return result;
		}
		
		public void updateData()//현재 액자의 크기에 비례해서 각 이미지의 x,y,w,h값을 재구성함.
		{
			Point baseLocation;
			Point location;
			Dimension baseSize;
			Dimension size;
			
			final float scaleW = widthScale/100;
			final float scaleH = heightScale/100;
			final float scaleA = allScale/100;
			
			for(int i = 0; i<file.size(); i++)
			{
				baseLocation = this.baseLocation.get(i);
				baseSize = this.baseSize.get(i);
				location = this.location.get(i);
				size = this.size.get(i);
				
				location.x = (int) (baseLocation.x * scaleW * scaleA);
				location.y = (int) (baseLocation.y * scaleH * scaleA);
				size.width = (int) (baseSize.width * scaleW * scaleA);
				size.height = (int) (baseSize.height * scaleH * scaleA);
			}
		}
	}
	
	public static class WorkingRunnable implements Runnable
	{
		protected byte holder;
		protected LayoutInfo info;
		protected float[] synValue = new float[2];
		
		public void run() {}
		public void setLayoutInfo(LayoutInfo info){this.info = info;}
		public float[] getSynValue() {return synValue;}
	}
	
	private static class Tools
	{
		@SuppressWarnings("deprecation")
		private Tools()
		{
			LayoutList.Logic e = new LayoutList.Logic()
			{
				public LayoutInfo newInfo() {return new LayoutInfo();}	
			};
			LayoutList.setLogic(e);
		}
	}
	/*
	 * 이 클래스느 실험적인 요소가 많습니다.
	 * LayoutInfo를 private로 지정했기떄문에 외부에서 LayoutInfo를 만드는것은 불가능 합니다.
	 * 하므로 LayoutInfo를 생성하는 부분을 내부에서 처리한뒤 LayoutList로 보냅니다.
	 * 그렇기 때문에 LayoutList 에서는 LayoutInfo를 만들수 있으며 오직 여기서만 생성 가능합니다.
	 */
	
	private class returnRunnable
	{
		public Float run() {return null;}
		public Boolean runs() {return null;}
	}
		
}

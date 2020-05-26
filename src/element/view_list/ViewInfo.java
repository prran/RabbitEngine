package element.view_list;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.RenderingHints;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;

import element.layout_list.LayoutList;
import element.layout_list.LayoutList.LayoutListData;
import tools.RenameLabel;
import tools.StaticTools;
import window.ViewList;

public class ViewInfo extends JPanel
{
	/*
	 * 이 클래스는 LayoutList의 작업 환경들을 저장하고 
	 * 현재 사용 중인 Screen 혹은 LayoutList의 내용에
	 * 가지고 있는 정보를 import, export 하는 한개 단위의 작업 기록 객체 입니다.
	 * 객체를 ViewStructure에서만 만들 수 있으며 외부로 노출되지 않으므로 별도의 설명은 없습니다.
	 */
	
	private static final long serialVersionUID = 1L;
	
	private final static int WIDTH = 240;
	private final static Color NORMAL_BORDER_COLOR = Color.getColor("grayless",0XFFCCCCCC)
			,FOCUS_BORDER_COLOR = Color.WHITE;
	
	private final static Color NORMAL_BACKGOUND_COLOR = Color.LIGHT_GRAY
			,FOCUS_BACKGROUND_COLOR = Color.getColor("graywhite", 0XDDD8D8D8);
	
	private final static String DEFAULT_VIEW_NAME = "New View";
	
	//---------------layout instance
	
	private JPanel view;
	private final JLabel name = new JLabel("Name : ");
	private RenameLabel nameValue;
	private final JLabel link = new JLabel("Linked : ");
	
	//---------------store instance
	
	private LayoutListData data;
	private Image viewImage;
	
	//---------------store for logic
	
	@SuppressWarnings("unused")
	private final static Tools tools = new Tools();
	
	//-----------------------------
	//creator
	//-----------------------------
	
	private ViewInfo(final Image scrImg)//레이아웃에 현재 작업의 screen화면을 보여주는 판넬,뷰 이름판넬,관련정보 판넬을 추가함.
	{
		setFrame();
		
		setImageViewPanel(scrImg);
		
		//LayoutList 데이터 받아오는거 있었음.
		
		final JPanel namePanel = getTextLeftPanel(name);
		nameValue = new RenameLabel(DEFAULT_VIEW_NAME,namePanel);//클릭시 나타나는 textField로 라벨 내용을 변경 가능한 라벨
		nameValue.setLimit(15);
		namePanel.add(nameValue);
		
		final GridBagConstraints namelayoutInfo = StaticTools.setFullGrid(0, 1, 1, 1, 0f, 0f);
		namelayoutInfo.anchor = GridBagConstraints.WEST;
		add(namePanel,namelayoutInfo);
		
		final JPanel linkPanel = getTextLeftPanel(link);
		GridBagConstraints linklayoutInfo = StaticTools.setFullGrid(0, 2, 1, 1, 0f, 0f);
		linklayoutInfo.anchor = GridBagConstraints.WEST;
		add(linkPanel,linklayoutInfo);
		
	}
	//=======>linked procedure
		private void setFrame()//설명 생략
		{
			setPreferredSize(new Dimension(WIDTH,ViewList.HEIGHT));
			setMaximumSize(new Dimension(WIDTH,ViewList.HEIGHT));
			setBackground(Color.WHITE);
			setBorder(new LineBorder(NORMAL_BORDER_COLOR,1));
			setLayout(new GridBagLayout());
		}
	//=======>linked procedure2
		@SuppressWarnings("serial")
		private void setImageViewPanel(final Image img)
		{
			viewImage = img;
			
			view =
			new JPanel() 
			{
				{
					setBackground(NORMAL_BACKGOUND_COLOR);
				}
				
				@Override
				public void paintComponent(Graphics g)
				{
					super.paintComponent(g);
					
					try
					{
					final float rate =  (float)viewImage.getWidth(null)/viewImage.getHeight(null);//받은 이미지를 이 이미지뷰와 대조하여
					final Dimension canvasSize = new Dimension(getWidth(),getHeight());
					final int w = (int) (viewImage.getWidth(null)>viewImage.getHeight(null)?canvasSize.width:canvasSize.width * rate);
					final int h = (int) (viewImage.getHeight(null)>viewImage.getWidth(null)?canvasSize.height:canvasSize.height / rate);
					
					((Graphics2D)g).setRenderingHint(RenderingHints.KEY_ANTIALIASING,
			                RenderingHints.VALUE_ANTIALIAS_ON);//안티엘리어싱을 적용하여
					
					g.drawImage(viewImage, (int)(canvasSize.width/2f - w/2f), (int)(canvasSize.height/2f - h/2f), w, h,null);//이 이미지 뷰의 크기에 맞게 출력한다.
					}
					catch(Exception e){}
				}
			};
			
			add(view,StaticTools.setGridFill(StaticTools.setFullGrid(0, 0, 1, 1, 1f, 1f)));
		}
	//=======>linked procedure3
		private JPanel getTextLeftPanel(JLabel text)//수정이 고려됨.
		{
			final JPanel textPanel = new JPanel();
			final FlowLayout namePanellayout = new FlowLayout();
			namePanellayout.setAlignment(FlowLayout.LEFT);
			textPanel.setLayout(namePanellayout);
			textPanel.setBackground(Color.WHITE);
			textPanel.add(text);
			return textPanel;
		}
		
	//-----------------------------
	//setter
	//-----------------------------
	
	public void setViewImage(Image image)//Screen.setPrefferedSize > ViewStructure.listener.screenSizeChanged() > this.setViewImage() 순으로 호출됨.
	{viewImage = image;}
	
	//-----------------------------
	//actions
	//-----------------------------
	
	public void inFocus(LayoutList list)//view 에 저장 되어있는 요소들을 LayoutList에 넘겨준다.
	{
		setBorder(new LineBorder(FOCUS_BORDER_COLOR,1));
		view.setBackground(FOCUS_BACKGROUND_COLOR);
		try
		{
			list.importData(data);
			viewImage = data.getFetcher().tools.getScreen().getScreenImage();
		}
		catch(Exception e) {}	
	}
	
	public void outFocus(LayoutList list)//view를 교체하기전에 현재 작업 환겸을 이 뷰에 저장해둠
	{
		setBorder(new LineBorder(NORMAL_BORDER_COLOR,2));
		view.setBackground(NORMAL_BACKGOUND_COLOR);
		data = list.expertData();
	}
	
	public void repaintImage()
	{view.repaint();}
	
	//-----------------------------
	//inner Class
	//-----------------------------
	
	private static class Tools
	{
		@SuppressWarnings("deprecation")
		private Tools()
		{
			ViewStructure.Logic e = new ViewStructure.Logic()
			{
				public ViewInfo newInfo(Image img) {return new ViewInfo(img);}	
			};
			ViewStructure.setLogic(e);
		}
	}
	/*
	 * 이 클래스느 실험적인 요소가 많습니다.
	 * LayoutInfo를 private로 지정했기떄문에 외부에서 LayoutInfo를 만드는것은 불가능 합니다.
	 * 하므로 LayoutInfo를 생성하는 부분을 내부에서 처리한뒤 LayoutList로 보냅니다.
	 * 그렇기 때문에 LayoutList 에서는 LayoutInfo를 만들수 있으며 오직 여기서만 생성 가능합니다.
	 */
	//=======>linked method
	public static void StaReadyer(){}//하단 설명 참조
	/*
	 * 이 메소드는 Static 으로 정의된 클래스, 함수, 변수
	 * 의 생성 순서를 제어하는 메소드다.
	 * 기존에 존재하진 않는 로직이라서 작성자가 이름을 임의로 지엇다. 스타레디어. Static Ready Controller
	 */
	
}

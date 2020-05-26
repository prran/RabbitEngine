package element.lmage_manager;

import java.awt.ComponentOrientation;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.AbstractButton;
import javax.swing.ImageIcon;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;
import javax.swing.JToggleButton;

import element.layout_list.LayoutInfo;
import logic.NullExceptionEscape;

@SuppressWarnings("serial")
public class PopupMenu extends JPopupMenu
{
	/*
	 * LayoutList 에서 focus 모드인 Layout 의 boolean 값들을 수정합니다.
	 * 이미지의 시작 기준지점과 화면 회전시에 이미지 크기 재구성의 기준 지점을 정합니다.
	 */
	
	private static final String SIZE_CONTENTS = "Size by Contents";
	private static final String SIZE_SCREEN = "Size by Screen";
	private static final String LOCATION_CONTENTS = "Location by Contents";
	private static final String LOCATION_SCREEN = "Location by Screen";
	private static final String REVERSE_STARTX = "Reverse X start point";
	private static final String REVERSE_STARTY = "Reverse Y start point";
	
	//---------------layout instance
	
	final private JMenuItem sizeContents = new JMenuItem(SIZE_CONTENTS);
	final private JMenuItem sizeScreen = new JMenuItem(SIZE_SCREEN);
	final private JMenuItem locContents = new JMenuItem(LOCATION_CONTENTS);
	final private JMenuItem locScreen = new JMenuItem(LOCATION_SCREEN);
	final private JMenuItem reverseX = new JMenuItem(REVERSE_STARTX);
	final private JMenuItem reverseY = new JMenuItem(REVERSE_STARTY);
	
	//---------------store instance
	
	private NullExceptionEscape focusVar;
	private LayoutInfo focusWork;
	
	//-----------------------------
	//creator
	//-----------------------------
	
	/**
	 * it can set toggle Option in Layout by a pop up menu.
	 */
	public PopupMenu()//레이아웃을 설정
	{
		focusVar = new NullExceptionEscape();
		
		sizeScreen.setSelected(true);
		setCheckMenu(sizeContents,sizeScreen);
		setCheckMenu(sizeScreen,sizeContents);
		sizeContents.setSelected(true);
		add(new JSeparator());
		locScreen.setSelected(true);
		setCheckMenu(locContents,locScreen);
		setCheckMenu(locScreen,locContents);
		locContents.setSelected(true);
		add(new JSeparator());
		setCheckMenu(reverseX);
		setCheckMenu(reverseY);
	}
	//========>linked procedure
		private void setCheckMenu(final JMenuItem ...item)//클릭시에 지정된 Layout 안의 boolean 값을 반전하는 기능을 추가한다.
		{
			ItemListener listener = null;
			
			switch(item[0].getText())
			{
			case SIZE_CONTENTS:
			case SIZE_SCREEN:
				listener = getSynToggleListener(item[0], item[1], focusVar.sizeSw);
				break;
			case LOCATION_CONTENTS:
			case LOCATION_SCREEN:
				listener = getSynToggleListener(item[0], item[1], focusVar.locationSw);
				break;
			
			case REVERSE_STARTX:
				listener = getToggleListener(item[0],focusVar.reverseXSw);
				break;
			case REVERSE_STARTY:
				listener = getToggleListener(item[0],focusVar.reverseYSw);
				break;
			}

			item[0].setModel(new JToggleButton.ToggleButtonModel());
			item[0].setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
			item[0].addItemListener(listener);
			
			add(item[0]);
		}
		//========>linked procedure
			private ItemListener getSynToggleListener(final JMenuItem item1, final JMenuItem item2, Object focusVlaue)// 하단 설명 참조
			{
				final ImageIcon selected = new ImageIcon("images/check.png");
				final ImageIcon deselected = null;
				final boolean flag = item1.isSelected();
				
				final MenuItem result=
						
				new MenuItem()
				{
					@Override
				    public void itemStateChanged(ItemEvent e)
				    {
				        	
				        AbstractButton ab = (AbstractButton) e.getSource();
				        ab.setIcon(selected);
				        
				        Object vlaue = true;
				        
				        if(holder==1)
				        	vlaue = focusVar.sizeSw;
				        else if(holder == 2)
				        	vlaue = focusVar.locationSw;
				          
				        switch(e.getStateChange())
				        {
				            case ItemEvent.SELECTED://item1 이 true면
				                ab.setIcon(selected);//체크표시를 표시하고
				                item2.setSelected(false);//item2를 변동후 > listener가 발동되어 이쪽은 check가 풀림
				                try{focusWork.setSwitch(vlaue,flag);}//내부 값을 업데이트
				                catch(Exception ex) {}
				                break;
				            case ItemEvent.DESELECTED:
				                ab.setIcon(deselected);
				                item2.setSelected(true);
				                break;
				        }    
				    }
				};
				
				if(focusVlaue == (Object)focusVar.sizeSw)
					result.holder = 1;
				else if(focusVlaue == (Object)focusVar.locationSw)
					result.holder = 2;
				
				return result;
			}
			/*
			 * 기능: focus안의 boolean 값을 수정하며 item1이 true면 item2가 false가 되도록, 혹은 반대로도 설정.
			 */
		//========>linked procedure2
			private ItemListener getToggleListener(final JMenuItem item, Object focusVlaue)//item의 SELECTED,DESELECTED에 따라 focus안의 boolean 값을 수정함.
			{
				final ImageIcon selected = new ImageIcon("images/check.png");
				final ImageIcon deselected = null;
				
				final MenuItem result=
						
				new MenuItem()
				{
				      @Override
				      public void itemStateChanged(ItemEvent e)
				      {
				        	
				         AbstractButton ab = (AbstractButton) e.getSource();
				         ab.setIcon(selected);
				         
				         Object vlaue = false;
				         
				         if(holder==1)
				        	 vlaue = focusVar.reverseXSw;
					     else if(holder == 2)
					     {
					    	 vlaue = focusVar.reverseYSw;
					     }
					         
				         
				         switch(e.getStateChange())
				         {
				           	case ItemEvent.SELECTED:
				           		ab.setIcon(selected);
				           		try{focusWork.setSwitch(vlaue, true);}
				              	catch(Exception ex) {}
				              	break;
				            case ItemEvent.DESELECTED:
				              	ab.setIcon(deselected);
				              	try{focusWork.setSwitch(vlaue, false);}
				              	catch(Exception ex) {}
				              	break;
				         }
				         focusWork.updateDataByReverse();//이미지의 시작 기준점이 변하는 작용이므로 관련값들을 정정한다.
				      }
				};
				

				if(focusVlaue == (Object)focusVar.reverseXSw)
					result.holder = 1;
				else if(focusVlaue == (Object)focusVar.reverseYSw)
					result.holder = 2;
				
				return result;
			}
			
	//-----------------------------
	//actions
	//-----------------------------
	
	/**
	 * When focus is changed in LayoutList, focusing new Layout variable
	 */
	public void inFocus(LayoutInfo info)
	{
		focusVar = info;
		focusWork = info;
		sizeScreen.setSelected(focusVar.sizeBy);
		locScreen.setSelected(focusVar.locationBy);
		reverseX.setSelected(focusVar.isReverseX);
		reverseY.setSelected(focusVar.isReverseY);
	}
	
	public class MenuItem implements ItemListener
	{
		public Byte holder;
		public void itemStateChanged(ItemEvent e) {}
	}

}

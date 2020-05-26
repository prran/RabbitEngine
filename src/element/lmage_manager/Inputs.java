package element.lmage_manager;

import java.awt.BorderLayout;

import javax.swing.JLabel;

import element.graphic_screen.UISizeChanger;
import element.layout_list.LayoutInfo;
import element.layout_list.LayoutInfo.WorkingRunnable;
import logic.FileConnection;
import logic.NullExceptionEscape;
import tools.InputField;

@SuppressWarnings("serial")
public class Inputs extends InputField{
	
	/*
	 * 이 클래스는 LayoutManager 내부에 들어가는 입력 텍스트창들입니다.
	 * LayoutList에서 focus 처리되어있는 Info의 값들을 택스트 입력을 통해서 조정하며
	 * 그 값과 연동된 값을을 같이 업데이트 해 줍니다.
	 */
	
	public final static TextWatcher NULL_LISTENER = new TextWatcher() {public void update() {}};
	
	public final static String X_BY_SCREEN = "X";
	public final static String Y_BY_SCREEN = "Y";
	public final static String W_BY_SCREEN = "rateW";
	public final static String H_BY_SCREEN = "rateH";
	public final static String ALL_SCALE = "Scale";
	public final static String X_BY_CONTENT = "rateX";
	public final static String Y_BY_CONTENT = "rateY";
	public final static String W_BY_CONTENT = "W";
	public final static String H_BY_CONTENT = "H";
	
	//---------------store instance
	
	private NullExceptionEscape focusVar = new NullExceptionEscape();
	private LayoutInfo focusWork;
	private WorkingRunnable work;
	private TextWatcher updateListener;
	private Inputs[] syn;
	public Tools tools = new Tools();
	
	//-----------------------------
	//creator
	//-----------------------------
	
	/**
	 * this class is Editor of Layout where focused in LayoutList.
	 * it can change rates of image in Layout by keyboard.
	 * 
	 * you can use parameter '?_BY_SCREEN or ?_BY_CONTENT or ALL_SCALE'
	 * 
	 * @param kindOf A separator of information what do Edit it 
	 */
	public Inputs(String name)//인자로 받은 지정 값을 이 Input 에 지정하여 input의 텍스트가 변하면 지정 값이 변하는 리스너를 설정, 텍스트 끝에 %를 붙힘
	{
		super(name, X,30);
		
		switch(name)
		{
		case X_BY_SCREEN:
			updateListener = getWithoutSynWorkListener(LayoutInfo.getUpdateWorkByLocation(focusVar.rateX, this, focusVar));
			break;
		case Y_BY_SCREEN:
			updateListener = getWithoutSynWorkListener(LayoutInfo.getUpdateWorkByLocation(focusVar.rateY, this, focusVar));
			break;
		case X_BY_CONTENT:
			updateListener = getWithoutSynWorkListener(LayoutInfo.getUpdateWorkByLocation(focusVar.XScale, this, focusVar));
			break;
		case Y_BY_CONTENT:
			updateListener = getWithoutSynWorkListener(LayoutInfo.getUpdateWorkByLocation(focusVar.YScale, this, focusVar));
			break;
		case W_BY_SCREEN:
			updateListener = getWithoutSynWorkListener(LayoutInfo.getUpdateWorkBySize(focusVar.rateW, this, focusVar));
			break;
		case H_BY_SCREEN:
			updateListener = getWithoutSynWorkListener(LayoutInfo.getUpdateWorkBySize(focusVar.rateH, this, focusVar));
			break;
		case W_BY_CONTENT:
			updateListener = getWithoutSynWorkListener(LayoutInfo.getUpdateWorkBySize(focusVar.widthScale, this, focusVar));
			break;
		case H_BY_CONTENT:
			updateListener = getWithoutSynWorkListener(LayoutInfo.getUpdateWorkBySize(focusVar.heightScale, this, focusVar),focusVar.rateH,focusVar.YScale);
			break;
		case ALL_SCALE:
			updateListener = getWithoutSynWorkListener(LayoutInfo.getUpdateWorkBySize(focusVar.allScale, this, focusVar),focusVar.XScale,focusVar.YScale);
			break;
		}
		
		setListener(updateListener);
			
		text.setLayout(new BorderLayout());
		text.add(new JLabel("%"),BorderLayout.EAST);
	}
	//======>linked procedure
		private TextWatcher getWithoutSynWorkListener(final WorkingRunnable r,final Float ...synedLocaRate)//리스너 동작시에 연동되어있는 inputs들의 리스너를 잠시 제거한뒤 작업후 복구한다.
		{
			work = r;
			
			return
					
			new TextWatcher()
			{

				@Override
				public void update() 
				{
					try
					{
						if(((UISizeChanger)FileConnection.getObject("UI size changer")).isRun()|| focusVar == null)//하단 설명 참조 (1)
							return;
					}
					catch(Exception e) {}
					
					TextWatcher listener[] = new TextWatcher[syn.length];
					
					int i = 0;
					for(Inputs input: syn)
					{
						listener[i++] = input.tools.getListener();
						input.setListener(NULL_LISTENER);
					}
					r.run();
					
					float[] synValue = work.getSynValue();
					
					i = 0;
					for(Inputs input: syn)
					{
						input.setValue(synValue[i]);
						input.setListener(listener[i++]);
					}
					try{focusWork.updateDataByIM();}
					catch(Exception e) {}
					}
			};
		}
		/*
		 * Other Why
		 * (1) 출동방지
		 * inputs의 텍스트는 Screen에서 마우스로 이미지를 조정하는 UISIzeChanger와 연동되어 있다.
		 * UISizeChanger의 사용으로 인한 값의 변동으로 Input의 내용이 연동되어 수정되면서 이 리스너가 발생하게되고
		 * UISizeChanger에서 업데이트한 내용을 또 업데이트한다. 
		 * 이를 방지하기 위해서 UISizeChanger객체가 사용 중이라면 작업을 수행하지 않고 종료한다.
		 */
	
	//-----------------------------
	//actions
	//-----------------------------
		
	/**
	 * When focus is changed in LayoutList, focusing new Layout variable
	 */
	public void inFocus(LayoutInfo info, final float value)//설명 생략
	{
		focusVar = info;
		focusWork = info;
		work.setLayoutInfo(info);
		setListener(NULL_LISTENER);
		setValue(value);
		setListener(updateListener);
	}
	/**
	 * set Inputs object for refresh text together
	 */
	public void setSynInputs(Inputs ...syn)//값이 변동 되었을때 텍스트 출력을 새로고침할 inputs를 설정할 수 있다.
	{
		this.syn = syn;
	}
	
	public void updateTextNoAction(final float value)//설명 생략.
	{
		setListener(NULL_LISTENER);
		setValue(value);
		setListener(updateListener);
	}
	
	private class Tools
	{
		public TextWatcher getListener()//설명 생략.
		{return updateListener;}
	}
}

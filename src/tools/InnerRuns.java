package tools;

import java.util.ArrayList;

import javax.swing.JFrame;

import element.lmage_manager.ImageManager;

public final class InnerRuns {
	
	/*
	 * 이 클래스는 JFrame 객체들을 지니고 관련된 동작들을 동시에 수행하기 위해서 존재합니다.
	 * 구조가 워낙 단순하여 부가적인 설명 없이 넘어갑니다.
	 */
	
	public final static byte IMAGE_MANAGER = 0;
	public final static byte CORE = 1;
	//public final static byte VISUAL_DEVICE = 2; 진행중..
	
	private final ArrayList<JFrame> innerRuns = new ArrayList<>();
	
	
	public InnerRuns()
	{innerRuns.add(new ImageManager("default").get());}
	
	public InnerRuns(final JFrame core)
	{
		innerRuns.add(new ImageManager("default").get());
		innerRuns.add(core);
	}
	
	public void startAll()
	{
		for(JFrame frame: innerRuns)
		{
			frame.setVisible(true);
		}
	}
	
	public void changeWindowStateAll(int state)
	{
		for(JFrame frame: innerRuns)
		{
			frame.setState(state);
		}
	}
	
	
	public JFrame get(final byte info)
	{
		switch(info)
		{
		case IMAGE_MANAGER:
			return innerRuns.get(IMAGE_MANAGER);
		case CORE:
			return innerRuns.get(CORE);
		default :
			return null;
		}
	}

}

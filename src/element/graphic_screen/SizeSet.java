package element.graphic_screen;


import java.awt.Dimension;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

import tools.Title;

public class SizeSet implements Screen.ScreenComponent{
	/*
	 * 이 클래스는 그래픽 스크린의 레이아웃 구성요소중 하나입니다.
	 * Screen의 크기를 수정하는 택스트 필드 입니다.
	 */
	
	private final static int TITLE_WIDTH = 25, TITLE_HEIGHT = 25;
	
	public final static int X = 0 , Y = 1;
	
	//---------------layout instance
	
	private final JPanel result = new JPanel();
	private final JTextField text = new JTextField(3);
	
	//---------------store instance
	
	private final DocumentListener listener = new TextFieldListener();
	private final Screen scr;
	private final int direction;
	private final Dimension changedSize = new Dimension();
	
	//-----------------------------
	//creator
	//-----------------------------	
	/**
	 * 
	 * @param direction SizeSet.X or SizeSet.Y
	 * @param scr Screen of want to changing size Width or Height.
	 */
	public SizeSet(final int direction, Screen scr)//초기화,초기 입력값 설정,텍스트기능추가,레이아웃구성
	{
		this.scr = scr;
		this.direction = direction;
		
		result.setLayout(new BoxLayout(result,X));
		
		final Title title = new Title(TITLE_WIDTH,TITLE_HEIGHT);
		
		final JTextFieldLimit document = new JTextFieldLimit(4);
		document.addDocumentListener(listener);
		text.setDocument(document);
		
		if(direction == X)
		{
			title.setText("X");
			text.setText(Screen.DEFAULT_WIDTH+"");
		}
		else
		{
			title.setText("Y");
			text.setText(Screen.DEFAULT_HEIGHT+"");
		}
		
		result.add(title.get());
		result.add(text);
		
		scr.addSynComponent(this);//SizeSet이 Screen그룹 행동에 속하게함.
	}
	
	//-----------------------------
	//getter
	//-----------------------------	
	
	/**
	 * @return a JPanel of get TextField have resize screen work 
	 */
	public JPanel get()//설명생략
	{return result;}
	
	public int getDirection()
	{return direction;}
	
	public int getValue()//설명생략
	{
		try
		{return Integer.parseInt(text.getText());}//텍스트필드에 숫자만 쓸수 있도록 Document에서 막아놨다
		catch(Exception e)
		{return 0;}
	}
	
	//-----------------------------
	//setter
	//-----------------------------	
	
	public void setValue(int value)//설명 생략
	{text.setText(""+value);}
	
	/**
	 * It use to change Text Only
	 */
	public void setValueNoAction(Integer value)//설명 생략
	{
		text.getDocument().removeDocumentListener(listener);
		text.setText(value.toString());
		text.getDocument().addDocumentListener(listener);
	}
	
	//-----------------------------
	//inner Class
	//-----------------------------	
	
	@SuppressWarnings("serial")
	private class JTextFieldLimit extends PlainDocument //TextField 내에서 특정 글자수에 제한을 두는 클래스
	{
		  private int limit;
		  
		  JTextFieldLimit(int limit) //설명 생략
		  {
			  super();
			  this.limit = limit;
		  }
		  
		  /**
		   * this method has Override for text limit
		   */
		  public void insertString(int offset, String str, AttributeSet attr) throws BadLocationException//limit만큼의 숫자만을 쓸수 있게한다.
		  {
			  if (str == null)
				  return;
			  try 
			  {Integer.parseInt(str);} 
			  catch (NumberFormatException e) 
			  {
				  super.insertString(offset, "", attr); 
				  return;
			  }

			  if ((getLength() + str.length()) <= limit) 
				  super.insertString(offset, str, attr);
		    
		  }
	}
	
	private class TextFieldListener implements DocumentListener//글씨에 변환이 있으면 화면의 크기를 바꿈.
	{
		@Override
		public void insertUpdate(DocumentEvent e) {updateScrSize();}//설명 생략
		@Override
		public void removeUpdate(DocumentEvent e) {updateScrSize();}
		@Override
		public void changedUpdate(DocumentEvent e) {updateScrSize();}
		
		private void updateScrSize()//direction 에 따라서 너비 혹은 높이를 조정한다.
		{
			final int input = getValue();
			float rate;
			
			if(direction == X)
			{
				rate = input/scr.getScrSize().height; // 수정된 화면의 가로/세로 값.
			}
			else
			{
				rate = scr.getScrSize().width/input;
			}
			
			int w;
			int h;
			
			if(scr.getRotation().get())//가로화면 모드일 경우
			{
				w = (int)Screen.MAX_WIDTH;
				h = rate > 1 ? (int)(Screen.MAX_WIDTH / rate) : (int)Screen.MAX_HEIGHT;//하단 설명 참조(1)
			}
			else//세로화면 모드일 경우
			{
				w = rate < 1 ? (int)(Screen.MAX_HEIGHT * rate) : (int)Screen.MAX_WIDTH;
				h = (int)Screen.MAX_HEIGHT;
			}
			
			changedSize.setSize(w, h);
			
			scr.get().setPreferredSize(changedSize);
			scr.get().revalidate();
		}
		/*
		 * Other Why
		 * (1)화면의 크기조정을 제한하는 이유는?
		 * 휴대폰이 가로모드인데 세로화면이 더 크면 이상하다. 
		 * 이 프로그램을 사용 하면서 사용자가 지금 화면이 가로화면인지 세로화면인지 구분을 하기 힘들어 진다.
		 */
	}
	
	//-----------------------------
	//override
	//-----------------------------	

	@Override
	public void destoryer() {}

}

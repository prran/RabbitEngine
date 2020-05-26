package tools;


import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

import tools.Title;

@SuppressWarnings("serial")
public class InputField extends JPanel{
	
	public final static int X = 0;
	public final static int Y = 1;
	
	protected final JTextField text = new JTextField();
	
	private final static int TEXTLIMITE = 10;
	
	private TextWatcher tw;
	
	//-----------------------------
	//creator
	//-----------------------------
	
	public InputField(final String name,final int direction,final int size,final TextWatcher textWatcher)
	{
		this(name, direction, size);
		tw = textWatcher;
	}
	
	public InputField(final String name,final int direction,final int size)
	{
		setLayout(new BoxLayout(this,direction));
		Title title = new Title(size,size);
		text.setDocument(new JTextFieldLimit(TEXTLIMITE));
		text.getDocument().addDocumentListener(new TextFieldListener());

		title.setText(name);
		text.setText("");

		add(title.get());
		add(text);
	}
	
	//-----------------------------
	//setter
	//-----------------------------
	
	public void setListener(final TextWatcher textWatcher)
	{tw = textWatcher;}
	
	public void setValue(int value)
	{text.setText(""+value);}
	
	public void setValue(float value)
	{text.setText(""+value);}
	
	//-----------------------------
	//getter
	//-----------------------------
	
	public int getValue()
	{
		int value = 0;
		
		try {value = Integer.parseInt(text.getText());}
		catch(Exception e){}
		
		return value;
	}
	
	public float getValueF()//float로 텍스트 필드 값을 가져 옵니다.
	{
		float value = 0;
		
		try {value = Float.parseFloat(text.getText());}
		catch(Exception e){}
		
		return value;
	}
	
	class JTextFieldLimit extends PlainDocument {
		  private int limit;
		  JTextFieldLimit(int limit) {
		    super();
		    this.limit = limit;
		  }

		  public void insertString(int offset, String str, AttributeSet attr) throws BadLocationException {
			  
			try 
			{Integer.parseInt(str);} 
			catch (NumberFormatException e) 
			{
				try {Float.parseFloat(str + 0);}
				catch (NumberFormatException e2)
				{
					super.insertString(offset, "", attr); return;//숫자와 . 만 사용할수 있게 조정합니다.
				}
			}
			 
		    if (str == null)
		      return;

		    if ((getLength() + str.length()) <= limit)//지정한 글자의 갯수를 넘지 못하게 합니다.
		    {
		      super.insertString(offset, str, attr);
		    }
		    
		  }
		}
	
	private class TextFieldListener implements DocumentListener
	{
		public void insertUpdate(DocumentEvent e) {tw.update();}

		public void removeUpdate(DocumentEvent e) {tw.update();}

		public void changedUpdate(DocumentEvent e) {tw.update();}	
	}
	
	public interface TextWatcher//텍스트 내용이 변경되면 호출됩니다.
	{
		public void update();
	}

}

package tools;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;


@SuppressWarnings("serial")
public class RenameLabel extends JLabel{
	/*
	 * 라벨을 클릭하면 텍스트 필드가 나오며
	 * 텍스트 필드에 입력 값이 들어오면 라벨의 텍스트가 변환됩니다.
	 */
	
	private final static int DEFAULT_INPUT_HEIGHT = 20;
	private final static int DEFAULT_INPUT_WIDTH = 175;
	
	private final JPanel parent;
	private final JLabel label = this;
	private final JTextField inputs = new JTextField();
	private LabelChangedListener getter;
	
	//-----------------------------
	//creator
	//-----------------------------
	
	public RenameLabel(JPanel parents)
	{this("",parents);}
	
	public RenameLabel(JPanel parents,LabelChangedListener e)
	{
		this("",parents);
		this.getter = e;
	}
	
	public RenameLabel(String text,JPanel parents,LabelChangedListener e)
	{
		this(text,parents);
		this.getter = e;
	}
	
	public RenameLabel(String text,JPanel parents)
	{
		super(text);
		parent = parents;
		
		inputs.setPreferredSize(new Dimension(DEFAULT_INPUT_WIDTH,DEFAULT_INPUT_HEIGHT));
		
		addMouseListener(
				new MouseAdapter()
				{
					public void mouseClicked(MouseEvent me) 
					{
						parent.remove(label);
						inputs.addActionListener(
							new ActionListener()
							{
								@Override
								public void actionPerformed(ActionEvent e)//입력후에 엔터를 눌렀을때 작동
								{
									if(!inputs.getText().equals(""))
										label.setText(inputs.getText());//라벨에 입력한 값을 적용
									
									changeComp(label,inputs);//그후에 텍스트 필드를 숨기고 라벨을 보인뒤
									parent.repaint();
									
									if(getter != null)
										getter.whenTextUpdated(label.getText());//리스너 실행
								}
							});
						inputs.addMouseListener(
						new MouseAdapter()
						{
							public void mouseClicked(MouseEvent me) //마우스로 클릭 했을때 작동
							{
								if(inputs.getText().equals(""))//내용이 없는 상태에서 한번더 눌르면
									changeComp(label,inputs);//입력을 취소하고 라벨을 보여줌
									
								parent.repaint();
							}
						});
						inputs.addFocusListener(new FocusListener()//다른곳을 클릭하거나 다른 입력창으로 포커스가 변경 되었을때 작동
						{
							public void focusGained(FocusEvent e) {}
							
							public void focusLost(FocusEvent e) 
							{
								changeComp(label,inputs);//입력을 취소하고 라벨을 보여줌
								parent.repaint();
							}
						});
						parent.add(inputs,StaticTools.setFullGrid(0, 0, 1, 1, 1f, 0f));
						parent.revalidate();
						parent.repaint();
						inputs.requestFocus();
					}
				}
				
				);
	}
	
	//-----------------------------
	//setter
	//-----------------------------
	
	public void setLimit(final int limit)//텍스트의 최대 입력 갯수를 조정합니다.
	{inputs.setDocument(new LimitDocument(limit));}
	
	public static void setOutClick(final JFrame frame)//이 함수를 이용하여 프레임을 지정하면 다른 곳을 클릭했을때 텍스트 필드를 숨길 수 있습니다.
	{
		frame.getContentPane().setFocusable(true);
		frame.addMouseListener(
			new MouseAdapter() 
			{
            @Override
            public void mousePressed(MouseEvent e) 
            {frame.getContentPane().requestFocus();}
			});
	}
	
	//-----------------------------
	//actions
	//-----------------------------
	
	private void changeComp(final JComponent inComp,final JComponent outComp)//라벨을 숨기고 텍스트 필드를 보이거나 그 반대의 작업을 합니다.
	{
		parent.add(inComp,StaticTools.setFullGrid(0, 0, 1, 1, 1f, 0f));
		parent.remove(outComp);
		parent.revalidate();
		parent.repaint();
	}
	
	private class LimitDocument extends PlainDocument 
	{
		  private int limit;
		  
		  private LimitDocument(int limit) 
		  {
		    super();
		    this.limit = limit;
		  }

		  public void insertString(int offset, String str, AttributeSet attr) throws BadLocationException 
		  {
			  if ((getLength() + str.length()) <= limit)//입력이 지정한 글자수를 넘어가지 못하게 합니다.
			  {super.insertString(offset, str, attr);}		    
		  }
	}
	
	public interface LabelChangedListener
	{public abstract void whenTextUpdated(String text);}

}

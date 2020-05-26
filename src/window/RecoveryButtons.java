package window;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JPanel;

import element.recovery_buttons.RedoButton;
import element.recovery_buttons.UndoButton;

public class RecoveryButtons extends JPanel
{/*
	 * 버전에 + 는 0.?를 뜻힘
	 * 
	 * 기능...
	 * 1.작업을 Redo 한다
	 * 2.작업을 Undo 한다
	 */
	private static final long serialVersionUID = 0L + 1;
	
	public RecoveryButtons()
	{
		super();
		setLayout(new GridBagLayout());
		add(new RedoButton(),setFullGrid(0,0,1,1,0.5f,0f));
		add(new UndoButton(),setFullGrid(1,0,1,1,0.5f,0f));
	}
	
	private GridBagConstraints setFullGrid(int x,int y,int w,int h,float wx,float wy)
	{
		final GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		
		c.gridx=x;
		c.gridy=y;
		c.gridwidth=w;
		c.gridheight=h;
		c.weightx = wx;
		c.weighty = wy;
		
		return c;
	}
}

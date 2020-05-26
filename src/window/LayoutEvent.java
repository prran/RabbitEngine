package window;

import java.awt.BorderLayout;

import javax.swing.BoxLayout;
import javax.swing.JPanel;

public class LayoutEvent extends JPanel
{
	/*
	 * 버전에 + 는 0.?를 뜻힘
	 * 
	 */
	private static final long serialVersionUID = 0L + 1;
	
	private final JPanel title = new JPanel();
	private final JPanel ActiveSet = new JPanel();
	
	public LayoutEvent()
	{
		super();
		setLayout(new BorderLayout());
		title.setLayout(new BorderLayout());
		ActiveSet.setLayout(new BoxLayout(ActiveSet,BoxLayout.Y_AXIS));
		add(title,BorderLayout.NORTH);
		add(ActiveSet,BorderLayout.CENTER);
	}
}

package tools;

import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;

@SuppressWarnings("serial")
public class RemovePopup extends JPopupMenu
{
	/*
	 * 인자와 셋터로 받아오는 객체들을 기본으로
	 * 특정 페널에서 지정된 컴포넌트를 제거하거나
	 * 혹은 Runnable을 대입하여 다른 삭제 작업들을 할 수 있습니다.
	 * 여러번 사용되서 따로 빼둔 클래스 입니다.
	 */
	private Runnable run;
	private Component comp;
	
	public RemovePopup(JPanel panel)
	{
		super();
		
		final JMenuItem rm = new JMenuItem("remove");
		rm.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
		rm.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) 
			{
				try
				{run.run();}
				catch(Exception ex)
				{panel.remove(comp);}
			}});
		
		add(rm);
	}
	
	public RemovePopup(Runnable run)
	{
		super();
		
		this.run = run;
		final JMenuItem rm = new JMenuItem("remove");
		rm.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
		rm.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) 
			{
				run.run();
			}});
		
		add(rm);
	}
	
	
	public void setRemove(Component comp)
	{this.comp = comp;}
	
	public void setRunnable(Runnable r)
	{run = r;}
	
	public Runnable getRunnable()
	{return run;}

}

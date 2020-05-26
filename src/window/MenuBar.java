package window;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import element.menu_bar.LoadWorks;
import element.menu_bar.SaveWorks;
import tools.InnerRuns;

public class MenuBar extends JMenuBar{
	
	/*
	 * 버전에 + 는 0.?를 뜻힘
	 * 
	 * 기능...
	 * 1.작업을 저장하는 기능
	 * 2.작업을 불러오는 기능
	 * 3.이미지 매니저가 꺼져있을때 복구
	 * 4.데이터 리큐가 꺼져있을때 복구
	 * 5.레이아웃 이벤트가 꺼져있을때 복구
	 * 6.네트워크 설정
	 */
	private static final long serialVersionUID = 0L + 1;

	public MenuBar(final InnerRuns inRuns)
	{
		final JMenu saves = new JMenu("Saves");
		final JMenuItem save = new JMenuItem("Save works");
		save.addActionListener(new SaveWorks());
		saves.add(save);
		final JMenuItem load = new JMenuItem("Load works");
		load.addActionListener(new LoadWorks());
		saves.add(load);
		final JMenu open = new JMenu("Open");
		final JMenuItem imageManager = new JMenuItem("Image manager");
		
		imageManager.addActionListener(
				new ActionListener() 
				{
					public void actionPerformed(ActionEvent e) 
					{inRuns.get(InnerRuns.IMAGE_MANAGER).setVisible(true);}
				});
		
		open.add(imageManager);
		open.add(new JMenuItem("Data liquidity"));
		open.add(new JMenuItem("View event"));
		add(saves);
		add(open);
	}

}

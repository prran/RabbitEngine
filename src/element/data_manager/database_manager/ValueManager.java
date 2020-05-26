package element.data_manager.database_manager;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;

import tools.GradationPanel;
import tools.ImageButton;

@SuppressWarnings("serial")
public class ValueManager
{	
	private static final int TITLE_WIDTH = 280, TITLE_HEIGHT = 30;
	private static final Color TITLE_COLOR_UP = Color.getColor("deepSkyblue", 0XFFCCBBFF), 
			TITLE_COLOR_DOWN = Color.getColor("Skyblue", 0XFFE1E1FF),
			TITLE_BORDER_COLOR = Color.getColor("little gray",0XFFBBBBBB);
	
	private static final String SETTING_NORMAL = "images/setting_normal.png",
			SETTING_PRESSED = "images/setting_pressed.png",
			SETTING_PUT = "images/setting_put.png";
	
	private static final String SHOW_TABLE_BUTTON = "Show Table";
	
	public static final String SERVER = "Server DB", CILENT = "Cilent DB";
	
	private JPanel list = new JPanel();
	private JPanel result = new JPanel();
	
	/**
	 * make up data in table and revise it where used in LayoutEvent.
	 * @param l String of title label
	 */
	public ValueManager(final String l)//설명 생략
	{
		result.setLayout(new BorderLayout());
		
		setList(l);
		setTitle(l);
	}
	//=======>linked procedure
		private void setList(final String l)//DB내의 변수의 사용 정보들을 담는 리스트를 초기화 한다.
		{
			list.setLayout(new BoxLayout(list,BoxLayout.Y_AXIS));
			list.setBackground(Color.WHITE);
			
			JPanel settingPanel = new JPanel();
			settingPanel.setLayout(new FlowLayout(FlowLayout.TRAILING));
			settingPanel.setBackground(Color.WHITE);
			
			ImageButton settingButton = new ImageButton(new String[]{SETTING_NORMAL,SETTING_PRESSED,SETTING_PUT}, 32, 32);
			settingButton.addActionListener(getConfigurationShowingWork());
			if(l != CILENT)
			settingPanel.add(settingButton);
			list.add(settingPanel);
			
			result.add(list,BorderLayout.CENTER);
		}
		//=======>linked procedure
			private ActionListener getConfigurationShowingWork()
			{
				return
			
				new ActionListener()
				{
					private final JFrame tableViewer = new LoginAccesser().get();
				
					public void actionPerformed(ActionEvent e)
					{tableViewer.setVisible(true);}
				};
			}
	//=======>linked procedure2
		private void setTitle(final String l)//타이틀의 크기와 라벨, 버튼을 지정한다.
		{
			GradationPanel title = new GradationPanel(TITLE_COLOR_UP,TITLE_COLOR_DOWN);
			title.setPreferredSize(new Dimension(TITLE_WIDTH,TITLE_HEIGHT));
			title.setMaximumSize(new Dimension(TITLE_WIDTH,TITLE_HEIGHT));
			
			title.setBorder(new LineBorder(TITLE_BORDER_COLOR));
			
			title.setLayout(new BorderLayout());
			
			JLabel titleLabel = new JLabel(l);
			Font font = new Font("Bookman Old Style", Font.PLAIN, 20);
			titleLabel.setFont(font);
			title.add(titleLabel,BorderLayout.CENTER);
			
			JButton button = new JButton(SHOW_TABLE_BUTTON);
			button.addActionListener(getTableShowingWork());
			button.setFocusable(false);
			title.add(button,BorderLayout.EAST);
			
			result.add(title,BorderLayout.NORTH);
		}
		//=======>linked procedure
			private ActionListener getTableShowingWork()
			{
				return
				
				new ActionListener()
				{
					private final JFrame tableViewer = new TableViewer().get();
					
					public void actionPerformed(ActionEvent e)
					{tableViewer.setVisible(true);}
				};
			}
			
	public JPanel get()
	{
		return result;
	}

}

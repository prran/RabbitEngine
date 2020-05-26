package window;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.WindowEvent;
import java.awt.event.WindowStateListener;
import java.io.File;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.UIManager;

import logic.FileConnection;
import tools.InnerRuns;
import tools.RenameLabel;

import element.layout_list.LayoutList;

public class Core{
	
	/*
	 * 
	 * 
	 * 지금 할일.. 
	 * 3.다했어...이제...진짜 어려운거 해야지..... 저장과 불러오기.......
	 * 
	 * 
	 * 현재버전의 기준
	 * 가장큰 객체 단위로 기능을 완성 할때마다 0.1씩 추가
	 * 전체 완료후 1.0 (비쥬얼 스크린 포함 11개)
	 * 
	 * 버전 1이후로 각 객체단위로 기능이 추가 될때 마다 0.01씩 추가
	 * 버그 픽스를 할 경우 0.0001을 추가
	 * 해당 객체의 버그가 완전히 해결되었을때  0.001단위를추가
	 * 
	 * 
	 */
	
	@SuppressWarnings("unused")
	private static final float VERSION_NUMBER = 0.49f;
	public static final String ICONIMAGE = "images/icon.png";
	
	
	private final static int WIDTH = 1400;
	private final static int HEIGHT = 1000;
	
	private final static int LOCATION_X = 100;
	private final static int LOCATION_Y = 15;
	
	private final JFrame frame = new JFrame();
	private final InnerRuns innerRuns = new InnerRuns(frame);
	
	private Core()
	{
		FileConnection.setObject("main frame", frame);
		frameSet();
		Combine();
	}
	
	private void start()
	{
		frame.setVisible(true);
		innerRuns.startAll();
	}
	
	private void frameSet()
	{
		frame.setTitle("RabbitEngine");
		frame.setIconImage(new ImageIcon("images/icon.png").getImage());
		frame.setSize(WIDTH, HEIGHT);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setJMenuBar(new MenuBar(innerRuns));
		frame.setLocation(LOCATION_X, LOCATION_Y);
		frame.addWindowStateListener(new WindowStateListener(){
			public void windowStateChanged(WindowEvent e) {
				innerRuns.changeWindowStateAll(e.getNewState());
			}});
		RenameLabel.setOutClick(frame);
	}
	
	private void Combine()
	{
		LayoutList mainCoreObject = new LayoutList();
		final Container pane = frame.getContentPane();
		final JPanel eastPanel = new JPanel();
		eastPanel.setLayout(new BorderLayout());
		eastPanel.add(new LayoutStructure(mainCoreObject),BorderLayout.CENTER);
		eastPanel.add(new RecoveryButtons(),BorderLayout.SOUTH);
		pane.add(eastPanel,BorderLayout.EAST);
		final JPanel inPanel = new JPanel();
		inPanel.setLayout(new BorderLayout());
		inPanel.add(new GraphicScreen(mainCoreObject),BorderLayout.CENTER);
		final JPanel centerPanel = new JPanel();
		centerPanel.setLayout(new BorderLayout());
		centerPanel.add(new Tags(mainCoreObject),BorderLayout.EAST);
		centerPanel.add(new DataManager(),BorderLayout.WEST);
		final JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BorderLayout());
		mainPanel.add(new ViewList(mainCoreObject),BorderLayout.SOUTH);
		centerPanel.add(inPanel,BorderLayout.CENTER);
		mainPanel.add(centerPanel,BorderLayout.CENTER);
		pane.add(mainPanel,BorderLayout.CENTER);
	}

	public static void main(String args[])
	{
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
            	final File catchFile = new File("catch");
        		try 
        		{
        			File[] folder_list = catchFile.listFiles();
        			
        			for (int i = 0; i < folder_list.length; i++) 
        				folder_list[i].delete();
        		}
        		catch(Exception ex){}
            }
        });
		try {UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");}
		catch(Exception e) {}
		
		FileConnection.startConnection();
		
		final Core core = new Core();
		core.start();
	}

}

package element.data_manager.directory_manager;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileSystemView;

import tools.FreeSizeButton;
import tools.GradationPanel;

public class Buttons 
{
	/*
	 * 이 클래스는 디렉토리 매니저의 레이아웃 구성요소중 하나입니다.
	 * 디렉토리 매니저 하단에 위치하는 두개의 기능성 버튼을 담은 레이아웃입니다.
	 * 버튼은 각각 폴더 위치 이동과 폴더 생성 기능이 있습니다.
	 */
	
	private static final int LAYOUT_WIDTH = 280 , LAYOUT_HEIGHT = 50;	
	private static final int BUTTON_WIDTH = 110 , BUTTON_HEIGHT = 35;
	private static final int BUTTON_PADDING_X  = 22, BUTTON_PADDING_Y  = 7;
	
	private static final Color BACKGROUND_COLOR_TOP = Color.getColor("lightblue", 0XFFddddFF) 
			,BACKGROUND_COLOR_BUTTOM = Color.getColor("skyblue", 0XFFaaaaFF);
	
	private static final String BUTTON_LABEL_LEFT = "Move..." , BUTTON_LABEL_RIGHT = "Make folder";
	
	//---------------layout instance
	
	private final GradationPanel result = new GradationPanel(BACKGROUND_COLOR_TOP,BACKGROUND_COLOR_BUTTOM);
	//-----------------------------
	//creator
	//-----------------------------
	/**
	 * this class is Use to move the Path or make the additional Path in PathManager path.
	 * 
	 * @param data pathManager that directory location was set
	 */
	public Buttons(final PathManager data)//설명 생략
	{
		setLayout(data);
		setArguments(data);
	}
	//======>linked procedure
		private void setLayout(final PathManager data)//각 요소들의 크기,위치 조정
		{
			result.setLayout(new FlowLayout(FlowLayout.CENTER,BUTTON_PADDING_X,BUTTON_PADDING_Y));
			result.setPreferredSize(new Dimension(LAYOUT_WIDTH, LAYOUT_HEIGHT));
			result.setMaximumSize(new Dimension(LAYOUT_WIDTH, LAYOUT_HEIGHT));
		}
	//======>linked procedure2
		private void setArguments(final PathManager data)//각 버튼에 기능을 집어 넣고 배경에 장착
		{
			final FreeSizeButton moveDirButton = new FreeSizeButton(BUTTON_WIDTH,BUTTON_HEIGHT,BUTTON_LABEL_LEFT);
			moveDirButton.addActionListener(dirMovBtnWorks(data));
			result.add(moveDirButton);
			
			final FreeSizeButton mkFolderButton = new FreeSizeButton(BUTTON_WIDTH,BUTTON_HEIGHT,BUTTON_LABEL_RIGHT);
			mkFolderButton.addActionListener(mkFdBtnWorks(data));
			result.add(mkFolderButton);
		}
	//======>>linked procedure
			private ActionListener dirMovBtnWorks(final PathManager data)//Directory Move Button Works, 하단 참조
			{
				return 
				
				new ActionListener()
				{
					@Override
					public void actionPerformed(ActionEvent e) //checked
					{
						final JFileChooser chooser = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
						final FileNameExtensionFilter filter = new FileNameExtensionFilter("Binary File", "cd11");
						final File directory = new File(data.getDirectory());
						
						chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
						chooser.setCurrentDirectory(directory);
						chooser.setAcceptAllFileFilterUsed(true);
				        chooser.setFileFilter(filter);
				        chooser.setSelectedFile(directory);
				        
				        try
				        {
				        	final int returnVal = chooser.showOpenDialog(null);
				        	
					        if(returnVal == JFileChooser.APPROVE_OPTION) 
					            data.setDirectory(chooser.getSelectedFile().toString());
				        }
				        catch(NullPointerException ex)
				        {}
					}
				};
			}
			/*
			 *기능:화면에 파일 탐색기를 열고 여기에서 선택한 폴더의 경로로 디렉토리 매니저를 이동하는 리스너
			 * 1.파일 탐색기 화면에 폴더만 보이도록 filter 로 거름
			 * 2.파일 탐색기 를 현재 의 경로로 이동함 
			 * 3.파일 탐색기 하단 입력창에 현재 경로를 표기해둠
			 * >>Why? 입력창이 비어있는 상태에서 OK 버튼을 클릭시 파일 탐색기가 닫히지 않음
			 * >>이는 사용자에게 불편하다고 판단하여 OK 버튼을 눌러서 바로 끌수 있게 할수 있게하기위함
			 * 
			 * 4.디렉토리 매니저의 경로를 선택한 폴더의 경로로 이동함.
			 */
	//======>>linked procedure2
			private ActionListener mkFdBtnWorks(final PathManager data)//make Folder Button Works, 현재 경로에 폴더를 생성
			{
				return
						
				new ActionListener()
				{
					@Override
					public void actionPerformed(ActionEvent e) //checked
					{
						File folder = new File(data.getDirectory()+"\\new Folder");
						for(int i=1;folder.exists();i++)//생성할 폴더의 명이 겹치면 안돼므로 겹치지 않을때 까지 폴더의 명을 변환한다.
						{
							folder = new File(data.getDirectory()+"\\new Folder"+i);
						}
						try {folder.mkdir();}
						catch(Exception ex) {ex.printStackTrace();}
					}
				};
			}
	
	//-----------------------------
	//getter
	//-----------------------------	
	
	/**
	 * 
	 * @return Jpanel of this button frame
	 */
	public JPanel get()
	{return result;}
	
			
}

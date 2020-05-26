package element.data_manager.directory_manager;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JLabel;
import tools.ImageButton;

public class Title extends tools.Title {
	
	/*
	 * 이 클래스는 디렉토리 매니저의 레이아웃 구성요소중 하나입니다.
	 * 디렉토리 메니저의 상단에 위치하여 현재 경로와 파일의 갯수를 보여줍니다.
	 * UndoButton이 안에 들어가 있습니다.
	 */
	
	private static final int TITLE_WIDTH = 280, TITLE_HEIGHT = 30;
	private static final int BUTTON_WIDTH = 30,BUTTON_HEIGHT = 30;
	
	private static final String FILE_COUNT_TEXT = "files: ";
	
	private static final String BUTON_NOMAL_IMAGE = "images/undo_nomal.png",
			BUTTON_MOUSE_PUT_IMAGE = "images/undo_put.png",
			BUTTON_PRESSED_IMAGE = "images/undo_pressed.png";
	
	//---------------layout instance
	
	private JLabel directoryLabel;
	private JLabel fileCountLabel;
	
	//---------------store instance
	
	private final PathManager data;
	private final Runnable handler;
	
	//-----------------------------
	//creator
	//-----------------------------	
	
	/**
	 * @param data Want looking for Path info in pathManager that directory location was set
	 */
	public Title(PathManager data)//하단 설명 참조
	{
		super(TITLE_WIDTH,TITLE_HEIGHT);
		this.data = data;
		
		handler = new Runnable()
		{public void run(){updateDir();}};
		data.setHandler(handler);
		
		setLayout();
		updateDir();
	}
	//생성자
	//1.초기화
	//2.타이틀의 경로를 업데이트하는 작업을 PathManeger의 경로 변경 메소드에 추가함
	//3.레이아웃 설정
	//4.현재 경로를 표기
	//=======>linked procedure
		protected void setLayout()//UndoButton,현재경로라벨,파일갯수라벨을 추가함.
		{
			get().add(new UndoButton(),BorderLayout.WEST);
			directoryLabel = new JLabel();
			fileCountLabel = new JLabel();
			directoryLabel.setFont(new Font("Arial", Font.PLAIN, 15));
			get().add(directoryLabel,BorderLayout.CENTER);
			get().add(fileCountLabel,BorderLayout.EAST);
		}

		private void updateDir()//현재 경로명을 축약해서 보여주고 경로 내의 파일 갯수를 업데이트함
		{
			String[] getDirs = data.getDirectory().split("\\\\");//하단 설명 참조(1)
			if(getDirs.length>2)
				directoryLabel.setText("...\\" + getDirs[getDirs.length-1]);//경로명을 축약해서 현재 경로의 이름만 보여줌
			else // 현재 경로가 C:/ 일경우
				directoryLabel.setText(data.getDirectory());
		
			fileCountLabel.setText(FILE_COUNT_TEXT + data.getFiles().length + " ");//하단 설명 참조(2)
		}
		/*
		 * Other Why
		 * (1)\\\\
		 * 문자열에서 '\'을 찾고자 할때는 '\'로도 감지가 안되고 '\\' 로도 감지할수 없다. 오직 \\\\로 감지할수 있다.
		 * 물론 읽는게 아니라 '\'를 쓰고자 할때는 '\\'로 표기 하는 것이 맞다.
		 * 
		 * (2)String + String vs StringBulider.append()
		 * 이 메소드는 여러번 반복하는것이 아니라 경로를 이동할때 가끔 한번씩 작동하므로 String+String을 써서 간결하게 쓰는편이 좋다.
		 */
	
	//-----------------------------
	//inner class
	//-----------------------------
		
	@SuppressWarnings("serial")
	private class UndoButton extends ImageButton
	{	
		/*
		 * 이 클래스는 타이틀안에 존재하는 버튼입니다.
		 * 현재 경로에서 한단계 밖으로 나오는 기능을 가지고 있습니다.
		 */
		
		public UndoButton()//버튼 이미지를 설정후 기능 추가
		{
			super(new String[] {BUTON_NOMAL_IMAGE,BUTTON_MOUSE_PUT_IMAGE,BUTTON_PRESSED_IMAGE}, BUTTON_WIDTH, BUTTON_HEIGHT);	
			addActionListener(new ActionListener() 
			{
				@Override
				public void actionPerformed(ActionEvent e)
				{
					final String[] dirs = data.getDirectory().split("\\\\");//하단 설명 참조(1)
					if(dirs.length<3) //C:/ 에서 더이상 나갈수 없게 제한을 둠
						return;
					String calculDir = "";
					for(int i= 0; i<dirs.length-1; i++)//현재 경로에서 경로를 한단계 제거한 값을 가져옴 Path\path\file > Path\path\
					{
						calculDir += dirs[i]+"\\";
					}
					data.setDirectory(calculDir.substring(0,calculDir.length()-1));// Path\path\ > Path\path
				}
			});
		}
		/*
		 * Other Why
		 * (1)\\\\
		 * 문자열에서 '\'을 찾고자 할때는 '\'로도 감지가 안되고 '\\' 로도 감지할수 없다. 오직 \\\\로 감지할수 있다.
		 * 물론 읽는게 아니라 '\'를 쓰고자 할때는 '\\'로 표기 하는 것이 맞다.
		 */
		
	}
}

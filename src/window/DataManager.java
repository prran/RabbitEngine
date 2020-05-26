package window;

import java.awt.BorderLayout;
import java.awt.GridBagLayout;
import java.io.File;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import element.data_manager.data_manager.SelectButton;
import element.data_manager.database_manager.ValueManager;
import element.data_manager.directory_manager.Buttons;
import element.data_manager.directory_manager.FileList;
import element.data_manager.directory_manager.PathManager;
import element.data_manager.directory_manager.Title;
import tools.FileDrop;
import tools.StaticTools;
import tools.UserScrollBarUI;

public class DataManager extends JPanel
{	
	/*
	 * 버전에 + 는 0.?를 뜻힘
	 * 
	 * 기능..
	 * 1.디렉토리 매니저를 통해서 파일을 넘나듦
	 * >뒤로가기 버튼
	 * >타이틀
	 * >파일 정보 표기 리스트
	 * >외부에서 파일 가져오기
	 * >삭제
	 * >폴더추가
	 * >팝업창을 이용한 폴더 이동
	 * >내부에 표기된 폴더의 작동 기능
	 * 
	 * 2.DB매니저를 통해서 DB관리를 가능하게함
	 * 3.디렉토리 매니저 와 DB매니저 선택할수 있는 버튼
	 */
	private static final long serialVersionUID = 0L + 3;
	
	private final DirectoryManager directoryManager = new DirectoryManager();
	private final DataBaseManager databaseManager = new DataBaseManager();
	private final JPanel selectButton = new JPanel();
	
	public DataManager()
	{
		super();
		setLayout(new BorderLayout());
		selectButton.setLayout(new BoxLayout(selectButton,BoxLayout.Y_AXIS));
		selectButton.add(new SelectButton(SelectButton.DIRECTORY,this,directoryManager));
		selectButton.add(new SelectButton(SelectButton.DATABASE,this,databaseManager));
		add(directoryManager,BorderLayout.CENTER);
		add(selectButton,BorderLayout.EAST);
	}

	private class DirectoryManager extends JPanel
	{
		private static final long serialVersionUID = 1L;
		
		private final PathManager data = new PathManager();
		private final Title title = new Title(data);
		private final FileList fileList;
		private final Buttons buttons = new Buttons(data);
		
		private DirectoryManager()
		{
			super();
			fileList = new FileList(data);
			setLayout(new BorderLayout());
			add(title.get(),BorderLayout.NORTH);
			add(buttons.get(),BorderLayout.SOUTH);
			
			final JScrollPane fileListSc 
			= new JScrollPane(fileList.getResurce(),
					JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
					JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
			
			fileListSc.getVerticalScrollBar().setUnitIncrement(16);
			fileListSc.getVerticalScrollBar().setUI(new UserScrollBarUI());
			
			new FileDrop(fileListSc,new FileDrop.DropedListener() {
				@Override
				public void filesDropped(File[] files) 
				{
					for(File f:files)
					{
						final String[] fileName = f.getPath().split("\\\\");
						final String dir = data.getDirectory() +"\\"+ fileName[fileName.length-1];
						
						File moveDir = new File(dir);
						for(int i = 2; moveDir.exists();i++)
						{
							final String[] newDir = dir.split("\\.");
							System.out.println(newDir[0]);
							moveDir = new File(newDir[0] + i + "." + newDir[1]);
						}
						if(!f.renameTo(moveDir))System.out.println("파일이동에 실패했습니다." + data.getDirectory() + fileName[fileName.length-1]);
					}
				}
			});
			add(fileListSc,BorderLayout.CENTER);
		}
		
	}
	
	public class DataBaseManager extends JPanel
	{
		private static final long serialVersionUID = 1L;
		
		private DataBaseManager()
		{
			super();
			
			setLayout(new GridBagLayout());
			
			add(new ValueManager(ValueManager.CILENT).get(),StaticTools.setGridFill(StaticTools.setFullGrid(0, 0, 1, 1, 1, 1)));
			add(new ValueManager(ValueManager.SERVER).get(),StaticTools.setGridFill(StaticTools.setFullGrid(0, 1, 1, 1, 1, 1)));
		}
	}
}

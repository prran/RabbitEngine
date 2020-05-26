package tools;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileSystemView;

public class SetFileChooser implements ActionListener{
	
	/*
	 * 경로 선택창을 화면에 띄우는 리스너입니다.
	 * 여러번 사용되기 때문에 클래스로 따로 빼 두었습니다.
	 */
	
	protected int returnVal;
	private final String title;
	
	public SetFileChooser(String title)
	{this.title = title;}

	@Override
	public void actionPerformed(ActionEvent e) 
	{
		// TODO Auto-generated method stub
		JFileChooser chooser = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
		chooser.setDialogTitle(title);
		chooser.setCurrentDirectory(new File("c:\\"));
		chooser.setAcceptAllFileFilterUsed(true);
		chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		FileNameExtensionFilter filter = new FileNameExtensionFilter("Binary File", "cd11");
	    chooser.setFileFilter(filter);
	    
	    returnVal = chooser.showOpenDialog(null);
	    
	    /*if(returnVal == JFileChooser.APPROVE_OPTION) 
	    {
	    	
	    }
	    else if(returnVal == JFileChooser.CANCEL_OPTION)
	    {}*/
	}

}
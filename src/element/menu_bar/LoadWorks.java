package element.menu_bar;

import java.awt.event.ActionEvent;

import javax.swing.JFileChooser;

import tools.SetFileChooser;

public class LoadWorks extends SetFileChooser{
	
	/*
	 *지금은 아무것도 없음.
	 */
	
	public LoadWorks()
	{
		super("Load");
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		super.actionPerformed(e);
	    if(returnVal == JFileChooser.APPROVE_OPTION) 
	    {
	    	
	    }
	    else if(returnVal == JFileChooser.CANCEL_OPTION)
	    {}
	}

}

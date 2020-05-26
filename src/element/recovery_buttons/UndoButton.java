package element.recovery_buttons;

import tools.ImageButton;

public class UndoButton extends ImageButton{
	
	/*
	 *지금은 아무것도 없음.
	 */
	
	private final static int WIDTH = 125;
	private final static int HEIGHT = 66;
	
	public UndoButton()
	{
		super(new String[] {"images/undo2_nomal.png","images/undo2_press.png","images/undo2_put.png"}, WIDTH, HEIGHT);
	}
	
	private static final long serialVersionUID = 1L;
	

}
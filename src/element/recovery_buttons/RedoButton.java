package element.recovery_buttons;

import tools.ImageButton;

public class RedoButton extends ImageButton{
	
	/*
	 *지금은 아무것도 없음.
	 */
	
	private final static int WIDTH = 125;
	private final static int HEIGHT = 66;
	
	public RedoButton()
	{
		super(new String[] {"images/redo_nomal.png","images/redo_press.png","images/redo_put.png"}, WIDTH, HEIGHT);
	}
	
	private static final long serialVersionUID = 1L;
	

}

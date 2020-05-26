package element.data_manager.database_manager;

import javax.swing.ImageIcon;
import javax.swing.JFrame;

public class LoginAccesser {
	
	private final JFrame frame= new JFrame(); 
	private final static int WIDTH = 500, HEIGHT = 600;
	private final static int LOCATION_X = 100, LOCATION_Y = 200;
	
	public LoginAccesser()
	{
		frame.setTitle("Database Login");
		frame.setIconImage(new ImageIcon("images/icon.png").getImage());
		frame.setSize(WIDTH, HEIGHT);
		frame.setLocation(LOCATION_X, LOCATION_Y);
	}
	
	public JFrame get()
	{return frame;}

}

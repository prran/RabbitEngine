package tools;

import java.awt.Dimension;

import javax.swing.ImageIcon;
import javax.swing.JButton;

public class ImageButton extends JButton{
	
	/*
	 * 이미지 버튼입니다. Image 의 디렉토리를 입력하면 이미지를 지정할 수 있습니다.
	 * 이미지의 인자로 들어가는 최대 String 인자 값은 3개 이며
	 * [1] 평상모습 [2] 눌러진모습 [3] 마우스를 올렸을때의 모습 순으로 적용됩니다.
	 */
	
	private static final long serialVersionUID = 1L;
	
	public ImageButton(final String[] Images,final int w, final int h)
	{
		super(new ImageIcon(Images[0]));
		try 
		{
			setPressedIcon(new ImageIcon(Images[1]));
			setRolloverIcon(new ImageIcon(Images[2]));
		}
		catch(Exception e){}
		finally
		{
			setPreferredSize(new Dimension(w,h));
			setMaximumSize(new Dimension(w,h));
			setBorderPainted(false); 
			setFocusPainted(false); 
			setContentAreaFilled(false);
		}
	}

}

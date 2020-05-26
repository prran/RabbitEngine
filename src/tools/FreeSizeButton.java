package tools;

import java.awt.Dimension;

import javax.swing.JButton;

public class FreeSizeButton extends JButton
{
	private static final long serialVersionUID = 1L;
	
	public FreeSizeButton(int width, int height, String text)
	{
		setPreferredSize(new Dimension(width, height));
		setMaximumSize(new Dimension(width, height));
		setFocusPainted(false);
		setText(text);
	}
}
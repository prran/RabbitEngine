package tools;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;

import javax.swing.JPanel;

@SuppressWarnings("serial")
public class GradationPanel extends JPanel{
	
	/*
	 * 인자로 주는 color 의 순서대로 상하 그라데이션을 그리는 판넬입니다.
	 */
	
	private final Color color1;
	private final Color color2;
	
	public GradationPanel(Color color1, Color color2)
	{
		super();
		this.color1 = color1;
		this.color2 = color2;
	}
	@Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        final Point point1 = new Point(10, 0);
        final Point point2 = new Point(10, getHeight());
        final GradientPaint gp = new GradientPaint(
                point1, color1,
                point2, color2,
                true);

        // we need a Graphics2D to use GradientPaint.
        // If this is Swing, it should be one..
        final Graphics2D g2 = (Graphics2D) g;
        g2.setPaint(gp);
        g.fillRect(0, 0, getWidth(), getHeight());
    }
	
	
}

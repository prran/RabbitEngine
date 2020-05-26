package window;

import java.awt.BorderLayout;
import javax.swing.JPanel;

import element.data_liquidity.LiquidityList;
import tools.Title;

public class DataLiquidity extends JPanel
{
	/*
	 * 버전에 + 는 0.?를 뜻힘
	 * 
	 */
	
	private static final long serialVersionUID = 0L+1;
	
	private static final int TITLE_WIDTH = 240;
	private static final int TITLE_HEIGHT = 30;
	private static final String TITLE_TEXT = "Data Liquidity";
	
	private final LiquidityList lqList = new LiquidityList();
	
	public DataLiquidity()
	{
		super();
		setLayout(new BorderLayout());
		final Title title = new Title(TITLE_WIDTH,TITLE_HEIGHT);
		title.setText(TITLE_TEXT);
		add(title.get(),BorderLayout.NORTH);
		final JPanel freeSizeLayout = new JPanel();
		freeSizeLayout.setLayout(new BorderLayout());
		freeSizeLayout.add(lqList.get(),BorderLayout.NORTH);
		add(freeSizeLayout,BorderLayout.CENTER);
	}
}

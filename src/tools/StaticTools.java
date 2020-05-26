package tools;

import java.awt.GridBagConstraints;

public class StaticTools {
	
	/*
	 * 이 클래스는 객체의 장소 상관 없이 자주 불려져야 하는 함수들을 Static으로 담고 있습니다.
	 */
	
	private StaticTools(){}
	
	public static GridBagConstraints setFullGrid(int x,int y,int w,int h,float wx,float wy)//GridBagLayout을 사용중일때 컴포넌트의 add 옵션을 지정합니다.
	{
		final GridBagConstraints c = new GridBagConstraints();
		//c.fill = GridBagConstraints.BOTH;
		
		c.gridx=x;
		c.gridy=y;
		c.gridwidth=w;
		c.gridheight=h;
		c.weightx=wx;
		c.weighty=wy;
		
		return c;
	}
	
	public static GridBagConstraints setGridFill(GridBagConstraints grid)//GridBagLayout을 사용중일때 컴포넌트의 add 옵션을 지정합니다. 지정한 공간을 전부 채우게 합니다.
	{
		grid.fill = GridBagConstraints.BOTH;
		
		return grid;
	}
	
	public static void testLog(Object ob)//디버그 확인용입니다.
	{
		System.out.println(ob.getClass().toString() + ": isRun?");
	}//StaticTools.testLog(this);

}

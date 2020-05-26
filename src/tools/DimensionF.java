package tools;

import java.awt.Dimension;

public class DimensionF {
	
	/*
	 * 더 이상의 설명은 생략 한다.
	 */
	
	public float width ,height;
	
	
	public DimensionF()
	{}
	public DimensionF(float width, float height)
	{
		this.width = width; 
		this.height = height;
	}
	
	public int getWidth()
	{
		return (int)width;
	}
	
	public int getHeight()
	{
		return (int)height;
	}
	
	public void setSize(final float width, final float height)
	{
		this.width = width; 
		this.height = height;
	}
	
	public void setSize(Dimension dimension)
	{
		width = dimension.width;
		height = dimension.height;
	}
	
	public Dimension getSize()
	{
		return new Dimension((int)width,(int)height);
	}
	
	public String toString()
	{
		return "width = " + width + " height = " + height;
	}

}

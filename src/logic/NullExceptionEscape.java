package logic;

import java.awt.Dimension;
import java.awt.Point;

import javax.swing.JPanel;

@SuppressWarnings("serial")
public class NullExceptionEscape extends JPanel
{
	/*
	 * 이 클래스가 사용되는 이유는 객체를 처음 생성할때는 인자로 받은 객체가 존재하지 않아 가 null인 이유로 Exception이 발생하지만
	 * 그 객체의 메소드, 객체가 반드시 필요하며, 임시 값을 집어 넣을수 없는 상황.. 그리고 if(null) 이나 Try로 해결할 수 없는상황이라면
	 * 초기 실행을 위해 null 대신의 임시 객체를 집어 넣기 위해서 사용됩니다.
	 * LayoutInfo 에서 이 객체를 상속하고 있습니다.
	 */
	//---------------variable
	
		public final Dimension orignalRenderSize = new Dimension();
		public final Dimension totalSize = new Dimension();
		public final Point totalLocation = new Point();
		
		public Float rateX = 0f;
		public Float rateY = 0f;
		public Float rateW = 0f;
		public Float rateH = 0f;
		
		public Float allScale = 0f;
		
		public Float XScale = 0f;
		public Float YScale = 0f;
		public Float widthScale = 0f;
		public Float heightScale = 0f;
		
		//---------------switch
		
		public boolean sizeBy = false;
		public boolean locationBy = true;
		public boolean isReverseX = false;
		public boolean isReverseY = false;
		
		//---------------store for logic
		public Object sizeSw = new Object();
		public Object locationSw = new Object();
		public Object reverseXSw = new Object();
		public Object reverseYSw = new Object();
}

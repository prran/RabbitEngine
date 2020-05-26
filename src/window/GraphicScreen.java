package window;

import java.awt.BorderLayout;
import java.awt.GridBagLayout;

import javax.swing.JComponent;
import javax.swing.JPanel;

import org.jdesktop.jxlayer.JXLayer;
import org.pbjar.jxlayer.demo.TransformUtils;
import org.pbjar.jxlayer.plaf.ext.transform.DefaultTransformModel;

import element.graphic_screen.RotateButton;
import element.graphic_screen.Screen;
import element.graphic_screen.SizeSet;
import element.graphic_screen.UISizeChanger;
import element.layout_list.LayoutList;
import logic.FileConnection;
import tools.StaticTools;


public class GraphicScreen extends JPanel{
	
	/*
	 * 버전에 + 는 0.?를 뜻힘
	 * 
	 * 기능...
	 * 1.그래픽 화면을 표기
	 * 2.화면 회전
	 * 3.화면 비율 수정
	 * 4.레이아웃의 정보와 동기화 하여 이미지의 자유변형을 제공
	 */
	private static final long serialVersionUID = 1L + 0;
	
	public GraphicScreen(LayoutList layoutList)
	{
		super();
		final Screen scr = new Screen(layoutList);
		scr.addUISizeChanger(new UISizeChanger());
		
		FileConnection.setObject("screen", scr);
		
		final SizeSet sizeSetX = new SizeSet(SizeSet.X,scr);
		final SizeSet sizeSetY = new SizeSet(SizeSet.Y,scr);
		setLayout(new GridBagLayout());
		
		final JPanel layoutY = new JPanel();
		layoutY.setLayout(new BorderLayout());
		layoutY.add(rotateTexter(sizeSetY.get(), 90),BorderLayout.NORTH);	
		final JPanel layoutX = new JPanel();
		layoutX.setLayout(new BorderLayout());
		layoutX.add(rotateTexter(sizeSetX.get(), 0),BorderLayout.NORTH);
		
		add(layoutX,StaticTools.setFullGrid(0,0,1,1,0f,0f));
		add(new RotateButton(scr),StaticTools.setFullGrid(1,0,1,1,0f,0f));
		add(layoutY,StaticTools.setFullGrid(1,1,1,1,0f,0f));
		add(scr.get(),StaticTools.setFullGrid(0,1,1,1,0f,0f));
		
	}
	
	private JXLayer<JComponent> rotateTexter(JPanel panel, int radian)
	{
		final DefaultTransformModel transformModel = new DefaultTransformModel();
        transformModel.setRotation(Math.toRadians(radian));
        transformModel.setScaleToPreferredSize(true);
        JXLayer<JComponent> rotatePane = TransformUtils.createTransformJXLayer(panel, transformModel);
		return rotatePane;
	}

}

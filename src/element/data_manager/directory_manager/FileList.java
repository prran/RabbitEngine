package element.data_manager.directory_manager;

import java.awt.Color;
import java.awt.ComponentOrientation;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragSource;
import java.awt.dnd.DragSourceDragEvent;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.dnd.DragSourceEvent;
import java.awt.dnd.DragSourceListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import javax.swing.border.LineBorder;

import logic.FileConnection;
import tools.FileDrop;
import tools.StaticTools;

public class FileList {
	
	/*
	 * 이 클래스는 디렉토리 매니저의 레이아웃 구성요소중 하나입니다.
	 * 디렉토리 내부에있는 파일들을 출력하고 기능을 추가하는 세로축 파일 리스트입니다.
	 * 디렉토리 매니저의 중앙에 위치합니다.
	 */
	
	private static final Color BUTTON_BACK_COLOR_NORMAL = Color.getColor("bluelight", 0xFFF4F4FF)
			,BUTTON_BACK_COLOR_PRESSED = Color.LIGHT_GRAY;
	
	private static final Color SEPARATOR_GRADATION_COLOR = Color.lightGray;
	
	private static final Image FOLDER_ICON = new ImageIcon("images/folder.png").getImage()
			,FILE_ICON = new ImageIcon("images/file.png").getImage();
	
	
	//---------------layout instance
	
	private final JPanel fileList = new JPanel();
	private final JPopupMenu popup = new JPopupMenu();
	
	//---------------store instance
	
	private final PathManager data;
	private File focusFile;
	
	//-----------------------------
	//creator
	//-----------------------------
	/**
	 * @param data Want looking for files info in pathManager that directory location was set
	 */
	public FileList(final PathManager manager)//화면이 갱신 메소드를 PathManager로 보낸뒤 레이아웃과 팝업을 설정한다.
	{
		this.data = manager;
		data.setHandler(new Runnable() {public void run(){updateList();}});
		setLayout();
		setPopup();
	}
	//======>linked procedure
		private void setLayout()
		{
			fileList.setLayout(new BoxLayout(fileList,BoxLayout.Y_AXIS));
			fileList.setBackground(Color.white);
			fileList.setBorder(new LineBorder(Color.getColor("darkness",0XFFdddddd)));
		
			updateList();
		}
	//======>linked procedure2
		private void setPopup()
		{
			final JMenuItem rm = new JMenuItem("remove");
			rm.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
			rm.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) 
				{
					focusFile.delete();
				}});
		
			popup.add(rm);
		}
	
	//----------------------------
	//getter
	//----------------------------
	public JPanel getResurce()//설명 생략
	{
		return fileList;
	}
	
	//-----------------------------
	//actions
	//-----------------------------
	
	private void updateList()// setLayout(), this() / 현재 경로의 파일들을 받아서 파일별로 버튼을 만들고 출력 리스트에 재구성한다.
	{
		synchronized(fileList)
		{
			fileList.removeAll();
			final File[] files = data.getFiles();
			
			for(File file : files)
				fileList.add(new FileInfoButton(file));
					
			fileList.revalidate();// 하단 설명 참조(1)
			fileList.repaint();
		}
	}
	/*
	 * Other Why
	 * (1) 추가를 완료한 뒤에야 버튼을 보여주는 이유
	 * >>속도가 조금 늦어 지더라도 버튼이 하나씩 추가할때마다 위치수정, 재출력을 하는것이 맞다고 판단된다.
	 * >>그래서 반복문 안쪽에 버튼을 추가할때마다 출력신호를 줬으나 출력이 안된다.
	 * >>synchronized 로 락 제어를 해도 완료가 될때까지 출력이 안된다.
	 * >>그래서 속도라도 빠르게 하기 위해서 출력을 밖으로 빼놨다..
	 */
	
	@SuppressWarnings("serial")
	private class FileInfoButton extends JPanel
	{
		/*
		 * 이 클래스는 세로축 파일 리스트 안에 들어가는 버튼 객체 입니다.
		 * FileList 에서만 사용하는 내부 클래스 입니다.
		 * 파일의 형식별로 정보를 출력하고 버튼의 기능이 추가 됩니다.
		 * Folder:드래그로 폴더에 파일을 넣는 기능, 폴더에 들어가는 기능
		 * Image:Graphic Screen 으로 버튼을 드래그하여 이미지 레이아웃을 추가합니다.
		 * File:어떤 기능도 수행하지 않습니다
		 */
		private static final int IMAGE_WIDTH = 110;
		private static final int IMAGE_HEIGHT = 110;
		
		private static final int LAYOUT_WIDTH = 280;
		private static final int LAYOUT_HEIGHT = 120;
		
		//---------------store instance
		
		private final File file;
		private final String[] info;
		private Color backColor = BUTTON_BACK_COLOR_NORMAL;
		
		//-----------------------------
		//creator
		//-----------------------------
		
		private FileInfoButton(File file)// 초기화 작업, 레이아웃 설정, 요소들을 추가, 기능설정을 함.
		{
			String[] Info = data.getFileInformations(file);
			
			info = Info;
			this.file = file;
			
			setLayout(new GridBagLayout());
			setPreferredSize(new Dimension(LAYOUT_WIDTH, LAYOUT_HEIGHT));
			setMaximumSize(new Dimension(LAYOUT_WIDTH, LAYOUT_HEIGHT));
			
			add(getFormatIconPanel(info[PathManager.FORMAT]),StaticTools.setFullGrid(0,0,1,2,0.0f,1.0f));
			add(getLabel(info[PathManager.NAME],15),StaticTools.setFullGrid(1,0,1,1,0.5f,0.5f));
			add(getLabel(info[PathManager.SIZE],15),StaticTools.setFullGrid(1,1,1,1,0.5f,0.5f));
			setClicked();
		}
		//======>linked procedure
			private JPanel getFormatIconPanel(String format)// 파일의 포엣이 이미지/폴더/기타 인지에 따라서 아이콘을 부착해서 건네줌.
			{
				JPanel panel = null;

				if(format.equals(PathManager.FORMAT_IMAGE))
				{
					try 
					{
						final Image image = ImageIO.read(file);
						info[PathManager.SIZE] = ""+image.getWidth(null)+" X "+image.getHeight(null);//(1)
					
						panel = new JPanel()
						{public void paintComponent(Graphics g) 
						{g.drawImage(image, 0, 0, getWidth(), getHeight(), null);}};
					}
					catch(Exception e)//하단 설명 참조(1)
					{
						JOptionPane.showMessageDialog((JFrame)FileConnection.getObject("main frame"), "(-99) Undefined Image Error! : " + info[PathManager.NAME]);
						panel = new JPanel() 
						{public void paintComponent(Graphics g) {g.drawImage(FILE_ICON, 0, 0, null);}};
					}
				}
				else if(format.equals(PathManager.FORMAT_FOLDER))
				{
					panel = new JPanel() 
					{public void paintComponent(Graphics g) {g.drawImage(FOLDER_ICON, 0, 0, null);}};
				}
				else
				{
					panel = new JPanel() 
					{public void paintComponent(Graphics g) {g.drawImage(FILE_ICON, 0, 0, null);}};
				}
			
				panel.setPreferredSize(new Dimension(IMAGE_WIDTH, IMAGE_HEIGHT));
				panel.setMaximumSize(new Dimension(IMAGE_WIDTH, IMAGE_HEIGHT));
			
				return panel;
			}
		/*
		 * Other Why
		 * (1) 알수없는 이유
		 * 이미 PathManager에서 이미지 파일의 권한이 없다면 기타 파일로 취급하게 처리를 해 놓았다.
		 * PathManager 에서 파일의 존재 여부는 이미 확인했다.
		 * 굳이 가능성을 생각해 보자면 파일의 존재 여부확인과 권한 확인이 끝난후 그 짧은 시간 사이에 권한설정이나 파일 삭제를 했다는 것인데 외부적으로는 불가능에 가깝다고 생각한다.
		 * 실행될 것 같진 않지만 문제가 있는 이미지를 알린후 파일로 취급하여 처리한다.
		 */
		//======>linked procedure2
			private JLabel getLabel(String text,final int textSize)//설명 생략
			{
				final JLabel l = new JLabel(text);
				l.setFont(new Font("굴림", Font.PLAIN, textSize));
			
				return l;
			}
		
		//-----------------------------
		//actions
		//-----------------------------
		
		private void setClicked()//하단 설명 참조.
		{
			EventRunnable r = null;
			
			if(info[PathManager.FORMAT].equals(PathManager.FORMAT_IMAGE))
				startDragEvent();
			
			else if(info[PathManager.FORMAT].equals(PathManager.FORMAT_FOLDER))
			{	
				startInsertFolderAction();
				
				r = new EventRunnable() 
				{
					public void run(MouseEvent e) //하단 설명 참조(1)
					{
						if(e.getClickCount() > 1 && e.getButton() == MouseEvent.BUTTON1)
							data.setDirectory(data.getDirectory()+"\\"+info[PathManager.NAME]);
					}};
				}
			
			else
				;
			addMouseListener(getMouseActions(r));
		}
		/*
		 * 기능: 버튼의 기능을 추가합니다.
		 * >> 1.이미지 파일의 버튼일경우..
		 * >> )버튼을 드래그 할 경우 드래그를 전달 받는 화면에 이미지 File 객체의 경로를 전달해 주는 기능을 추가한다.
		 * >> 2.폴더 파일의 경우...
		 * >> )프로그램 외부에서 파일을 마우스로 끌어왔을때 현재 열고있는 경로로 파일을 이동하는 기능을 추가한다.
		 * >> )더블 클릭을 통해서 폴더의 내부로 이동할수 있는 기능을 추가한다.
		 * >> 3.무조건
		 * >> )버튼을 누르고 떼면 버튼의 색상이 변한다.
		 * 
		 * Other Why
		 * (1) Runnable의 작동 경로
		 * >> 개인적으로 쓰고 있는 기술중에 하나인데 안의 내용물이 미묘하게 달라서 리스너들을 일일히 새로 짜야하는 경우들이 있었다.
		 * >> 그래서 공통적인 부분들을 먼저 만들고 차이가 있는 부분들만 따로 인터페이스에 구현해서 원하는 부분에서 e.run()메소드를 실행시키고있다.
		 * >> 이 방법은 내부에서 스택 하나를 더 쌓고 메소드에 매개변수를 집어넣는 과정이 포함되게 되므로 일일히 리스너를 직접 짜주는 것에 비해서 효율과 속도가 느리다는 단점이 있다.
		 * >> 또한 때에 따라서는 작동성의 비효율을 초래하게된다.
		 * >> 아래에 예시가 하나 있다.
		 * >> if(e.getClickCount() > 1 && e.getButton() == MouseEvent.BUTTON1) 이 조건문은 이 기술을 사용하지 않고 정석대로 일일히 짠다면
		 * >> else if(e.getClickCount() > 1) 로 더 효율좋게 바꿔 짤수가 있다.
		 * >> 그럼에도 이 기술을 사용하는 이유는, 유지보수에 탁월한 장점이 있기 때문이다.
		 */
		//======>linked procedure
			private void startDragEvent()//버튼을 드래그하면 마우스 아이콘을 버튼의 이미지로 변환하고 받는 화면에 버튼의 파일을 전달합니다.
			{	
				final DragSource ds = new DragSource();
				DragGestureListener dragListner = new DragGestureListener() 
				{

					@Override
					public void dragGestureRecognized(DragGestureEvent dragDetect) 
					{
						Cursor cursor = null;
						try
						{cursor = Toolkit.getDefaultToolkit().createCustomCursor(ImageIO.read(file), new Point(), "Draged");}
						catch(Exception e) 
						{cursor = Cursor.getDefaultCursor();}//하단 설명 참조(1)
		
						ds.startDrag(dragDetect, cursor, getDragReturn(), 
								new DragSourceListener() 
						{
							public void dragEnter(DragSourceDragEvent dsde) {}
							public void dragOver(DragSourceDragEvent dsde) {}
							public void dropActionChanged(DragSourceDragEvent dsde) {}
							public void dragExit(DragSourceEvent dse) {}
							public void dragDropEnd(DragSourceDropEvent dsde) 
							{
								((JFrame) SwingUtilities.getWindowAncestor(fileList)).setCursor(Cursor.getDefaultCursor());
								backColor = BUTTON_BACK_COLOR_NORMAL;
								repaint();
							}
						});
					}
		
				};
					
				ds.createDefaultDragGestureRecognizer(this,DnDConstants.ACTION_COPY_OR_MOVE,dragListner);
			
			}
			/*
			 * 1.버튼 이미지를 커서이미지로 가져온다 
			 * 2.커서의 이미지로 바꾸고, 드래그할 대상(정해지지 않은)에 버튼파일의 경로를 리턴할것을 예약한다.
			 * 3.드롭이 끝나면 눌러진 버튼의 색을 복구하고 마우스 커서의 이미지도 복구한다.
			 *
			 * Other Why
			 * (1) 알수없는 이유
			 * >> 드래그 했다는 점은 이미 버튼이 확실하게 제대로 있다는 소리이다.
			 * >> 버튼에서 확실하게 이미지에 문제도 없어서 아이콘에 잘 출력 되어있다는 소리이기도 하다.
			 * >> 버튼들은 폴더와 동기화 되어있으므로 버튼들은 변동이 있다면 바로 적용된다.
			 * >> 어떻게 보더라도 여기에서 Exception이 나올 이유가 전혀 존재하지 않는다.
			 * >> IOException이 강제이기 썻지만 작동하면 cursor 가 nullPointerException을 일으키지 않게 하기 위해서 커서 내용을 써 놓았다.
			 * >> 받는 측에서 후처리를 하기 때문에 진행 후에 문제될 거리가 없다.
			 */
			//======>linked procedure
				private Transferable getDragReturn()//파일을 전달한다는 내용을 담은 리스너를 리턴한다.
				{
					return 
					
					new Transferable()
					{
						@Override
						public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException 
						{
							if(flavor.equals(DataFlavor.javaFileListFlavor))//하단 설명 참조(1)
								return file;
							else
								return null;
						}
						
						public DataFlavor[] getTransferDataFlavors() {return null;}
						public boolean isDataFlavorSupported(DataFlavor flavor) {return false;}
					};
				}
				/*
				 * Other Why
				 * (1)if 구절로 조건부 전달을 구성하는 이유
				 * >>여기에서 전달하는 파일은 본래는 Graphic Screen 에 드래그 하여 사용하도록 설계 되어있다.
				 * >>이 프로그램 내에서만 드래그를 사용한다면 문제가 없겠지만 진짜 문제는 이 드래그 기능은 타 프로그램으로도 드래그가 가능하다는 것이다.
				 * >>그래서 이 파일 전달 구성이 다른 프로그램에 적용되었을때 충돌을 최소화 하기 위해 DataFlavor를 통한 제한과 종류를 명확하게 할 필요가 있다.
				 */
		//======>linked procedure2
			private FileDrop startInsertFolderAction()//프로그램 외부에서 파일이 드래그해 왔을때 프로그램 내의 폴더 안으로 파일을 이동하는 리스너를 전달함
			{
				return
						
				new FileDrop(this,new FileDrop.DropedListener()
				{
					@Override
					public void filesDropped(File[] files) 
					{
						StringBuilder appender = new StringBuilder();//하단 참조(1)
						for(File f:files)
						{
							final String fileName = f.getName();
							appender.append(data.getDirectory());
							appender.append("\\");
							appender.append(file.getName());//폴더의 이름
							appender.append("\\");
							appender.append(fileName);//이동하고자 하는 폴더의 이름
							final String dir = appender.toString();
							
							File moveDir = new File(dir);
							for(int i = 2; moveDir.exists();i++)
							{
								appender.delete(0, appender.length());
								final String[] newDir = dir.split("\\.");//String[]{path\fileName, jpeg}
								appender.append(newDir[0]);
								appender.append(i);
								appender.append(".");
								appender.append(newDir[1]);
								moveDir = new File(appender.toString());//path\fileName2.jpeg
							}
							if(!f.renameTo(moveDir))//경로 이동에 실패할 경우 false를 리턴한다.
								JOptionPane.showMessageDialog((JFrame)FileConnection.getObject("main frame"), "(-6) Failed move Image! : " + fileName);
								//파일의 경로와 명을 바꾸려다가 실패한것 이므로 이경우 아직 파일에 아무 동작이 없어 안전하다.
						}
					}
				});
			}
			/*
			* Other Why
			* (1)String + String vs StringBulider.append()
			* >>string+string 는 더 큰 char[]배열을 하나 더 만들고 거기에 추가할 char을 대입한다.
			* >>즉 char은 그대로 두고 작업하기 때문에 메모리도 잡아먹고 속도도 더 느리다.
			* >>이 메소드는 횟수를 측정할수 없어 몇번이 반복될지 모르고 문자열의 문자열 결합을 빠르고 효율적으로 처리하기 위해서 StringBuilder를 사용했다.
			*/
		//======>linked procedure3
			private MouseListener getMouseActions(EventRunnable er)//마우스를 누르고 뗄때 색상 변경,마우스 우클릭으로 삭제판업 띄움, er.run을 추가로 실행하는 리스너를 반환함.
			{
				return
						
				new MouseListener()
				{
					@Override
					public void mouseClicked(MouseEvent e) 
					{
						if (e.getButton() == MouseEvent.BUTTON3)
						{
							focusFile = file;
							popup.show(e.getComponent(),e.getX(),e.getY());
						}
						try
						{er.run(e);}
						catch(Exception ex) {}
					}
					
					@Override
					public void mousePressed(MouseEvent e) 
					{
						backColor = BUTTON_BACK_COLOR_PRESSED;
						repaint();
					}
					@Override
					public void mouseReleased(MouseEvent e) 
					{
						backColor = BUTTON_BACK_COLOR_NORMAL;
						repaint();
					}
					
					@Override
					public void mouseEntered(MouseEvent e) {}
					@Override
					public void mouseExited(MouseEvent e) {}
				};
			}
		
		//-----------------------------
		//Override only
		//-----------------------------
		
		@Override
	    public void paintComponent(Graphics g) //버튼들 사이의 구분자를 그라데이션으로 위아래에 그림
		{
			g.setColor(backColor);
			g.fillRect(0, 0, getWidth(), getHeight());
			g.setColor(Color.lightGray);
			g.drawLine(0, getHeight()-1, getWidth()-1, getHeight()-1);
			final Point point1 = new Point(10,getHeight()-3);
	        final Point point2 = new Point(10,getHeight());
	        final GradientPaint gp = new GradientPaint(
	                point1, backColor,
	                point2, SEPARATOR_GRADATION_COLOR,
	                true);
	        final Graphics2D g2 = (Graphics2D) g;
	        g2.setPaint(gp);
	        g.fillRect(0, getHeight()-3, getWidth(), getHeight());
		}
	}
	
	private abstract class EventRunnable//getMouseActions(),setClicked() / 리스너에 추가적인 작동을 집어 넣기위해서 사용됨.
	{
		public abstract void run(final MouseEvent e);
	}
}

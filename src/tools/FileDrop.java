package tools;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;

import java.awt.dnd.DropTargetListener;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetAdapter;
import java.awt.Color;
import java.awt.Component;
import java.util.List;

import javax.swing.JComponent;

public class FileDrop
{
	/*
	 * 프로그램 외부에서 파일을 끌어 왔을때 이를 처리하는 작업을 합니다.
	 */
    
	private transient Border originalBorder;//transient 는 이 객체를 제이슨화한뒤 직렬화 했을때 이 데이터는 null로 취급하게 한다.?? 필요하지가 않는데?
    private transient DropTargetListener dropListener;
 
    public final static Color DEFAULT_BORDER_COLOR = Color.getColor("blue", 0XFF7b7bFF);
    
    private JComponent targetComponent;
 
    public FileDrop(final Component c, final DropedListener listener) //설명 생략
    {
    	if(!supportsDnD())//Drag and Drop을 지원하지 않는 JDK 버전이라면 
    		return;
    	
    	targetComponent = (JComponent)c;
    	
        dropListener = getDropTargetListener(listener);
        
        new DropTarget(targetComponent, dropListener);
    }
    //======>linked procedure
    	private DropTargetListener getDropTargetListener(final DropedListener listener)//하단 설명 참조
    	{
    		final Border dragBorder = new LineBorder(DEFAULT_BORDER_COLOR,3);
    		
    		return
    				
    		new DropTargetAdapter() 
            {
    			public void dragEnter(DropTargetDragEvent e)
                {
                	originalBorder = targetComponent.getBorder();
                    targetComponent.setBorder(dragBorder);
                    e.acceptDrag(DnDConstants.ACTION_MOVE);//프로그램 외부파일이므로 이용에 권한이 필요하다.
                }
    			
    			public void dragExit(DropTargetEvent e) //마우스가 화면을 벗어났을 때
                {targetComponent.setBorder(originalBorder);}
 
                public void dropActionChanged(DropTargetDragEvent e)//제스쳐 변동시 호출.. 혹시모를 예외처리에 해당하는 메소드.
                {e.acceptDrag(java.awt.dnd.DnDConstants.ACTION_MOVE);}
     
                public void drop(DropTargetDropEvent e) 
                {
                    final Transferable tr = e.getTransferable();
                    
                    try
                    {
                    	if (tr.isDataFlavorSupported(DataFlavor.javaFileListFlavor))//DnD는 외부/내부 기타자료형/파일 을 가리지 않으므로 이게 파일 일 경우로 필터링한다.
                        {
                            e.acceptDrop(DnDConstants.ACTION_MOVE);
                            
                            List<?> fileList = (List<?>) tr.getTransferData(DataFlavor.javaFileListFlavor);//외부의 파일을 드래그할 경우 List<File>이 리턴된다.
         
                            final File[] files = fileList.toArray(new File[fileList.size()]);

                            listener.filesDropped(files);//지정한 컴포넌트에 파일이 드롭 되었을때 작동할 listener 여기에서 처리한다.
                        }
                        else//만약 URI을 드래그 받았다면.
                        {
                            DataFlavor[] flavors = tr.getTransferDataFlavors();

                            for (DataFlavor f : flavors)
                            {
                                if (f.isRepresentationClassReader()) 
                                {
                                    e.acceptDrop(java.awt.dnd.DnDConstants.ACTION_COPY_OR_MOVE );
              
                                    final BufferedReader br = new BufferedReader(f.getReaderForText(tr));
                                             
                                    listener.filesDropped(createFileArray(br));
                                    break;
                                }
                            }
                        }
                    }
                    catch(Exception ex){}//getTransferData 로 리턴 되는 값에 이상이 있을 때 동작을 끝냄 or 프로그램 내부 DnD이거나 파일도 URI도 아닐 경우.
                    finally
                    {
                    	targetComponent.setBorder(originalBorder);
                    	try
                    	{e.rejectDrop();}
                    	catch(Exception ex) {}
                    }
                            
                    e.getDropTargetContext().dropComplete(true);
                }
            };
    	}
    	/*
    	 * 기능: 외부에서 파일을 드롭했을 때 작동할 작업을 건네준다.
    	 * 1.파일을 드래그해오면 타겟 화면에 테두리선을 추가한다.
    	 * 2.마우스가 화면을 나가면 테두리선을 해제한다.
    	 * 3.파일이 드롭되면 드롭된것이 파일인지 확인해서 배열을 생성한뒤 리스너에 작성한 작업을 진행한다.
    	 * 4.드롭이 끝나면 드롭을 해제하고 테두리선을 복구한뒤 끝났음을 알린다.
    	 */
    	//======>linked procedure
        	private final File[] createFileArray(BufferedReader bReader) //URI로 받아온 파일 정보들을 파일로 바꿔서 배열로 보내준다.
        	{
        		final String ZERO_CHAR_STRING = "" + (char) 0;
        	
        		try 
        		{
        			List<File> list = new java.util.ArrayList<>();
                
        			for (String line = null; (line = bReader.readLine()) != null;) 
        			{
        				try 
        				{
        					if (ZERO_CHAR_STRING.equals(line))
        						continue;
     
        					final File file = new File(new java.net.URI(line));
        					list.add(file);
        				} 
        				catch (Exception e) {}
        			}
        			return list.toArray(new File[list.size()]);
        		} catch (IOException ex) {}
            
        		return null;
        	}
 
    private boolean supportsDnD() //설명 생략
    {
    	boolean support = false;
    	try 
    	{
    		Class.forName("java.awt.dnd.DnDConstants");
    		support = true;
        }
        catch (Exception e) 
    	{
        	System.out.println("자바버전 7 이하.. 파일 끌어오기가 적용되지 않습니다.");
        	support = false;
        }
        return support;
    }

    public static void remove(Component c) //설명 생략
    {c.setDropTarget(null);}

 
    public static interface DropedListener //설명 생략
    {
    	public abstract void filesDropped(java.io.File[] files);
    }
 
}
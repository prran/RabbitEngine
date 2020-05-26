package logic;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.util.ArrayList;

import javax.swing.JPanel;

@SuppressWarnings("serial")
public class ReferenceList extends JPanel{
	
	/*
	 * Reference type Double Linked List
	 * 기본적으로 for 에서 : 작업을 사용할수 있어야 하므로 ArrayList를 응용합니다.
	 * 이 개념을 이해하려면 그림을 봐주세요
	 * 
	 * ● = 기존 데이터
	 * ◎ = 순서를 바꿀 데이터
	 * ㅁ = 데이터를 가리기는 객체
	 * 
	 * 기존 리스트:
	 * 
	 * ○=○=○=●=○=◎=○=○=○
	 *   ㅣ    ㅣ
	 *   ㅁ    ㅁ
	 *   
	 * >순서 변경시..
	 * 
	 * ○=○=○=◎=○=●=○=○=○
	 *   ㅣ         /
	 *   ㅁ    ㅁ'
	 *   
	 * 레퍼런스 리스트:
	 * 
	 * ㅇ ㅇ ㅇ  ● ㅇ ◎   ㅇ ㅇ ㅇ
	 * ㅣ ㅣ ㅣ ㅣ  ㅣ ㅣ ㅣ ㅣ ㅣ 
	 * △=△=△=△=△=△=△=△=△
	 *   ㅣ     ㅣ
	 *   ㅁ     ㅁ
	 *   
	 * >순서 변경시..
	 * 
	 * ㅇ ㅇ ㅇ  ◎   ㅇ ● ㅇ ㅇ ㅇ
	 * ㅣ ㅣ ㅣ ㅣ  ㅣ ㅣ ㅣ ㅣ ㅣ 
	 * △=△=△=△=△=△=△=△=△
	 *   ㅣ     ㅣ
	 *   ㅁ     ㅁ
	 */
	
	private Component designated;
	
	private ReferenceList next;
	private ReferenceList before;
	
	private final ArrayList<ReferenceList> priority = new ArrayList<>();
	
	private ReferenceList sleepList;
	private ReferenceList changeTarget;
	
	//-----------------------------
	//getter
	//-----------------------------
	
	public ReferenceList getNextList()
	{return next;}
	
	public ReferenceList getBeforeList()
	{return before;}
	
	public Component getDesignated()//레퍼런스로 지목한 값을 가져옵니다.
	{return designated;}
	
	public int getCount()
	{return priority.size();}
	
	public int getPriority(ReferenceList rList)//리스트의 번지를 받아옴.
	{return priority.indexOf(rList);}
	
	public Component get(final int index)//값을 보내줌.
	{return priority.get(index).getDesignated();}
	
	public Component getReferenceList(final int index)//값의 참조 레퍼런스를 보내줌.
	{return priority.get(index);}
	
	public ArrayList<ReferenceList> getList()//for문에서 사용 가능.
	{return priority;}
	
	//-----------------------------
	//setter
	//-----------------------------
	
	private void setNextList(final ReferenceList next)//getBoxing() 과 removeList() 에서 사용
	{this.next = next;}
	
	private void setBeforeList(final ReferenceList next)//getBoxing() 과 removeList() 에서 사용
	{this.before = next;}
	
	private void designateComp(final Component panel)//getBoxing() 과 changeLocation() 에서 사용
	{designated = panel;}
	
	public void setChangeTarget(final ReferenceList target)//순서를 교환할 대상을 지정함.
	{changeTarget = target;}
	//======>linked method
		public ReferenceList getChangeTarget()
		{return changeTarget;}
	//======>linked method2
		public void resetChangeTarget()
		{changeTarget = null;}
	
	//-----------------------------
	//actions
	//-----------------------------
	
	public ReferenceList getBoxing(Component comp)//새로운 리스트를 추가.
	{
		ReferenceList newBoxing = new ReferenceList();
		try
		{sleepList.setNextList(newBoxing);}
		catch(Exception e){}//가장 첫번째 리스트를 만들경우 호출됨.
		newBoxing.designateComp(comp);//레퍼런스로 인자를 지정
		newBoxing.setBeforeList(sleepList);
		sleepList = newBoxing;//다음 리스트가 추가 될때 까지 리스트 연결을 위해서 데이터를 대기함
		priority.add(newBoxing);
		
		return newBoxing;
	}
	
	public void removeList(final ReferenceList rmList)
	{
		ReferenceList beforeList = rmList.getBeforeList();
		ReferenceList nextList = rmList.getNextList();
		try 
		{beforeList.setNextList(nextList);}
		catch(Exception e) {}
		try
		{nextList.setBeforeList(beforeList);}
		catch(Exception e) {}
		priority.remove(rmList);
		
		sleepList = priority.get(priority.size()-1);
	}
	//======>overload
		public void removeList(final Component rmList)
		{removeList((ReferenceList)rmList);}
	
	public ReferenceList setViewMode()//이 ReferenceList 객체를 통해서 component 를 투영해서 보여줌.
	{
		setLayout(new BorderLayout());
		this.setMaximumSize(new Dimension(designated.getMaximumSize().width,designated.getMaximumSize().height));
		add(designated,BorderLayout.CENTER);
		return this;
	}
	
	public void changeLocation(final ReferenceList change,final ReferenceList change2)//인자로받은 객체와 지정하고 있는 객체의 위치 순서를 바꾼다
	{
		Component designate = change.getDesignated();
		change.remove(designate);
		change.designateComp(change2.getDesignated());
		change.add(change2.getDesignated());
		change2.remove(change2.getDesignated());
		change2.designateComp(designate);
		change2.add(designate);
		change.repaint();
		change2.repaint();
	}
	//=======>Overload
		public void changeLocation(final ReferenceList change)
		{changeLocation(change,changeTarget);}
	


}

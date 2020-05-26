package logic;

public class LockBoolean{
	
	/*
	 * getter를 통해서 특정 객체(여기에서는 boolean)를 건네야 할때
	 * 메소드의 사용자가 도중에 내부내용을 수정할 경우 문제를 초래할수 있다면
	 * 이 객체를 통해서 객체의 수정에 제한을 걸어두는 간단한 로직입니다.
	 */
	
	private String key;
	private boolean lock = true;
	private boolean value;
	
	public LockBoolean(String key)
	{
		this.key = key;
	}
	
	public final void set(boolean value)
	{
		if(!lock) 
		{
			this.value = value;
			lock = true;
		}
	}
	
	public final LockBoolean unlock(String key)
	{
		if(key == this.key)
		lock = false;
		return this;
	}
	
	public boolean get()
	{
		return value;
	}
}

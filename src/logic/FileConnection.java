package logic;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.sqlite.SQLiteConfig;

public class FileConnection {
	
	/*
	 *이 로직은 매개변수와 인자로 인한 복잡함을 줄이기 위해서 설계 되어있습니다. 
	 *크게 두분류의 작업을 합니다. 데이터의 이동과 DB에 관련된 작동들을 제공합니다.
	 *
	 *사용용도:
	 *
	 *A객체가 가지고 있는 인스턴스 i 가 C 라는 객체에서 필요로 한다고 가정해봅시다.
	 *그런데 A와 C가 서로를 넘나드는데엔 B라는 객체가 사이에 끼어져있고
	 *하므로 i를 필요로 하지 않는 B측에서는 인자를 받아서 C에 전달해 주는 역활을 해야 합니다.
	 *특히 이 현상은 A > B > C > D 같이 통로가 길어질수록 더욱 난잡해지는 특징을 지니고 있습니다.
	 *그래서 이를 해결 하는 역활을 하기 위해서 이 Class가 사용됩니다.
	 *Static.setObject("id",object)를 A 클래스에서 정의하고
	 *C 클래스에선 Static.getObject("id")로 객체를 받아옵니다.
	 *
	 *다른 사용법으로 이 메소드를 생성자에 정의하면
	 *해당 객체가 1개 이상 정의가 되어 있는가 아닌가에 대한 확인의 용도로도 활용할 수 있습니다.
	 *
	 *DB 관련 작동은 아직 개발중에 있습니다.
	 */
	private static ArrayList<Runnable> creators;
	
	public final static boolean CLOSE = false;
	public final static boolean OPEN = true;
	
	private final static String DB_KIND = "jdbc:sqlite:";
	private final static String DB_OBJECT = "org.sqlite.JDBC";
	private final static String DB_FILE_NAME = "client_db/metadata.db";
	
	private static DirectGiver giver;
	
	//-----------------------------
	//creator
	//-----------------------------
	
	private FileConnection()//만약 DB를 사용 하고자 한다면 생성자를 정의하고 그것이 아니라면 정의할 필요가 없다.
	{giver = new DirectGiver();}
	
	public static void startConnection()
	{new FileConnection();}
	/*
	 * Other Why
	 * (E)반드시 Static 으로 반드시 정의하면 문제가 있다.
	 * Static 필드에서 객체를 정의 해서 사용하는 행위는 그저 이 소스 파일을 패키지에 지니고 있는것으로
	 * 사용을 하건 안하건 메모리를 반드시 먹고 시작하겠다는 뜻이 될수 있다.
	 * 그래서 이를 방지하기 위해서 메소드로 객체생성을 따로 두고있다.
	 */
	
	public static void startSynCreator()//생성자들의 동기화를 시작한다.
	{creators = new ArrayList<>();}
	/*
	 * Other Why
	 * (E)이 설명은 위의 함수와 동일
	 */
	
	//-----------------------------
	//destroyer
	//-----------------------------
	
	public static void endSynCreator()
	{
		for(Runnable creator:creators)
			creator.run();
		creators = null;
	}
	/*
	 * 기능: 생성자들의 동기화를 적용하고 끝낸다.
	 * 
	 * Other Why
	 * (E)생성자를 동기화할 필요에 대해서..
	 * 이 함수는 생성자의 인자 문제를 해결하기 위해서 사용 한다.
	 * 객체 A가 호출되고 이후에 객체 B가 호출되는 상황에서
	 * 객체 A는 객체 B의 레퍼런스를 null로 받게 되는 문제가 생기는데..
	 * 보통은 객체B를 먼저 만들고 A를 만들어서 해결한다.
	 * 하지만 만일 객체의 사용자가 A를 먼저 쓸지 B를 먼저 쓸지 모르겠는 상황이라면 
	 * 혹은 인자로 넘기는 관계들이 많아서 복잡한 관계에 있다면 이를 일일히 확인해서 순서대로 객체를 생성해야하는데
	 * 이것이 워낙 쉬운일이 아니다 보니 이를 효율적으로 해결하기 위해서 고안된 로직이다.
	 * 객체를 일단 만들고,생성자의 작업들을 이 FileConnection에 모아둔다.
	 * 마지막에 endSynCreator를 호출하면 적용된 객체들이 만들어 진 이후에 관련된 모든 생성자 작업들을 진행한다.
	 */
	
	//-----------------------------
	//setter
	//-----------------------------
	
	public static void addCreator(final Runnable creator)//생성자를 목록에 추가한다.
	{creators.add(creator);}
	
	public static void setDBState(boolean state)//DB를 사용한다면 연결을 열고 닫음.
	{
		if(state)
			giver.open();
		else
			giver.close();
	}
	
	public static void setObject(String id,Object file)
	{giver.addPresent(id,file);}
	
	//-----------------------------
	//getter
	//-----------------------------
	
	public static boolean getDBState()//설명 생략 
	{return giver.isOpened;}
	
	public static List<List<String>> SQL(String SQL, String ... culums)//SQL 구문으로 DB의 상태를 제어하거나 데이터를 가져옵니다.
	{return giver.SQL(SQL, culums);}
	
	public static Object getObject(String id)//객체를 하나 저장해둡니다.
	{return giver.getPresent(id);}
	
	private class DirectGiver
	{
		/*
		 * DB상태를 제어하기 위해서 사용됩니다.
		 * 생성자가 일단 한번 호출된다면 이 클래스를 통해서 객체를 주고 받을 필요없이
		 * Static 메소드를 통해서 어디서든, DB에 접근할 수 있습니다.
		 */
		
		private final HashMap<String,Object> present = new HashMap<>();
		
		private boolean isOpened = false;
		private Connection connection;
		
		private DirectGiver()//설명 생략
		{
			try {Class.forName(DB_OBJECT);}
			catch(Exception e) {}
		}
		
		private boolean open()//설명생략
		{
			try 
			{
				SQLiteConfig config = new SQLiteConfig();
				connection = DriverManager.getConnection(DB_KIND + DB_FILE_NAME, config.toProperties());
				isOpened = true;
			}
			catch(SQLException e){e.printStackTrace(); isOpened = false;}
			return isOpened;
		}
		
		private boolean close()//설명생략
		{
			if(!isOpened)
				return true;
			try 
			{
				connection.close(); 
				isOpened = false; 
				return true;
			}
			catch(SQLException e){e.printStackTrace(); return false;}
		}
		
		private List<List<String>> SQL(String SQL, String ... culums)
		{
			try 
			{
				List<List<String>> data = new ArrayList<>();
				final PreparedStatement prep = connection.prepareStatement(SQL);
				ResultSet row = prep.executeQuery();
				
				for(String get:culums)
				{
					ArrayList<String> culum = new ArrayList<>();
					
					while(row.next())
					{
						culum.add(row.getString(get));
					}
					
					data.add(culum);
				}
				prep.close();
				row.close();
				
				return data;//degree X cardinality 크기의 테이블 정보를 내보낸다.
			}
			catch(SQLException e){return null;}
		}
		
		private void addPresent(String key,Object value)//객체를 저장함
		{present.put(key, value);}
		
		private Object getPresent(String key)//저장한 객체를 내보냄
		{return present.get(key);}
		
		
	}
}

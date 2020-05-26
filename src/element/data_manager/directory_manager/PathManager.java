package element.data_manager.directory_manager;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import logic.FileConnection;

public class PathManager 
{
	/*
	 * 이 클래스는 디렉토리 매니저의 데이터 코어입니다.
	 * 경로와 파일에 관련된 모든 일들을 담당합니다.
	 */
	
	
	private final static String DEFUALT_DIRECTORY = "c:\\";
	private final static String DIRECTORY_SAVED_FILE = "client_db/matadata.sav";
	
	private final static String SEPERATOR_RELATIVE = "/";
	
	
	private final static String CONSTRACTION_STRING = "...";
	private final static int CONSTRACTION_LEFT_LENGTH = 3,
			CONSTRACTION_RIGHT_LENGTH = 10;
	
	private final static int DECIMAL_POINT = 100;//two decimal
	private final static int FILE_NAME_STANDARD_LENGTH = 15;
	
	private final static String BYTE = "byte", KB = " kb", MB = " mb";
	
	public final static String FORMAT_FOLDER = "folder", FORMAT_IMAGE = "image", FORMAT_UNDIFINED = "undefined";
	
	public final static int NAME = 0, SIZE = 1, FORMAT = 2;
	
	
	//---------------stay data because logic
	
	private final int MB1 = 1048575;
	private final float BYTE_PROMOTE_VALUE = 1024f;
	private final StringBuilder appander = new StringBuilder();
	
	//---------------store instance
	
	private final ArrayList<Runnable> handlers = new ArrayList<>();
	private final PathSynWorks synPath = new PathSynWorks();
	
	private String directory;
	private File[] files;
	
	//-----------------------------
	//creator
	//-----------------------------
	
	public PathManager()//프로그램 실행시 디렉토리 매니저의 시작 경로를 읽어 적용하고 그 경로와 동기화함.
	{
		movSavDirectory();
		new Thread(synPath).start();
	}
	//=======>linked procedure
		private void movSavDirectory()//Move Saved Directory. 프로그램을 종료하기 전에 마지막으로 작업했던 경로로 디렉토리 메니저의 경로를 변경
		{
			try 
			{
				final File file = new File(DIRECTORY_SAVED_FILE);//작업저장 기능이 구현되면 여기에 들어갈 값은 저장파일에 있는 경로로 수정할 계획.
				if(file.exists())
				{
					final BufferedReader fr = new BufferedReader(new FileReader(file));
					directory = fr.readLine();
					fr.close();
					
					final File DirFolder = new File(directory);
					
					if(!DirFolder.exists())
						throw new Exception();
					
					files = sort(DirFolder.listFiles());
				}
				else//프로그램 종료 전에 마지막으로 작업했던 경로가 사용자에 의해서 변경되거나 삭제되어 알수 없음.
					throw new Exception();
			}
			catch(Exception e)//1.위의 이유로 경로없음 2.이 프로그램을 처음 실행해서 저장한 경로가 없음 3.접근권한이 없음
			{
				writeDirectory(DEFUALT_DIRECTORY);
				movSavDirectory();
			}
		}
		/*
		 * 1.마지막으로 작업한 최근 경로를 지닌 파일을 가져와서 읽음
		 * 2.얻어낸 경로로 이동하여 그 경로 내부에 있는 파일들을 가져옴
		 * 3.파일들을 기능별로 정렬
		 * >>Why?
		 * >>여기에서 정렬된 순서대로 디렉토리 매니저에 표기하기 위함
		 * >>안할경우 종류 상관없이 가나다 정렬되어 출력이 난잡함
		 * 
		 * 4.경로가 없거나 첫실행이라면 기본 위치 C:\경로로 이동하여 값을 파일에 저장하고 재귀시도.
		 */
	//=======>>linked procedure
			private void writeDirectory(String dir)//파일에 현재 경로를 저장
			{
				try
				{
					BufferedWriter bw = new BufferedWriter(new FileWriter(DIRECTORY_SAVED_FILE));
					bw.write(dir);
					bw.close();
				}
				catch(IOException ex)//BufferedWriter에 의한 Exception. 아마 권한 문제에 의한 쓰기 불가 문제때문에 강제되는것 같으나, 상대경로라서 실행될리 없음.
				{ex.printStackTrace();System.exit(0);}//이게 실행 된다면 그건 jar을 뜯었다는 거고. 그에 대한 프로그램 종료 책임은 뜯어본 본인에게 있음.
			}
			
	//----------------------------
	//getter
	//----------------------------
	public String getDirectory()//설명생략
	{return directory;}
	
	/**
	 * 
	 * @return files in Current Path.
	 */
	public File[] getFiles()//설명생략
	{return files;}
	
	/**
	 * 
	 * you can use this information like this.  
	 * 
	 * stringArrayObject[PathManager.NAME]
	 * 
	 * @param file any file wants get informations
	 * 
	 * @return String Array{file name, file size, file format}
	 */
	public String[] getFileInformations(File file)//파일의 이름,크기,형식을 건네줌
	{
		
		final long dataSize = file.length();
		float size = 0f;
		String byts = BYTE;
		
		if(dataSize<MB1)
		{
			size = (float)Math.round(dataSize / BYTE_PROMOTE_VALUE * DECIMAL_POINT)/DECIMAL_POINT;//값을 구한뒤 소수점 2 자리 까지만 남김. 하단 설명참초(1)(2)
			byts = KB;
		}
		else
		{
			size = Math.round(dataSize/BYTE_PROMOTE_VALUE/BYTE_PROMOTE_VALUE*DECIMAL_POINT)/DECIMAL_POINT;//값을 구한뒤 소수점 2 자리 까지만 남김. 하단 설명참초(1)(2)
			byts = MB;
		}
		String name = file.getName();
		if(name.length()>FILE_NAME_STANDARD_LENGTH)
		{
			appander.append(name.substring(0,CONSTRACTION_LEFT_LENGTH));//하단 설명 참조(3)
			appander.append(CONSTRACTION_STRING);
			appander.append(name.substring(name.length()-CONSTRACTION_RIGHT_LENGTH,name.length()));
			name = appander.toString();
			appander.delete(0,appander.length());
		}
		
		String format = FORMAT_UNDIFINED;//하단 설명 참조(4)
		
		if(file.isDirectory())
			format = FORMAT_FOLDER;
		else if(file.isFile())
		try{format = Files.probeContentType(Paths.get(file.getPath())).split(SEPERATOR_RELATIVE)[0];}
		catch(Exception e) {}//하단 설명 참조(5)
		
		
		
		return new String[] {name, size+byts,format};
	}
	/*
	 * 1.파일의 Byte 사이즈를 받고 이를 KB,MB단위로 정리해서 저장해둠
	 * 2.파일이믈의 글자가 기준 이상 보다 길다면 이름을 적절하게 축약함
	 * >>Why?
	 * >>파일의 이름이 지나치게 길 경우 디렉토리 매니저에 이름을 표기할때 레이아웃이 틀어짐
	 * 3.파일의 형식을 폴더,이미지,미분류 로 분류함
	 * 4.이렇게 얻은 이름,크기,형식을 건네줌
	 * 
	 * Other Why
	 * (1)BYTE_PROMOTE_VALUE, appander(StringBuilder), MB1 를 전역 변수로 둔 이유..
	 * >>이 객채,변수들은 이 메소드에서만 사용되므로 지역변수로 선언하여 메모리 해제가 용이하게 하는것이 맞을 수 있다.
	 * >>하지만 디렉토리 매니저에서 경로를 이동할때 마다 이 메소드가 파일의 숫자만큼 for문으로 호출된다.
	 * >>File[]을 받아 메소드 안에서 처리한다고 하면 같은 for를 불필요하게 두번 돌리게 된다.
	 * >>그래서 차라리 한번 할당하고 해제되지 않도록 하여 속도에 초점을 맞췄다.
	 * 
	 * (2)DECIMAL_POINT(소숫점기준) 을 곱한뒤 다시 나누는 이유
	 * >>데이터 바이트가 2000byte 라하면 ÷ 1024 를한값은 1.953125KB
	 * 여기에 DECIMAL_POINT(100) 를 곱하면 195.3125
	 * 이 값을 Round 로 반올림 195
	 * DECIMAL_POINT(100) 나누면 1.95KB
	 * 
	 * (3)String + String vs StringBulider.append()
	 * >>string은 상수다 변동 불가능하다.
	 * >>string+string 는 더 큰 char[]배열을 하나 더 만들고 거기에 추가할 char을 대입한다.
	 * >>즉 char은 그대로 두고 작업하기 때문에 메모리도 잡아먹고 속도도 더 느리다.
	 * >>이 메소드는 자주 사용될 가능성이 크고 문자열의 3중 +연산 을 빠르고 효율적으로 처리하기 위해서 StringBuilder를 사용했다.
	 * 
	 * (4)FORMAT_UNDIFINED 을 초기에 삽입하는 이유
	 * >>지역변수는 값을 지니지 않으면 없는 것으로 취급된다.
	 * >>이 값이 리턴되려면 FORMAT_UNDIFINED가 아니더라도 null을 할당해야한다.
	 * >>굳이 받드시 할당 작업을 해야한다면 if>else if>else 로 가는 연산을 줄이는 쪽이 더 빠르다고 판단했다.
	 * 
	 * (5)Exception 가능성..
	 * 이때 작업할 파일은 디렉토리에서 직접 존재를 확인하여 가져온 파일이기 때문에 반드시 존재한다.
	 * 문제가 되어 만약에 발생 한다면 권한의 문제인데 다른 프로그램에서 사용중인 파일에 접근하려해서 막힌것이라고 생각된다.
	 * 이 경우엔 사전에 선언한 String format = FORMAT_UNDIFINED; 에 의해서 포멧은 반드시 '기타 파일'로 지정된다.
	 * 이런 상황에서 본래의 포멧을 받아오고자 한다면 그 파일을 사용하고있는 상대방을 죽여야 되는데 시스템 프로세스일경우 심각한 결과를 초래한다.
	 * 즉, 이게 최선일것이다. 
	 */
	
	//-----------------------------
	//setter
	//-----------------------------
	public void setDirectory(final String dir) //새로운 경로를 설정, 경로와 동기화, 최근 경로를 기록, 경로안의 파일 갱신, 화면적용
	{
		directory = dir;
		synPath.refreshPath();//하단 설명 참조(1)
		writeDirectory(dir);
		filesRefresh();
	}
	/*
	 * Other Why
	 * (1) 추가 설명..
	 * synPath.refreshPath() 내부에서 Exception 이 발생하면 기본경로(DEFUALT_DIRECTORY)로 이 메소드가 재귀된다.
	 * 이렇게 될경우 writeDirectory(예외에 의한 경로)>filesRefresh()>writeDirectory(처음 실행하려했던 경로)>filesRefresh() 순으로 실행된다.
	 * filesRefresh() 는 this.directory에 의한 레퍼런스로 작동되므로 문제없이 기본경로를 화면에 나타낼것이다.
	 * 만약 이렇게 기본경로가 호출된 상황에서 프로그램을 종료 후에 실행하면 이전에 문제가 생겼던 '처음 실행하려했던 경로' 로 접근하려 할것이고
	 * 그렇더라도 생성자에서 Exception이 발동하여 기초경로로 다시 재설정되어 실행된다.
	 * ...실행에 결국 문제는 없다. 다만 의미없이 위의 메소드를 두번을 중복 호출되는 문제가 있다.
	 * 그러나 Exception 상황이 절대로 흔한 상황이 아니고, 예외없는 환경 압도적으로 많을 것으로 추정되기 때문에
	 * 이 현상을 잡자고 메소드에 연산을 추가하진 않을 생각이다.
	 */
	
	private void filesRefresh()//setDirectory(),PathSyn.() / 파일을 다시 정렬해서 받아오고 화면에 재출력함.
	{
		files = sort(new File(directory).listFiles());
		
		for(Runnable r:handlers)
			r.run();
	}
	
	/**
	 * @param handler you can insert multiple Interface method when Changed Path or in files 
	 */
	public void setHandler(Runnable works) // Title, FileList로부터 작업을 가져옴.
	{handlers.add(works);}
	/*
	 * 1.FileList로 부터 디렉토리 매니저의 파일목록 출력 화면을 갱신
	 * 2.Title 로 부터 경로 표기 텍스트를 갱신
	 */
	
	//-----------------------------
	//actions
	//-----------------------------
	private File[] sort(final File[] files)// filesRefresh(), movSavDirectory() / 파일 배열을 형식 순으로 재정렬함.
	{
		final ArrayList<File> sortByForder = new ArrayList<>();
		final ArrayList<File> sortByImage = new ArrayList<>();
		final ArrayList<File> sortByFile = new ArrayList<>();
		final List<File> sorted = new ArrayList<File>();
		
		for(File file: files)
		{
			switch(getFileFomat(file))//파일 형식에 따라서 파일을 나눠담음.
			{
			case FORMAT_FOLDER:
				sortByForder.add(file);
				break;
			
			case FORMAT_IMAGE:
				sortByImage.add(file);
				break;
			
			default:
				sortByFile.add(file);
			}
		}
		sorted.addAll(sortByForder);//나눠담은 파일들을 단위별로 리스트에 추가함.
		sorted.addAll(sortByImage);
		sorted.addAll(sortByFile);
		
		return sorted.toArray(new File[sorted.size()]);
	}
	//=======>>linked procedure
		private String getFileFomat(File file)//설명 생략
		{
			String format = FORMAT_UNDIFINED;
			
			if(file.isDirectory())
				format = FORMAT_FOLDER;
			else if(file.isFile())
				try{format = Files.probeContentType(Paths.get(file.getPath())).split(SEPERATOR_RELATIVE)[0];}
			catch(Exception e) {}//하단 설명 참조(1)
		
			return format;
		}
		/*
		 * (1)Exception 가능성..
		 * 이때 작업할 파일은 디렉토리에서 직접 존재를 확인하여 가져온 파일이기 때문에 반드시 존재한다.
		 * 문제가 되어 만약에 발생 한다면 권한의 문제인데 다른 프로그램에서 사용중인 파일에 접근하려해서 막힌것이라고 생각된다.
		 * 이 경우엔 사전에 선언한 String format = FORMAT_UNDIFINED; 에 의해서 포멧은 반드시 '기타 파일'로 지정된다.
		 * 이런 상황에서 본래의 포멧을 받아오고자 한다면 그 파일을 사용하고있는 상대방을 죽여야 되는데 시스템 프로세스일경우 심각한 결과를 초래한다.
		 * 즉, 이게 최선일것이다. 
		 */
		
	//-----------------------------
	//inner class
	//-----------------------------
	
	private class PathSynWorks implements Runnable
	{
		/*
		 * 파일 경로와 동기화합니다.
		 * PathManager 에서만 사용하는 내부 클래스 입니다.
		 * >>Why?
		 * 디렉토리 메니저는 해당 경로 안에 있는 파일들을 보여주고 활용하는 기능입니다.
		 * 하지만 프로그램 실행중에 프로그램 외적으로 그 경로의 사용자에 의해 내용물이 바뀔수 있고
		 * 이를 즉각적으로 확인하여 프로그램 화면에서도 이 정보를 바로 갱신해 주기위해 필요합니다.
		 * 
		 * Other Why
		 * 
		 * 1>PathManager 에서만 사용되는 class 이므로 내부클래스로 두었습니다.
		 */
		public boolean isRun = true;
		private WatchService watchService;
		
		@Override
		public void run()//현재 경로의 정보와 가지고 있는 정보를 동기화함
		{
			
			while(isRun)
			{
				try //(1) 하단 설명참초
				{
					WatchKey key = watchService.take();
							
					List<WatchEvent<?>> list = key.pollEvents(); // (2)
					
					for(WatchEvent<?> e : list)		
					if(e.kind().equals(StandardWatchEventKinds.ENTRY_CREATE)
								||e.kind().equals(StandardWatchEventKinds.ENTRY_DELETE)
										||e.kind().equals(StandardWatchEventKinds.ENTRY_MODIFY))
					{	
						filesRefresh();
						break;
					}
						
					if(!key.reset())
						watchService.close();
				}
				catch(Exception e)
				{
					try
					{
						watchService = FileSystems.getDefault().newWatchService();
						final Path path = Paths.get(directory);
						path.register(watchService,
								StandardWatchEventKinds.ENTRY_CREATE,
								StandardWatchEventKinds.ENTRY_DELETE,
								StandardWatchEventKinds.ENTRY_MODIFY);
					}
					catch(Exception ex)//사용자가 지정한 디렉토리의 권한에 의해서 파일에 접근할 수 없음.
					{
						JOptionPane.showMessageDialog((JFrame)FileConnection.getObject("main frame"), "(-1) File is unauthorized access!");
						setDirectory(DEFUALT_DIRECTORY);
						watchService = null;
					}
				}
			}
		}
		/*
		 *  1.외부 파일의 변동을 확인하는 watchService 객체를 생성하고 생성,삭제,수정 작업에 대한 감시 범위를 지정
		 *  2.디렉토리 경로가 변동 되기 전까지 지속적으로 감시하여 변동이 있을경우 파일을 재구성함.
		 *  3.파일에 접근할수 없다면 경로를 기본경로로 수정함.
		 *  
		 *  Other Why
		 *  
		 *  (1) try의 사용목적
		 *  >> exception 안에 있는 구절은 단 한번만 실행한 뒤 계속해서 다시 실행할 이유가 없는 구절이다.
		 *  >> 이들을 단 한번만 작동하게 하고자 반복문에서 if를 돌리면 조건이 맞는지 안맞는지 매 반복마다 확인하게 되고 이로인해 연산이 느려질 수 밖에없다.
		 *  >> 그래서 작성자가 이를 해결하고자 고민끝에 쓰고있는 개인적인 기술중 하나인데. NullPointerException을 활용하는 것이다.
		 *  >> 컴퓨터에서는 예외가 발생하지 않는한 무조건 try를 실행 할것이고, 이로인해 catch의 구절은 완벽하게 무시될것이다.
		 *  >> 이 스레드를 처음 돌릴시 객체를 할당하지 않은 watchService를 상대로 메소드를 연산하면 NullPointerException이 발생 할것이고
		 *  >> 이후 catch 에서 watchService 객체를 할당한다. 객체가 할당된 뒤에는 null이 아니므로 예외가 발생할리 없고
		 *  >> 이로인해 catch 구절안에 있는 내용들을 전부 무시하고 try 안의 내용만 반복하는 구조이다.
		 *  >> 이 기술은 절대로 Exception 이 발생하지 않을 자신이 있는 구절에서만 사용해야한다.
		 *  >> 실제로 문제가 생겨서 예외가 발생할 경우 예외 발생 지점이 엇나가서 예외 경로를 파고 들어야 하는 심각한 결과를 초래할수 있다.
		 *  (2) 리스트를 받아서 반복문을 사용 하는 이유
		 *  >> 파일 이벤트는 여러개가 동시에 발생한다.
		 *  >> 파일의 삭제를 예시로 들어보면 쉽다.
		 *  >> 파일의 삭제 1단계. 파일을 가리키는 데이터 포인터를 제거함 -> 데이터 수정
		 *  >> for를 사용하지 않는다면 가리키는 포인터가 없는 빈 껍데기 아이콘이 리스트에 출력되어 버리는 문제가 생긴다.
		 *  >> 파일의 삭제 2단계. 포인터가 사라진 껍데기 아이콘을 제거함 -> 데이터 삭제
		 *  >> for문을 통해서 제거가 인식되면 그 이후에 다시 작동하여 깔끔하게 제거된 모습을 볼 수 있다.
		 *  >> 파일의 생성은 이의 '역순'이기 때문에 생성과 삭제만 따로 잡기 위해서 
		 *  >> 이벤트의 발생 순서를 추정하여 list.get(list.size()-1) 로 잡아 오는 것도 안됀다.
		 *  >> 데이터 수정을 잡는 작업은 사용자가 작업 수행중에 포토샵 등으로 이미지를 수정할 수도 있기 때문에 반드시 필요하다.
		 */
		
		public void refreshPath()//thread에 NullPointerException 을 일으켜서 감시할 경로를 다시 지정함
		{watchService = null;}
	}

}

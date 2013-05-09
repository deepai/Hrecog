package utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;

public class SaveFile {

	public static synchronized int WriteFile(String name,HashMap<String,float[]> Map) 
	{
		int t=0;
		try{
			File f=new File(name);
			if(!f.exists())
				f.createNewFile();
			ObjectOutputStream out=new ObjectOutputStream(new FileOutputStream(f,false));
			out.writeObject(Map);
			out.close();
		}catch(Exception e)
		{
			t=1;
		}
		return t;
	}
	public static boolean exists(String name) 
	{
		
			File f=new File(name);
			if(!f.exists())
				return false;
			else
				return true;				
			
	}
}

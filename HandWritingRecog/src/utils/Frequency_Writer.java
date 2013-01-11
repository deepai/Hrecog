package utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.TreeMap;

public class Frequency_Writer {

	static String name="/mnt/sdcard/frequencies.txt";
	public static void write(HashMap<String,IDsampling> frequency,String Class,int type) throws IOException
	{
		TreeMap<String,IDsampling> frequencySorted=new TreeMap<String,IDsampling>();
		frequencySorted.putAll(frequency);
		File t=new File(name);
		if(!t.exists())
			t.createNewFile();
		FileWriter out=new FileWriter(t,true);
		if(type==1)
			out.append("\nFrequency corresponding to Class for DTW distance"+Class+"\n");
		else
			out.append("\nFrequency corresponding to Class for Euclidean Distance"+Class+"\n");
		for(String Ids:frequencySorted.keySet())
		{
			IDsampling temp=frequency.get(Ids);
			out.append(Ids+" = "+temp.occurance);
			for(int i=0;i<temp.positions.size();i++)
				out.append(" :"+temp.positions.get(i)+",");
			out.append("\n");			
		}
		out.close();
		
	}
}

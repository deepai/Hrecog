package utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;

public class filewriter {
	

	public static void writefile(double[][] inputarray,int N,int M,String Class,String filename) throws IOException
	{
		File file=new File(filename);
		if(!file.exists())
			file.createNewFile();
		FileWriter f=new FileWriter(file,true);
		BufferedWriter br=new BufferedWriter(f);
		
		br.append("\n\n");
		br.append("DTW matrix corresponding to "+Class+"\n");
		for(int i=1;i<N;i++)
		{
			for(int j=1;j<M;j++)
			{
			 DecimalFormat df = new DecimalFormat("#.##");
				br.append(df.format(inputarray[i][j])+" ");
			}
			br.append("\n");
		}
		
		br.close();
		f.close();		
	}
}

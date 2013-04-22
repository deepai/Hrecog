package com.example.handwritingrecog;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.xml.transform.sax.TemplatesHandler;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import utils.SaveFile;
import utils.Strokesloader;

import Centroid.*;
import Character_Stroke.Character_Stroke;

public class Matcher {
	
	String fCentroid="/mnt/sdcard/LUTCentroids.dat"; //file for centroid
	String fCharStrokes="/mnt/sdcard/LUTCharStrokes.dat"; 
	String fLutback="/mnt/sdcard/LUTBack.dat"; //for LUT backCharacter
	String UserSelStrokeSeq; //Store User Selected Stroke here
	String UserSelCharacter; //Store user Selected Character here
	Context ct;
	HashMap<String,ArrayList<StrokeCentroid>> LUTCentroid; //centroids of all the Characters Strokes
	HashMap<String,ArrayList<String>> LUTback; 
	HashMap<String,ArrayList<Character_Stroke>> LUTCharStrokes; //for thumbnail of each charactertype
	HashMap<String,float[]> Strokes;
	ArrayList<float[]> InputCharacter; //Userdrawn Character
	ArrayList<String> StrokeSequence;
	Set<String> keys;
	private ArrayList<float[]> UserInputCentroid=new ArrayList<float[]>(); //to Store the centroid of the userInput
	public Matcher(HashMap<String, ArrayList<Character_Stroke>> characterStrokes,HashMap<String,float[]> strokes,Context ct) throws Exception {
		// TODO Auto-generated constructor stub
			LUTback=Strokesloader.loadbackwardLUT(fLutback);
			LUTCentroid=Strokesloader.LoadCentroids(fCentroid);
			LUTCharStrokes=characterStrokes;
			Strokes=strokes;
			keys=Strokes.keySet();
			this.ct=ct;
			
	}
	public void StrokeMatchnonCentroid(String inputSequence,ArrayList<float[]> ip)
	{
		InputCharacter=ip;
		String[] keysInput=inputSequence.split(" ");
		
		ArrayList<String> Strokesname=new ArrayList<String>(); //to store all the strokes;
		for(int i=0;i<keysInput.length;i++) //add all the Strokes
		{
			Iterator<String> itr=keys.iterator();
			while(itr.hasNext())
			{
				String key=itr.next();
				String a=CharLUT.getStrokename(key);
				String b=CharLUT.getStrokename(keysInput[i]);
				if((a!=null && b!=null) &&(a.equals(b)) && a.length()!=0 && a.length()!=0) 
				{
					Strokesname.add(key);  //add to strokesname
				}
				
			}
		}
		new recogniser().execute(Strokesname); //execute the mapping
		
	}
	public ArrayList<String> NumStrokesSeq(String selChar,int usernumStroke) //return the number of Strokesequences matching user made Character
	{
		ArrayList<String> strokeseq=new ArrayList<String>();
		StrokeSequence=LUTback.get(selChar);
		for(String s:StrokeSequence)
		{
			if(s.split(" ").length==usernumStroke)
			{
				strokeseq.add(s);
			}
		}
		return strokeseq;
	}
	public String getSingleStrokeName(String Character) //get the StrokeName corresponding to the character(//for single stroke)
	{
		ArrayList<String> Sequences=LUTback.get(Character);
		String SingleStroke = null;
		for(String k:Sequences)
		{
			if(!k.contains(" "))
			{
				SingleStroke=k;
			}
		}
		return SingleStroke;
	}

	public ArrayList<Character_Stroke> StrokeMatch(String inputStrokeSeq,ArrayList<float[]> userinputStrokes)  //Inorder to Match the userStroke
	{
		UserInputCentroid.clear();// clearing the centroidStrokes
		UserSelStrokeSeq=inputStrokeSeq; //set the UserSelectedStroke to true;
		InputCharacter=userinputStrokes; 

/*********************************************CENTROID CALCULATION**************************************/
		for(int i=0;i<InputCharacter.size();i++)
		{
			
			float[] stoke=InputCharacter.get(i);
			/*
			 * calculate the centroid of the point
			 */
			float[] centroid=new float[2];
			for(int z=0;z<stoke.length;z+=2)
			{
				centroid[0]+=stoke[z];
			}
			for(int z=1;z<stoke.length;z+=2)
			{
				centroid[1]+=stoke[z];
			}
			centroid[0]=centroid[0]/((stoke.length/2));
			centroid[1]=centroid[0]/((stoke.length/2));
			/*
			 * end centroid computation
			 */
			UserInputCentroid.add(centroid); //add the centroid obtained
		    				
		}
		/*********************************************CENTROID CALCULATION ENDS**************************************/

		UserInputCentroid=scaleCentroid(UserInputCentroid); /*Scale Centroids*/
		
		ArrayList<StrokeCentroid> matchedStroke=LUTCentroid.get(UserSelStrokeSeq); //obtain the corresponding Centroid parametre
		ArrayList<float[]> SampleC=new ArrayList<float[]>(matchedStroke.size());
		ArrayList<Character_Stroke> userinput=new ArrayList<Character_Stroke>();
		for(StrokeCentroid t:matchedStroke)
		{
			SampleC.add(t.getCentroid());
		}
		
		int[] mapping=strokeMapping2(InputCharacter,SampleC); //get the mapping of the inputStrokes to the output Strokes
		for(int i=0;i<mapping.length;i++)
		{
			Character_Stroke temp=new Character_Stroke(InputCharacter.get(i));
			temp.setStroke_label(matchedStroke.get(mapping[i]).getStrokelabel());
			userinput.add(temp);
		}
		return userinput;
		
	}
	int[] strokeMapping(ArrayList<float[]> inputC,ArrayList<float[]> sampleC) //mapping the corresponding Strokes 
	{
		int i, j, pos;
		float x1, y1, x2, y2;
		float D, Dmin;
		int N = inputC.size();
		int matches[] = new int[N];	
		boolean matchesdone[]=new boolean[N];
		
		for(i=0; i< N; i++)
		{
			pos=0;
			Dmin = Float.MAX_VALUE;
			x1 = inputC.get(i)[0];
			y1 = inputC.get(i)[1];
			for(j=0; j<N; j++)
			{
				
				if(matchesdone[j]==true) //if already matched
					continue;
				
				x2 = sampleC.get(j)[0];
				y2 = sampleC.get(j)[1];
				
				//System.out.println("input:"+i;
				D = (x1-x2)*(x1-x2) + (y1-y2)*(y1-y2);
				if( D < Dmin )
				{
					Dmin = D;
					pos = j;
				}
			}
			matches[i] = pos;
			matchesdone[pos]=true; //matched,wont be taken in consideration next time
			
		}
		return matches;
	}
	
	static int[] strokeMapping2(ArrayList<float[]> inputC,ArrayList<float[]> sampleC) //mapping using slope-intercept
	{
		int i, j, pos;
		float x1, y1, x2, y2, m, c;
		float D, Dmin;
		int N = inputC.size();
		float Dist[][] = new float[N][N];
		
		float input_dual[][] = new float[N][2];
		float sample_dual[][] = new float[N][2];
		int matches[] = new int[N];	
		
		//boolean matchesdone[]=new boolean[N];
		
		//Dual of lines(Points to (200,200) in 400x400 scaled grid)
		for(i=0; i< N; i++)
		{
			x1 = inputC.get(i)[0];
			y1 = inputC.get(i)[1];
			m = (200-y1)/(200-x1); // slope m = (x2-x1)/(y2-y1)
			c = y1 - (m*x1);
			input_dual[i][0] = m;
			input_dual[i][1] = -c;
			
			x1 = sampleC.get(i)[0];
			y1 = sampleC.get(i)[1];
			m = (200-y1)/(200-x1); 
			c = y1 - (m*x1);
			sample_dual[i][0] = m;
			sample_dual[i][1] = -c;
		}
		
		//finding closest match to input strokes
		pos = 0;
		
		for(i=0; i< N; i++)
		{
			x1 = input_dual[i][0];
			y1 = input_dual[i][1];
			Dmin = Float.MAX_VALUE;
			
			for(j=0; j<N; j++)
			{
				/*if( sample_dual[j][0] == Float.NaN) //if already matched
					continue;*/
				x2 = sample_dual[j][0];
				y2 = sample_dual[j][1];
				//D = (x1-x2)*(x1-x2) + (y1-y2)*(y1-y2);
				D = Math.abs(x2 - x1);
				if( D < Dmin )
				{
					Dmin = D;
					pos = j;
				}
				Dist[i][j] = D;
			}
			matches[i] = pos;
			//sample_dual[pos][0] = Float.NaN;
		}
		
		for(i=0; i< N;i++)
		{
			for(j=0; j<N; j++)
				System.out.print(Dist[i][j]+" ");
			System.out.println();
		}
		return matches;
	}
	
	
	public ArrayList<float[]> scaleCentroid(ArrayList<float[]> arg)
	{
		float scale;
		float max_x=0;
		float max_y=0;
		float min_x=0;
		float min_y=0;
		/*
		 * find the maximum bottom right corner
		 */
		for(int j=0;j<arg.size();j++)
		  	{
			  if(arg.get(j)[0]>max_x)
				  max_x=arg.get(j)[0];
			  if(arg.get(j)[1]>max_y)
				  max_y=arg.get(j)[1];
		  	}
		/*
		 * find the minimum topleft
		 */
		  	min_x=max_x;
		  	min_y=max_y;
		  	for(int i=0; i<arg.size(); i++)
		  	{
		  		if(arg.get(i)[0]<min_x)
		  			min_x=arg.get(i)[0];
		  		if(arg.get(i)[1]<min_y)
		  			min_y=arg.get(i)[1];
		  	}
		  
			float width = max_x - min_x;
			float height = max_y - min_y; 
		
			//obtain the scale
			 scale = Math.max(width,height)/400;
			 for(int i=0; i<arg.size(); i++)
				 {
				arg.get(i) [0]/=scale; //x coordinate
				arg.get(i) [1]/=scale; //y coordinate
				 }
			 
			 min_x=Float.MAX_VALUE;
			 min_y=Float.MAX_VALUE;
			 
			for(int i=0; i<arg.size(); i++)
		  	{
		  		if(arg.get(i) [0]<min_x)
		  			min_x=arg.get(i) [0];
		  		if(arg.get(i) [1]<min_y)
		  			min_y=arg.get(i) [1];
		  	}
			/*
			 * TRANSLATE HERE,IF SINGLE STOKE then scaled to Infinity,
			 */ 
			//translate all points wrt min_x,min_y
			 for(int i=0; i<arg.size(); i++)
		 {
			arg.get(i) [0]-=min_x;
			arg.get(i) [1]-=min_y;
		 }			
			
			 
		return arg;
	}
	
	class recogniser extends AsyncTask<ArrayList<String>,Void,String> ///to perform recognition for the following Stroke
	{

		@Override
		protected String doInBackground(ArrayList<String>... params) {
			// TODO Auto-generated method stub
			//params is the Stroke numbers to be mapped
			String result;
			String Strokesadded="";
			try{
			for(int i=0;i<InputCharacter.size();i++)
			  {
				 double minValue=Double.MAX_VALUE;
				 String ClassRecognizedMin = null;
				 
				 for(int j=0;j<params[0].size();j++)
				 {
					 String tempClass=params[0].get(j); //temporary class name
					 double score=DTWRecogniser.DTWDistance(InputCharacter.get(i),Strokes.get(tempClass));
					 if(minValue>score)
					 {
						 minValue=score; //set as minimum score
						 ClassRecognizedMin=tempClass; //set as minimum Score corresponding class
					 }
					
				 }
				 Strokesadded+=" "+ClassRecognizedMin; //the main class required
				 //add the Stroke in the Strokes;
				Strokes.put(CharLUT.getStrokename(ClassRecognizedMin)+"_x",InputCharacter.get(i));				
			  }
			SaveFile.WriteFile("/mnt/sdcard/Library.dat",Strokes);
			result="successfully added "+Strokesadded;
			}catch(Exception e)
			{
				result=e.toString();
			}
			return result;
		}
		
		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			Toast.makeText(ct,result, Toast.LENGTH_SHORT).show();
		}
	}
	
	
	
}

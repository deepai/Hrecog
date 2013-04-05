package com.example.handwritingrecog;
import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.transform.sax.TemplatesHandler;

import utils.Strokesloader;

import Centroid.*;
import Character_Stroke.Character_Stroke;

public class Matcher {
	
	String fCentroid="/mnt/sdcard/LUTCentroids.dat"; //file for centroid
	String fCharStrokes="/mnt/sdcard/LUTCharStrokes.dat"; 
	String fLutback="/mnt/sdcard/LUTBack.dat"; //for LUT backCharacter
	String UserSelStrokeSeq; //Store User Selected Stroke here
	String UserSelCharacter; //Store user Selected Character here
	HashMap<String,ArrayList<StrokeCentroid>> LUTCentroid; //centroids of all the Characters Strokes
	HashMap<String,ArrayList<String>> LUTback; 
	HashMap<String,ArrayList<Character_Stroke>> LUTCharStrokes; //for thumbnail of each charactertype
	ArrayList<float[]> InputCharacter; //Userdrawn Character
	ArrayList<String> StrokeSequence;
	private ArrayList<float[]> UserInputCentroid=new ArrayList<float[]>(); //to Store the centroid of the userInput
	public Matcher(HashMap<String, ArrayList<Character_Stroke>> characterStrokes) throws Exception {
		// TODO Auto-generated constructor stub
			LUTback=Strokesloader.loadbackwardLUT(fLutback);
			LUTCentroid=Strokesloader.LoadCentroids(fCentroid);
			LUTCharStrokes=characterStrokes;
			
		
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

	public ArrayList<Character_Stroke> StrokeMatch(String inputStrokeSeq,ArrayList<float[]> userinputStrokes)  //Inorder to Match the userStroke
	{
/*************************************conditions for a single Stroke Character**************************/
		if(userinputStrokes.size()==1)
		{
			float[] temp=userinputStrokes.get(0);
			String name=LUTCharStrokes.get(inputStrokeSeq).get(0).getStroke_label();//get the stroke label of the first string
			ArrayList<Character_Stroke> tempSingleStroke=new ArrayList<Character_Stroke>();
			tempSingleStroke.add(new Character_Stroke(temp));
			tempSingleStroke.get(0).setStroke_label(name);
			return tempSingleStroke;
		}
/*************************************end conditions for a single Stroke Character**************************/		
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
		
		int[] mapping=strokeMapping(InputCharacter,SampleC); //get the mapping of the inputStrokes to the output Strokes
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
	
	
	
	
	
}

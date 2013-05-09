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
	
	String UserSelStrokeSeq; //Store User Selected Stroke here
	String UserSelCharacter; //Store user Selected Character here
	Context ct;
	HashMap<String,ArrayList<String>> LUTback; 
	HashMap<String,ArrayList<Character_Stroke>> LUTCharStrokes; //for thumbnail of each charactertype
	HashMap<String,float[]> Strokes;
	ArrayList<float[]> InputCharacter; //Userdrawn Character
	ArrayList<String> StrokeSequence;
	Set<String> keys;
	public Matcher(HashMap<String, ArrayList<Character_Stroke>> characterStrokes,HashMap<String,float[]> strokes,Context ct,HashMap<String,ArrayList<String>> lutback) throws Exception {
		// TODO Auto-generated constructor stub
			LUTback=lutback;
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
				if((a!=null && b!=null) &&(a.equals(b)) && a.length()!=0 && b.length()!=0) 
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

	
	class recogniser extends AsyncTask<ArrayList<String>,Void,String> ///to perform recognition for the following Stroke
	{
		int MAX_X = 5;
		public void LRUReplace(String strokeClass, float[] strokePoints, HashMap<String, float[]> strokeMap)
		{
			boolean x_present = false;
			int maxNum = 0, num = 0;
			
			// Read in serialised object file Library.dat (the stroke library)
			
			for (String key: strokeMap.keySet()) 
			{
				//System.out.println(key + ": "+key.indexOf(strokeClass+"_x"));
				
				if( key.indexOf(strokeClass+"_x") != -1) //user-made stroke 
				{
					//System.out.println(key);
					
					if( x_present == false)
						x_present = true;
					
					num = Integer.parseInt(key.substring(key.indexOf("x")+1)); //serial number
					if( num > maxNum)
						maxNum = num;
				}
				
			}
			
			//System.out.println("maxNum = "+maxNum);
			
			//a. This is the first user-made sample to be added 
			//OR b. if more than MAX_X, then replace least recently added stroke. i.e. _x1
			//N.B. - numbering starts from 1
			if(x_present == false || maxNum >= MAX_X )
			{
				strokeMap.put(strokeClass+"_x1", strokePoints);
				
			}
			else
			{
				strokeMap.put(strokeClass+"_x"+(maxNum+1), strokePoints);
			}
				
		}

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
				 LRUReplace(CharLUT.getStrokename(ClassRecognizedMin),InputCharacter.get(i),Strokes);
				//Strokes.put(CharLUT.getStrokename(ClassRecognizedMin)+"_x",InputCharacter.get(i));				
			  }
			SaveFile.WriteFile("/mnt/sdcard/HWREcogfiles/Library.dat",Strokes);
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

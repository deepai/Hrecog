package com.example.handwritingrecog;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;


public class CharLUT
{
	HashMap<String,String> LUTforward;
	String strokeSeq;
	String charClass;
	String correctedCharClass;
	String correctedStrokeSeq;
	
	/**
	 * public constructor of class CharLUT
	 * @param HashMap<String,String> LUTfwd
	 */
	public CharLUT(HashMap<String,String> LUTfwd)
	{
		LUTforward = LUTfwd;
	}
	
	/**
	 * @param ArrayList<String> stroke_seq
	 * @return String character class
	 */
	public String getValue(String[] stroke_seq)
	{
			Arrays.sort(stroke_seq,new compare());
			String s=" ";
			for(int i=0;i<stroke_seq.length;i++)
			{
				s+= (stroke_seq[i]+" ");
			}
			return (LUTforward.get(s.trim()));			

	}
	
	/**
	 * @param String correctCharClass from user's correction
	 * @return String character class
	 */
	public void setCorrectCharClass(String correctClass)
	{
		correctedCharClass = correctClass;
	}
	class compare implements Comparator<String>
	{

		@Override
		public int compare(String arg0, String arg1) {
			// TODO Auto-generated method stub
			if(Integer.parseInt(arg0)<Integer.parseInt(arg1))
				return -1;
			else
				return 1;
		}
		
	}
	
}

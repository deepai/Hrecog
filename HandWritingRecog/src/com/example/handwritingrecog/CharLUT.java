package com.example.handwritingrecog;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;


public class CharLUT
{
	public HashMap<String,String> LUTforward;
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
		
		/*
		 * //function to get all the numerical value of the Strokes and discard the suffixes
		 * ex. 2a 3b 4c becomes 2 3 4
		 */
			String defaultvalue="";
			Arrays.sort(stroke_seq);
			String s=" ";
			for(int i=0;i<stroke_seq.length;i++)
			{
				
				String temp=stroke_seq[i];
				int j=0;
				while(j<temp.length())
				{
					if(Character.isDigit(temp.charAt(j))) //check if the character is a Digit
						s+=temp.charAt(j);
					j++;
				}
				s+=" ";
			}
			return (arrange(s.trim()));			

	}
	
	/**
	 * @param String correctCharClass from user's correction
	 * @return String character class
	 */
	public void setCorrectCharClass(String correctClass)
	{
		correctedCharClass = correctClass;
	}
	public String arrange(String input)
	{
				String[] tempStringArray=input.split(" ");
				Arrays.sort(tempStringArray);
				String tempString="";
				for(String t:tempStringArray)
				{
					tempString+=t+" ";
				}
				return tempString.trim();
	}
	
}

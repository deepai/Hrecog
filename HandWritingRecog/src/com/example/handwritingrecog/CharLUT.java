package com.example.handwritingrecog;

import java.util.ArrayList;
import java.util.Collections;
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
	public String getValue(ArrayList<String> stroke_seq)
	{
		Collections.sort(stroke_seq);
		String arr = (String) stroke_seq.toString();
		String key_string = arr.replaceAll("(^\\[|\\]$)", "").replace(", ", " ");
		strokeSeq = key_string;
		charClass = LUTforward.get(key_string);
		return charClass;
	}
	
	/**
	 * @param String correctCharClass from user's correction
	 * @return String character class
	 */
	public void setCorrectCharClass(String correctClass)
	{
		correctedCharClass = correctClass;
	}
	
}

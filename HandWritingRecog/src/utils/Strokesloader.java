package utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.StreamCorruptedException;
import java.util.ArrayList;
import java.util.HashMap;

import Centroid.StrokeCentroid;
import Character_Stroke.Character_Stroke;

public class Strokesloader {
	
	public static HashMap<String,float[]> loadStrokes(String filename) throws Exception 
	{
		ObjectInputStream oin;
		oin=new ObjectInputStream(new FileInputStream(new File(filename)));
		HashMap<String,float[]> readObject = ((HashMap<String,float[]>) oin.readObject());	
		oin.close();
		return readObject;
	}
	
	public static HashMap<String,String> loadForwardLUT(String filename) throws Exception 
	{
		ObjectInputStream oin;
		oin=new ObjectInputStream(new FileInputStream(new File(filename)));
		HashMap<String,String> readObject = ((HashMap<String,String>) oin.readObject());	
		oin.close();
		return readObject;
	}
	
	public static HashMap<String,String> loadbackwardLUT(String filename) throws Exception 
	{
		ObjectInputStream oin;
		oin=new ObjectInputStream(new FileInputStream(new File(filename)));
		HashMap<String,String> readObject = ((HashMap<String,String>) oin.readObject());	
		oin.close();
		return readObject;
	}
	
	public static HashMap<String,ArrayList<StrokeCentroid>> LoadCentroids(String filename) throws  Exception
	{
		ObjectInputStream oin;
		oin=new ObjectInputStream(new FileInputStream(new File(filename)));
		HashMap<String,ArrayList<StrokeCentroid>> readObject = (HashMap<String,ArrayList<StrokeCentroid>>) oin.readObject();	
		oin.close();
		return readObject;
	}
	public static HashMap<String,ArrayList<Character_Stroke>> loadStrokesClass(String filename) throws Exception 
	{
		ObjectInputStream oin;
		oin=new ObjectInputStream(new FileInputStream(new File(filename)));
		HashMap<String,ArrayList<Character_Stroke>> readObject = ((HashMap<String,ArrayList<Character_Stroke>>) oin.readObject());	
		oin.close();
		return readObject;
	}
	
	
	
}

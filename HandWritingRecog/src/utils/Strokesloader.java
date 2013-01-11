package utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;

public class Strokesloader {
	
	public static ArrayList<StrokeClass> loadStrokes(String filename) throws Exception 
	{
		ObjectInputStream oin;
		oin=new ObjectInputStream(new FileInputStream(new File(filename)));
		ArrayList<StrokeClass> readObject = ((ArrayList<StrokeClass>) oin.readObject());	
		oin.close();
		return readObject;
	}
	
}

package com.example.handwritingrecog;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.HashMap;

import Centroid.StrokeCentroid;
import Character_Stroke.Character_Stroke;
import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.TextView;

public class splashScreen extends Activity {
	
	AssetManager assets;
	ProgressBar mprogress;
	TextView text;
	ObjectInputStream inp;
	HashMap<String,float[]> Strokes;
	HashMap<String,ArrayList<String>> backwardLUT;
	HashMap<String,String> forwardLUT;
	HashMap<String,ArrayList<StrokeCentroid>> LUTCentroids;
	HashMap<String,ArrayList<Character_Stroke>> LUTCharStrokes;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		mprogress=(ProgressBar) findViewById(R.id.progressBar1);
		mprogress.setMax(5);
		mprogress.setProgress(0);
		text=(TextView) findViewById(R.id.textView1);
		assets=getAssets();
		
		try {
			inp=new ObjectInputStream(assets.open("Library.dat"));
			Strokes=(HashMap<String, float[]>) inp.readObject();
			inp.close();
			mprogress.setProgress(1);
			text.setText("Loaded Library.dat");
			
			inp=new ObjectInputStream(assets.open("LUTback.dat"));
			backwardLUT=(HashMap<String, ArrayList<String>>) inp.readObject();
			inp.close();
			mprogress.setProgress(2);
			text.setText("Loaded backwardLUT.dat");
			
			inp=new ObjectInputStream(assets.open("LUTcentroids.dat"));
			LUTCentroids=(HashMap<String, ArrayList<StrokeCentroid>>) inp.readObject();
			inp.close();
			mprogress.setProgress(3);
			text.setText("Loaded Centroid.dat");
			
			inp=new ObjectInputStream(assets.open("LUTCharStrokes.dat"));
			LUTCharStrokes=(HashMap<String, ArrayList<Character_Stroke>>) inp.readObject();
			inp.close();
			mprogress.setProgress(4);
			text.setText("Loaded CharStrokes.dat");
			
			inp=new ObjectInputStream(assets.open("LUTLex.dat"));
			forwardLUT=(HashMap<String, String>) inp.readObject();
			inp.close();	
			mprogress.setProgress(5);
			text.setText("Loaded forwardLUT.dat");
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Intent i = new Intent(getApplicationContext(), Recogniser.class);
		i.putExtra("LIBRARY",Strokes); //library of strokes
		i.putExtra("LUTforward",forwardLUT); //forwardLUT
		i.putExtra("LUTbackward",backwardLUT); //Backward Lut
		i.putExtra("LUTCentroids",LUTCentroids); //Centroids
		i.putExtra("LUTCharStrokes",LUTCharStrokes); //CharStrokes
		
		startActivity(i);
	}
}

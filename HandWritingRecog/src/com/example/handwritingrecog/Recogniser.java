package com.example.handwritingrecog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import com.example.handwritingrecog.DTWRecogniser;
import preprocessing.Scaling;
import preprocessing.smoothing;

import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Toast;
import android.app.Activity;
import android.gesture.Gesture;
import android.gesture.GestureOverlayView;
import android.gesture.GestureOverlayView.OnGesturePerformedListener;

public class Recogniser extends Activity {

	HashMap<String,float[]> Strokes;
	CharLUT LutMatcher;
	GestureOverlayView mv;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recogniser);
        try {
    		Strokes=utils.Strokesloader.loadStrokes("/mnt/sdcard/outputMean.dat");
    		LutMatcher=new CharLUT(utils.Strokesloader.loadForwardLUT("/mnt/sdcard/Lutforward.dat"));
    		//Toast.makeText(getApplicationContext(), Strokes.size(),Toast.LENGTH_SHORT).show();
    	} catch (Exception e1) {
    		// TODO Auto-generated catch block
    		e1.printStackTrace();
    	}
        
        
        mv=(GestureOverlayView) findViewById(R.id.gestureOverlayView1);
        mv.addOnGesturePerformedListener(new OnGesturePerformedListener() {
			
			@Override
			public void onGesturePerformed(GestureOverlayView arg0, Gesture arg1) {
				// TODO Auto-generated method stub
				 
				ArrayList<float[]> UserDrawnStroke=new ArrayList<float[]>(arg1.getStrokesCount()); //create an arraylist to temporary hold the float arrays
				for(int i=0;i<arg1.getStrokesCount();i++)
				{
					 float[] temp=arg1.getStrokes().get(i).points; //	float points of the gesture		
					 temp=Scaling.scale(temp); // Apply Scaling
					 temp=smoothing.smoothFunction(temp); //apply Smoothing 
					 UserDrawnStroke.add(temp);
				}				 
				performRecognition Recogniser=new performRecognition();
				Recogniser.execute(UserDrawnStroke);
			}
		});
        
    }
    class performRecognition extends AsyncTask<ArrayList<float[]>,Void,String>
    {

		@Override
		protected String doInBackground(ArrayList<float[]>... params) {
			
			String finalCharacterClass=null;
		  ArrayList<String> RecognizedStrokes=new ArrayList<String>(params[0].size());
		  Set<String> libraryClassesKeys=Strokes.keySet(); //obtain the keys
		  
		  for(int i=0;i<params[0].size();i++)
		  {
			 double minValue=Double.MAX_VALUE;
			 String ClassRecognizedMin = null;
			 for(String tempClass:libraryClassesKeys)
			 {
				 double score=DTWRecogniser.DTWDistance(params[0].get(i),Strokes.get(tempClass));
				 if(minValue>score)
				 {
					 minValue=score; //set as minimum score
					 ClassRecognizedMin=tempClass; //set as minimum Score corresponding class
				 }
				 RecognizedStrokes.add(ClassRecognizedMin);	
				 finalCharacterClass=LutMatcher.getValue(RecognizedStrokes);
			 }
		  }
			
			return finalCharacterClass;
		}
		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();
		}
    	
    }
   
    
    
 }
   



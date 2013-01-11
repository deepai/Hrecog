package com.example.handwritingrecog;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import com.example.handwritingrecog.DTWRecogniser;
import com.example.handwritingrecog.Stroke;


import preprocessing.Scaling;
import preprocessing.smoothing;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.app.Activity;
import android.gesture.Gesture;
import android.gesture.GestureOverlayView;
import android.gesture.GestureOverlayView.OnGestureListener;
import utils.*;

public class Recogniser extends Activity {

	String filename="/mnt/sdcard/OutputDTW.txt";
	ArrayList<StrokeClass> Strokes;
	GestureOverlayView mv;
	ListView Scores;
	HashMap<String,IDsampling> frequencies=new HashMap<String,IDsampling>(); //to store the frequency of each output
	HashMap<String,IDsampling> frequencies_euclidean=new HashMap<String,IDsampling>();
	
	Button Update; //to update the frequencies in a text file
	Button Clear; //to clear the frequencies in a text file
	EditText inputClass;
	ProgressBar pv;
	
	ArrayList<String> ScoresArray=new ArrayList<String>();
	ArrayAdapter<String> Scoresadapter;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recogniser);
        try {
    		Strokes=utils.Strokesloader.loadStrokes("/mnt/sdcard/outputMean.dat");
    		//Toast.makeText(getApplicationContext(), Strokes.size(),Toast.LENGTH_SHORT).show();
    	} catch (Exception e1) {
    		// TODO Auto-generated catch block
    		e1.printStackTrace();
    	}
        pv=(ProgressBar) findViewById(R.id.progressBar1);
        Scores=(ListView) findViewById(R.id.listView1);
        Scoresadapter=new ArrayAdapter<String>(getApplicationContext(),android.R.layout.simple_list_item_1
        		,ScoresArray);
        Scores.setAdapter(Scoresadapter);
        
        Update=(Button) findViewById(R.id.button1);
        Clear=(Button)findViewById(R.id.button2);
        inputClass=(EditText) findViewById(R.id.editText1);
        
        // //set initial size of the stroke class in the Class
        
        
        Update.setOnClickListener(new OnClickListener() {
			
        	//write the frequencies to the file
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				new Thread(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						try {
							Frequency_Writer.write(frequencies,inputClass.getText().toString(),1);
							//for Euclidean distance
							Frequency_Writer.write(frequencies_euclidean,inputClass.getText().toString(), 2);
							
							runOnUiThread(new Runnable() {
								
								@Override
								public void run() {
									// TODO Auto-generated method stub
									Toast.makeText(getApplicationContext(), "Class "+inputClass.getText().toString()+" written updated successfully",Toast.LENGTH_SHORT).show();
								}
							});
							
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}).start();
				
			}
			
		});
        
        
        Clear.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				frequencies=new HashMap<String, IDsampling>();
				frequencies_euclidean=new HashMap<String, IDsampling>();
			}
		});
       
        
        
        mv=(GestureOverlayView) findViewById(R.id.gestureOverlayView1);
        //mv.setDrawingCacheEnabled(true);
        mv.addOnGestureListener(new OnGestureListener() {
			
			@Override
			public void onGestureStarted(GestureOverlayView overlay, MotionEvent event) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onGestureEnded(GestureOverlayView overlay, MotionEvent event) {
				// TODO Auto-generated method stub
				 Gesture g=overlay.getGesture();
				 
				float[] temp=g.getStrokes().get(0).points;
				
				 temp=Scaling.scale(temp);
				 temp=smoothing.smoothFunction(temp);
				 //Toast.makeText(getApplicationContext(),"successfully till here",Toast.LENGTH_SHORT).show();
				 
				 perform t=new perform();
				 t.execute(temp);
				 			 
				
			}
			
			@Override
			public void onGestureCancelled(GestureOverlayView overlay, MotionEvent event) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onGesture(GestureOverlayView overlay, MotionEvent event) {
				// TODO Auto-generated method stub
				
			}
		});
       
        //inputClass.setText(Strokes.size());
        
    }
    class perform extends AsyncTask<float[],Integer,Void>
    {
    	    	
		@Override
		protected Void doInBackground(float[]... arg0) {
			
			ScoresArray.clear();
			ArrayList<Stroke> t=new ArrayList<Stroke>();
			ArrayList<Stroke> euclidean=new ArrayList<Stroke>(); //for euclidean distance
			for(int i=0;i<Strokes.size();i++)
			{
				double score=DTWRecogniser.DTWDistance(arg0[0],Strokes.get(i).m,Strokes.get(i).StrokeID,filename);
				String ID=Strokes.get(i).StrokeID;
				Stroke temp=new Stroke(ID,score);
				t.add(temp);
				
				/*
				 * Euclidean part now
				 *
				 */
			
				float euscore=Euclidean.euclidean(arg0[0],Strokes.get(i).m,Strokes.get(i).StrokeID,filename);
				Stroke temp2=new Stroke(ID,euscore);
				euclidean.add(temp2);
				
				
			}
				
			Collections.sort(t);
			Collections.sort(euclidean);//for the euclidean distance
				
				int Count=0; //take top 5 scores;
				for(int i=0;i<t.size();i++)
				{
					Stroke tempStrokes=t.get(i);
					Stroke tempStrokes2=euclidean.get(i);
					ScoresArray.add(tempStrokes.strokeName+" = "+tempStrokes.dist);
				/* 
				 * Update the frequency of the top 5 results
				 * 
				 */
					if(Count++< 5)
					{
						IDsampling tempObj=frequencies.get(tempStrokes.strokeName);
						if(tempObj==null)
							tempObj=new IDsampling();
						tempObj.update(Count);
						frequencies.put(tempStrokes.strokeName,tempObj); //update frequencies		
						
						tempObj=null;
						
						/*
						 * EUCLIDEAN PART HERE
						 */
						tempObj=frequencies_euclidean.get(tempStrokes2.strokeName);
						if(tempObj==null)
							tempObj=new IDsampling();
						tempObj.update(Count);
						frequencies_euclidean.put(tempStrokes2.strokeName,tempObj);
						
						tempObj=null;
						
						
					}
				
				
				
						
					
				
			}
			
			return null;		
			
			
			
		}
		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			Scoresadapter.notifyDataSetChanged();
		
		}
 	   
		
    }
    
    
 }
   



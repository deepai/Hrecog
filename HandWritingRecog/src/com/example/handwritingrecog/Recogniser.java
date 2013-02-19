package com.example.handwritingrecog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import com.example.handwritingrecog.DTWRecogniser;
import preprocessing.Scaling;
import preprocessing.smoothing;

import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.app.Activity;
import android.gesture.Gesture;
import android.gesture.GestureOverlayView;
import android.gesture.GestureOverlayView.OnGesturePerformedListener;

public class Recogniser extends Activity {

	HashMap<String,float[]> Strokes;
	HashMap<String,String> uniVals;
	CharLUT LutMatcher; 
	GestureOverlayView mv;
	Button SendSMS;
	EditText PhoneEntry;
	EditText TextArea;
	Button combinecharacter;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recogniser);
        try {
    		Strokes=utils.Strokesloader.loadStrokes("/mnt/sdcard/outputMeanHash.dat");
    		LutMatcher=new CharLUT(utils.Strokesloader.loadForwardLUT("/mnt/sdcard/LutLex.dat"));
    		uniVals=character.initvalue(); //load the character map
    		//Toast.makeText(getApplicationContext(), Strokes.size(),Toast.LENGTH_SHORT).show();
    	} catch (Exception e1) {
    		// TODO Auto-generated catch block
    		e1.printStackTrace();
    	}
        PhoneEntry = (EditText) findViewById(R.id.editText2);
        SendSMS=(Button) findViewById(R.id.button1);
        TextArea=(EditText) findViewById(R.id.editText1);
        TextArea.setText("\u0985"+","+"\u0987"+"\u0987");
        
        SendSMS.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				String message=TextArea.getText().toString();
				String PhoneNumber=PhoneEntry.getText().toString();
				
				if((PhoneNumber.equals("")||PhoneNumber==null))
				{
					Toast.makeText(getApplicationContext(),"No Phone Number Given",Toast.LENGTH_SHORT).show();
				}
				else
				{
					SmsManager smsManager = SmsManager.getDefault();
					smsManager.sendTextMessage(PhoneNumber, null, message, null, null);
					Toast.makeText(getApplicationContext(),"sent successfully",Toast.LENGTH_SHORT).show();
				}
			}
		});
        
        
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
        
        combinecharacter=(Button) findViewById(R.id.button2);
        combinecharacter.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				int start=TextArea.getSelectionStart();
				int end=TextArea.getSelectionEnd();
				String toCombine=TextArea.getText().toString().substring(start, end);
				String finalString=character.combineChar(toCombine);
				String newString=TextArea.getText().toString().substring(0,start);
				newString+=finalString;
				TextArea.setText(newString);				
			}
		});
        
    }
    class performRecognition extends AsyncTask<ArrayList<float[]>,Void,String>
    {

		@Override
		protected String doInBackground(ArrayList<float[]>... params) {
			
			String finalCharacterClass=null;
		  String[] RecognizedStrokes=new String[params[0].size()];
		  Set<String> libraryClassesKeys=Strokes.keySet(); //obtain the keys
		
		  
		  for(int i=0;i<params[0].size();i++)
		  {
			 double minValue=Double.MAX_VALUE;
			 String ClassRecognizedMin = null;
			  Iterator<String> key=libraryClassesKeys.iterator();
			while(key.hasNext())
			 {
				String tempClass=key.next();
				 double score=DTWRecogniser.DTWDistance(params[0].get(i),Strokes.get(tempClass));
				 if(minValue>score)
				 {
					 minValue=score; //set as minimum score
					 ClassRecognizedMin=tempClass; //set as minimum Score corresponding class
				 }
				
			 }
			 RecognizedStrokes[i]=ClassRecognizedMin;
			
		  }
		  finalCharacterClass=LutMatcher.getValue(RecognizedStrokes)+":"+RecognizedStrokes.toString();
		  
		 // finalCharacterClass=RecognizedStrokes.toString();
		  return finalCharacterClass;
		}
		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			if(uniVals.get(result)==null)
				TextArea.setText("null");
			else
				TextArea.setText(result);
		}
    	
    }
   
    
    
 }
   



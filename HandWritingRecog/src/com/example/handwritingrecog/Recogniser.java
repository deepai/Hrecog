package com.example.handwritingrecog;

import java.util.ArrayList;
import java.util.Collections;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
	Button Reload;
	ListView charchoice;
	ArrayAdapter<String> charchoiceAdapt;
	ArrayList<String> charchoices=new ArrayList<String>();	
	final Context context = this;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recogniser);
        try {
    		Strokes=utils.Strokesloader.loadStrokes("/mnt/sdcard/Library.dat");
    		LutMatcher=new CharLUT(utils.Strokesloader.loadForwardLUT("/mnt/sdcard/LutLex.dat"));
    		uniVals=character.initvalue(); //load the character map
    		//Toast.makeText(getApplicationContext(), Strokes.size(),Toast.LENGTH_SHORT).show();
    	} catch (Exception e1) {
    		// TODO Auto-generated catch block
    		e1.printStackTrace();
    	}
        uniVals=character.initvalue(); //load the character map
        PhoneEntry = ( EditText) findViewById(R.id.editText2);
        SendSMS=(Button) findViewById(R.id.button1);
        Reload=(Button) findViewById(R.id.button3);
        TextArea=(EditText) findViewById(R.id.editText1);
        TextArea.setText("\u0985"+","+"\u0987"+"\u0987");
        charchoiceAdapt= new ArrayAdapter<String>(getApplicationContext(),R.layout.listview,charchoices);
        charchoice=(ListView) findViewById(R.id.listView1);
        charchoice.setAdapter(charchoiceAdapt);
        
        for(String s:uniVals.keySet()) //store all the unicode into charchoices array
        {
        	charchoices.add(uniVals.get(s));
        }
        Collections.sort(charchoices);
        charchoiceAdapt.notifyDataSetChanged();
        
        
        /*
         * RELOAD THE LUT AND STROKE FILES;
         */
        charchoice.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				
				String value=charchoices.get(arg2);
				String previoustext=TextArea.getText().toString();
				previoustext+=value;
				TextArea.setText(previoustext);
				
			}
        	
		});
        
        
        Reload.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Strokes=null;
				try {
					Strokes=utils.Strokesloader.loadStrokes("/mnt/sdcard/Library.dat");
					LutMatcher=new CharLUT(utils.Strokesloader.loadForwardLUT("/mnt/sdcard/LutLex.dat"));
					Toast.makeText(getApplicationContext(),"reload successfull", Toast.LENGTH_SHORT).show();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					Toast.makeText(getApplicationContext(), "reload failed", Toast.LENGTH_SHORT).show();
					System.exit(0);
				}
				
			}
		});
        
        SendSMS.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub	
				
				/*
				 * DISPLAY SMS AND EMAIL OPTIONS
				 */
	            
				AlertDialog.Builder builder = new AlertDialog.Builder(context);
	            builder.setMessage("SMS or EMAIL?")
	            .setTitle("CHOOSE WINDOW");
	            builder.setPositiveButton("SMS", new DialogInterface.OnClickListener() {
	                public void onClick(DialogInterface dialog, int id) {
	                    // User clicked OK button
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
	            builder.setNegativeButton("EMAIL", new DialogInterface.OnClickListener() {
	                public void onClick(DialogInterface dialog, int id) {
	                
	    				/*this part for email
	    				 */
	    				 //TODO Auto-generated method stub
	    	            Intent email = new Intent(android.content.Intent.ACTION_SEND);

	    	            /* Fill it with Data */
	    	            email.setType("plain/text");
	    	            email.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{"deepai.dutta@gmail.com"});
	    	            
	    	            email.putExtra(android.content.Intent.EXTRA_SUBJECT, "Test");
	    	            email.putExtra(android.content.Intent.EXTRA_TEXT,TextArea.getText().toString());

	    	            /* Send it off to the Activity-Chooser */
	    	            startActivity(Intent.createChooser(email, "Send mail..."));
	                }
	            });

	            AlertDialog dialog = builder.create();
	            dialog.show();

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
				 //oast.makeText(getApplicationContext(), UserDrawnStroke.s+"", Toast.LENGTH_SHORT).show();
				performRecognition Recogniser=new performRecognition();
				Recogniser.execute(UserDrawnStroke);
			}
		});
        
        combinecharacter=(Button) findViewById(R.id.button2);
        combinecharacter.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
						
			}
		});
        
    }
    class performRecognition extends AsyncTask<ArrayList<float[]>,Void,String>
    {

		@Override
		protected String doInBackground(ArrayList<float[]>... params) {
			
			String finalCharacterClass=null;
		  String[] RecognizedStrokes=new String[params[0].size()];
		  //Set<String> libraryClassesKeys=Strokes.keySet(); //obtain the keys
		
		  
		  for(int i=0;i<params[0].size();i++)
		  {
			 double minValue=Double.MAX_VALUE;
			 String ClassRecognizedMin = null;
			 Iterator<String> key=Strokes.keySet().iterator();
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
		  finalCharacterClass=LutMatcher.getValue(RecognizedStrokes);
		  
		 // finalCharacterClass=RecognizedStrokes.toString();
		  return finalCharacterClass;
		}
		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			
				String previoustext=TextArea.getText().toString();
				previoustext+=uniVals.get(LutMatcher.LUTforward.get(result));
				TextArea.setText(previoustext);
				Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT).show(); 
		}
    	
    }
   
    
    
 }
   



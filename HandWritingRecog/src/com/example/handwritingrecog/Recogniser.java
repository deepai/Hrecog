package com.example.handwritingrecog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import com.example.handwritingrecog.DTWRecogniser;

import preprocessing.Scaling;
import preprocessing.smoothing;
import utils.SaveFile;
import utils.Strokesloader;

import Character_Stroke.Character_Stroke;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.gesture.Gesture;
import android.gesture.GestureOverlayView;
import android.gesture.GestureOverlayView.OnGesturePerformedListener;

public class Recogniser extends Activity {
/*********************************************Fields**************************************/
	HashMap<String,float[]> Strokes;
	HashMap<String,String> uniVals;
	CharLUT LutMatcher; 
	GestureOverlayView mv;
	Button SendSMS;
	EditText PhoneEntry;
	EditText TextArea;
	Button combinecharacter;
	Button Reload;
	Button userCorrection; 
	ArrayAdapter<String> charchoiceAdapt;
	ArrayList<float[]> InputCharacter; //to hold the UserInput Character after preprocessing
	ArrayList<unicodeMapping> Unicodemapper=new ArrayList<unicodeMapping>();
	Matcher mt;
	final ArrayList<Character_Stroke> finallist=new ArrayList<Character_Stroke>();
	
	final Context context = this;
	public HashMap<String, ArrayList<Character_Stroke>> characterStrokes;
    boolean showDialog=true;
    
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recogniser);
        try {
     /*****************************LOAD THE LIBRARY FILES*****************************************************/
    		
        	Strokes=utils.Strokesloader.loadStrokes("/mnt/sdcard/Library.dat");
    		LutMatcher=new CharLUT(utils.Strokesloader.loadForwardLUT("/mnt/sdcard/LutLex.dat"));
    		uniVals=character.initvalue(); //load the character map
    		characterStrokes=Strokesloader.loadStrokesClass("/mnt/sdcard/LUTCharStrokes.dat");
    		
     
    		//Toast.makeText(getApplicationContext(), Strokes.size(),Toast.LENGTH_SHORT).show();
    	} catch (Exception e1) {
    		// TODO Auto-generated catch block
    		e1.printStackTrace();
    	}
     /*********************************************************************************************************/
        try {
			mt=new Matcher(characterStrokes);
			//Toast.makeText(context, "Success loading library files", Toast.LENGTH_SHORT).show();
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			Toast.makeText(context,e1.toString(), Toast.LENGTH_SHORT).show();
		}
       /************************ATTACH THE UI COMPONENTS*****************************************************/
       
        PhoneEntry = ( EditText) findViewById(R.id.editText2);
        SendSMS=(Button) findViewById(R.id.button1);
        Reload=(Button) findViewById(R.id.button3);
        TextArea=(EditText) findViewById(R.id.editText1);
        mv=(GestureOverlayView) findViewById(R.id.gestureOverlayView1);
        combinecharacter=(Button) findViewById(R.id.button2);
        userCorrection=(Button) findViewById(R.id.button4);
        
        /*********************************************************************************************************/
        
        for(String s:uniVals.keySet()) //store all the unicode into charchoices array
        {
        	Unicodemapper.add(new unicodeMapping(s, uniVals.get(s)));
        	
        }
       
        /*******************************************ATTACH THE LISTENERS******************************************/

        Reload.setOnClickListener(new OnClickListener() {  //Listener for RefreshButton to reload the primary files
			
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
        
        SendSMS.setOnClickListener(new OnClickListener() { //Listener for SMS and Email application
			
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
	    	            //email.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{"deepai.dutta@gmail.com"});
	    	            
	    	            //email.putExtra(android.content.Intent.EXTRA_SUBJECT, "Test");
	    	            email.putExtra(android.content.Intent.EXTRA_TEXT,TextArea.getText().toString());

	    	            /* Send it off to the Activity-Chooser */
	    	            startActivity(Intent.createChooser(email, "Send mail..."));
	                }
	            });

	            AlertDialog dialog = builder.create();
	            dialog.show();

			}
		});
        
        mv.addOnGesturePerformedListener(new OnGesturePerformedListener() { //Listener for GestureOverlayView for recognition
			
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
				InputCharacter=UserDrawnStroke;//store globally;
				 //oast.makeText(getApplicationContext(), UserDrawnStroke.s+"", Toast.LENGTH_SHORT).show();
				performRecognition Recogniser=new performRecognition();
				Recogniser.execute(UserDrawnStroke);
			}
		});
       
        combinecharacter.setOnClickListener(new OnClickListener() { //Listener for Combine character function
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				String temp=TextArea.getText().toString();
				String toCombine=TextArea.getText().toString().substring(temp.length()-2);
				String finalString=character.combineChar(toCombine);
				String newString=TextArea.getText().toString().substring(0,temp.length()-2);
				newString+=finalString;
				TextArea.setText(newString);	
				//Toast.makeText(context,toCombine.length()+"", Toast.LENGTH_SHORT).show();
			}
		});

        /************************Matcher Dialog*****************************************************/
        userCorrection.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				 StrokeMatcher();
		    	   
			}
		});
        
        
        /************************Matcher Ends here*****************************************************/
        
    }
    public void StrokeMatcher()   //Stroke Matcher function

    {
    	    
    	    final Dialog dialog = new Dialog(context);
			dialog.setContentView(R.layout.dialog_unicode);
			dialog.setTitle("Choose Correct Character.");
			GridView charchoices=(GridView) dialog.findViewById(R.id.gridView1);
			charchoices.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					// TODO Auto-generated method stub
					TextView t=(TextView) arg1.findViewById(R.id.text1);
					String charactername=(String) t.getTag(); //get the tag name					
					
					/*
					 * Number of Characters found
					 */
					
					ArrayList<String> Charactersequences=mt.NumStrokesSeq(charactername, InputCharacter.size()); //number of Strokes present in the InputCharacter
					//Toast.makeText(context,Charactersequences.size()+"", Toast.LENGTH_SHORT).show();
					//Toast.makeText(context,Charactersequences.get(0),Toast.LENGTH_SHORT).show();
					
					/*if(Charactersequences.size()==1)
					{
						ArrayList<Character_Stroke> temp=mt.StrokeMatch(Charactersequences.get(0), InputCharacter);
						finallist.clear(); //clear the final list 
						for(Character_Stroke e:temp) //add all the individual strokes in the finallist!
						{
							finallist.add(e);
						}
						for(Character_Stroke e:finallist)
						{
							Strokes.put(e.getStroke_label()+"_x",e.getStroke());
						}
						SaveFile.WriteFile("/mnt/sdcard/Library.dat",Strokes);
						Toast.makeText(context,"Success", Toast.LENGTH_SHORT).show();	
						dialog.dismiss();
						
						
					}*/
					
					//else
					{
						
						dialog.dismiss();
						if(Charactersequences.size()!=0)
						{
							final Dialog multiselect=new Dialog(context);
							multiselect.setContentView(R.layout.dialogmulchoice); //show the multiple dialog
							ListView lv=(ListView) multiselect.findViewById(R.id.listView12);
							customadaptermulti adapter=new customadaptermulti(context,R.layout.editcharacter,Charactersequences,characterStrokes);
							lv.setAdapter(adapter);
							adapter.notifyDataSetChanged(); //update view
							lv.setOnItemClickListener(new OnItemClickListener() { //listener event for the onclick!

								@Override
								public void onItemClick(
										AdapterView<?> arga0, View arga1,int arga2, long arga3) {
									// TODO Auto-generated method stub
									ImageView v=(ImageView) arga1.findViewById(R.id.imageView1);
									String seq=(String) v.getTag(); //obtain the final sequence
									ArrayList<Character_Stroke> temp=mt.StrokeMatch(seq, InputCharacter); //match with the Character
									finallist.clear(); //clear the final list 
									for(Character_Stroke e:temp) //add all the individual strokes in the finallist!
									{
										finallist.add(e);
									}
									//
									for(Character_Stroke e:finallist)
									{
										Strokes.put(e.getStroke_label()+"_x",e.getStroke());
									}
									SaveFile.WriteFile("/mnt/sdcard/Library.dat",Strokes);
									//
									Toast.makeText(context,"Success", Toast.LENGTH_SHORT).show();
									multiselect.dismiss();
								}
							});
							multiselect.show();
						}
						else
						{
							Toast.makeText(context,"Error",Toast.LENGTH_SHORT).show();
						}
						
					}
				}
				
			});
			customAdapterSingle adapt=new customAdapterSingle(context,R.layout.listview,Unicodemapper); //attach the adapter
			charchoices.setAdapter(adapt);
			adapt.notifyDataSetChanged();
			dialog.show();
    }
    
    
    
    /************************Recogniser Class Separate Thread*****************************************************/
    
    class performRecognition extends AsyncTask<ArrayList<float[]>,Void,String>
    {

		@Override
		protected String doInBackground(ArrayList<float[]>... params) { ///Compute the Recognition herez
			
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
		
		 /************************TJOB TO PERFORM AFTER RECOGNITION*****************************************************/
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			
				String previoustext=TextArea.getText().toString();
				String newresult=uniVals.get(LutMatcher.LUTforward.get(result));
				if(newresult==null)
				{
					if(showDialog)
					{

						final Dialog dialogview = new Dialog(context);
						dialogview.setContentView(R.layout.dialogcorrection);
						dialogview.setTitle("Assistance");
						Button text=(Button) dialogview.findViewById(R.id.button_ok);
						final CheckBox ct=(CheckBox) dialogview.findViewById(R.id.checkBox1);
						
						text.setOnClickListener(new OnClickListener() {
							
							@Override
							public void onClick(View arg0) {
								// TODO Auto-generated method stub
								if(ct.isChecked())
									showDialog=false;
								dialogview.dismiss();
								StrokeMatcher();
							}
						});
						dialogview.show();
						
					}
					else
					{
						StrokeMatcher();
					}
					
					
				}
				else
				{
					previoustext+=newresult;		
					
					TextArea.setText(previoustext);
					Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT).show(); 
				}
				
		}
    	
    }
   
    
    
 }
   



package com.example.handwritingrecog;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.example.handwritingrecog.DTWRecogniser;

import preprocessing.Scaling;
import preprocessing.smoothing;
import utils.SaveFile;

import Character_Stroke.Character_Stroke;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
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
import android.content.SharedPreferences;
import android.gesture.Gesture;
import android.gesture.GestureOverlayView;
import android.gesture.GestureOverlayView.OnGestureListener;
import android.gesture.GestureOverlayView.OnGesturePerformedListener;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

public class Recogniser extends Activity {
/*********************************************Fields**************************************/
	HashMap<String,float[]> Strokes;
	HashMap<String,ArrayList<String>> LUTback;
	HashMap<String,String> uniVals;
	HashMap<String,String> unicodeGrid;
	CharLUT LutMatcher; 
	GestureOverlayView mv;
	Button SendSMS;
	EditText PhoneEntry;
	EditText TextArea;
	Button combinecharacter;
	Button userCorrection; 
	Button Clear;
	Button Spacebaar;
	Button Help;
	ImageButton Exit;
	Bitmap preexist;
	Bitmap currentGesture;
	ImageButton revert;
	ImageButton backspace;
	ImageView img;
	ArrayAdapter<String> charchoiceAdapt;
	ArrayList<float[]> InputCharacter; //to hold the UserInput Character after preprocessing
	ArrayList<unicodeMapping> Unicodemapper=new ArrayList<unicodeMapping>();
	Matcher mt;
	String correctedChar;
	SharedPreferences preference;
	Bitmap Empty=Bitmap.createBitmap(11160,648,Bitmap.Config.ARGB_8888);
	Bitmap currentbitmap=Empty;
	final ArrayList<Character_Stroke> finallist=new ArrayList<Character_Stroke>();
	
	final Context context = this;
	public HashMap<String, ArrayList<Character_Stroke>> characterStrokes;
    boolean showDialog=true;
	private Paint pt=new Paint();
	 ExecutorService executor = Executors.newFixedThreadPool(15);
    
    
	@Override
		protected void onStop() {
			// TODO Auto-generated method stub
			super.onStop();
			utils.SaveFile.WriteFile("/mnt/sdcard/HWREcogfiles/Library.dat", Strokes); 
		}
	 
	  protected void onRestart() {
		  super.onRestart();
		if(!utils.SaveFile.exists("/mnt/sdcard/HWREcogfiles/Library.dat"))
      	{
      		finish();       	
      	}
      	else
      	{
      		//load strokes
      		try {
				Strokes=utils.Strokesloader.loadStrokes("/mnt/sdcard/HWREcogfiles/Library.dat");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
      	}
	  };
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recogniser);

        try {
     /*****************************LOAD THE LIBRARY FILES*****************************************************/
    		
        	Intent intent=getIntent();
        	if(!utils.SaveFile.exists("/mnt/sdcard/HWREcogfiles/Library.dat"))
        	{
        		Strokes=(HashMap<String, float[]>) intent.getSerializableExtra("LIBRARY");
        		File f=new File("/mnt/sdcard/HWREcogfiles");
        		f.mkdir();
        		utils.SaveFile.WriteFile("/mnt/sdcard/HWREcogfiles/Library.dat", Strokes);        	
        	}
        	else
        	{
        		//load strokes
        		Strokes=utils.Strokesloader.loadStrokes("/mnt/sdcard/HWREcogfiles/Library.dat");
        	}
        	
        	
        	LutMatcher=new CharLUT((HashMap<String, String>) intent.getSerializableExtra("LUTforward"));
        	uniVals=character.initvalue(); //load the character map
    		unicodeGrid=character.unicodeGridView(); //load the character map for unicode gridview	
    		characterStrokes=(HashMap<String, ArrayList<Character_Stroke>>) intent.getSerializableExtra("LUTCharStrokes");
        	LUTback=(HashMap<String, ArrayList<String>>) intent.getSerializableExtra("LUTbackward");
    		/*
        	 * 
			*/
    		
     
    		//Toast.makeText(getApplicationContext(), Strokes.size(),Toast.LENGTH_SHORT).show();
    	} catch (Exception e1) {
    		// TODO Auto-generated catch block
    		e1.printStackTrace();
    	}
     /*********************************************************************************************************/
        try {
			mt=new Matcher(characterStrokes,Strokes,this,LUTback);
			//Toast.makeText(context, "Success loading library files", Toast.LENGTH_SHORT).show();
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			Toast.makeText(context,e1.toString(), Toast.LENGTH_SHORT).show();
		}
       /************************ATTACH THE UI COMPONENTS*****************************************************/
       
        SendSMS=(Button) findViewById(R.id.button1);
        TextArea=(EditText) findViewById(R.id.editText1);
        mv=(GestureOverlayView) findViewById(R.id.gestureOverlayView1);
        combinecharacter=(Button) findViewById(R.id.Button01);
        userCorrection=(Button) findViewById(R.id.button4);
        Exit=(ImageButton) findViewById(R.id.button_quit);
        Clear=(Button) findViewById(R.id.button_clear);
        img=(ImageView)findViewById(R.id.imageView1);
        Spacebaar=(Button) findViewById(R.id.button_space);
        backspace=(ImageButton) findViewById(R.id.imageButton_backspace);
        Help=(Button) findViewById(R.id.button_help);
        
        
        /*********************************************************************************************************/
        
        //Bitmap bl=Bitmap.createBitmap(400,400,Bitmap.Config.ARGB_8888);
        //img.setImageBitmap(bl);
        for(String s:unicodeGrid.keySet()) //store all the unicode into charchoices array
        {
        	Unicodemapper.add(new unicodeMapping(s, unicodeGrid.get(s)));
        	
        }
        //img.setImageBitmap(Bitmap.createBitmap(mv.));
       
        /*******************************************ATTACH THE LISTENERS******************************************/

        
        Exit.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				onStop();
				finish();
				System.exit(0);
			}
		});
        backspace.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				
				String text=TextArea.getText().toString();
				if(text!=null || text.length()>0)
				{
					text=text.substring(0,text.length()-1);
					TextArea.setText(text);
				}
				
			}
		});
        Spacebaar.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				TextArea.append(" ");
			}
		});
        //mv.setAlwaysDrawnWithCacheEnabled(true);
        
        Clear.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				mv.destroyDrawingCache();
				img.setImageBitmap(null);
				//Toast.makeText(getApplicationContext(), "height:"+mv.getHeight()+" width:"+mv.getWidth(),Toast.LENGTH_LONG).show();
			}
		});
        
        Help.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				final Dialog Helpdialog = new Dialog(context);
	    	    Helpdialog.setTitle("Readme");
				Helpdialog.setContentView(R.layout.dialogcorrection);
				TextView et=(TextView) Helpdialog.findViewById(R.id.textView_help);
				et.setMovementMethod(ScrollingMovementMethod.getInstance());
				et.setText(Html.fromHtml(getResources().getString(R.string.README)));
				Button bt=(Button) Helpdialog.findViewById(R.id.button_ok);
				bt.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						Helpdialog.dismiss();
					}
				});
				Helpdialog.show();
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
	    				/*
	    				 * SHOW SMS DIALOG HERE
	    				 */

	    	    	    final Dialog SMSdialog = new Dialog(context);
	    	    	    SMSdialog.setTitle("SEND SMS ");
	    				SMSdialog.setContentView(R.layout.dialog_sms);
	    				final EditText SMScontent=(EditText) SMSdialog.findViewById(R.id.SMScontent);
	    				SMScontent.setText(TextArea.getText().toString());//setting the text here
	    				final EditText SMSNumberfield=(EditText) SMSdialog.findViewById(R.id.phnNumber);
	    				final Button smsButton=(Button)SMSdialog.findViewById(R.id.button_sms);
	    				smsButton.setOnClickListener(new OnClickListener() {
							
							@Override
							public void onClick(View arg0) {
								// TODO Auto-generated method stub
								String message=SMScontent.getText().toString();
								message=unicodeVowelMod(message);
			    				String PhoneNumber=SMSNumberfield.getText().toString();
			    				
			    				
			    				if((PhoneNumber.equals("")||PhoneNumber==null))
			    				{
			    					Toast.makeText(getApplicationContext(),"No Phone Number Given",Toast.LENGTH_SHORT).show();
			    				}
			    				else
			    				{
			    					SmsManager smsManager = SmsManager.getDefault();
			    					smsManager.sendTextMessage(PhoneNumber, null, message, null, null);
			    					Toast.makeText(getApplicationContext(),"sent successfully",Toast.LENGTH_SHORT).show();
			    					SMSdialog.dismiss();
			    				}
							}
						});
	    				SMSdialog.show();
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
	    	            email.putExtra(android.content.Intent.EXTRA_TEXT,unicodeVowelMod(TextArea.getText().toString()));

	    	            /* Send it off to the Activity-Chooser */
	    	            startActivity(Intent.createChooser(email, "Send mail..."));
	                }
	            });

	            AlertDialog dialog = builder.create();
	            dialog.show();

			}
		});
      // mv.setDrawingCacheEnabled(true);
      mv.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_LOW);
      mv.addOnGesturePerformedListener(new OnGesturePerformedListener() {
		
		@Override
		public void onGesturePerformed(GestureOverlayView overlay, Gesture gesture) {
			// TODO Auto-generated method stub
			
			//img.setImageBitmap(convert(currentbitmap));
			
			ArrayList<float[]> UserDrawnStroke=new ArrayList<float[]>(gesture.getStrokesCount()); //create an arraylist to temporary hold the float arrays
			for(int i=0;i<gesture.getStrokesCount();i++)
			{
				 float[] temp=gesture.getStrokes().get(i).points; //	float points of the gesture		
				 temp=Scaling.scale(temp); // Apply Scaling
				 temp=smoothing.smoothFunction(temp); //apply Smoothing 
				 UserDrawnStroke.add(temp);
			}
			InputCharacter=UserDrawnStroke;//store globally;
			mv.setDrawingCacheEnabled(false);
			 //oast.makeText(getApplicationContext(), UserDrawnStroke.s+"", Toast.LENGTH_SHORT).show();
			
			new performRecognition().execute(UserDrawnStroke);
			
		}
	});
	

      mv.addOnGestureListener(new OnGestureListener() {
		
		@Override
		public void onGestureStarted(GestureOverlayView arg0, MotionEvent arg1) {
			// TODO Auto-generated method stub
			mv.setDrawingCacheEnabled(true);
		}
		
		@Override
		public void onGestureEnded(GestureOverlayView arg0, MotionEvent arg1) {
			// TODO Auto-generated method stub
			
			currentbitmap=Bitmap.createBitmap(arg0.getDrawingCache());
			executor.execute(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					
					runOnUiThread(new Runnable() {
						
						@Override
						public void run() {
							// TODO Auto-generated method stub
							img.setImageBitmap(convert(currentbitmap));
						}
					});
				}
			});
			
			mv.setDrawingCacheEnabled(false);
		}
		
		@Override
		public void onGestureCancelled(GestureOverlayView arg0, MotionEvent arg1) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void onGesture(GestureOverlayView arg0, MotionEvent arg1) {
			// TODO Auto-generated method stub
			
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
        
       // img.setImageBitmap(Bitmap.createBitmap(img.getWidth(),img.getHeight(),Bitmap.Config.ARGB_8888));

        /************************Matcher Dialog*****************************************************/
        userCorrection.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				 StrokeMatcher(2);
		    	   
			}
		});
        
        
        
        /************************Matcher Ends here*****************************************************/
        
    }
    public Bitmap convert(Bitmap myBitmap)
    {
    	 int [] pixels = new int [ myBitmap.getHeight()*myBitmap.getWidth()];
		 myBitmap.getPixels(pixels, 0, myBitmap.getWidth(), 0, 0, myBitmap.getWidth(),myBitmap.getHeight());
    	 for(int i =0; i<myBitmap.getHeight()*myBitmap.getWidth();i++){
    	  if( pixels[i] == Color.YELLOW)
    	              pixels[i] = Color.RED;
    	  }

    	   myBitmap.setPixels(pixels, 0, myBitmap.getWidth(), 0, 0, myBitmap.getWidth(), myBitmap.getHeight());
    	   return myBitmap;
    }
    public void StrokeMatcher(int type)   //Stroke Matcher function

    {
    		final int valtype=type;
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
					correctedChar=t.getText().toString();
					
					if(valtype==1)
					{
						String preText=TextArea.getText().toString();
						TextArea.setText(preText+uniVals.get(charactername));//set the corrected character
					}
					else
					{
						String preText=TextArea.getText().toString();
						
						if(InputCharacter==null)
						{						
							preText+=uniVals.get(charactername);
							TextArea.setText(preText);
							dialog.dismiss();
							return;
						}
						else
						{
							if(preText.length()!=0)
							{
								preText=preText.substring(0,preText.length()-1);
							}						
							preText+=uniVals.get(charactername);
							TextArea.setText(preText);
						}
							
						
					}
					/*
					 * Number of Characters found
					*/
					if(InputCharacter.size()==1) //handle single Stroke
					{
						final String InputCharName=mt.getSingleStrokeName(charactername);
						if(InputCharName!=null)
						{
							executor.execute(new Runnable() {
								
								@Override
								public void run() {
									// TODO Auto-generated method stub
									LRUReplace(InputCharName, InputCharacter.get(0), Strokes);
									//SaveFile.WriteFile("/mnt/sdcard/HWREcogfiles/Library.dat",Strokes);
								}
							});
							
							Toast.makeText(context,"Successfully saved the stroke", Toast.LENGTH_SHORT).show();	
							dialog.dismiss();//
						}
						else
						{
							Toast.makeText(context,"Found Null", Toast.LENGTH_SHORT).show();	
							dialog.dismiss();//
						}
						return;
					}
					
					ArrayList<String> Charactersequences=mt.NumStrokesSeq(charactername, InputCharacter.size()); //number of Strokes present in the InputCharacter
					
					
					//Toast.makeText(context,Charactersequences.size()+"", Toast.LENGTH_SHORT).show();
					//Toast.makeText(context,Charactersequences.get(0),Toast.LENGTH_SHORT).show();
					//Toast.makeText(context,Charactersequences.get(1),Toast.LENGTH_SHORT).show();
			
					if(Charactersequences.size()==1)
					{
						mt.StrokeMatchnonCentroid(Charactersequences.get(0),InputCharacter);
						Toast.makeText(context,"Success", Toast.LENGTH_SHORT).show();	
						dialog.dismiss();
												
					}
					
					else					
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
									
									mt.StrokeMatchnonCentroid(seq,InputCharacter);
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
    
    
    public Bitmap overlay3(Bitmap b1,Bitmap bt){
		
		 Bitmap bmOverlay = Bitmap.createBitmap(mv.getHeight(),mv.getHeight(),Bitmap.Config.ARGB_8888);
		 Canvas canvas = new Canvas(bmOverlay);
		 canvas.drawBitmap(b1,0,0,pt);
		 canvas.drawBitmap(bt,0,0,pt);
		return bmOverlay;
	       
	}
    private Bitmap codec(Bitmap src, Bitmap.CompressFormat format,
			int quality) {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		src.compress(format, quality, os);
 
		byte[] array = os.toByteArray();
		return BitmapFactory.decodeByteArray(array, 0, array.length);
	}
    public String unicodeVowelMod(String str)
	{ 
		return ((str.replaceAll("([\u09C7])(.)([\u09BE])", "$2\u09CB"))
							.replaceAll("([\u09C7])(.)([\u09D7])", "$2\u09CC"))
								.replaceAll("([\u09C7|\u09BF|\u09C8])(.)", "$2$1");
		
	}
    public void LRUReplace(String strokeClass, float[] strokePoints, HashMap<String, float[]> strokeMap)
	{
		boolean x_present = false;
		int maxNum = 0, num = 0;
		
		// Read in serialised object file Library.dat (the stroke library)
		
		for (String key: strokeMap.keySet()) 
		{
			//System.out.println(key + ": "+key.indexOf(strokeClass+"_x"));
			
			if( key.indexOf(strokeClass+"_x") != -1) //user-made stroke 
			{
				//System.out.println(key);
				
				if( x_present == false)
					x_present = true;
				
				num = Integer.parseInt(key.substring(key.indexOf("x")+1)); //serial number
				if( num > maxNum)
					maxNum = num;
			}
			
		}
		
		//System.out.println("maxNum = "+maxNum);
		
		int MAX_X=5;
		//a. This is the first user-made sample to be added 
		//OR b. if more than MAX_X, then replace least recently added stroke. i.e. _x1
		//N.B. - numbering starts from 1
		if(x_present == false || maxNum >= MAX_X )
		{
			strokeMap.put(strokeClass+"_x1", strokePoints);
			
		}
		else
		{
			strokeMap.put(strokeClass+"_x"+(maxNum+1), strokePoints);
		}
			
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
		
		 /************************THE END OF JOB TO PERFORM AFTER RECOGNITION*****************************************************/
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			
				String previoustext=TextArea.getText().toString();
				Toast.makeText(getApplicationContext(), "result:"+result,Toast.LENGTH_SHORT).show();
				String newresult=uniVals.get(LutMatcher.LUTforward.get(result));
				if(newresult==null)
				{
					StrokeMatcher(1);
					previoustext+=correctedChar;
					
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
   



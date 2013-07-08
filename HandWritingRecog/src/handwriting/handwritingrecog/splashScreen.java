package handwriting.handwritingrecog;

import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import Character_Stroke.Character_Stroke;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class splashScreen extends Activity {
	AssetManager assets;
	ProgressBar mprogress;
	TextView text;
	ObjectInputStream inp;
	TextView Terms;
	TextView Contact;
	HashMap<String,float[]> Strokes;
	HashMap<String,ArrayList<String>> backwardLUT;
	HashMap<String,String> forwardLUT;
	HashMap<String,ArrayList<Character_Stroke>> LUTCharStrokes;
	Bundle bt=new Bundle();
	Button Continue;
	Context context=this;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		mprogress=(ProgressBar) findViewById(R.id.progressBar1);
		mprogress.setMax(4);
		mprogress.setProgress(0);
		text=(TextView) findViewById(R.id.textView_grp);
		Terms=(TextView) findViewById(R.id.textView_terms);
		Contact=(TextView) findViewById(R.id.textView_contacts);
		assets=getAssets();
		Continue=(Button) findViewById(R.id.button_continue);
		Continue.setEnabled(false);
		
		Thread splash=new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					inp=new ObjectInputStream(assets.open("Library.dat"));
					Strokes=(HashMap<String, float[]>) inp.readObject();
					inp.close();
					// Thread.sleep(1000);
					mprogress.post(new Runnable() {
						
						@Override
						public void run() {
							// TODO Auto-generated method stub
							mprogress.setProgress(1);
							text.setText("Loading Libraries - Library.dat");
						}
					});
					
					//// Thread.sleep(1000);
					
					inp=new ObjectInputStream(assets.open("LUTback.dat"));
					backwardLUT=(HashMap<String, ArrayList<String>>) inp.readObject();
					inp.close();
					// Thread.sleep(1000);
					mprogress.post(new Runnable() {
						
						@Override
						public void run() {
							// TODO Auto-generated method stub
							mprogress.setProgress(2);
							text.setText("Loading Libraries - LUTbackdat");
						}
					});
					//// Thread.sleep(1000);
					
					//// Thread.sleep(1000);
					
					inp=new ObjectInputStream(assets.open("LUTCharStrokes.dat"));
					LUTCharStrokes=(HashMap<String, ArrayList<Character_Stroke>>) inp.readObject();
					//System.out.println(LUTCharStrokes);
					//for(String s:Lut)
					inp.close();
					// Thread.sleep(1000);
					mprogress.post(new Runnable() {
						
						@Override
						public void run() {
							// TODO Auto-generated method stub
							mprogress.setProgress(3);
							text.setText("Loading Libraries - CharStrokes.dat");
						}
					});
										//// Thread.sleep(1000);
					
					inp=new ObjectInputStream(assets.open("LutLex.dat"));
					forwardLUT=(HashMap<String, String>) inp.readObject();
					inp.close();	
					
					// Thread.sleep(1000);
					mprogress.post(new Runnable() {
						
						@Override
						public void run() {
							// TODO Auto-generated method stub
							mprogress.setProgress(4);
							text.setText("Loading Libraries - LuTlex.dat");
							//ctb.setEnabled(true);
							
						} 
					});
					// Thread.sleep(2000);
					text.post(new Runnable() {
						
						@Override
						public void run() {
							// TODO Auto-generated method stub
							text.setText("libraries loaded");
						}
					});
					Continue.post(new Runnable() {
						
						@Override
						public void run() {
							// TODO Auto-generated method stub
							Continue.setEnabled(true);
						}
					});
					
					//// Thread.sleep(1000);
					
					
				} catch (Exception e)
				{
					Toast.makeText(getApplicationContext(), e.toString()+"", Toast.LENGTH_SHORT).show();
					text.setText("Error in loading the libraries");
					
				}
				
			}
			
		});
		splash.start();
		
		Continue.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent i = new Intent(getApplicationContext(), Recogniser.class);
				i.putExtra("LIBRARY",Strokes); //library of strokes
				i.putExtra("LUTforward",forwardLUT); //forwardLUT
				i.putExtra("LUTbackward",backwardLUT); //Backward Lut
				i.putExtra("LUTCharStrokes",LUTCharStrokes); //CharStrokes
				startActivity(i);
			}
		});
		Terms.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setCancelable(true);
                builder.setTitle("Terms and Conditioners");
                builder.setMessage(getResources().getString(R.string.termsandcondtions));
                builder.setInverseBackgroundForced(true);
                AlertDialog alert = builder.create();
                alert.show();
			}
		});
		Contact.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				final Dialog Helpdialog = new Dialog(context);
	    	    Helpdialog.setTitle("Contacts");
				Helpdialog.setContentView(R.layout.dialogcorrection);
				TextView et=(TextView) Helpdialog.findViewById(R.id.textView_help);
				et.setMovementMethod(ScrollingMovementMethod.getInstance());
				et.setText(Html.fromHtml(getResources().getString(R.string.contacts)));
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
		
		
		
	}
}

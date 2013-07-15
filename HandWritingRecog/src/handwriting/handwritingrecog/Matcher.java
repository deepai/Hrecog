package handwriting.handwritingrecog;

import java.util.ArrayList;
import java.util.HashMap;

import preprocessing.BoundingBox;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import Character_Stroke.Character_Stroke;

public class Matcher {

	String UserSelStrokeSeq; // Store User Selected Stroke here
	String UserSelCharacter; // Store user Selected Character here
	Context ct;
	HashMap<String, ArrayList<String>> LUTback;
	HashMap<String, ArrayList<Character_Stroke>> LUTCharStrokes; // for
																	// thumbnail
																	// of each
																	// charactertype
	HashMap<String, float[]> Strokes;
	ArrayList<float[]> InputCharacter; // Userdrawn Character
	ArrayList<String> StrokeSequence;
	String[] mappedStrokesSequences;
	String CharacterID;
	ArrayList<BoundingBox> StrokesHeight;
	public int errorcount = 0;

	public Matcher(
			HashMap<String, ArrayList<Character_Stroke>> characterStrokes,
			HashMap<String, float[]> strokes, Context ct,
			HashMap<String, ArrayList<String>> lutback) throws Exception {
		// TODO Auto-generated constructor stub
		LUTback = lutback;
		LUTCharStrokes = characterStrokes;
		Strokes = strokes;
		// keys=Strokes.keySet();
		this.ct = ct;

	}

	public void StrokeMatchnonCentroid(String inputSequence,ArrayList<float[]> ip, String[] mappedStrokes, String CharacterID,
			ArrayList<BoundingBox> StrokeHeight) {
		this.CharacterID = CharacterID;
		this.StrokesHeight = StrokeHeight;
		mappedStrokesSequences = mappedStrokes;
		InputCharacter = ip;
		String[] keysInput = inputSequence.split(" "); //the obtained sequence of more than one strokes
		//System.out.println("testsequences"+ keysInput.toString());
		String debugsequence="";
		for(String s:keysInput)
		{
			debugsequence+=s+" ";
		}
		Log.v("debugHWRECOGNISER","CharacterID="+CharacterID);
		Log.v("debugHWRECOGNISER", "aftersplitting"+debugsequence);
		Log.v("debugHWRECOGNISER","inputsequence="+inputSequence);
		ArrayList<String> Strokesname = new ArrayList<String>(); // to store all
																	// the
																	// strokes;
		if (CharacterID.contains("38")) {  //if it contains the Character 38 i.e () then directly save it according to its height
			synchronized (Strokes) { 
				if (InputCharacter.size() == 2) {
					if (StrokesHeight.get(0).height < StrokesHeight.get(1).height) {

						LRUReplace("33", InputCharacter.get(0), Strokes);
						LRUReplace("16", InputCharacter.get(1), Strokes);

					} else {

						LRUReplace("16", InputCharacter.get(0), Strokes);
						LRUReplace("33", InputCharacter.get(1), Strokes);

					}
					return;
				}
			}
			
		}
		
		synchronized (Strokes) {
			for (int i = 0; i < keysInput.length; i++) // add all the Strokes
			{

				for (String s : Strokes.keySet()) {  // build the reduced set comprising of all the samples present in the library having the same strokes as the corrected Stroke
					String key = s;
					String a = CharLUT.getStrokename(key); // Just Strokeclass from the Main Stroke library
				
					
					String b = CharLUT.getStrokename(keysInput[i]); //Just Strokeclass from UserCorrected Stroke Seq
					if ((a != null && b != null) && (a.equals(b))	&& a.length() != 0 && b.length() != 0) {
						Log.v("debugHWRECOGNISER","building the reduced Stroke");
						Log.v("debugHWRECOGNISER","output from main library="+a);
						Log.v("debugHWRECOGNISER","output from inputStroke="+b);
						Strokesname.add(key); // add to strokesname
					}

				}
			}
			Log.v("debugHWRECOGNISER",Strokesname.toString());
		}
		
		new recogniser().execute(Strokesname); // execute the mapping

	}

	public ArrayList<String> NumStrokesSeq(String selChar, int usernumStroke) // return
																				// the
																				// number
																				// of
																				// Strokesequences
																				// matching
																				// user
																				// made
																				// Character
	{
		ArrayList<String> strokeseq = new ArrayList<String>();
		StrokeSequence = LUTback.get(selChar);
		for (String s : StrokeSequence) {
			if (s.split(" ").length == usernumStroke) {
				strokeseq.add(s);
			}
		}
		return strokeseq;
	}

	public String getSingleStrokeName(String Character) // get the StrokeName
														// corresponding to the
														// character(//for
														// single stroke)
	{
		ArrayList<String> Sequences = LUTback.get(Character);
		String SingleStroke = null;
		for (String k : Sequences) {
			if (!k.contains(" ")) {
				SingleStroke = k;
			}
		}
		return SingleStroke;
	}

	public void LRUReplace(String strokeClass, float[] strokePoints,
			HashMap<String, float[]> strokeMap, String sampleMatchName) {
		boolean x_present = false;
		int maxNum = 0, num = 0;
		for (String key : strokeMap.keySet()) {
			if (key.indexOf(strokeClass + "_x") != -1) // user-made stroke
			{
				// System.out.println(key);

				if (x_present == false)
					x_present = true;

				num = Integer.parseInt(key.substring(key.indexOf("x") + 1)); // serial
																				// number
				if (num > maxNum)
					maxNum = num;
			}

		}

		// System.out.println("maxNum = "+maxNum);

		int MAX_X = 5;
		// a. This is the first user-made sample to be added
		// OR b. if more than MAX_X, then replace least recently added stroke.
		// i.e. _x1
		// N.B. - numbering starts from 1
		if (x_present == false || maxNum >= MAX_X) {

			if (sampleMatchName.contains("_x"))
				strokeMap.put(sampleMatchName, strokePoints);
			else
				strokeMap.put(strokeClass + "_x" + (maxNum + 1), strokePoints); // add
																				// user
																				// made
																				// sample

			// deleting all non-usermade samples of that StrokeClass from
			// Library
			if (x_present == true)

			{
				boolean temp = true;
				while (temp) {
					for (String key : strokeMap.keySet()) {
						temp = false;
						if (key.contains(strokeClass) && !key.contains("_x")) {
							strokeMap.remove(key);
							temp = true;
							break;
						}
					}
				}

			}

		} else {
			strokeMap.put(strokeClass + "_x" + (maxNum + 1), strokePoints);
		}

	}

	public void LRUReplace(String strokeClass, float[] strokePoints,
			HashMap<String, float[]> strokeMap) {
		boolean x_present = false;
		int maxNum = 0, num = 0;
		for (String key : strokeMap.keySet()) {
			if (key.indexOf(strokeClass + "_x") != -1) // user-made stroke
			{
				// System.out.println(key);

				if (x_present == false)
					x_present = true;

				num = Integer.parseInt(key.substring(key.indexOf("x") + 1)); // serial
																				// number
				if (num > maxNum)
					maxNum = num;
			}

		}

		// System.out.println("maxNum = "+maxNum);

		int MAX_X = 5;
		// a. This is the first user-made sample to be added
		// OR b. if more than MAX_X, then replace least recently added stroke.
		// i.e. _x1
		// N.B. - numbering starts from 1
		if (x_present == false || maxNum >= MAX_X) {
			strokeMap.put(strokeClass + "_x1", strokePoints);

		} else {
			strokeMap.put(strokeClass + "_x" + (maxNum + 1), strokePoints);
		}

	}

	class recogniser extends AsyncTask<ArrayList<String>, Void, String> // /to
																		// perform
																		// recognition
																		// for
																		// the
																		// following
																		// Stroke
	{
		int MAX_X = 5;

		@Override
		protected String doInBackground(ArrayList<String>... params) {
			// TODO Auto-generated method stub
			// params is the Stroke numbers to be mapped
			String result;
			String Strokesadded = "";
			String debugsequence="";
			for(String s:params[0])
			{
				debugsequence+=s+" ";
			}
			/*
			Log.v("debugHWRECOGNISER","sequence of characters in reduced set");
			Log.v("debugHWRECOGNISER", debugsequence);
			*/
				synchronized (Strokes) {
					for (int i = 0; i < InputCharacter.size(); i++) {
						double minValue = Double.MAX_VALUE;
						String ClassRecognizedMin = null;

						for (int j = 0; j < params[0].size(); j++) {
							String tempClass = params[0].get(j); // temporary
																	// class
																	// name
							double score = DTWRecogniser.DTWDistance(InputCharacter.get(i),	Strokes.get(tempClass));
							if (score<minValue) {
								minValue = score; // set as minimum score
								ClassRecognizedMin = tempClass; // set as
																// minimum Score
																// corresponding
																// class
							}

						}
						if (!mappedStrokesSequences[i].equals(ClassRecognizedMin))
						{
							//Log.v("debugHWRECOGNISER",mappedStrokesSequences[i]+" "+ClassRecognizedMin);
							Strokesadded +=" " + ClassRecognizedMin; 
							LRUReplace(
									CharLUT.getStrokename(ClassRecognizedMin),InputCharacter.get(i), Strokes,ClassRecognizedMin); // add
									errorcount++; // increase the count of an error
							}

					}
					// SaveFile.WriteFile("/mnt/sdcard/HWREcogfiles/Library.dat",Strokes);
					result = "successfully added " + Strokesadded;
				}
			return result;
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			Toast.makeText(ct, result, Toast.LENGTH_SHORT).show();
		}
	}

}

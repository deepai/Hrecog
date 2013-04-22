package utils;

import java.util.ArrayList;

public class Centroid {

	public static ArrayList<float[]> computeScaled(ArrayList<float[]> strokes)
	{
		ArrayList<float[]> UserInputCentroid=new ArrayList<float[]>();
		for(int i=0;i<strokes.size();i++)
		{
			
			float[] stoke=strokes.get(i);
			/*
			 * calculate the centroid of the point
			 */
			float[] centroid=new float[2];
			for(int z=0;z<stoke.length;z+=2)
			{
				centroid[0]+=stoke[z];
			}
			for(int z=1;z<stoke.length;z+=2)
			{
				centroid[1]+=stoke[z];
			}
			centroid[0]=centroid[0]/((stoke.length/2));
			centroid[1]=centroid[0]/((stoke.length/2));
			/*
			 * end centroid computation
			 */
			UserInputCentroid.add(centroid); //add the centroid obtained
		    				
		}
		UserInputCentroid=scaleCentroid(UserInputCentroid);
		return UserInputCentroid;
	}
	public static ArrayList<float[]> scaleCentroid(ArrayList<float[]> arg)
	{
		float scale;
		float max_x=0;
		float max_y=0;
		float min_x=0;
		float min_y=0;
		/*
		 * find the maximum bottom right corner
		 */
		for(int j=0;j<arg.size();j++)
		  	{
			  if(arg.get(j)[0]>max_x)
				  max_x=arg.get(j)[0];
			  if(arg.get(j)[1]>max_y)
				  max_y=arg.get(j)[1];
		  	}
		/*
		 * find the minimum topleft
		 */
		  	min_x=max_x;
		  	min_y=max_y;
		  	for(int i=0; i<arg.size(); i++)
		  	{
		  		if(arg.get(i)[0]<min_x)
		  			min_x=arg.get(i)[0];
		  		if(arg.get(i)[1]<min_y)
		  			min_y=arg.get(i)[1];
		  	}
		  
			float width = max_x - min_x;
			float height = max_y - min_y; 
		
			//obtain the scale
			 scale = Math.max(width,height)/400;
			 for(int i=0; i<arg.size(); i++)
				 {
				arg.get(i) [0]/=scale; //x coordinate
				arg.get(i) [1]/=scale; //y coordinate
				 }
			 
			 min_x=Float.MAX_VALUE;
			 min_y=Float.MAX_VALUE;
			 
			for(int i=0; i<arg.size(); i++)
		  	{
		  		if(arg.get(i) [0]<min_x)
		  			min_x=arg.get(i) [0];
		  		if(arg.get(i) [1]<min_y)
		  			min_y=arg.get(i) [1];
		  	}
			/*
			 * TRANSLATE HERE,IF SINGLE STOKE then scaled to Infinity,
			 */ 
			//translate all points wrt min_x,min_y
			 for(int i=0; i<arg.size(); i++)
		 {
			arg.get(i) [0]-=min_x;
			arg.get(i) [1]-=min_y;
		 }			
			
			 
		return arg;
	}
}

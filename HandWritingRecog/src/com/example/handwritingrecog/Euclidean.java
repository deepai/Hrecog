package com.example.handwritingrecog;

public class Euclidean {

	public static float euclidean(float[] input,float[] meanVector,String Class,String filename)
	{
		float distance=0;
		for(int i=0;i<input.length/2;i++)
		{
			distance+=calculateDist(input[2*i],meanVector[2*i],input[2*i+1],meanVector[2*i+1]);
		}
		return distance;
	}
	public static double calculateDist(float x1, float x2, float y1,float y2){
		return ((y2-y1)*(y2-y1))+((x2-x1)*(x2-x1));//calculate the euclidean distance between each pair of consecutive points
	}
}


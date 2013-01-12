package com.example.handwritingrecog;
public class DTWRecogniser {

	public static double DTWDistance(float[] input,float[] meanVector)
	{
		int n=(input.length/2);
		int m=(meanVector.length/2);
		double[][] Dtw=new double[n+1][m+1];
		for(int i=1;i<=m;i++)
		{
			Dtw[0][i]=Double.MAX_VALUE;
		}
		for(int i=1;i<=n;i++)
		{
			Dtw[i][0]=Double.MAX_VALUE;
		}
		Dtw[0][0]=0;
		for(int i=1;i<=n;i++)
			for(int j=1;j<=m;j++)
			{
				double cost=calculateDist(input[2*(i-1)],meanVector[2*(j-1)],input[2*(i-1)+1],meanVector[2*(j-1)+1]);
				Dtw[i][j]=cost + minimum(Dtw[i-1][j],Dtw[i][j-1],Dtw[i-1][j-1]);
			}
		
		return Dtw[n][m];
	}
	public static double calculateDist(float x1, float x2, float y1,float y2){
		return ((y2-y1)*(y2-y1))+((x2-x1)*(x2-x1));//calculate the euclidean distance between each pair of consecutive points
	}
	public static double minimum(double a,double b,double c)
	{
		double d=Math.min(a, b);
		return Math.min(d, c);
	}
}

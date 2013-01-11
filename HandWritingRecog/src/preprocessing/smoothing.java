package preprocessing;

import android.util.FloatMath;

public class smoothing {
	
	public static float calculateDist(float x1, float x2, float y1,float y2){
		return FloatMath.sqrt(((y2-y1)*(y2-y1))+((x2-x1)*(x2-x1)));//calculate the euclidean distance between each pair of consecutive points
	}
	
	public static  float[] temporalsampling(float totaldistance,float[] substrokes,int number)
	{
		final float increment = (totaldistance) / (number - 1);
		int vectorLength = number*2;
		float[] vector = new float[vectorLength];
		float distanceSoFar = 0;
		float[] pts = substrokes;
		float lstPointX = pts[0];
		float lstPointY = pts[1];
		int index = 0;
		float currentPointX = Float.MIN_VALUE;
		float currentPointY = Float.MIN_VALUE;
		vector[index] = lstPointX;
		index++;
		vector[index] = lstPointY;
		index++;
		int i = 0;
		int count = (pts.length / 2);
		while ((i < count) ) {
			if (currentPointX == Float.MIN_VALUE) {
				i++;
				if (i >= count) {
					break;
				}
				currentPointX = pts[i * 2];
				currentPointY = pts[(i * 2) + 1];
			}
			float deltaX = currentPointX - lstPointX;
			float deltaY = currentPointY - lstPointY;
			float distance = FloatMath.sqrt((deltaX * deltaX) + (deltaY * deltaY));
			if ((distanceSoFar + distance) >= increment) {
				float ratio = (increment - distanceSoFar) / distance;
				float nx = lstPointX + (ratio * deltaX);
				float ny = lstPointY + (ratio * deltaY);
				vector[index] = nx;
				index++;
				vector[index] = ny;
				index++;
				lstPointX = nx;
				lstPointY = ny;
				distanceSoFar = 0;
			} else {
				lstPointX = currentPointX;
				lstPointY = currentPointY;
				currentPointX = Float.MIN_VALUE;
				currentPointY = Float.MIN_VALUE;
				distanceSoFar += distance;
			}
		}
		
		for (i = index; i < (vectorLength); i += 2) {
			vector[i] = lstPointX;
			vector[i + 1] = lstPointY;
		}
		return vector;
		
	}
	
	public static float[] smoothFunction(float[] pathscale) //
	{
		float[] newPts=threePtSmoothing(pathscale); //smooth for the first time
		float strokeTotalDistance=0;
		for(int pt=3;pt<=(newPts.length-1);pt+=2)
		{
			float y2=newPts[pt];
			float y1=newPts[pt-2];
			float x2=newPts[pt-1];
			float x1=newPts[pt-3];
			strokeTotalDistance += calculateDist(x1, x2, y1, y2); //calculate total stroke distance
		}
		float[] newpts1=temporalsampling(strokeTotalDistance,newPts,32); //construct equidistant points 
		//float[] finalPts=threePtSmoothing(newpts1);
		return newpts1;
		
	}
	
	public static float[] threePtSmoothing(float[] arr)// three point moving average(for smoothing)
	{ 
		float[] a=new float[arr.length];
		a[0]=arr[0];
		a[1]=arr[1];
		for(int i=1;i<((arr.length/2)-1);i++){
			a[2*i]=(arr[2*i-2]+arr[2*i]+arr[2*i+2])/3;
			a[2*i+1]=(arr[2*i-1]+arr[2*i+1]+arr[2*i+3])/3;
		}
		a[arr.length-2]=arr[arr.length-2];
		a[arr.length-1]=arr[arr.length-1];
		return a;
	}
	
	
}

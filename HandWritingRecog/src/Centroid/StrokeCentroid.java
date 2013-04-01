package Centroid;

import java.io.Serializable;

public class StrokeCentroid implements Serializable {
	String Strokelabel;
	float[] Centroid;
	public String getStrokelabel() {
		return Strokelabel;
	}
	public void setStrokelabel(String strokelabel) {
		Strokelabel = strokelabel;
	}
	public float[] getCentroid() {
		return Centroid;
	}
	public void setCentroid(float[] centroid) {
		Centroid = centroid;
	}
	public StrokeCentroid(String t,float[] p)
	{
		Strokelabel=t;
		Centroid=p;
	}
}

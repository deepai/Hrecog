package Character_Stroke;

import java.io.Serializable;

public class Character_Stroke implements Serializable{
	String Stroke_label=""; //Name of the class;
	float[] Stroke;
	
	public Character_Stroke(float[] t) {
		// TODO Auto-generated constructor stub
		Stroke=t;
	}
	
	public String getStroke_label() {
		return Stroke_label;
	}
	public void setStroke_label(String stroke_label) {
		Stroke_label = stroke_label;
	}
	public float[] getStroke() {
		return Stroke;
	}
	public void setStroke(float[] stroke) {
		Stroke = stroke;
	}
	
}

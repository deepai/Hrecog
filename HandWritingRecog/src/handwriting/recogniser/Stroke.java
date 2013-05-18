package handwriting.recogniser;


public class Stroke implements Comparable<Stroke>{
    public String strokeName;
    public double dist;

    public Stroke(String t, double d)
    {
    	strokeName=t;
    	dist=d;
    }

    public String getStrokeName(){
        return strokeName; 
    }
    
    public double getDist(){
        return dist; 
    }
    
    public int compareTo(Stroke s){
        
    	if( dist < s.dist )
    		return -1;
    	else 
    		return 1;
    }
	}
package handwriting.recogniser;

public class unicodeMapping implements Comparable<unicodeMapping> {
	public String Charactername;
	public String unicode;
	public unicodeMapping(String a,String b) {
		// TODO Auto-generated constructor stub
		Charactername=a;
		unicode=b;
	}
	@Override
	public int compareTo(unicodeMapping another) {
		// TODO Auto-generated method stub
		if(Integer.parseInt(this.Charactername)<Integer.parseInt(another.Charactername))
			return -1;
		else
			return 1;
	}
	
}

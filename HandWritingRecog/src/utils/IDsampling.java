package utils;

import java.util.ArrayList;
import java.util.Collections;

public class IDsampling implements Comparable<IDsampling>{

	int occurance=0;
	ArrayList<Integer> positions=new ArrayList<Integer>();
	
	@Override
	public int compareTo(IDsampling arg0) {
		// TODO Auto-generated method stub
		if(this.occurance<arg0.occurance)
			return -1;
		else if(this.occurance>arg0.occurance)
			return 1;
		else
		{
			for(int i=0;i<positions.size();i++)
			{
				if(positions.get(i)<arg0.positions.get(i))
					return -1;
				else if(positions.get(i)>arg0.positions.get(i))
					return 1;
			}
			return -1;
		}
	}
	
	public void update(int position)
	{
		positions.add(position);
		occurance++;
		Collections.sort(positions);
	}

}

package handwriting.handwritingrecog;

import java.util.ArrayList;
import java.util.Collections;


import android.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class customAdapterSingle extends ArrayAdapter<unicodeMapping> {
	ArrayList<unicodeMapping> unicodeMapper;
	
	private Context context;
	

	public customAdapterSingle(Context context, int textViewResourceId,ArrayList<unicodeMapping> temp) {
		super(context, textViewResourceId,temp);
		
		// TODO Auto-generated constructor stub
		this.context=context;
		unicodeMapper=temp;
		Collections.sort(unicodeMapper);
	}
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		final int i=position;
		if (convertView == null) {
		    // This a new view we inflate the new layout
		    LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		    convertView = inflater.inflate(R.layout.listview, parent, false);
		}
		TextView item=(TextView)convertView.findViewById(R.id.text1);
		item.setText(unicodeMapper.get(i).unicode);
		item.setTag(unicodeMapper.get(i).Charactername); //set the tag as the itemcharcter
		return convertView;
	}
	
}

package preprocessing;


public class Scaling {

	private static double height_dflt=400;
	private static double width_dflt=400;
	
	

	public static float[] scale(float[] _drawn) //scaling function to scale_down the bounding box of the shape
	{
		float[] pathscale;
		double scale=0;

		double width=0, height=0, DiffW=0, DiffH=0;
		double sWth=width_dflt,sHth=height_dflt;
		  float max_x = 0, max_y = 0, min_x=0,min_y=0;
		  int i;
		 //double scale;
		  
		  int numcount=_drawn.length/2;
		  for(int j=0;j<numcount;j++)
		  {
			  if(_drawn[2*j]>max_x)
				  max_x=_drawn[2*j];
			  if(_drawn[2*j+1]>max_y)
				  max_y=_drawn[2*j+1];
		  }
		  min_x=max_x;
		  min_y=max_y;
		 // System.out.println("Min_x="+min_x+","+"Min_y="+min_y);
		  pathscale=new float[numcount*2];
		  for(i=0; i<numcount; i++)
		  {
			
				
			if(_drawn[2*i]< min_x )
				min_x =_drawn[2*i];
				
							
			if(_drawn[2*i+1] < min_y )
				min_y = _drawn[2*i+1];
				
		  }
		  
			width = max_x - min_x;
			height = max_y - min_y; 
			//System.out.println("width="+width+","+"height="+height);
			
			if(width<=sWth&& width >=0.5*sWth&&height<=sHth&&height >=0.5*sHth) //then do only translation
			{
			
				for(i=0; i<numcount; i++) 
				{
					pathscale[i*2]=_drawn[2*i]-(float)min_x;
					//System.out.println("x:"+pathScale[i*2]);
					pathscale[i*2+1]=_drawn[2*i+1]-(float)min_y;
					//System.out.println("y:"+pathScale[i*2+1]);
				}
				 
				
			}
			else if(width>sWth||height>sHth) 
			{
				if(width<=sWth)
				{
					scale=height/sHth;
				}
				else if(height<=sHth)
				{
					scale=width/sWth;
				}
				else
				{
					DiffW = Math.abs(sWth - width);
					DiffH = Math.abs(sHth - height);
					//System.out.println("width="+DiffW+","+"height="+DiffH);
					if( DiffW > DiffH )
						scale = width/sWth;
					else
						scale = height/sHth;
				}
					
				
				width=width/scale;
				height=height/scale;
					
				//scaling of the shape
				//System.out.println("scale:"+scale);
				for(i=0; i<numcount; i++)
				{
					pathscale[i*2] =_drawn[2*i];
					pathscale[i*2]=(float)(pathscale[i*2]/scale);
					pathscale[i*2+1] =_drawn[2*i+1];
					pathscale[i*2+1]=(float)(pathscale[i*2+1]/scale);
				}
				
				
				
				
				double diff_x = min_x/scale;
				double diff_y = min_y/scale;
				
				
			
				
				for(i=0; i<numcount; i++) //final scaling of the shape(center adjustment)
				{
					pathscale[i*2]=pathscale[i*2]-(float)diff_x;
					//System.out.println("x:"+pathScale[i*2]);
					pathscale[i*2+1]=pathscale[i*2+1]-(float)diff_y;
					//System.out.println("y:"+pathScale[i*2+1]);
				}
				 
			
			}
			else
			{
				
				
					DiffW = Math.abs(sWth - width);
					DiffH = Math.abs(sHth - height);
					//System.out.println("width="+DiffW+","+"height="+DiffH);
					if( DiffW < DiffH )
						scale = sWth/width;
					else
						scale = sHth/height;
								
				
				width=width*scale;
				height=height*scale;
					
				//scaling of the shape
				//System.out.println("scale:"+scale);
				for(i=0; i<numcount; i++)
				{
					pathscale[i*2] =_drawn[2*i];
					pathscale[i*2]=(float)(pathscale[i*2]*scale);
					pathscale[i*2+1] =_drawn[2*i+1];
					pathscale[i*2+1]=(float)(pathscale[i*2+1]*scale);
				}
				
				
				
				double diff_x = min_x*scale;
				double diff_y = min_y*scale;
				
				
			
				
				for(i=0; i<numcount; i++) //final scaling of the shape(center adjustment)
				{
					pathscale[i*2]=pathscale[i*2]-(float)diff_x;
					//System.out.println("x:"+pathScale[i*2]);
					pathscale[i*2+1]=pathscale[i*2+1]-(float)diff_y;
					//System.out.println("y:"+pathScale[i*2+1]);
				}
				 
			
			}
			return pathscale;
	}
	
		
}

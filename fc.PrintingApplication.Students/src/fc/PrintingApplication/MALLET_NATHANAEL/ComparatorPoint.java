package fc.PrintingApplication.MALLET_NATHANAEL;

import java.util.Comparator;

public class ComparatorPoint implements Comparator<Point>{
	
	@Override
	public int compare(Point arg0, Point arg1) {
		if(arg0.x<arg1.x) {
			return 1;
		}
		return 0;
	}

	
}

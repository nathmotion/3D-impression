package fc.PrintingApplication.TP1;

import java.util.ArrayList;

public class Tranche {
	
	public static ArrayList<Triangle> listesPoints=new ArrayList<Triangle>();
	
	
	public Tranche(){
		listesPoints = new ArrayList<Triangle>();
	}
	
	public int nbSommets(){
		int nb= 0 + listesPoints.size()*2;
		
		return nb;
	}
}

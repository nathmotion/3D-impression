package fc.PrintingApplication.MALLET_NATHANAEL;


import java.util.ArrayList;
import java.util.List;

import com.owens.oobjloader.builder.FaceVertex;
import com.owens.oobjloader.builder.VertexGeometric;
import com.owens.oobjloader.builder.VertexNormal;

public class Triangle {
	List<VertexGeometric> sommet = new ArrayList<>();
	List<FaceVertex> pointIntersection = new ArrayList<>();
	public Triangle() {

	}
	

	public float ymin = +999;
	public float ymax = -999;

	public void maxY() {
		for (int i = 0; i < sommet.size(); i++) {
			ymax = Math.max(sommet.get(i).y, ymax);
		}

	}

	public void minY() {

		for (int i = 0; i < sommet.size(); i++) {
			ymin = Math.min(sommet.get(i).y, ymin);
		}

	}
	public float maxInter() {
		for (int i = 0; i < pointIntersection.size(); i++) {
			ymax = Math.max(sommet.get(i).y, ymax);
		}
		return ymax;
	}

	public float minInter() {

		for (int i = 0; i < pointIntersection.size(); i++) {
			ymin = Math.min(sommet.get(i).y, ymin);
		}
		return ymin;
	}
	/***
	 * 
	 * -------------------------------------- Detection d'intersection avec la
	 * tranche et Trace les points d'intersection
	 * -------------------------------------------
	 */

	public void intersectionTrancheSegment(Float pTranche) {

		float zmax = 0;
		float zmin = 0;
		int indMax = 0;
		int indMin = 0;

		for (int i = 0; i < 3; i++) {

			if (i == 2) {

				// detection de zmin et zmax

				zmax = Math.max(this.sommet.get(2).z, this.sommet.get(0).z);
				zmin = Math.min(this.sommet.get(2).z, this.sommet.get(0).z);

				// on regarde le sens du edges
				if (zmax == sommet.get(0).z) {

					indMax = 0;
					indMin = 2;

				} else {

					indMax = 2;
					indMin = 0;
				}
			} else {

				zmax = Math.max(this.sommet.get(i).z, this.sommet.get(i + 1).z);
				zmin = Math.min(this.sommet.get(i).z, this.sommet.get(i + 1).z);

				if (zmax == this.sommet.get(i).z) {

					indMax = i;
					indMin = i + 1;

				} else {

					indMax = i + 1;
					indMin = i;
				}
			}

			if ((pTranche > zmin && pTranche < zmax)) // ** test qu'il y a possibilit� d'intersection pour cette face
			{
				// **Theoreme de thales
				float t = (pTranche - this.sommet.get(indMin).z)
						/ (this.sommet.get(indMax).z - this.sommet.get(indMin).z);

				if (t <= 1 && t >= 0) {
					float x = 0;
					float y = 0;
					x = this.sommet.get(indMin).x + (this.sommet.get(indMax).x - this.sommet.get(indMin).x) * t;
					y = this.sommet.get(indMin).y + (this.sommet.get(indMax).y - this.sommet.get(indMin).y) * t;
					FaceVertex tmp = new FaceVertex();
					tmp.n = new VertexNormal(0, 0, 0);
					tmp.v = new VertexGeometric(0, 0, 0);
					tmp.v.x = x;
					tmp.v.y = y;
					tmp.v.z = pTranche;
					this.pointIntersection.add(tmp);

				} // end if

			} // end if

		} // end For

	}// end

	

	

}

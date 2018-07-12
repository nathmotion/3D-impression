package fc.PrintingApplication.Students;

import java.util.ArrayList;
import java.util.List;

import com.owens.oobjloader.builder.FaceVertex;
import com.owens.oobjloader.builder.VertexGeometric;
import com.owens.oobjloader.builder.VertexNormal;

public class Triangle {
	List<FaceVertex> vertex = new ArrayList<>();
	List<FaceVertex> pointIntersection = new ArrayList<>();

	public Triangle() {

	}

	public Triangle(FaceVertex v1, FaceVertex v2, FaceVertex v3) {
		this.vertex = new ArrayList<>();
		vertex.add(v1);
		vertex.add(v2);
		vertex.add(v3);
	}

	public float ymin = +999;
	public float ymax = -999;

	public void maxY() {
		for (int i = 0; i < vertex.size(); i++) {
			ymax = Math.max(vertex.get(i).v.y, ymax);
		}

	}

	public void minY() {

		for (int i = 0; i < vertex.size(); i++) {
			ymin = Math.min(vertex.get(i).v.y, ymin);
		}

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

				zmax = Math.max(this.vertex.get(2).v.z, this.vertex.get(0).v.z);
				zmin = Math.min(this.vertex.get(2).v.z, this.vertex.get(0).v.z);

				// on regarde le sens du edges
				if (zmax == vertex.get(0).v.z) {

					indMax = 0;
					indMin = 2;

				} else {

					indMax = 2;
					indMin = 0;
				}
			} else {

				zmax = Math.max(this.vertex.get(i).v.z, this.vertex.get(i + 1).v.z);
				zmin = Math.min(this.vertex.get(i).v.z, this.vertex.get(i + 1).v.z);

				if (zmax == this.vertex.get(i).v.z) {

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
				float t = (pTranche - this.vertex.get(indMin).v.z)
						/ (this.vertex.get(indMax).v.z - this.vertex.get(indMin).v.z);

				if (t <= 1 && t >= 0) {
					float x = 0;
					float y = 0;
					x = vertex.get(indMin).v.x + (this.vertex.get(indMax).v.x - this.vertex.get(indMin).v.x) * t;
					y = vertex.get(indMin).v.y + (this.vertex.get(indMax).v.y - this.vertex.get(indMin).v.y) * t;
					FaceVertex tmp = new FaceVertex();
					tmp.n=new VertexNormal(0, 0, 0);
					tmp.v= new VertexGeometric(0, 0, 0);
					tmp.v.x = x;
					tmp.v.y = y;
					tmp.v.z = pTranche;
					this.pointIntersection.add(tmp);

				} // end if

			} // end if

		} // end For

	}// end

	public List<FaceVertex> getVertex() {
		return vertex;
	}

	public void setVertex(List<FaceVertex> vertex) {
		this.vertex = vertex;
	}

	public void addVertex(FaceVertex v) {
		// TODO Auto-generated method stub
		vertex.add(v);
	}

	@Override
	public String toString() {
		return "Triangle [vertex=" + vertex + "]";
	}

}

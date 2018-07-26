package fc.PrintingApplication.Students;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.attribute.FileOwnerAttributeView;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.text.StyledEditorKit.ForegroundAction;

import com.owens.oobjloader.builder.Build;
import com.owens.oobjloader.builder.Face;
import com.owens.oobjloader.builder.FaceVertex;
import com.owens.oobjloader.builder.VertexGeometric;
import com.owens.oobjloader.builder.VertexNormal;
import com.owens.oobjloader.parser.Parse;

public class Application {
	List<Tranche> listTranche = new ArrayList<>();
	List<Face> listeFaceObjet = new ArrayList<>();
	List<FaceVertex> listeSommetObjet = new ArrayList<>();
	static final int widthImg = 300;
	static final int heightImg = 300;
	static float zminObj;
	static float zmaxObj;

	public static void main(String[] argv) {
		//
		// This is just example code.
		// Do not use a resolution of 100x100 for your slices.
		// As discussed during the course, the resolution match to 1 pixel = 0.05
		// millimeter
		// and the bitmap should enclose the bounding box X/Y plane, along the Z axis.
		//
		// if(nameFile != null) {
		//
		// }else {
		String name = "CuteOcto.obj";
		// }
		Application application = new Application();
		application.parseObjFile(name);
		application.traceTranche(application.listeSommetObjet, application.listeFaceObjet);

		BufferedImage bi = new BufferedImage(widthImg, widthImg, BufferedImage.TYPE_INT_RGB);
		Graphics2D g = bi.createGraphics();
		g.drawLine(10, 15, 35, 40);
		g.dispose();

		try {
			ImageIO.write(bi, "png", new File("myImage.png"));
		} catch (IOException e) {
			System.out.println("Error saving image ");

		}

		System.out.println("Program terminated.");
	}

	//
	// Here is some code to read and parse OBJ files.
	//
	public void parseObjFile(String filename) {
		try {
			Build builder = new Build();
			Parse obj = new Parse(builder, new File(filename).toURI().toURL());

			// Enumeration of vertices

			for (FaceVertex vertex : builder.faceVerticeList) {
				float x = vertex.v.x;
				float y = vertex.v.y;
				float z = vertex.v.z;
				listeSommetObjet.add(vertex);
			}
			// Enumeration of faces (a face is a triangle fan. Often, but not always, it
			// only consists of 1 single triangle.)
			for (Face face : builder.faces) {
				// Enumerate triangles in this face

				for (int i = 1; i <= (face.vertices.size() - 2); i++) {
					int vertexIndex1 = face.vertices.get(0).index;
					int vertexIndex2 = face.vertices.get(i).index;
					int vertexIndex3 = face.vertices.get(i + 1).index;

					FaceVertex vertex1 = builder.faceVerticeList.get(vertexIndex1);
					FaceVertex vertex2 = builder.faceVerticeList.get(vertexIndex2);
					FaceVertex vertex3 = builder.faceVerticeList.get(vertexIndex3);
					// Please examine the FaceVertex class and other types for more information on
					// how to use things
					listeFaceObjet.add(face);

					// it is up to I am going to need to change certain things about posting my
					// content to you guy's, a fair bit of censoring will be needed on here it
					// seem's but it shouldn't effect the final production.
				}
			}
		} catch (java.io.FileNotFoundException e) {
			System.out.println("FileNotFoundException loading file " + filename + ", e=" + e);
			e.printStackTrace();
		} catch (java.io.IOException e) {
			System.out.println("IOException loading file " + filename + ", e=" + e);
			e.printStackTrace();
		}
	}

	public static void updateTriangleTrancheObjet() {

	}

	public void traceTranche(List<FaceVertex> listeSommetObjet, List<Face> listeFaceObjet) {
		zminObj = 999;
		zmaxObj = 0;
		// parcours la liste complète des tous les sommet de l'Objet pour trouve le min
		// et max de l'axis de Z
		for (FaceVertex f : listeSommetObjet) {
			zminObj = Math.min(zminObj, f.v.z);
			zmaxObj = Math.max(zmaxObj, f.v.z);
		}

		float pTranche = zminObj;
		System.out.println("zmax" + zmaxObj);
		System.out.println("zmin" + zminObj);
		float currentTranche = 0;
		BufferedImage currentTrancheImage;
		while (pTranche < zmaxObj) {
			Tranche tranche = new Tranche();
			currentTranche++;
			currentTrancheImage = new BufferedImage(widthImg, heightImg, BufferedImage.TYPE_INT_RGB);
			Graphics2D g = currentTrancheImage.createGraphics();
			for (Face face : listeFaceObjet) {
				Triangle triangle = new Triangle();
				for (FaceVertex vertex : face.vertices) {
					triangle.sommet.add(new VertexGeometric(vertex.v.x, vertex.v.y, vertex.v.z));
				}
				triangle.intersectionTrancheSegment(pTranche);
				g.setColor(Color.GREEN);
				if (triangle.pointIntersection.size() == 2) {
					tranche.listetrianglesTrancheObjet.add(triangle);
					listTranche.add(tranche);
				}

			}
			currentTrancheImage = traceTranche(currentTrancheImage, tranche.listetrianglesTrancheObjet, g);
			currentTrancheImage = remplissageScanline(currentTrancheImage);
			g.dispose();
			try {
				ImageIO.write(currentTrancheImage, "png", new File("tranche" + currentTranche + ".png"));
			} catch (IOException e) {
				System.out.println("Error saving image ");

			}

			pTranche = (float) (pTranche + 0.20);
			System.out.println("Tranche position " + pTranche);
		}
	}

	static BufferedImage traceTranche(BufferedImage im, List<Triangle> listetriangle, Graphics2D g) {
		System.out.println(" tranche size " + listetriangle.size());
		for (Triangle triangle : listetriangle) {
			int x1 = (int) ((triangle.pointIntersection.get(0).v.x + 20) * 700) / 100;
			int y1 = (int) ((triangle.pointIntersection.get(0).v.y + 20) * 700) / 100;
			int x2 = (int) ((triangle.pointIntersection.get(1).v.x + 20) * 700) / 100;
			int y2 = (int) ((triangle.pointIntersection.get(1).v.y + 20) * 700) / 100;
			g.setColor(Color.CYAN);
			g.drawLine(x1, y1, x2, y2);
			// im.setRGB(x1, y1, Color.GREEN.getRGB());
			// im.setRGB(x2, y2, Color.G.getRGB());
		}
		return im;
	}

	BufferedImage remplissageScanline(BufferedImage im) {

		int ymin = 999;
		int ymax = 0;
		// parcours tout les *
		for (int i = 0; i < im.getHeight(); i++) {
			for (int j = 0; j < im.getWidth(); j++) {
				int yCurrent = i;
				if (ymin > yCurrent && im.getRGB(j, i) == Color.CYAN.getRGB()) {
					ymin = yCurrent;
				}
				if (ymax < yCurrent && im.getRGB(j, i) == Color.CYAN.getRGB()) {
					ymax = yCurrent;
				}
			}
		}
		for (int i = 0; i < listTranche.size(); i++)
			intersectionSegment(i, ymin, ymax);

		for (int i = ymin + 1; i < ymax; i++) {

		}
		return im;
	}

	public void intersectionSegment(int pTranche, int zmin, int zmax) {

		float yMax = 0;
		float yMin = 999;
		int indMax=0;
		int indMin=1;
		for (Triangle triangle : listTranche.get(pTranche).listetrianglesTrancheObjet) {
			float xP1 = triangle.pointIntersection.get(0).v.x;
			float yP1 = triangle.pointIntersection.get(0).v.y;
			float xP2 = triangle.pointIntersection.get(1).v.x;
			float yP2 = triangle.pointIntersection.get(1).v.y;

			yMax = Math.max(Math.max(yMax, yP1), yP2);
			yMin = Math.min(Math.min(yMax, yP1), yP2);
			if(yP1>yP2) {
				indMax=0;
				indMin=1;
			}
			else {
				indMax=1;
				indMin=0;
			}
			xP1 = triangle.pointIntersection.get(indMin).v.x;
			 yP1 = triangle.pointIntersection.get(indMin).v.y;
			 xP2 = triangle.pointIntersection.get(indMax).v.x;
			 yP2 = triangle.pointIntersection.get(indMax).v.y;
			if ((pTranche > yMin && pTranche < yMax)) {
				float t = (pTranche-yP1)/(yP2-yP1);
				if(t<=1 && t>=0) {
					float x=0;
					float y=0;
					x= xP1+ (xP2-xP1)*t;
					y=pTranche;
					
				}
			}
		}

		// if ((pTranche > zmin && pTranche < zmax)) // ** test qu'il y a possibilit�
		// d'intersection pour cette face
		// {
		// // **Theoreme de thales
		// float t = (pTranche - this.sommet.get(indMin).z)
		// / (this.sommet.get(indMax).z - this.sommet.get(indMin).z);
		//
		// if (t <= 1 && t >= 0) {
		// float x = 0;
		// float y = 0;
		// x = this.sommet.get(indMin).x + (this.sommet.get(indMax).x -
		// this.sommet.get(indMin).x) * t;
		// y = this.sommet.get(indMin).y + (this.sommet.get(indMax).y -
		// this.sommet.get(indMin).y) * t;
		// FaceVertex tmp = new FaceVertex();
		// tmp.n = new VertexNormal(0, 0, 0);
		// tmp.v = new VertexGeometric(0, 0, 0);
		// tmp.v.x = x;
		// tmp.v.y = y;
		// tmp.v.z = pTranche;
		// this.pointIntersection.add(tmp);
		//
		// } // end if
		//
		// } // end if
		//
		// } // end For

	}// end

}

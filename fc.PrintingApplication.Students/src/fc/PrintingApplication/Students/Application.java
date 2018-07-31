package fc.PrintingApplication.Students;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack;

import javax.imageio.ImageIO;

import com.owens.oobjloader.builder.Build;
import com.owens.oobjloader.builder.Face;
import com.owens.oobjloader.builder.FaceVertex;
import com.owens.oobjloader.builder.VertexGeometric;
import com.owens.oobjloader.parser.Parse;

public class Application {
	List<Tranche> listTranche = new ArrayList<>();
	List<Point> listePointIntersectTranche = new ArrayList<>();
	List<Face> listeFaceObjet = new ArrayList<>();
	List<FaceVertex> listeSommetObjet = new ArrayList<>();
	static final int widthImg = 300;
	static final int heightImg = 300;
	static float zminObj;
	static float zmaxObj;
	static Polygon polygon = new Polygon();

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
		application.doTranche(application.listeSommetObjet, application.listeFaceObjet);

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

	public void doTranche(List<FaceVertex> listeSommetObjet, List<Face> listeFaceObjet) {
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
			listePointIntersectTranche = new ArrayList<>();
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
					reductionPointIntersection(triangle);
					tranche.listetrianglesTrancheObjet.add(triangle);
					listTranche.add(tranche);
				}

			}
			System.out.println("n point tranche" + tranche.listetrianglesTrancheObjet.size());
			System.out.println(" n reduit" + listePointIntersectTranche.size());
			currentTrancheImage = traceLineTranche(currentTrancheImage, tranche.listetrianglesTrancheObjet, g);
		//	currentTrancheImage = remplissageScanline( currentTrancheImage);
			currentTrancheImage=remplissage(tranche.listetrianglesTrancheObjet,currentTrancheImage );

			g.dispose();
			polygon = new Polygon();
			listePointIntersectTranche = new ArrayList<>();
			try {
				ImageIO.write(currentTrancheImage, "png", new File("tranche" + currentTranche + ".png"));
			} catch (IOException e) {
				System.out.println("Error saving image ");

			}

			pTranche = (float) (pTranche + 0.20);
			System.out.println("Tranche position " + pTranche);
		}
		
	}

	static BufferedImage traceLineTranche(BufferedImage im, List<Triangle> listetriangle, Graphics2D g) {
		System.out.println(" tranche size " + listetriangle.size());
		for (Triangle triangle : listetriangle) {
			int x1 = (int) ((triangle.pointIntersection.get(0).v.x + 20) * 700) / 100;
			int y1 = (int) ((triangle.pointIntersection.get(0).v.y + 20) * 700) / 100;
			int x2 = (int) ((triangle.pointIntersection.get(1).v.x + 20) * 700) / 100;
			int y2 = (int) ((triangle.pointIntersection.get(1).v.y + 20) * 700) / 100;
			g.setColor(Color.CYAN);
			g.drawLine(x1, y1, x2, y2);
<<<<<<< HEAD

		}

		return im;
	}

	boolean isInside(int x, int y, List<Triangle> triangles) {
		boolean res = false;
		int j = triangles.size() - 1;
		for (int i = 0; i < triangles.size(); j = i++) {
			if ((triangles.get(i).pointIntersection.get(0).v.y > 0) != (triangles.get(j).pointIntersection
					.get(0).v.y > 0)
					&& (x < (triangles.get(j).pointIntersection.get(0).v.x
							- triangles.get(i).pointIntersection.get(0).v.x)
							* (y - triangles.get(i).pointIntersection.get(0).v.y)
							/ (triangles.get(j).pointIntersection.get(0).v.y
									- triangles.get(i).pointIntersection.get(0).v.y
									+ triangles.get(i).pointIntersection.get(0).v.y))) {

				res=!res;
=======
		}
		//im=remplissage(listetriangle,im);
		return im;
	}

	static BufferedImage remplissage(List<Triangle> triangles, BufferedImage im) {

		float ymin = 999;
		float ymax = -999;

		// parcours tout les *
		for (int i = 0; i < triangles.size(); i++) {
			for (int j = 0; j < triangles.get(i).pointIntersection.size(); j++) {
				ymin = Math.min(ymin, triangles.get(i).pointIntersection.get(j).v.y);
				ymax = Math.max(ymax, triangles.get(i).pointIntersection.get(j).v.y);
>>>>>>> 0aff2b40120098d565e3a28e1fa1c7df2e7415ab
			}
		}
		return res;
	}
<<<<<<< HEAD


	public BufferedImage intersectionSegment(List<Triangle> triangles, float ymin, float ymax, BufferedImage im) {

		int indMax = 0;
		int indMin = 1;

		int ymaxInt = (int) ((ymax + 20) * 700) / 100;
		int yminInt = (int) ((ymin + 20) * 700) / 100;
		for (Triangle triangle : triangles) {
			float xP1 = triangle.pointIntersection.get(0).v.x;
			float yP1 = triangle.pointIntersection.get(0).v.y;
			float xP2 = triangle.pointIntersection.get(1).v.x;
			float yP2 = triangle.pointIntersection.get(1).v.y;
			if (yP1 > yP2) {
				indMax = 0;
				indMin = 1;
			} else {
				indMax = 1;
				indMin = 0;
=======
	BufferedImage remplissageScanline(BufferedImage im) {
 		int ymin = 999;
		int ymax = 0;
		
		// parcours tout les *
		for (int i = 0; i < im.getHeight(); i++) {
			for (int j = 0; j < im.getWidth(); j++) {
				int yCurrent = i;
				if (ymin > yCurrent && im.getRGB(j, i)==Color.CYAN.getRGB()) {
					ymin = yCurrent;
				}
				if (ymax < yCurrent && im.getRGB(j, i)==Color.CYAN.getRGB()) {
					ymax = yCurrent;
				}
>>>>>>> 0aff2b40120098d565e3a28e1fa1c7df2e7415ab
			}
		}
	
		for (int i = ymin; i < ymax; i++) {
			int countArretetouche = 0;
			for (int j = 0; j < im.getWidth(); j++) {
				int colorCurrent =im.getRGB(j, i);
				
				
				if (colorCurrent == Color.CYAN.getRGB()) {
					if(im.getRGB(j+1,i)!=Color.CYAN.getRGB()) {
					countArretetouche++;
					}
				}
				if (countArretetouche % 2 != 0) {
					if(im.getRGB(j,i)!=Color.CYAN.getRGB())
						{
						im.setRGB(j, i, Color.red.getRGB());
						}
				}
				
			}
		}
		return im;
	}
	public static BufferedImage intersectionSegment(List<Triangle> triangles, float ymin, float ymax, BufferedImage im) {

		int yPolymin = (int) ((ymin + 20) * 700) / 100;
		int yPolymax = (int) ((ymax + 20) * 700) / 100;

		for (int pas = yPolymin; pas < yPolymax; pas++) {
			float xmin = +999;
			float xmax = -999;
			int cptX = 0;
	        ArrayList<Point> intersectionPoints = new ArrayList<>();

			for (Triangle arrete : triangles) {
				int x1, x2, y1, y2;
	            double deltax, deltay, x;
	            x1 =(int) ((arrete.pointIntersection.get(0).v.x + 20) * 700) / 100;
	            y1 = (int) ((arrete.pointIntersection.get(0).v.y + 20) * 700 / 100);
	            x2 = (int) ((arrete.pointIntersection.get(1).v.x + 20) * 700) / 100;
	            y2 = (int) ((arrete.pointIntersection.get(1).v.y + 20) * 700 / 100);
				
				deltax=x2-x1;
				deltay=y2-y1;
				int roundedx;
				x=x1+deltax/deltay*(pas-y1);
				roundedx=(int) Math.round(x);
				if((y1<=pas && y2>pas) || (y2<=pas && y1>pas))
				{
					intersectionPoints.add(new Point(roundedx, pas));
				}
			}
			int x1, x2, y1, y2;
			x1 =(int) ((triangles.get(0).pointIntersection.get(0).v.x + 20) * 700) / 100;
            y1 = (int) ((triangles.get(0).pointIntersection.get(0).v.y + 20) * 700 / 100);
            x2 = (int) ((triangles.get(triangles.size()-1).pointIntersection.get(1).v.x + 20) * 700) / 100;
            y2 = (int) ((triangles.get(triangles.size()-1).pointIntersection.get(1).v.y + 20) * 700 / 100);
            if ((y1 <= pas && y2 > pas) || (y2 <= pas && y1 > pas)) {
                intersectionPoints.add(new Point(x1 + (x2 - x1) / (y2 - y1) * pas - y1, pas));
            }
            Collections.sort(intersectionPoints,new ComparatorPoint());
            for(int i=0;i<intersectionPoints.size()-1;i=i+2) {
            	 x1=intersectionPoints.get(i).x;
            	 y1=intersectionPoints.get(i).y;
            	 x2=intersectionPoints.get(i+1).x;
            	 y2=intersectionPoints.get(i+1).y;
            	 System.out.println(" Point "+intersectionPoints.size());
            
            	Graphics2D g= im.createGraphics();
            	g.setColor(Color.red);
            	g.drawLine(x1, y1, x2, y2);
            }
		}
				
			/*	if (pas < yMaxArrete && pas > yMinArrete) {
					double Dy = yMaxArrete - yMinArrete;
					double Dx = x1-x2;
					int a = -Dy;
					int b = Dx;
					int c = (Dy * (int) ((arrete.pointIntersection.get(0).v.x + 20) * 700) / 100
							- (int) ((arrete.pointIntersection.get(0).v.x + 20) * 700) / 100);
					int x = (int) ((b * pas) - c / a);
					xmin = Math.min(xmin, x);
					xmax = Math.max(xmax, x);
					cptX++;
				}

			}

			if (cptX >= 2) {

<<<<<<< HEAD
}
	
/*	public void floodFill(int x, int y, Color fillColor) {

		Stack<Point> callStack = new Stack<>();
		callStack.add(new Point(x, y));
		while (!callStack.isEmpty()) {
			Point point = callStack.pop();
			if (isInside(point.x, point.y)) {

				if (mImage.getRGB(point.X, point.Y) != fillColor.getRGB()) {
					System.out.println("adding point " + point.toString());
					mImage.setRGB(point.X, point.Y, fillColor.getRGB());
					repaint();
					revalidate();
					callStack.add(new Point(point.X + 1, point.Y));
					callStack.add(new Point(point.X - 1, point.Y));
					callStack.add(new Point(point.X, point.Y + 1));
					callStack.add(new Point(point.X, point.Y - 1));
				}
			}
		}
=======
				int xLineMin = (int) ((xmin + 20) * 700) / 100;
				int xLineMax = (int) ((xmax) + 20) * 700 / 100;
				xLineMin++;
				xLineMax--;
				int y = (int) ((pas + 20) * 700) / 100;
				// System.out.println(" xmin = " + xLineMin + " xmax = " + xLineMax);
				im.setRGB(xLineMin, y, Color.red.getRGB());
				im.setRGB(xLineMax, y, Color.red.getRGB());
				
			}*/

		
>>>>>>> 0aff2b40120098d565e3a28e1fa1c7df2e7415ab

		return im;
	}*/

}

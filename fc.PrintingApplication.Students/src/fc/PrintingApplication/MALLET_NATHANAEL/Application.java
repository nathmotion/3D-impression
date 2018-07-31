package fc.PrintingApplication.MALLET_NATHANAEL;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.Buffer;
import java.nio.file.attribute.FileOwnerAttributeView;
import java.util.ArrayList;
import java.util.Collections;
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

		String name = "CuteOcto.obj";

		Application application = new Application();
		application.parseObjFile(name);
		application.doTranche(application.listeSommetObjet, application.listeFaceObjet);

		BufferedImage bi = new BufferedImage(widthImg, widthImg, BufferedImage.TYPE_INT_RGB);
		Graphics2D g = bi.createGraphics();
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
		int currentTranche = 0;
		BufferedImage currentTrancheImage;
		while (pTranche < zmaxObj) {
			// NOUVELLE TRANCHE
			Tranche tranche = new Tranche();
			currentTranche++;
			currentTrancheImage = new BufferedImage(widthImg, heightImg, BufferedImage.TYPE_INT_RGB);
			Graphics2D g = currentTrancheImage.createGraphics();
			for (Face face : listeFaceObjet) {

				// ON RECUPERE LES VERTICES DE L'OBJET
				Triangle triangle = new Triangle();
				for (FaceVertex vertex : face.vertices) {
					triangle.sommet.add(new VertexGeometric(vertex.v.x, vertex.v.y, vertex.v.z));
				}

				// FONCTION QUI DETECTE LES POINTS D'INTERSECTION POUR CE TRIANGLE ET LES
				// STOCKENT DANS UNE LISTE
				triangle.intersectionTrancheSegment(pTranche);
				if (triangle.pointIntersection.size() == 2) {
					tranche.listetrianglesTrancheObjet.add(triangle);
					listTranche.add(tranche);
				}
			}
			currentTrancheImage = traceLineTranche(currentTrancheImage, tranche.listetrianglesTrancheObjet, g);
			currentTrancheImage = remplissage(tranche.listetrianglesTrancheObjet, currentTrancheImage);
			g.dispose();
			try {
				String numFormatTranche = String.format("%03d", currentTranche);
				ImageIO.write(currentTrancheImage, "png", new File("tranche" + numFormatTranche + ".png"));
			} catch (IOException e) {
				System.out.println("Error saving image ");
			}
			pTranche = (float) (pTranche + 0.20);
			System.out.println("Tranche position " + pTranche);
		}

	}

	static BufferedImage traceLineTranche(BufferedImage im, List<Triangle> listetriangle, Graphics2D g) {

		// ON PARCOURS TOUS LES TRIANGLES INTERSECTES ET ON TRACE LE SEGMENT DEFINI PÄR
		// LES DEUX POINT
		for (Triangle triangle : listetriangle) {
			int x1 = (int) ((triangle.pointIntersection.get(0).v.x + 20) * 700) / 100;
			int y1 = (int) ((triangle.pointIntersection.get(0).v.y + 20) * 700) / 100;
			int x2 = (int) ((triangle.pointIntersection.get(1).v.x + 20) * 700) / 100;
			int y2 = (int) ((triangle.pointIntersection.get(1).v.y + 20) * 700) / 100;
			g.setColor(Color.RED);
			g.drawLine(x1, y1, x2, y2);
		}
		return im;
	}

	static BufferedImage remplissage(List<Triangle> triangles, BufferedImage im) {

		float ymin = 999;
		float ymax = -999;

		// DETECTION YMIN ET YMAX
		for (int i = 0; i < triangles.size(); i++) {
			for (int j = 0; j < triangles.get(i).pointIntersection.size(); j++) {
				ymin = Math.min(ymin, triangles.get(i).pointIntersection.get(j).v.y);
				ymax = Math.max(ymax, triangles.get(i).pointIntersection.get(j).v.y);
			}
		}

		im = scanline(triangles, ymin, ymax, im);

		return im;
	}

	public static BufferedImage scanline(List<Triangle> triangles, float ymin, float ymax, BufferedImage im) {

		int yPolymin = Math.round(((ymin + 20) * 700) / 100);
		int yPolymax = Math.round(((ymax + 20) * 700) / 100);
		
		// ON PARCOURS SUR LA LIGNE Y DE LA TRANCHE
		for (int pas = yPolymin; pas < yPolymax; pas++) {
			ArrayList<Point> intersectionPoints = new ArrayList<>();
			
			// POUR CHAQUE TRIANGLE INTERSECTE = ARRETE
			for (Triangle arrete : triangles) {
				int x1, x2, y1, y2;
				double deltax, deltay, x;
				x1 = Math.round(((arrete.pointIntersection.get(0).v.x + 20) * 700) / 100);
				y1 = Math.round(((arrete.pointIntersection.get(0).v.y + 20) * 700) / 100);
				x2 = Math.round(((arrete.pointIntersection.get(1).v.x + 20) * 700) / 100);
				y2 = Math.round(((arrete.pointIntersection.get(1).v.y + 20) * 700) / 100);

				deltax = x2 - x1;
				deltay = y2 - y1;
				int roundedx;
				x = x1 + deltax / deltay * (pas - y1);
				roundedx = (int) Math.round(x);
				if ((y1 <= pas && y2 > pas) || (y2 <= pas && y1 > pas)) {
					intersectionPoints.add(new Point(roundedx, pas));
				}
			}
			int x1, x2, y1, y2;
			x1 = Math.round(((triangles.get(0).pointIntersection.get(0).v.x + 20) * 700) / 100);
			y1 = Math.round(((triangles.get(0).pointIntersection.get(0).v.y + 20) * 700) / 100);
			x2 = Math.round(((triangles.get(triangles.size() - 1).pointIntersection.get(1).v.x + 20) * 700) / 100);
			y2 = Math.round(((triangles.get(triangles.size() - 1).pointIntersection.get(1).v.y + 20) * 700) / 100);

			// ONR REGARDE SI L'ARRETE COUPE LA LIGNE DU Y CURRENT
			if ((y1 <= pas && y2 > pas) || (y2 <= pas && y1 > pas)) {
				intersectionPoints.add(new Point(x1 + (x2 - x1) / (y2 - y1) * pas - y1, pas));
			}

			// ON TRIE LES POINT INTERSECT
			Collections.sort(intersectionPoints, new ComparatorPoint());

			// POUR CHAQUE POINT ON TRACE LA LINE
			for (int i = 0; i < intersectionPoints.size() - 1; i = i + 2) {
				x1 = intersectionPoints.get(i).x;
				y1 = intersectionPoints.get(i).y;
				x2 = intersectionPoints.get(i + 1).x;
				y2 = intersectionPoints.get(i + 1).y;
				Graphics2D g = im.createGraphics();
				g.setColor(Color.red);
				//System.out.println(" trace " + x1 + "," + y1 + "  ->" + x2 + "," + y2);
				g.drawLine(x1, y1, x2, y2);
			}
		}
		return im;
	}
}
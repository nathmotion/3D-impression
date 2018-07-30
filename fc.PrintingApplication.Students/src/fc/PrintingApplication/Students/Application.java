package fc.PrintingApplication.Students;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.Buffer;
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
			currentTrancheImage = traceLineTranche(currentTrancheImage, tranche.listetrianglesTrancheObjet, g);
			currentTrancheImage = remplissageScanline(tranche.listetrianglesTrancheObjet, currentTrancheImage);
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

	static BufferedImage traceLineTranche(BufferedImage im, List<Triangle> listetriangle, Graphics2D g) {
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

	BufferedImage remplissageScanline(List<Triangle> triangles, BufferedImage im) {

		float ymin = 999;
		float ymax = -999;

		// parcours tout les *
		for (int i = 0; i < triangles.size(); i++) {
			for (int j = 0; j < triangles.get(i).pointIntersection.size(); j++) {
				ymin = Math.min(ymin, triangles.get(i).pointIntersection.get(j).v.y);
				ymax = Math.max(ymax, triangles.get(i).pointIntersection.get(j).v.y);
			}
		}

		im = intersectionSegment(triangles, ymin, ymax, im);

		return im;
	}

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
			}

			xP1 = triangle.pointIntersection.get(indMin).v.x;
			yP1 = triangle.pointIntersection.get(indMin).v.y;
			xP2 = triangle.pointIntersection.get(indMax).v.x;
			yP2 = triangle.pointIntersection.get(indMax).v.y;
		}

		for (int pas = (int) ((ymin + 20) * 700) / 100; pas < (int) ((ymax + 20) * 700) / 100; pas++) {
			float xmin = +999;
			float xmax = -999;
			int cptX = 0;
			for (Triangle arrete : triangles) {
				if (pas >= (int) ((arrete.maxInter() + 20) * 700) / 100
						&& pas <= (int) ((arrete.minInter() + 20) * 700) / 100) {
					continue;
				} else {
					float Dy = arrete.pointIntersection.get(1).v.y - arrete.pointIntersection.get(0).v.y;
					float Dx = arrete.pointIntersection.get(1).v.x - arrete.pointIntersection.get(0).v.x;
					float a = -Dy;
					float b = Dx;
					float c = (Dy * arrete.pointIntersection.get(0).v.x) - (Dx * arrete.pointIntersection.get(0).v.y);
					float x = ((b * pas) - c / a);
					xmin = Math.min(xmin, x);
					xmax = Math.max(xmax, x);
					cptX++;
				}

			}
			System.out.println(" x = "+cptX);

			if(cptX==2) {
				for(int x=(int)((xmin+20)*700)/100;x<(int)((xmax)+20)*700/100;x++) {
					im.setRGB(x, pas, Color.RED.getRGB());
				}
			}

		}

		/*
		 * if(pas>yP1 && pas<yP2) {
		 * 
		 * float t = (pas- yP1) / (yP2 - yP1);
		 * 
		 * if (t <= 1 && t >= 0) { float x = 0; float y = 0; x = xP1 + (xP2 - xP1) * t;
		 * y = pas; listX.add(x); if(listX.size()==2) { float xmax =
		 * Math.max(Math.max(0, listX.get(0)), listX.get(1)); float xmin =
		 * Math.min(Math.min(999, listX.get(0)), listX.get(1)); int IndXmax = (int)
		 * ((xmax + 20) * 700) / 100; ; int IndXmin = (int) ((xmin + 20) * 700) / 100; ;
		 * IndXmin++; IndXmax--; for (int j = IndXmin; j <= IndXmax; j++) { im.setRGB(j,
		 * (int) ((pas+20)*700)/100, Color.red.getRGB()); }
		 * 
		 * } } }
		 */

		return im;

	}
}

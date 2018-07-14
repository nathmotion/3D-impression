package fc.PrintingApplication.Students;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import com.owens.oobjloader.builder.Build;
import com.owens.oobjloader.builder.Face;
import com.owens.oobjloader.builder.FaceVertex;
import com.owens.oobjloader.builder.VertexGeometric;
import com.owens.oobjloader.parser.Parse;

public class Application {
	List<Triangle> trianglesObjet = new ArrayList<>();
	static final int widthImg = 100;
	static final int heightImg = 100;
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
		application.traceTranche(application.trianglesObjet);

		BufferedImage bi = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
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
					Triangle triangle = new Triangle(vertex1, vertex2, vertex3);
					trianglesObjet.add(triangle);

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

	public void traceTranche(List<Triangle> triangleO) {
		zminObj = 999;
		zmaxObj = 0;

		for (Triangle triangle : triangleO) {
			for (FaceVertex f : triangle.getVertex()) {
				zminObj = Math.min(zminObj, f.v.z);
				zmaxObj = Math.max(zmaxObj, f.v.z);
			}

		}
		float pTranche = zminObj;
		System.out.println("zmax" + zmaxObj);
		System.out.println("zmin" + zminObj);
		float currentTranche = 0;
		BufferedImage currentTrancheImage;
		while (pTranche < zmaxObj) {
			currentTranche++;
			currentTrancheImage = new BufferedImage(widthImg, heightImg, BufferedImage.TYPE_INT_RGB);
			Graphics2D g = currentTrancheImage.createGraphics();
			for (Triangle triangle : triangleO) {
				triangle.intersectionTrancheSegment(pTranche);
				g.setColor(Color.GREEN);
				if(triangle.pointIntersection.size()!=0) {
					VertexGeometric p1 = triangle.pointIntersection.get(0).v;
					VertexGeometric p2 = triangle.pointIntersection.get(1).v;
					g.drawLine((int)p1.x+20,(int) p1.y+20,(int) p2.x+20,(int) p2.y+20);
				}
			
			}
		
			g.dispose();
			try {
				ImageIO.write(currentTrancheImage, "png", new File("tranche"+currentTranche+".png"));
			} catch (IOException e) {
				System.out.println("Error saving image ");

			}

			pTranche = (float) (pTranche + 0.20);
			System.out.println("Tranche position " + pTranche);
		}
	}

}

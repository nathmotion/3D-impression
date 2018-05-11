package fc.PrintingApplication.Students;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

import com.owens.oobjloader.builder.Build;
import com.owens.oobjloader.builder.Face;
import com.owens.oobjloader.builder.FaceVertex;
import com.owens.oobjloader.parser.Parse;

public class Application
{
    public static void main(String[] argv)
    {
    	//
    	// This is just example code.
    	// Do not use a resolution of 100x100 for your slices.
    	// As discussed during the course, the resolution match to 1 pixel = 0.05 millimeter
    	// and the bitmap should enclose the bounding box X/Y plane, along the Z axis.
    	//
		BufferedImage bi = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);

		Graphics2D g = bi.createGraphics();
		
		g.drawLine(10, 15, 35, 40);
		
		g.dispose();
		
		try
		{
			ImageIO.write(bi, "png", new File("myImage.png"));
		}
		catch (Exception e)
		{
			System.out.println(e.toString());
		}
    	
    	System.out.println("Program terminated.");
    }
    
	//
	// Here is some code to read and parse OBJ files.
	//
	public void parseObjFile(String filename)
	{
	    try
	    {
	        Build builder = new Build();
	        Parse obj = new Parse(builder, new File(filename).toURI().toURL());
	        
	        // Enumeration of vertices
	        
	        for (FaceVertex vertex : builder.faceVerticeList)
	        {
	        	float x = vertex.v.x;
		    	float y = vertex.v.y;
	        	float z = vertex.v.z;
	        	
	        	// ...
	        }

	        // Enumeration of faces (a face is a triangle fan. Often, but not always, it only consists of 1 single triangle.)
	        
	        for (Face face : builder.faces)
	        {
	        	// Enumerate triangles in this face
	        	
	        	for (int i=1; i <= (face.vertices.size() - 2); i++)
	        	{
	        		int vertexIndex1 = face.vertices.get(0).index;
	        		int vertexIndex2 = face.vertices.get(i).index;
	        		int vertexIndex3 = face.vertices.get(i+1).index;
	        		
	        		FaceVertex vertex1 = builder.faceVerticeList.get(vertexIndex1);
	        		FaceVertex vertex2 = builder.faceVerticeList.get(vertexIndex2);
	        		FaceVertex vertex3 = builder.faceVerticeList.get(vertexIndex3);
	        		// Please examine the FaceVertex class and other types for more information on how to use things
	        		
	        		// it is up to you here to do whatever you want...
	        	}
	        }
	    }
	    catch (java.io.FileNotFoundException e)
	    {
	    	System.out.println("FileNotFoundException loading file "+filename+", e=" + e);
	        e.printStackTrace();
	    }
	    catch (java.io.IOException e)
	    {
	    	System.out.println("IOException loading file "+filename+", e=" + e);
	        e.printStackTrace();
	    }
	}
}

package fc.PrintingApplication.TP1;


import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;


import com.owens.oobjloader.builder.Build;
import com.owens.oobjloader.builder.Face;
import com.owens.oobjloader.builder.FaceVertex;
import com.owens.oobjloader.builder.VertexGeometric;
import com.owens.oobjloader.parser.Parse;
import java.awt.Graphics2D;


public class Main
{

	
	static Graphics2D g;
	static BufferedImage im = null;
	static BufferedImage imgtranche = null;
	static int sizeX=800;
	static int sizeY=800;
	static Graphics2D g2d ;
	

	
	

																				 //   PAS DE LA TRANCHE EN Z
	static float pasTranche = (float)0.2;

	public static ArrayList<Tranche> listeTranches =new ArrayList<Tranche>();  // toutes les tranche découpers
	
	 public static Tranche t= new Tranche();
	
	public static ArrayList<FaceVertex> Sommetobjet=new ArrayList<FaceVertex>();//tout les sommet de lobjet charge
	
	
	
	static ArrayList<Face> FaceObjet=new ArrayList<Face>();						// tout les triangle de l'objet charge
	

	
	
	public static void main(String[] argv)
	{
		float boiteEnglobantX[]=new float[2] ;
		float boiteEnglobantY[]=new float[2] ;
		float boiteEnglobantZ[]=new float[2] ;
	///	Matrix.createOrtho(xmin, xmax,ymin, ymax,zmin zmax); 
		
		
										//  ***          NOM  OBJET  *** 
		String name="CuteOcto.obj";
		
		im= new BufferedImage(sizeX,sizeY,BufferedImage.TYPE_INT_RGB);
		
		imgtranche= new BufferedImage(sizeX,sizeY,BufferedImage.TYPE_INT_RGB);
		g2d = imgtranche.createGraphics();
		g2d.setBackground(Color.WHITE);
	    g2d.fillRect(0, 0, sizeX, sizeY);
		
	    
										// *** 		Initialitsation de l'image BMP    *** 
		for( int x=0;x<sizeX;x++ ){
			for( int y=0; y<sizeY; y++ ){
				
						im.setRGB( x, y, Color.BLACK.getRGB() );
			
			}
		}
		
		// on recupere les traingle  > face ?
		// on faier un boite englobante  on agrandie un peu dans le cas d'un cube 
		// on parcour l'axe des y (ou Z) et on ignore les faces/triangles qui sont pas intersecter par la tranche 
		// on doit calcul les point des des intersection avec la tranche ...puis on es stocker dans un tableau qu'on trace sur un bitmp
							
		
							//  ***   Recuperation de l'objet  *** 
		parseObjFile(name);
		
							
		
		/*try {
			
			ImageIO.write( im, "png", new File( "./Test.bmp" ) );
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		
		float min = 5000;
		float max = -5000;
		float minY = 5000;
		float maxY = -5000;
		float minZ = 10;
		float maxZ = 0;
		
						 // *** on recupere des boites englobant   ***
		
		for(FaceVertex vertex : Sommetobjet ){
				minZ = Math.min( minZ,vertex.v.z );
				maxZ = Math.max( maxZ, vertex.v.z );
		}
		
		
	
	
		// 
		//------------------------------------------------------ Parcours chaque de face( triangle ) pour determiné les tranches sur les Z-----------------
		
		float pTranche = minZ;
		
		int j = 0;
		
		System.out.println( "zmax"+ maxZ );
		System.out.println( "zmin"+ minZ );
		
		while( pTranche<maxZ ){
			
			t = new Tranche();	
			
			
			imgtranche = new BufferedImage( sizeX, sizeY, BufferedImage.TYPE_INT_RGB );
			
			//g = imgtranche.createGraphics();
			
			//g.setColor( Color.GREEN );
			
			for( Face f:FaceObjet ){
				
				Triangle triangle= new Triangle();
		
				triangle.sommet.add(new Vec3f(f.vertices.get(0).v.x,f.vertices.get(0).v.y,f.vertices.get(0).v.z));
				triangle.sommet.add(new Vec3f(f.vertices.get(1).v.x,f.vertices.get(1).v.y,f.vertices.get(1).v.z));
				triangle.sommet.add(new Vec3f(f.vertices.get(2).v.x,f.vertices.get(2).v.y,f.vertices.get(2).v.z));

				
				 triangle.intersectionTrancheSegment( pTranche );
				
				 if(triangle.pointIntersection.size()==2){ // triangle intersecté ou non
					 
					 t.listesPoints.add(triangle);  // 		on recupere que les triangle intersecter par la tranche
					
				 }
				
				
			}
			
			
			/***            On affiches les differentes tranches ( actuelles et precedentes)           ****/
			
			
			
			listeTranches.add(t);
			
			Color c = new Color(255,255,0);
			
			// la tranche actuelle
			traceTranche(imgtranche, t , c);
			
		
			pTranche = (float) ( pTranche+0.20 );
			//System.out.println(" .... pTRanche /"+maxZ +" = "+ pTranche);
			try {
				
				j++;
				
				ImageIO.write( imgtranche, "png", new File( "./tranche0"+j+".png" ) );
			
			} catch (IOException e) {
				
				// TODO Auto-generated catch block
				e.printStackTrace();
				
				System.out.println( "error" );
			}
	
	
		
			
		}
}// end main 
	
	
	
	/**
	 * 				----------------------------------------                  REMPLISSAGE     ----------------------------------------------------------
	 * **/
/*	public static BufferedImage Remplissage(BufferedImage im,PolygoneT p){
		int i;
		int x;

		int ymax=+9999;
		int ymin=-9999;
		
		
		for(int s=0 ;s<(p.sommets.size()*2)-1;s++){ // chaque sommet
			if(p.sommets.get(s).y >= ymax  ){
				ymax=(int)p.sommets.get(s).y;
				
			}
			if(p.sommets.get(s).y <= ymin  ){
				ymin=(int)p.sommets.get(s).y;
						
			}
			p.arretes.get(s).p1= new VertexGeometric( p.sommets.get(s).x, p.sommets.get(s).y, p.sommets.get(s).z );
			p.arretes.get(s).p2= new VertexGeometric( p.sommets.get(s).x, p.sommets.get(s).y, p.sommets.get(s).z );
		}
		for(int y=ymin;y<ymax;y++){ // ymin & ymax
			int xmin=-9999;
			int xmax=+9999;
			for(int k=0;k< p.arretes.size();k++){// chaque arrete
				if( y<= p.arretes.get(k).getMax() && y>=p.arretes.get(k).getMin() ){
					continue;
				}
				else{
					Equation eq =new Equation(p.arretes.get(k));
					 x = ((eq.b*y)- eq.c/eq.a);
					xmin=Math.min(xmin, x);
					xmax=Math.max(xmax, x);
				}//fin if
			}//fin for
			
			for(x=xmin;x<=xmax;x++){
				//tracer(x,y)
				im.setRGB(x,y, Color.WHITE.getRGB());
			}
			
		}//fin for
		return im;
	}
*/
	static void RemplissageTranche(BufferedImage im, Tranche tranche, Color c){
		int y;
		int x;
		ArrayList<Segment2f> segment=  new ArrayList<Segment2f>();
		Tranche tmp= tranche;
		float ymax=+9999;
		float ymin=-9999;
		
			//  region  detection 
			// parcours du ymin jusquan ymax, si on trouve un y on defini une nouvelle zone qui sera de ymin a ymax et on ajoute les arretes de chaque zones
			// on detect les intersection a chaque scanline de ymin a ycourant et on remplie la ligne
			// a la fin du scanline y courant on supprime une arrete remplitotalement et on defini la zone suivant de y courant a y suivant
		
		// detection de la boite englobante de la tranche en 2D , on recuper ymin et ymax
		for( int s=0 ;s<tmp.listesPoints.size();s++){
			
			for(int i =0;i<3;i++){
			
				ymin =  Math.min(tmp.listesPoints.get(s).sommet.get(i).y,ymin);
				ymax =  Math.min(tmp.listesPoints.get(s).sommet.get(i).y,ymax);
			
			}
		}
		//-----------------------------------------------------------------------------
		
		for( float i=ymin;i<ymax;i++){
			
			// on 
			
		}
		
	}
	
	
	/***
	 * 
	 * --------------------------------------   				TRACAGE  			---------------------------------------------
	 */
	
	static BufferedImage traceTranche(BufferedImage im, Tranche tranche,Color c){
		System.out.println(" tranche size "+ tranche.listesPoints.size());
	for(Triangle t : tranche.listesPoints)
		bresenhamLine(im,(t.pointIntersection.get(0).x+40)*5,(t.pointIntersection.get(1).x+40)*5,(t.pointIntersection.get(0).y+40)*5,(t.pointIntersection.get(1).y+40)*5,c);
			
		return im;
	}

	/***
	 * 	--------------------------------------			 TRACAGE LIGNE BRESENHAM       -------------------------------------------------------------
	 * **/
	

	static BufferedImage bresenhamLine(BufferedImage im,float x1,float x2,float y1, float y2,Color c){
		int x=(int)x1;
		int y=(int)y1;
		int dx= (int)(x2-x1);
		int dy=(int)(y2-y1);
		int xinc=(dx>0)?1:-1;
		int yinc=(dy>0)?1:-1;
		dx=Math.abs(dx);
		dy=Math.abs(dy);
		im.setRGB((int)x1,(int)y1, c.getRGB());
		im.setRGB((int)x2,(int)y2, c.getRGB());
		im.setRGB(x,y, c.getRGB());
		if(dx>dy){
		 int cumul=dx/2;
		 for(int i=1;i<=dx;i++){
			 x+=xinc;
			 cumul+=dy;
			 if(cumul>=dx){
				cumul-=dx;
				y+=yinc;
			 }

		  		im.setRGB(x,y, c.getRGB()); 

		 }
		}
		else{
			int cumul = dy / 2 ;

			    for (int i = 1 ; i <= dy ; i++ ) {
			      y += yinc ;
			      cumul += dx ;
			      if ( cumul >= dy ) {
			        cumul -= dy ;
			        x += xinc ; }
			 
			  		im.setRGB(x,y, c.getRGB()); 
			  	}
			   }		
				
				
		
		
		
		return im;
		
	}

	
	/**---------------------------------------------   Récuperation des donnée de l'objet ----------------------------------------------------------*/

	public static void parseObjFile(String filename)
	{
		im= new BufferedImage(sizeX,sizeY,BufferedImage.TYPE_INT_RGB);

	    try
	    {
	        Build builder = new Build();
	        Parse obj = new Parse(builder, new File(filename).toURI().toURL());
	        
	        // Enumeration des sommets
	        
 	        for (FaceVertex vertex : builder.faceVerticeList)
	        {
	        	float x = vertex.v.x;
		    	float y = vertex.v.y;
	        	float z = vertex.v.z;
	        	//...
	        	//im.setRGB((int)((x+50)*8),(int)((y+50)*8), Color.WHITE.getRGB());
	        	
	        	Sommetobjet.add(vertex);
	        }

	        // Enumeration des faces (souvent des triangles, mais peuvent comporter plus de sommets dans certains cas)
	        
	        for (Face face : builder.faces)
	        {
	        	// Parcours des triangles de cette face
	        	for (int i=1; i <= (face.vertices.size() - 2); i++)
	        	{
	        		
	        		int vertexIndex1 = face.vertices.get(0).index;
	        		int vertexIndex2 = face.vertices.get(i).index;
	        		int vertexIndex3 = face.vertices.get(i+1).index;
	        		
	        		FaceVertex vertex1 = builder.faceVerticeList.get(vertexIndex1);
	        		FaceVertex vertex2 = builder.faceVerticeList.get(vertexIndex2);
	        		FaceVertex vertex3 = builder.faceVerticeList.get(vertexIndex3);
	        		
	        		// ...
	        		FaceObjet.add(face);
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

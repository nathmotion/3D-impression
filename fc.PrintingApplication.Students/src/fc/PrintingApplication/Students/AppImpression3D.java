package fc.PrintingApplication.Students;

public class AppImpression3D {

/*	private static final String Sommetobjet = null;
	static Graphics2D g;
	static BufferedImage im = null;
	static BufferedImage imgtranche = null;
	static int sizeX = 800;
	static int sizeY = 800;
	float boiteEnglobantX[] = new float[2];
	float boiteEnglobantY[] = new float[2];
	float boiteEnglobantZ[] = new float[2];

	void excute(List<FaceVertex> sommetObjet,List<FaceVertex>) {
		// *** NOM OBJET ***

		float min = 5000;
		float max = -5000;
		float minY = 5000;
		float maxY = -5000;
		float minZ = 10;
		float maxZ = 0;
		*/
						 // *** on recupere des boites englobant   ***
		
		/*for(FaceVertex vertex : sommetObjet ){
				minZ = Math.min( minZ,vertex.v.z );
				maxZ = Math.max( maxZ, vertex.v.z );
		}
			
		// 
		//------------------------------------------------------ Parcours chaque de face( triangle ) pour determin� les tranches sur les Z-----------------
		
		float pTranche = minZ;
		
		int j = 0;
		
		System.out.println( "zmax"+ maxZ );
		System.out.println( "zmin"+ minZ );
		
		while( pTranche<maxZ ){
			
			t = new Tranche();	
			
			
			imgtranche = new BufferedImage( sizeX, sizeY, BufferedImage.TYPE_INT_RGB );
			
			//g = imgtranche.createGraphics();
			
			//g.setColor( Color.GREEN );
			
			for( FaceVertex f:FaceObjet ){
				
				Triangle triangle= new Triangle();
		
				triangle.sommet.add(new FaceVertex(f.vertices.get(0).v.x,f.vertices.get(0).v.y,f.vertices.get(0).v.z));
				triangle.sommet.add(new FaceVertex(f.vertices.get(1).v.x,f.vertices.get(1).v.y,f.vertices.get(1).v.z));
				triangle.sommet.add(new FaceVertex(f.vertices.get(2).v.x,f.vertices.get(2).v.y,f.vertices.get(2).v.z));

				
				 triangle.intersectionTrancheSegment( pTranche );
				
				 if(triangle.pointIntersection.size()==2){ // triangle intersect� ou non
					 
					 t.listesPoints.add(triangle);  // 		on recupere que les triangle intersecter par la tranche
					
				 }
				
				
			}
			*/
			
			/***            On affiches les differentes tranches ( actuelles et precedentes)           ****/
			
			
			
			/*listeTranches.add(t);
			
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
	}

	BufferedImage init() {

		im = new BufferedImage(sizeX, sizeY, BufferedImage.TYPE_INT_RGB);
		imgtranche = new BufferedImage(sizeX, sizeY, BufferedImage.TYPE_INT_RGB);
		g = imgtranche.createGraphics();
		g.setBackground(Color.WHITE);
		g.fillRect(0, 0, sizeX, sizeY);
		g.drawLine(10, 15, 35, 40);
		g.dispose();

		for (int x = 0; x < sizeX; x++) {
			for (int y = 0; y < sizeY; y++) {
				im.setRGB(x, y, Color.BLACK.getRGB());
			}
		}
		return im;
	}
	
	*/
}

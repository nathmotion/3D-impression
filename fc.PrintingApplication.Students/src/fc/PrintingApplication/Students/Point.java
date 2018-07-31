package fc.PrintingApplication.Students;

public class Point {
<<<<<<< HEAD
	float x;
	float y;

=======
	int x;
	int y;
	
	
	
	
	@Override
	public String toString() {
		return "Point [x=" + x + ", y=" + y + "]";
	}
>>>>>>> 0aff2b40120098d565e3a28e1fa1c7df2e7415ab
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Float.floatToIntBits(x);
		result = prime * result + Float.floatToIntBits(y);
		return result;
	}
<<<<<<< HEAD

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Point other = (Point) obj;
		if (Float.floatToIntBits(x) != Float.floatToIntBits(other.x))
			return false;
		if (Float.floatToIntBits(y) != Float.floatToIntBits(other.y))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Point [x=" + x + ", y=" + y + "]";
	}

	public Point(float x, float y) {
=======
	public boolean equals(Point obj) {
		if (obj == null)
			return false;
		if (x != obj.x)
			return false;
		if (y != obj.y)
			return false;
		return true;
	}
	
	public Point(int x, int y) {
>>>>>>> 0aff2b40120098d565e3a28e1fa1c7df2e7415ab
		super();
		this.x = x;
		this.y = y;
	}
<<<<<<< HEAD

	public float getX() {
		return x;
	}

	public void setX(float x) {
		this.x = x;
	}

	public float getY() {
		return y;
	}

	public void setY(float y) {
		this.y = y;
	}

=======
	public int getX() {
		return x;
	}
	public void setX(int x) {
		this.x = x;
	}
	public float getY() {
		return y;
	}
	public void setY(int y) {
		this.y = y;
	}
	
	
>>>>>>> 0aff2b40120098d565e3a28e1fa1c7df2e7415ab
}

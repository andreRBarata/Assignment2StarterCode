interface CallBack {
	public void run();
}

boolean isNull(Object o1, Object o2) {
	return (o1 == null || o2 == null);
}

class Shape extends ArrayList<PVector> {
	
	Shape() {
		super();
	}
	
	Shape(Shape shape) {
		super(shape);
	}
	
	Shape(PVector[] shape) {
		super(Arrays.asList(shape));
	}
	
	Shape clone() {
		Shape shape = new Shape();
		
		for (PVector point: this) {
			shape.add(point.get());
		}
		
		return shape;
	}
	
	Shape rotate(float degrees) {
		Shape shape = (Shape)this.clone();
		
		for (int i = 0; i < shape.size(); i++) {
			shape.get(i).rotate(degrees);
		}
		
		return shape;
	}
	
	Shape transpose(PVector val) {
		Shape shape = (Shape)this.clone();
		
		for (int i = 0; i < shape.size(); i++) {
			shape.get(i).add(val);
		}
		
		return shape;
	}
	
	Shape scale(PVector val) {
		Shape shape = (Shape)this.clone();
	
		for (int i = 0; i < shape.size(); i++) {
			shape.get(i).x *= val.x;
			shape.get(i).y *= val.y;
		}
		
		return shape;
	}
	
	Shape scale(float val) {
		Shape shape = (Shape)this.clone();
	
		for (int i = 0; i < shape.size(); i++) {
			shape.get(i).mult(val);
		}
		
		return shape;
	}
	
	float maxWidth() {
		float minWidth = 0;
		float maxWidth = 0;
		for (int i = 0; i < this.size(); i++) {
			if (i == 0 || this.get(i).x > maxWidth) {
				maxWidth = this.get(i).x;
			}
			
			if (i == 0 || this.get(i).x < minWidth) {
				minWidth = this.get(i).x;
			}
		}
		
		return maxWidth - minWidth;
	}
	
	float maxHeight() {
		float minHeight = 0;
		float maxHeight = 0;
		for (int i = 0; i < this.size(); i++) {
			if (i == 0 || this.get(i).y > maxHeight) {
				maxHeight = this.get(i).y;
			}
			
			if (i == 0 || this.get(i).y < minHeight) {
				minHeight = this.get(i).y;
			}
		}
		
		return maxHeight - minHeight;
	}
}

class Drawable {
	PVector position;
	Shape shape;
	
	Drawable(PVector position, Shape shape) {
		this.position = position;
		this.shape = shape;
	}
	
	void draw() {
		int i = 0;

		Shape shapeInSpace = shape.transpose(position);

		while (i < shape.size()) {
			PVector current1;
			PVector current2;
			
			current1 = shapeInSpace.get(
				i % shape.size()
			);	
			
			i++;

			current2 = shapeInSpace.get(
				i % shape.size()
			);
			
			line(
				current1.x,
				current1.y,
				current2.x,
				current2.y
			);
		}
	}
}

boolean inLine(PVector point, PVector[] line) {
	return  (
			(x > max(line[0].x, line[1].x)) &&
			(x < min(line[0].x, line[1].x))
		);	
}

PVector intersectionLines(PVector[] line1, PVector[] line2) {
	float m1 = (
		line1[0].y - line1[1].y /
		line1[0].x - line1[1].x
	);
	float m2 = (
		line2[0].y - line2[1].y /
		line2[0].x - line2[1].x
	);

	if (m1 != m2) {
		float b1 = line1[0].y - m1 * line1[0].x;
		float b2 = line2[0].y - m2 * line2[0].x;
		
		float x = (
			(b2 - b1) / (m1 - m2)
		);
		float y = m1 * x + b1;
		
		PVector intersection = new PVector(
			x,
			y
		);
		
		if (inLine(intersection, line1) && inLine(intersection, line2)) {
			return intersection;
		}
		else {
			return null;
		}
	}
	else {
		return null;
	}
}

Shape collider(Drawable p1, Drawable p2) {
	Shape shapeInSpace1 = (Shape)(p1.shape).clone();
	Shape shapeInSpace2 = (Shape)(p2.shape).clone();
	Shape intersection = new Shape();
	
	shapeInSpace1.transpose(p1.position);
	shapeInSpace2.transpose(p2.position);
	
	for (int i = 0; i < shapeInSpace1.size(); i++) {
		for (int e = 0; e < shapeInSpace2.size(); e++) {
			int nextIndex1 = (i + 1) % shapeInSpace1.size();
			int nextIndex2 = (e + 1) % shapeInSpace2.size();
		
			PVector intersectionPoint = intersectionLines(
				new PVector[] {
					shapeInSpace1.get(i),
					shapeInSpace1.get(nextIndex1)
				},
				new PVector[] {
					shapeInSpace2.get(i),
					shapeInSpace2.get(nextIndex2)
				}
			);
			
			if (intersectionPoint != null) {
				intersection.add(intersectionPoint);
			}
		}
	}
	
	return intersection;
}

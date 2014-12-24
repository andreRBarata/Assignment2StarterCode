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
	
	PVector highestX() {
		PVector highest = new PVector();
		
		for (int i = 0; i < this.size(); i++) {
			if (i == 0 || this.get(i).x > highest.x) {
				highest = this.get(i);
			}
		}
		
		return highest;
	}
	
	PVector lowestX() {
		PVector lowest = new PVector();
		
		for (int i = 0; i < this.size(); i++) {
			if (i == 0 || this.get(i).x < lowest.x) {
				lowest = this.get(i);
			}
		}
		
		return lowest;
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
			(point.x < max(line[0].x, line[1].x)) &&
			(point.x > min(line[0].x, line[1].x))
		);	
}

float lineSlope(PVector[] line) {
	return (
		(line[1].y - line[0].y) /
		(line[1].x - line[0].x)
	);
}

PVector getIntersection(PVector[] line1, PVector[] line2) {
	float m1 = lineSlope(line1);
	float m2 = lineSlope(line2);

	if (Float.isNaN(m1)) {
		float b2 = line2[0].y - m2 * line2[0].x;
		
		return new PVector(
			line1[0].x,
			m2 * line1[0].x + b2
		);
	}
	else if (Float.isNaN(m2)) {
		float b1 = line1[0].y - m2 * line1[0].x;
		
		return new PVector(
			line2[0].x,
			m1 * line2[0].x + b1
		);
	}
	else {
		if (m1 != m2) {
			float b1 = line1[0].y - m1 * line1[0].x;
			float b2 = line2[0].y - m2 * line2[0].x;
		
			float x = (
				(b2 - b1) / (m1 - m2)
			);
			float y = m1 * x + b1;
		
			return new PVector(
				x,
				y
			);
		}
		else {
			return null;
		}
	}
}

PVector intersectionInline(PVector[] line1, PVector[] line2) {
	PVector intersection = getIntersection(line1, line2);
	
	if (intersection != null) {	
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

float lineAngle(PVector[] line1, PVector[] line2) {
	return (
		atan2((line2[1].y - line2[0].y), (line2[1].x - line2[0].x)) -
		atan2((line1[1].y - line1[0].y), (line1[1].x - line1[0].x))
	);
}

Poligon collider(Drawable p1, Drawable p2) {
	Poligon toReturn = new Poligon();
	Shape shapeInSpace1 = (Shape)(p1.shape)
		.transpose(p1.position);
	Shape shapeInSpace2 = (Shape)(p2.shape)
		.transpose(p2.position);
	Shape intersection = new Shape();
	
	for (int i = 0; i < shapeInSpace1.size(); i++) {
		for (int e = 0; e < shapeInSpace2.size(); e++) {
			PVector[] line1 = {
				shapeInSpace1.get(i),
				shapeInSpace1.get(
					(i + 1) % shapeInSpace1.size()
				)
			};
			PVector[] line2 = {
				shapeInSpace2.get(e),
				shapeInSpace2.get(
					(e + 1) % shapeInSpace2.size()
				)
			};
		
			PVector intersectionPoint = intersectionInline(
				line1,
				line2
			);
			
			if (intersectionPoint != null) {
				line(0, intersectionPoint.y, width, intersectionPoint.y);
				line(intersectionPoint.x, 0, intersectionPoint.x, height);
			
				intersection.add(intersectionPoint);
				toReturn.put("obj1_line", new Shape(line1));
				toReturn.put("obj2_line", new Shape(line2));
			}
		}
	}
	
	toReturn.put("intersection", intersection);
	
	
	return toReturn;
}

interface CallBack {
	public void run();
}

interface Vectorial {
	public void draw();
	public int size();

	public PVector[] getLine(int i);
	public PVector center();
	public float getRadius();
	public int lineCount();

	public Vectorial transpose(PVector val);
	public Vectorial rotate(float degrees);
}

boolean isNull(Object o1, Object o2) {
	return (o1 == null || o2 == null);
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

Shape collider(Drawable p1, Drawable p2) {
	Shape toReturn = new Shape();
	Vectorial spriteInSpace1 = p1.sprite
		.transpose(p1.position);
	Vectorial spriteInSpace2 = p2.sprite
		.transpose(p2.position);
	Poligon intersection = new Poligon();
	
	for (int i = 0; i < spriteInSpace1.size(); i++) {
		for (int e = 0; e < spriteInSpace2.size(); e++) {
			PVector[] line1 = spriteInSpace1.getLine(i);
			PVector[] line2 = spriteInSpace2.getLine(e);
				
			PVector intersectionPoint = intersectionInline(
				line1,
				line2
			);
			
			if (intersectionPoint != null) {
				intersection.add(intersectionPoint);
				toReturn.put("obj1_line", new Poligon(line1));
				toReturn.put("obj2_line", new Poligon(line2));
			}
		}
	}
	
	toReturn.put("intersection", intersection);
	
	
	return toReturn;
}

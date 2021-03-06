interface CallBack {
	public void run();
}

interface Vectorial {
	public void draw();
	public int size();
	public Vectorial clone();

	public PVector[] getLine(int i);
	public PVector center();
	public float getRadius();
	public float getArea();
	public boolean contains(Object point);
	public int count();

	public Vectorial transpose(PVector val);
	public Vectorial rotate(float degrees);
	public Vectorial scale(float val);
	public Vectorial scale(PVector val);
}

PVector gravity(Droppable p, Drawable m) {
	return new PVector(
		- sin(
			HALF_PI + atan2(
				p.position.y - m.position.y,
				p.position.x - m.position.x
			)
		) * (gravity/frameRate),
		- cos(
			HALF_PI + atan2(
				p.position.y - m.position.y,
				p.position.x - m.position.x
			)
		) * (gravity/frameRate)
	);
}

PVector yToProcessing(PVector point) {
	return new PVector(
		point.x,
		-point.y
	);
}

boolean isNull(Object o1, Object o2) {
	return (o1 == null || o2 == null);
}

boolean equalApproximately(PVector p1, PVector p2) {
	return (
		(abs(p1.x - p2.x) < 0.0001) &&
		(abs(p1.y - p2.y) < 0.0001)
	);
}


boolean inBox(PVector point, PVector[] line) {
	/*print("Starting in box", point);
	//println(line );
	//println("y >= min", point.y, min(line[0].y, line[1].y));*/
	if (point.y >= min(line[0].y, line[1].y)-0.00001) {
		//println("y <=max", point.y, max(line[0].y, line[1].y));
		if (point.y <= max(line[0].y, line[1].y)+0.00001) {
			//println("x >= min", point.x, min(line[0].x, line[1].x));
			if (point.x >= min(line[0].x, line[1].x)-0.00001) {
				//println("x <= max", point.x, max(line[0].x, line[1].x));
				if (point.x <= max(line[0].x, line[1].x)+0.00001) {
					return true;
				}
			}
		}
	}
	
	return false;
}

/*boolean inBox(PVector point, PVector[] line) {
	return inLine(point, line);
}*/

boolean inLine(PVector point, PVector[] line) {
	float m = lineSlope(line);
	float b = line[0].y - m * line[0].x;
	//print("point",point);
	//print("line");
	//println(line);
	if ((Float.isNaN(m) || Float.isInfinite(m))) {
		return (
			(abs(point.x - line[0].x) <= 0.0000001) &&
			(point.y <= max(line[0].y, line[1].y)) &&
			(point.y >= min(line[0].y, line[1].y))
		);
	}
	else {
		return  (	
				((m * point.x + b) == point.y) &&
				(point.y <= max(line[0].y, line[1].y)) &&
				(point.y >= min(line[0].y, line[1].y))
			);
	}	
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
	//////println("ms",m1,m2);
	if ((Float.isNaN(m1) || Float.isInfinite(m1)) && (Float.isNaN(m2) || Float.isInfinite(m2)) ) {
		//////println("NaN1");
		return null;
	}
	else if (Float.isNaN(m1) || Float.isInfinite(m1)) {
		float b2 = line2[0].y - m2 * line2[0].x;
		//////println("NaN2", b2,m2);
		//////println(line2);
		return new PVector(
			line1[0].x,
			m2 * line1[0].x + b2
		);
	}
	else if (Float.isNaN(m2) || Float.isInfinite(m2)) {
		float b1 = line1[0].y - m1 * line1[0].x;
		//////println("NaN3");
		return new PVector(
			line2[0].x,
			m1 * line2[0].x + b1
		);
	}
	else {
		//////println("NaN4");
		if (abs(m1 - m2) > 0.00001) {
			PVector intersection;
			//////println("NaN5");
			float b1 = line1[0].y - m1 * line1[0].x;
			float b2 = line2[0].y - m2 * line2[0].x;
			
			intersection = new PVector(
				(b2 - b1) / (m1 - m2),
				m1 * ((b2 - b1) / (m1 - m2)) + b1
			);
			//////println(intersection);
			return intersection;
		}
		else {
			//////println("NaN6");
			return null;
		}
	}
}

PVector intersectionInline(PVector[] line1, PVector[] line2) {
	PVector intersection = getIntersection(line1, line2);
	
	if (intersection != null) {
		//println("point", intersection);
		if (inBox(intersection, line1) && inBox(intersection, line2)) {
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

boolean pointInVectorial(PVector point, Vectorial sprite) {
	ArrayList<PVector> tested = new ArrayList<PVector>();
	int count = 0;
	boolean isInside = false;
	
	//println("Begining pointInVectorial: is point", point, "in polygon", sprite);
	
	if (sprite.contains(point)) {
		//println("point is one of the vertices of the poligon");
		isInside = true;
	}
	else {
		int i = 0;
		while (i < sprite.count() && !isInside) {
			PVector[] side = sprite.getLine(i);
			
			////print("testing side", i);
			//println(side);
			
			if (inLine(point, side)) {
				//println("point is over side");
				isInside = true;
			}
			else {
				//println("point is not over side, testing further");
				PVector[] probe = new PVector[] {
					point.get(),
					new PVector(new Float(point.x), 0)
				};
				PVector intersection = getIntersection(
					probe,
					side
				);
				//println("found intersection", intersection);
				if (intersection != null) {
					//println("testing",intersection.y, point.y);
					//println("testing2",intersection);
					//println("testing3", (intersection.y < point.y), !tested.contains(intersection));
					//println(side);
					if (intersection.y < point.y && !tested.contains(intersection)) {
						//if (inLine(intersection, side)) {
						//println("testing y>min:", intersection.y , min(side[0].y, side[1].y));
						if (inBox(intersection, side)) {
							//println("Counting", point);
							tested.add(intersection);
							count++;
							//println("Count is now", count);		
						}
					}
				}
			}
			i++;
		}
		if (!isInside) {
			isInside = ((count % 2) == 1);
		}
		//println("count", count);
	}
	
	//println("is inside", isInside);
	return (isInside);
}

Shape collider(Drawable p1, Drawable p2) {
	Vectorial spriteInSpace1 = p1.sprite;
	Vectorial spriteInSpace2 = p2.sprite;
		
		
	if (spriteInSpace1 instanceof Shape) {
		spriteInSpace1 = ((Shape)spriteInSpace1).getOutline();
	}
	
	if (spriteInSpace2 instanceof Shape) {
		spriteInSpace2 = ((Shape)spriteInSpace2).getOutline();
	}
	
	spriteInSpace1 = spriteInSpace1.transpose(p1.position);
	spriteInSpace2 = spriteInSpace2.transpose(p2.position);

	return collider(
		(Poligon)spriteInSpace1,
		(Poligon)spriteInSpace2
	);
}

Shape collider(Poligon p1, Poligon p2) {
	Shape toReturn = new Shape();
	Poligon intersection = new Poligon();
	Polygon l1 = new Polygon();
	Polygon l2 = new Polygon();
	Area a1;
	Area a2;
	PathIterator pi;
	
	for (PVector point: p1) {
		l1.addPoint(
			(int)(point.x * 10000),
			(int)(point.y * 10000)
		);
	}
	
	for (PVector point: p2) {
		l2.addPoint(
			(int)(point.x * 10000),
			(int)(point.y * 10000)
		);
	}
	
	a1 = new Area(l1);
	a2 = new Area(l2);
	
	a1.intersect(a2);
	
	pi = a1.getPathIterator(new AffineTransform());
	
	while (!pi.isDone()) {
		float[] coords = new float[2];
		
		pi.currentSegment(coords);
		intersection.add(new PVector(coords[0]/10000, coords[1]/10000));
		pi.next();
	}
	
	for (int i = 0; i < p1.count(); i++) {
		for (int e = 0; e < p2.count(); e++) {
			PVector tmp = intersectionInline(
				p1.getLine(i),
				p2.getLine(e)
			);

			if (tmp != null) {
				if (!equalApproximately(p1.get(i), tmp)) {
					if (!equalApproximately(p2.get(e), tmp)) {
						toReturn.put("obj1_line",
							new Poligon(
								p1.getLine(i)
							)
						);
						toReturn.put("obj2_line",
							new Poligon(
								p2.getLine(e)
							)
						);
					}
				}
			}

		}
	}
	
	toReturn.put("intersection", intersection);

	return toReturn;
}

Shape collider(Vectorial spriteInSpace1, Vectorial spriteInSpace2) {
	if (spriteInSpace1 instanceof Shape) {
		spriteInSpace1 = ((Shape)spriteInSpace1).getOutline();
	}
	
	if (spriteInSpace2 instanceof Shape) {
		spriteInSpace2 = ((Shape)spriteInSpace2).getOutline();
	}

	return collider(
		(Poligon)spriteInSpace1,
		(Poligon)spriteInSpace2
	);
}

void rotateToSurface(Droppable p, Drawable m) {
	Shape collision = collider(p, m);
	float factor = 1;
	float oldrotation = new Float(p.spinoffset);
	float nextrotation = lineAngle(
		collision
			.get("obj2_line")
			.toArray(new PVector[2]),
		new PVector[] {
			new PVector(0,0),
			new PVector(1,0)
		}
	);
	
	println("while starting");
	while (collision.get("intersection").getArea() > 20 && factor > 0.0001) {
		Droppable copy = p.clone();
		
		copy.sprite = copy.sprite.rotate(
			(nextrotation - copy.spinoffset) * factor
		);
		copy.spinoffset += (nextrotation - copy.spinoffset) * factor;
		
		collision = collider(copy, m);
		
		factor /= 2;
	}
	println("new angle", nextrotation * (factor * 2), factor);
	//if (factor > 0.0001) {
		p.sprite = p.sprite.rotate(
			 (nextrotation - p.spinoffset) * (factor * 2)
		);
		p.spinoffset += (nextrotation - p.spinoffset) * (factor * 2);
	//}
}

void adjustToSurface(Droppable p1, Drawable p2) {
	float lerp = 1;
	Droppable copy = p1.clone();
	Shape collision = null;
	PVector nextposition = PVector.add(
		p1.position,
		PVector.mult(
			yToProcessing(p1.speed),
			1 / frameRate
		)
	);
	
	do {
		copy.position = PVector.lerp(
			p1.position,
			nextposition,
			lerp
		);
		
		collision = collider(copy, p2);
		//println("position", copy.position,"area", area, "lerp", lerp, collision.get("intersection"));
		
		lerp /= 2;
	}
	while (collision.get("intersection").getArea() > 20 && lerp > 0.01);

	if (collision.get("intersection").getArea() > 40) {
		PVector preposition = copy.position;
		lerp = 0.001;
	
		nextposition = PVector.lerp(
			p2.position,
			p1.position,
			2
		);


		while (collision.get("intersection").getArea() > 40 && lerp <= 1) {
			copy.position = PVector.lerp(
				p1.position,
				nextposition,
				lerp
			);
		
			collision = collider(copy, p2);
	
			lerp *= 2;
		}
		
		if (collision.get("intersection").getArea() > 40) {
			copy.position = preposition;
		}
	}
	
	p1.position = copy.position.get();
}

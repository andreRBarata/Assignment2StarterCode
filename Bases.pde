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
	println(line );
	println("y >= min", point.y, min(line[0].y, line[1].y));*/
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
		spriteInSpace1 = ((Shape)spriteInSpace1).outline.clone();
	}
	
	if (spriteInSpace2 instanceof Shape) {
		spriteInSpace2 = ((Shape)spriteInSpace2).outline.clone();
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
	Poligon p1clone = p1.clone();
	Poligon p2clone = p2.clone();
	Poligon intersection = new Poligon();
	PVector pointception = null;
	
	//////print("Calculating intersection between");
	//////print(p1clone);
	//////print(" and ");
	////println(p2clone);
	
	////println("== Adding intersections to polygons");
	for (int i = 0; i < p1clone.count(); i++) {
		for (int e = 0; e < p2clone.count(); e++) {
			////println("Cycle: i=", i, "e=", e);
			PVector tmp = intersectionInline(
				p1clone.getLine(i),
				p2clone.getLine(e)
			);
			//////print("Calculating intersection of");
			//////print(p1clone.getLine(i));
			//////print(" and ");
			////println(p2clone.getLine(e));
			////println("found intersection:", tmp);
			if (tmp != null) {
				////println("intersection is not null");
				pointception = tmp;
				toReturn.put("obj1_line",
					new Poligon(
						p1clone.getLine(i)
					)
				);
				toReturn.put("obj2_line",
					new Poligon(
						p2clone.getLine(e)
					)
				);
				////println("testing p1clone: ", p1clone, "does not contain", pointception);
				if (!p1clone.contains(pointception)) {
					////println("testing p1clone: ", p1clone.get(i), "is not equal to", pointception);
					if (!equalApproximately(p1clone.get(i), pointception)) {
						////println("adding ",pointception, "to p1clone:", p1clone);
						p1clone.add(i + 1, pointception);
					}
				}
				
				////println("testing p2clone: ", p2clone, "does not contain", pointception);
				if (!p2clone.contains(pointception)) {
					////println("testing p2clone: ", p2clone.get(e), "is not equal to", pointception);
					if (!equalApproximately(p2clone.get(e), pointception)) {
						////println("adding ",pointception, "to p2clone:", p2clone);
						p2clone.add(e + 1, pointception);
					}
				}
				
			}

		}
	}

	//println("p1clone with intersections", p1clone);
	//println("p2clone with intersections", p2clone);
	
	//println("== Creating intersection polygon");
	Collections.rotate(p1clone, -p1clone.indexOf(pointception));
	Collections.rotate(p2clone, -p2clone.indexOf(pointception));
	//println("pointception", pointception);
	if (pointception == null) {
		if (pointInVectorial(p1clone.get(0), p2)) {
			intersection = p1.clone();
		}
		else if (pointInVectorial(p2clone.get(0), p1)) {
			intersection = p2.clone();
		}
	}
	else {
		//println("cicle");
		do {
			//println("P1=",p1clone);
			//println("P2=",p2clone);
			intersection.add(p1clone.get(0));
			//println("adding", p1clone.get(0));
			//println("considering", p1clone.get(1), p2clone);
			if (!pointInVectorial(p1clone.get(1), p2clone)) {
				Poligon tmp = p1clone;
				//println("does not contain 1");
				p1clone = p2clone;
				p2clone = tmp;
				
				//println("switched polygon, now considering", p1clone.get(1));
			}
			
			if (!pointInVectorial(p1clone.get(1), p2clone)) {
				//println("does not contain 2");
				Collections.reverse(p1clone);
				//println("rotated polygon, now considering", p1clone.get(1));
			}
			
			//println("In", p1clone, p1clone.size());
			Collections.rotate(p1clone, -1);
			Collections.rotate(p2clone, -p2clone.indexOf(p1clone.get(0)));
			//println("Intersection now contains",intersection);
			//println("p1clone now contains",p1clone);
			//println("testing: ",p1clone.get(0), "in", p2, p1clone.get(0), "not in", intersection);
			//println( "results: ", pointInVectorial(p1clone.get(0), p2) , !intersection.contains(p1clone.get(0) ));
		}
		while (pointInVectorial(p1clone.get(0), p2clone) && !intersection.contains(p1clone.get(0)));
	}
	
	toReturn.put("intersection", intersection);

	return toReturn;
}

Shape collider(Vectorial spriteInSpace1, Vectorial spriteInSpace2) {
	if (spriteInSpace1 instanceof Shape) {
		spriteInSpace1 = ((Shape)spriteInSpace1).outline.clone();
	}
	
	if (spriteInSpace2 instanceof Shape) {
		spriteInSpace2 = ((Shape)spriteInSpace2).outline.clone();
	}

	return collider(
		(Poligon)spriteInSpace1,
		(Poligon)spriteInSpace2
	);
}

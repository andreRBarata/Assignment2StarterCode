class Poligon extends ArrayList<PVector> implements Vectorial {
	
	Poligon() {
		super();
	}
	
	Poligon(Poligon poligon) {
		super(poligon);
	}
	
	Poligon(PVector[] poligon) {
		super(Arrays.asList(poligon));
	}
	
	int count() {
		return this.size();
	}
	
	boolean containsApproximately(PVector point) {
		int i = 0;
		
		while (!equalApproximately(this.get(i),point) && i > this.size()) {
			i++;
		}
		return (equalApproximately(this.get(i),point));
	}
	
	float getRadius() {
		float total = 0;
		PVector center = this.center();
		
		for (int i = 0; i < this.size(); i++) {
			total += PVector.dist(center, this.get(i));
		}
		
		return total/this.size();
	}
	
	float getArea() {
		float sum = 0;
		
		for (int i = 0; i < this.size(); i++) {
			PVector l1 = this.get(i);
			PVector l2 = this.get(
				(i + 1) % this.size()
			);
			
			sum += (l1.x * l2.y) - (l1.y * l2.x);
		}
		
		return abs(sum/2);
	}
	
	void draw() {
		beginShape();
		
		for (int i = 0; i < this.size(); i++) {
			vertex(
				this.get(i).x,
				this.get(i).y
			);
		}

		endShape(CLOSE);
	}
	
	PVector[] getLine(int i) {
		return new PVector[] {
			this.get(i).get(),
			this.get((i + 1) % this.size())
		};
	}
	
	Poligon clone() {
		Poligon poligon = new Poligon();
		
		for (PVector point: this) {
			poligon.add(point.get());
		}
		
		return poligon;
	}
	
	PVector center() {
		float sumx = 0;
		float sumy = 0;
		
		for (int i = 0; i < this.size(); i++) {
			sumx += this.get(i).x;
			sumy += this.get(i).y;
		}
		
		return new PVector(
			sumx / this.size(),
			sumy / this.size()
		);
	}
	
	Poligon union(Poligon union) {
		Poligon poligon = new Poligon();
		Poligon ori1 = this.clone();
		Poligon ori2 = union.clone();
		
		PVector coords = ori1.get(0);
		
		//println("before", ori1, ori2);
		
		for (int i = 0; i < ori1.count(); i++) {
			for (int e = 0; e < ori2.count(); e++) {
				PVector tmp = intersectionInline(
					ori1.getLine(i),
					ori2.getLine(e)
				);
				//print("lines");
				//print(ori1.getLine(i));
				//println(ori2.getLine(e));
				if (tmp != null) {
					coords = tmp;
					//println("tmp", tmp);
					if (!ori1.containsApproximately(coords)) {
						if (!equalApproximately(ori1.get(i), coords)) {
							//println("tmp2", coords);
							ori1.add(i + 1, coords);
						}
					}
			
					if (!ori2.containsApproximately(coords)) {
						if (!equalApproximately(ori2.get(e), coords)) {
							ori2.add(e + 1, coords);
						}
					}
				}
			}
		}
		
		Collections.rotate(ori1, -ori1.indexOf(coords));
		
		print("poligons");
		print(ori1);
		println(ori2);
		
		do {
			poligon.add(ori1.get(0));
			//println("adding", ori1.get(0));
			if (ori2.contains(ori1.get(0))) {
				Poligon tmp = ori1;
				
				ori1 = ori2;
				ori2 = tmp;
				//println("switched");
				Collections.rotate(ori1, -ori1.indexOf(ori2.get(0)));
				
				if (pointInVectorial(ori1.get(1), ori2)) {
					Collections.reverse(ori1);
					//println("reversed");
				}
			}
			
			Collections.rotate(ori1, 1);
		}
		while (!poligon.contains(ori1.get(0)));
		
		//println("poligon", poligon);
		return poligon;
	}
	
	Poligon merge(Poligon merge) {
		Poligon poligon = new Poligon();
		Poligon ori1 = this.clone();
		Poligon ori2 = merge.clone();
		
		PVector coords = ori1.get(0);
		
		while (coords != null) {
			poligon.add(coords);
			ori1.remove(coords);
			
			if (ori2.contains(coords) || ori1.size() == 0) {
				Poligon tmp = ori1;
				
				ori2.remove(coords);
				
				ori1 = ori2;
				ori2 = tmp;
				Collections.rotate(ori1, -ori1.indexOf(coords));
				
				if (ori2.contains(coords) && ori2.contains(ori1.get(0))) {
					Collections.reverse(ori1);
				}
			}
			
			if (ori1.size() > 0) {
				coords = ori1.get(0);
			}
			else {
				coords = null;
			}
		}
		//println("return", poligon);
		return poligon;	
	}
	
	Poligon rotate(float degrees) {
		Poligon poligon = (Poligon)this.clone();
		
		for (int i = 0; i < poligon.size(); i++) {
			poligon.get(i).rotate(degrees);
		}
		
		return poligon;
	}
	
	Poligon roundRotate(float degrees) {
		Poligon poligon = (Poligon)this.clone();
		
		for (int i = 0; i < poligon.size(); i++) {
			poligon.get(i).rotate(degrees);
			poligon.get(i).x = round(poligon.get(i).x);
			poligon.get(i).y = round(poligon.get(i).y);
		}
		
		return poligon;
	}
	
	Poligon transpose(PVector val) {
		Poligon poligon = (Poligon)this.clone();
		
		for (int i = 0; i < poligon.size(); i++) {
			poligon.get(i).add(val);
		}
		
		return poligon;
	}
	
	Poligon scale(PVector val) {
		Poligon poligon = (Poligon)this.clone();
	
		for (int i = 0; i < poligon.size(); i++) {
			poligon.get(i).x *= val.x;
			poligon.get(i).y *= val.y;
		}
		
		return poligon;
	}
	
	Poligon scale(float val) {
		Poligon poligon = (Poligon)this.clone();
	
		for (int i = 0; i < poligon.size(); i++) {
			poligon.get(i).mult(val);
		}
		
		return poligon;
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

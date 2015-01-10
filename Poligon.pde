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
	
	int lineCount() {
		return this.size();
	}
	
	float getRadius() {
		float total = 0;
		PVector center = this.center();
		
		for (int i = 0; i < this.size(); i++) {
			total += PVector.dist(center, this.get(i));
		}
		
		return total/this.size();
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
			this.get(i),
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
	
	Poligon merge(Poligon merge) {
		Poligon poligon = new Poligon();
		Poligon ori1 = this.clone();
		Poligon ori2 = merge.clone();
		
		PVector coords = ori1.get(0);
		
		println("new poligon");
		
		while (coords != null) {
			println(coords);
			poligon.add(coords);
			ori1.remove(coords);
			
			if (ori2.contains(coords) || ori1.size() == 0) {
				Poligon tmp = ori1;
				
				ori2.remove(coords);
				
				ori1 = ori2;
				ori2 = tmp;
				Collections.rotate(ori1, ori1.indexOf(coords));
				
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
		
		return poligon;	
	}
	
	Poligon rotate(float degrees) {
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

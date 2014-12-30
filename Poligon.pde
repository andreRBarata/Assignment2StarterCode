class Poligon extends ArrayList<PVector> implements Vectorial {
	
	Poligon() {
		super();
	}
	
	Poligon(Poligon Poligon) {
		super(Poligon);
	}
	
	Poligon(PVector[] Poligon) {
		super(Arrays.asList(Poligon));
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
		Poligon Poligon = new Poligon();
		
		for (PVector point: this) {
			Poligon.add(point.get());
		}
		
		return Poligon;
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
		Poligon Poligon = (Poligon)this.clone();
	
		for (int i = 0; i < Poligon.size(); i++) {
			for (int e = 0; e < merge.size(); e++) {
				if (Poligon.get(i) == merge.get(e)) {
					if (e + 1 < merge.size()) {
						Poligon.add(i + 1, Poligon.get(e));
					}
				}
			}
		}
		
		return Poligon;	
	}
	
	Poligon rotate(float degrees) {
		Poligon Poligon = (Poligon)this.clone();
		
		for (int i = 0; i < Poligon.size(); i++) {
			Poligon.get(i).rotate(degrees);
		}
		
		return Poligon;
	}
	
	Poligon transpose(PVector val) {
		Poligon Poligon = (Poligon)this.clone();
		
		for (int i = 0; i < Poligon.size(); i++) {
			Poligon.get(i).add(val);
		}
		
		return Poligon;
	}
	
	Poligon scale(PVector val) {
		Poligon Poligon = (Poligon)this.clone();
	
		for (int i = 0; i < Poligon.size(); i++) {
			Poligon.get(i).x *= val.x;
			Poligon.get(i).y *= val.y;
		}
		
		return Poligon;
	}
	
	Poligon scale(float val) {
		Poligon Poligon = (Poligon)this.clone();
	
		for (int i = 0; i < Poligon.size(); i++) {
			Poligon.get(i).mult(val);
		}
		
		return Poligon;
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

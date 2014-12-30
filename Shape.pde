class Shape extends TreeMap<String,Poligon> implements Vectorial {
	
	Shape clone() {
		Shape shape = new Shape();
		String[] keys = this
			.keySet()
			.toArray(
				new String[this.size()]
			);
		
		for (int i = 0; i < this.size(); i++) {
			shape.put(
				new String((String) keys[i]),
				this.get(keys[i]).clone()
			);
		}
		
		return shape;
	}
	
	PVector center() {
		float sumx = 0;
		float sumy = 0;
		String[] keys = this
			.keySet()
			.toArray(
				new String[this.size()]
			);
	
		for (int i = 0; i < keys.length; i++) {
			PVector center = this.get(keys[i]).center();
		
			sumx += center.x;
			sumy += center.y;
		}
		
		return new PVector(
			sumx/this.size(),
			sumy/this.size()	
		);
	}
	
	int lineCount() {
		String[] keys = this
			.keySet()
			.toArray(
				new String[this.size()]
			);

		int size = 0;
	
		for (int i = 0; i < keys.length; i++) {
			size += this.get(keys[i]).size();
		}
		
		return size;
	}
	
	float getRadius() {
		PVector center = this.center();
		float total = 0;
		
		String[] keys = this
			.keySet()
			.toArray(
				new String[this.size()]
			);
	
		for (int i = 0; i < keys.length; i++) {	
			total += PVector.dist(
				this.get(keys[i]).center(),
				center
			);
		}
		
		return total/this.size();
	}
	
	Shape transpose(PVector val) {
		Shape shape = this.clone();
		String[] keys = this
			.keySet()
			.toArray(
				new String[this.size()]
			);
	
		for (int i = 0; i < keys.length; i++) {
			shape.get(keys[i]).transpose(val);
		}
		
		return shape;
	}
	
	Shape rotate(float degrees) {
		Shape shape = this.clone();
		String[] keys = this
			.keySet()
			.toArray(
				new String[this.size()]
			);
	
		for (int i = 0; i < keys.length; i++) {
			shape.get(keys[i]).rotate(degrees);
		}
		
		return shape;
	}
	
	void draw() {
		String[] keys = this
			.keySet()
			.toArray(
				new String[this.size()]
			);
	
		for (int i = 0; i < keys.length; i++) {
			this.get(keys[i]).draw();
		}
	}
	
	PVector[] getLine(int i) {
		String[] keys = this
			.keySet()
			.toArray(
				new String[this.size()]
			);
		int e = 0;
		PVector[] line = new PVector[2];
		
		while (i > this.get(keys[e]).lineCount()) {
			if (i <= this.get(keys[e]).lineCount()) {
				Poligon poligon = this.get(keys[e]);
			
				line = new PVector[] {
					poligon.get(i),
					poligon.get((i + 1) % this.lineCount())
				};
			}
			else {
			
			}
		
			e++;
		}
		
		return line;
	}
}

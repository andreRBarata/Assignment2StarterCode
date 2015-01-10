class Shape extends TreeMap<String,Poligon> implements Vectorial {
	
	Shape clone() {
		Shape shape = new Shape();
		
		for (String key: this.keySet()) {
			shape.put(
				new String((String) key),
				this.get(key).clone()
			);
		}
		
		return shape;
	}
	
	Shape add(String key, Poligon poligon) {
		super.put(key, poligon);
		
		return this;
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
		int size = 0;

		for (String key: this.keySet()) {
			size += this.get(key).lineCount();
		}
		
		return size;
	}
	
	float getRadius() {
		PVector center = this.center();
		float total = 0;
	
		for (String key: this.keySet()) {	
			total += PVector.dist(
				this.get(key).center(),
				center
			);
		}
		
		return total/this.size();
	}
	
	Shape transpose(PVector val) {
		Shape shape = this.clone();
	
		for (String key: this.keySet()) {
			shape.put(key, shape.get(key).transpose(val));
		}
		
		return shape;
	}
	
	Shape rotate(float degrees) {
		Shape shape = this.clone();
	
		for (String key: this.keySet()) {
			shape.get(key).rotate(degrees);
		}
		
		return shape;
	}
	
	void draw() {
		
		for (String key: this.keySet()) {
			this.get(key).draw();
		}
	}
	
	PVector[] getLine(int i) {
		String[] keys = this
			.keySet()
			.toArray(
				new String[this.size()]
			);
		int e = 0;
		
		while (e < this.size() && i >= this.get(keys[e]).size()) {
			i -= this.get(keys[e]).lineCount();
			e++;
		}
		
		
		if (i < this.get(keys[e]).size()) {
			Poligon poligon = this.get(keys[e]);
		
			return poligon.getLine(i);
		}
		else {
			return null;
		}
	}
}

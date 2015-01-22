class Shape extends TreeMap<String,Poligon> implements Vectorial {
	Poligon outline;

	Shape clone() {
		Shape shape = new Shape();
		
		shape.outline = outline.clone();
		
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
		Poligon composite = new Poligon();
	
		for (String key: this.keySet()) {	
			composite.addAll(this.get(key));
		}
		
		return composite.center();
	}
	
	void updateOutline() {
		this.outline = getOutline();
	}
	
	boolean contains(Object point) {
		String[] keys = this
			.keySet()
			.toArray(
				new String[this.size()]
			);
		int i = 0;
		boolean contains = false;
		
		while (i < keys.length && !contains) {
			contains = this.get(keys[i]).contains(point);
		}
		
		return contains;
	}
	
	int count() {
		int size = 0;

		for (String key: this.keySet()) {
			size += this.get(key).count();
		}
		
		return size;
	}
	
	float getRadius() {
		Poligon composite = new Poligon();
	
		for (String key: this.keySet()) {	
			composite.addAll(this.get(key));
		}
		
		return composite.getRadius();
	}
	
	float getArea() {
		float area = 0;
		
		for (String key: this.keySet()) {
			area += this.get(key).getArea();
		}
		
		for (String outerkey: this.keySet()) {
			for (String innerkey: this.keySet()) {
				area -= collider(
					this.get(outerkey), this.get(innerkey)
				).get("intersection").getArea();
			}
		}
		
		return area;
	}
	
	Poligon getOutline() {
		String[] keys = this
			.keySet()
			.toArray(
				new String[this.size()]
			);
		Poligon outline = null;
		
		if (this.size() >= 1) {
			outline = this.get(keys[0]).clone();
		}
		
		if (this.size() > 1) {
			for (int i = 1; i < keys.length; i++) {
				outline = outline.union(this.get(keys[i]).clone());
			}
		}
		
		/*print("outline");
		println(outline);*/
		return outline;
	}
	
	Shape transpose(PVector val) {
		Shape shape = this.clone();
	
		for (String key: this.keySet()) {
			shape.put(key, shape.get(key).transpose(val));
		}
		
		return shape;
	}
	
	Shape scale(PVector val) {
		Shape shape = this.clone();
	
		for (String key: this.keySet()) {
			shape.put(key, shape.get(key).scale(val));
		}
		
		return shape;
	}
	
	Shape scale(float val) {
		Shape shape = this.clone();
	
		for (String key: this.keySet()) {
			shape.put(key, shape.get(key).scale(val));
		}
		
		return shape;
	}
	
	Shape rotate(float degrees) {
		Shape shape = this.clone();
	
		for (String key: this.keySet()) {
			shape.put(key,
				shape.get(key).rotate(degrees)
			);
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
			i -= this.get(keys[e]).count();
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

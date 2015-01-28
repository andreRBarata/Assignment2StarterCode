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
		Poligon composite = new Poligon();
	
		for (String key: this.keySet()) {	
			composite.addAll(this.get(key));
		}
		
		return composite.center();
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
		Polygon l1 = new Polygon();
		Polygon l2 = new Polygon();
		Area a1;
		Area a2;
		PathIterator pi;
	
		String[] keys = this
			.keySet()
			.toArray(
				new String[this.size()]
			);
		Poligon outline = null;
	
		for (PVector point: this.get(keys[0])) {
			l1.addPoint(
				(int)(point.x * 10000),
				(int)(point.y * 10000)
			);
		}
	
		a1 = new Area(l1);
	
		if (this.size() >= 1) {
			outline = this.get(keys[0]).clone();
		}
	
		if (this.size() > 1) {
			for (int i = 1; i < keys.length; i++) {
				for (PVector point: this.get(keys[i])) {
					l2.addPoint(
						(int)(point.x * 10000),
						(int)(point.y * 10000)
					);
				}
			
				a2 = new Area(l2);
			
				a1.add(a2);
			}
		
			pi = a1.getPathIterator(null);
			outline = new Poligon();
		
			while (!pi.isDone()) {
				float[] coords = new float[2];
	
				pi.currentSegment(coords);
				outline.add(new PVector(coords[0]/10000, coords[1]/10000));
				pi.next();
			}
		}	
	
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

interface CallBack {
	public void run();
}

class Shape extends ArrayList<PVector[]> {
	
	Shape(PVector[][] shape) {
		super(Arrays.asList(shape));
	}
	
	Shape transpose(PVector val) {
		for (int i = 0; i < this.size(); i++) {
			this.get(i)[0].add(val);
			this.get(i)[1].add(val);
		}
		
		return this;
	}
	
	float maxWidth() {
		float minWidth = 0;
		float maxWidth = 0;
		for (int i = 0; i < this.size(); i++) {
			if (i == 0 || this.get(i)[0].x > maxWidth) {
				maxWidth = this.get(i)[0].x;
			}
			if (this.get(i)[1].x > maxWidth) {
				maxWidth = this.get(i)[1].x;
			}
			
			if (i == 0 || this.get(i)[0].x < minWidth) {
				minWidth = this.get(i)[0].x;
			}
			if (this.get(i)[1].x < minWidth) {
				minWidth = this.get(i)[1].x;
			}
		}
		
		return maxWidth - minWidth;
	}
	
	float maxHeight() {
		float minHeight = 0;
		float maxHeight = 0;
		for (int i = 0; i < this.size(); i++) {
			if (i == 0 || this.get(i)[0].y > maxHeight) {
				maxHeight = this.get(i)[0].y;
			}
			if (this.get(i)[1].y > maxHeight) {
				maxHeight = this.get(i)[1].y;
			}
			
			if (i == 0 || this.get(i)[0].y < minHeight) {
				minHeight = this.get(i)[0].y;
			}
			if (this.get(i)[1].y < minHeight) {
				minHeight = this.get(i)[1].y;
			}
		}
		
		return maxHeight - minHeight;
	}
}

class Drawable {
	PVector position;
	Shape shape;
	
	Drawable(PVector position, Shape shape) {
		this.position = position;
		this.shape = shape;
	}
	
	void draw() {
		for (int i = 0; i < shape.size(); i++) {
			PVector current1 = PVector.add(shape.get(i)[0], position);
			PVector current2 = PVector.add(shape.get(i)[1], position);
			
			line(
				current1.x,
				current1.y,
				current2.x,
				current2.y
			);
		}
	}
}

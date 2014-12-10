interface CallBack {
	public void run();
}

class Shape {
	PVector[][] shape;
	
	Shape(PVector[][] shape) {
		this.shape = shape;
	}
	
	int size() {
		return shape.length;
	}
	
	PVector[] get(int i) {
		return shape[i];
	}
	
	Shape clone() {
		return new Shape(Arrays.copyOf(this.shape, this.shape.length));
	}
	
	void transpose(PVector val) {
		for (int i = 0; i < shape.length; i++) {
			shape[i][0].add(val);
			shape[i][1].add(val);
		}
	}
	
	float maxWidth() {
		float minWidth = 0;
		float maxWidth = 0;
		for (int i = 0; i < shape.length; i++) {
			if (i == 0 || shape[i][0].x > maxWidth) {
				maxWidth = shape[i][0].x;
			}
			if (shape[i][1].x > maxWidth) {
				maxWidth = shape[i][1].x;
			}
			
			if (i == 0 || shape[i][0].x < minWidth) {
				minWidth = shape[i][0].x;
			}
			if (shape[i][1].x < minWidth) {
				minWidth = shape[i][1].x;
			}
		}
		
		return maxWidth - minWidth;
	}
	
	float maxHeight() {
		float minHeight = 0;
		float maxHeight = 0;
		for (int i = 0; i < shape.length; i++) {
			if (i == 0 || shape[i][0].y > maxHeight) {
				maxHeight = shape[i][0].y;
			}
			if (shape[i][1].y > maxHeight) {
				maxHeight = shape[i][1].y;
			}
			
			if (i == 0 || shape[i][0].y < minHeight) {
				minHeight = shape[i][0].y;
			}
			if (shape[i][1].y < minHeight) {
				minHeight = shape[i][1].y;
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

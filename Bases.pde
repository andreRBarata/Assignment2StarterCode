interface CallBack {
	public void run();
}

class Shape extends ArrayList<PVector[]> {
	ArrayList<PVector> points;
	
	Shape(PVector[][] shape) {
		points = new ArrayList<PVector>();
		
		for (int i = 0; i < shape.length; i++) {
			PVector[] line = new PVector[2];
			
			for (int e = 0; e < 2; e++) {
				if (!points.contains(shape[i][e])) {
					points.add(shape[i][e]);
					line[e] = shape[i][e];
				}
				else {
					line[e] = points.get(points.indexOf(shape[i][e]));
				}
			}
			this.add(line);
		}
	}
	
	Shape transpose(PVector val) {
		for (int i = 0; i < this.points.size(); i++) {
			points.get(i).add(val);
		}
		
		return this;
	}
	
	//To be altered later
	Shape scale(PVector val) {
		for (int i = 0; i < this.points.size(); i++) {
			points.get(i).x *= val.x;
			points.get(i).y *= val.y;
		}
		
		return this;
	}
	
	Shape scale(float val) {
		for (int i = 0; i < this.points.size(); i++) {
			points.get(i).mult(val);
		}
		
		return this;
	}
	
	float maxWidth() {
		float minWidth = 0;
		float maxWidth = 0;
		for (int i = 0; i < this.points.size(); i++) {
			if (i == 0 || points.get(i).x > maxWidth) {
				maxWidth = points.get(i).x;
			}
			
			if (i == 0 || points.get(i).x < minWidth) {
				minWidth = points.get(i).x;
			}
		}
		
		return maxWidth - minWidth;
	}
	
	float maxHeight() {
		float minHeight = 0;
		float maxHeight = 0;
		for (int i = 0; i < this.points.size(); i++) {
			if (i == 0 || points.get(i).y > maxHeight) {
				maxHeight = points.get(i).y;
			}
			
			if (i == 0 || points.get(i).y < minHeight) {
				minHeight = points.get(i).y;
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

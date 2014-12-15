class Droppable extends Drawable {
	PVector speed;
	
	Droppable(PVector position, Shape shape) {
		super(position, shape);
		speed = new PVector(0,0);
	}
	
	void draw() {
		this.position.add(this.speed);
		if (this.speed.x > 0) {
			this.speed.x--;
		}
		else if (this.speed.x < 0) {
			this.speed.x++;
		}
		
		super.draw();
	}
}

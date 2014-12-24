class Droppable extends Drawable {
	PVector speed;
	
	Droppable(PVector position, Shape shape) {
		super(position, shape);
		speed = new PVector(0,0);
	}
	
	void draw() {
		Poligon collision = collider(this, map);
		
		if (collision.get("intersection").size() > 1) {
			PVector old_position = this.position.get();
			old_position.sub(this.speed);
			PVector edgeVector = collision
				.get("obj2_line")
				.get(1)
				.get();
				
			edgeVector.sub(
				collision
					.get("obj2_line")
					.get(0)
			);
		
			println("speedBefore:", this.speed);
		
			float baseAngle = lineAngle(
				collision
					.get("obj2_line")
					.toArray(new PVector[2]),
				new PVector[] {
					new PVector(0,0),
					new PVector(1,0)
				}
			);
			
			println("baseAngle:", baseAngle);
			
			float magnitude = speed.mag();
			float angle = lineAngle(
				new PVector[] {
					old_position,
					this.position
				},
				collision
					.get("obj2_line")
					.toArray(new PVector[2])
			);

			if (angle != -1) {
		
				this.speed = PVector.fromAngle(
					PI - angle + baseAngle
				);
				speed.mult(magnitude);
				println("speedAfter:", this.speed);
			} else	{
				println("speed was unchanged:", this.speed);
			}		

		}
		
		this.speed.y += 3.1/60;
		this.position.add(this.speed);

		super.draw();
	}
}

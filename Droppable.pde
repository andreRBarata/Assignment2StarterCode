class Droppable extends Drawable {
	PVector speed;
	boolean colliding;
	float spin;
	float spinoffset;
	float momentum;
	
	Droppable(PVector position, Vectorial sprite) {
		super(position, sprite);
		this.speed = new PVector(0,0);
		this.colliding = false;
		this.spin = 0;
		this.spinoffset = 0;
		this.momentum = 0;
	}
	
	Droppable clone() {
		Droppable clone = new Droppable(
			this.position.get(),
			this.sprite.clone()
		);
		
		clone.speed = this.speed.get();
		clone.colliding = new Boolean(this.colliding);
		clone.spin = new Float(this.spin);
		clone.spinoffset = new Float(this.spinoffset);
		clone.momentum = new Float(this.momentum);
		
		return clone;
	}
	
	void update() {
		if (this.sprite instanceof Shape) {
			((Shape)this.sprite).updateOutline();
		}
		Droppable copy = this.clone();
		Shape collision = null;
		
		/*println("position", this.position,"intersection P",collision.get("intersection") ,"speed", speed,"colliding", collision.get("obj2_line"));*/
		
		adjustToSurface(this, map);
		
		copy.position = PVector.add(
			this.position,
			this.speed
		);
		
		collision = collider(copy, map);
		
		if (collision.get("intersection").getArea() > 1) {
			float magnitude = speed.mag()/1.5;
			
			Vectorial spriteInSpace = copy
				.sprite
				.transpose(
					position
				);
				
			PVector transfer = PVector.sub(
					spriteInSpace.center(),
					collision
						.get("intersection")
						.center()
				).cross(this.speed);
			
			this.spin -= (
				(
					transfer.mag()/(
						TWO_PI * spriteInSpace.getRadius() * spinradius
					)
				)
			);
			
			
			println("area",collision.get("intersection").getArea());
			
			if (collision.get("obj2_line") != null) {
				this.speed = PVector.fromAngle(
					PI - lineAngle(
						new PVector[] {
							PVector.sub(position, speed),
							copy.position
						},
						collision
							.get("obj2_line")
							.toArray(new PVector[2])
					)
				);
			}
			else {
				//This is not supposed to happen
				this.speed = new PVector(0,0);
			}
			
			this.speed.mult(magnitude);
			
			colliding = true;
		}
		else {
			colliding = false;	
		}
		
		this.sprite = this.sprite.rotate(
			spin
		);
		this.spin *= 0.5;
		

		this.speed.x -= sin(
			HALF_PI + atan2(copy.position.y, copy.position.x) -
				atan2(map.position.y, map.position.x)
		) * (gravity/frameRate);

		this.speed.y += cos(
			HALF_PI + atan2(copy.position.y, copy.position.x) -
				atan2(map.position.y, map.position.x)
		) * (gravity/frameRate);
		
		this.spinoffset = (this.spinoffset + this.spin) % TWO_PI;
	}
}

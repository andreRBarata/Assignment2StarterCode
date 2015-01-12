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
	
	void update() {
		Shape collision = collider(this, map);
		
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
			
			float baseAngle = lineAngle(
				collision
					.get("obj2_line")
					.toArray(new PVector[2]),
				new PVector[] {
					new PVector(0,0),
					new PVector(1,0)
				}
			);
			
			float magnitude = speed.mag()/1.5;
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
				Vectorial spriteInSpace = this
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
				
				this.spin *= 0.5;
				
				this.speed = PVector.fromAngle(
					lineAngle(
						collision
							.get("obj1_line")
							.toArray(new PVector[2]),
						collision
							.get("obj2_line")
							.toArray(new PVector[2])
					) - HALF_PI
				);
				
				/*this.speed = PVector.fromAngle(
					PI - (angle + baseAngle)
				);*/
				
				this.speed.mult(magnitude);
				
				colliding = true;
			}
			else {
				colliding = false;
			}
		}
		else {
			colliding = false;	
		}
		
		this.speed.x -= sin(
			HALF_PI + atan2(this.position.y, this.position.x) -
				atan2(map.position.y, map.position.x)
		) * (gravity/frameRate);
	
		this.speed.y += cos(
			HALF_PI + atan2(this.position.y, this.position.x) -
				atan2(map.position.y, map.position.x)
		) * (gravity/frameRate);
		
		this.position.add(this.speed);
		
		this.sprite = this.sprite.rotate(
			spin
		);
		this.spinoffset += spin % TWO_PI;
	}
}

class Droppable extends Drawable {
	PVector speed;
	boolean isColliding;
	float spin;
	float spinoffset;
	float momentum;
	
	Droppable(PVector position, Vectorial sprite) {
		super(position, sprite);
		this.speed = new PVector(0,0);
		this.isColliding = false;
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
		clone.isColliding = new Boolean(this.isColliding);
		clone.spin = new Float(this.spin);
		clone.spinoffset = new Float(this.spinoffset);
		clone.momentum = new Float(this.momentum);
		
		return clone;
	}
	
	void update() {
		Droppable copy = this.clone();
		Shape collision = null;
		PVector force = new PVector();
		
		collision = collider(this, map);
		
		
		if (collision.get("intersection").getArea() == 0) {
			isColliding = false;
			force.add(gravity(this, map));
			
			//Angle from spin
			this.sprite = this.sprite.rotate(
				this.spin / frameRate
			);
	
			this.spinoffset = (
				TWO_PI + this.spinoffset + (this.spin / frameRate)
			) % TWO_PI;
		}
		else {
			float magnitude = this.speed.mag();
			
			magnitude /= 1.5;
			//magnitude--;
		
			if (magnitude < 1) {
				magnitude = 0;
			}
			
			println("Magnitude recalculated", magnitude);
			
			if (collision.get("intersection").getArea() > 20) {
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
				
			
				PVector speedTransfer = transfer.get();
				//speedTransfer.setMag(transfer.mag()/1000);

				/*this.speed.sub(
					speedTransfer
				);*/
			
				if (collision.get("obj2_line") != null) {
					float angle = HALF_PI - lineAngle(
						new PVector[] {
							PVector.sub(
								yToProcessing(this.position),
								this.speed
							),
							yToProcessing(this.position)
						},
						collision
							.get("obj2_line")
							.toArray(new PVector[2])
					);
				
					//rotateToSurface(this, map);
					
					/*if (abs(angle) < 0.1) {
						magnitude /= 1.1;
					}
					else {
						magnitude /= 2;
					}*/
					
					println("speed recalculated");
					this.speed = (
						PVector.fromAngle(
							angle + lineAngle(
								new PVector[] {
									new PVector(0,0),
									new PVector(1,0)
								},
								collision
									.get("obj2_line")
									.toArray(new PVector[2])
							)
						)
					);
		
					println("angles: ", angle);

			
				}
				else {
					//This is not supposed to happen
					this.speed = new PVector(0,0);
				}
			}
			isColliding = true;
			
			this.speed.setMag(magnitude);
		}
		
		//Velocity from force
		this.speed.add(force);
		
		//Position from speed
		adjustToSurface(this, map);
		/*this.position.add(
			PVector.mult(
				yToProcessing(this.speed),
				1 / frameRate
			)
		);*/
		
		
	}
}

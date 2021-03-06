class Player extends Droppable {
	TreeMap<String, Character> keyBinds;
	int index;
	color colour;
	float pathtraveled;
	char[] name;
		
	Player() {
		super(
			new PVector(width / 2, height / 2),
			//rectangle
			new Shape().add("weel1", circle
					.transpose(
						new PVector(15,-20)
					)
				)
				.add("weel2", circle
					.transpose(
						new PVector(-15,-20)
					)
				)
				.add("windows",
					trapezium
						.transpose(
							new PVector(0,10)
						)
						.scale(
							new PVector(1,0.6)
						)
						
			)
			.add(
				"body", trapezium
						.merge(
							triangle
								.transpose(
									new PVector(20,0)
								)
						)
						.transpose(
							new PVector(-5,-10)
						)
						.scale(
							new PVector(1.8,1)
						)
			)
		
		);
		
		this.keyBinds = new TreeMap<String, Character>();
		this.pathtraveled = 0;
		this.name = "AAA".toCharArray();
	}
	
	Player(int index, color colour) {
		this();
		this.index = index;
		this.colour = colour;
	}
	
	Player(int index, color colour, TreeMap keyBinds) {
		this();
		this.index = index;
		this.colour = colour;
		this.keyBinds = keyBinds;
	}
	
	Player(int index, color colour, XML xml) {
		this(
			index,
			colour
		);

		String[] keys = {
			"up",
			"down",
			"left",
			"right",
			"start",
			"button1",
			"button2"
		};

		for (String key: keys) {
			this.keyBinds.put(
				key,
				buttonNameToKey(xml, key)
			);
		}
	}
	
	void display() {
		fill(colour);
		super.display();
		
	}
	
	void update() {
		float startAngle;
		float endAngle;
		
		if (!isColliding) {	
			if (checkKey(keyBinds.get("up"))) {
				spin += 0.15;
			}
			else if (checkKey(keyBinds.get("down"))) {
				spin -= 0.15;
			}
			else if (spin != 0) {
				spin -= 0.15 * (abs(spin) / spin);
			}
		}
		else {
			spin=0;
		}
		
		
		
		if (checkKey(keyBinds.get("right")) && isColliding) {
			speed.x += (cos(PI - spinoffset)) * movementspeed;
			speed.y += (sin(PI - spinoffset)) * movementspeed;
		}
		
		if (checkKey(keyBinds.get("left")) && isColliding) {
			speed.x -= (cos(PI - spinoffset)) * movementspeed;
			speed.y -= (sin(PI - spinoffset)) * movementspeed;
		}
		
		if (checkKey(keyBinds.get("start"))) {
			println("Player " + index + " start");
		}
		if (checkKey(keyBinds.get("button1"))) {
			println("Player " + index + " button 1");
		}
		if (checkKey(keyBinds.get("button2"))) {
			println("Player " + index + " button 2");
		}
		
		startAngle = (
			 - HALF_PI + atan2(
				this.position.y,
				this.position.x
			)
		);
		
		startAngle = (
			startAngle > 0 ?
				startAngle :
				(TWO_PI + startAngle)
		);
			
		super.update();
		
		endAngle = (
			- HALF_PI + atan2(
				this.position.y,
				this.position.x
			)
		);
		
		endAngle = (
			endAngle > 0 ?
				 endAngle :
				 (TWO_PI + endAngle)
		);
		
		if (abs(endAngle - startAngle) < PI) {
			pathtraveled += endAngle - startAngle;
		}
		
		//println("angles", endAngle, startAngle);
	}
}

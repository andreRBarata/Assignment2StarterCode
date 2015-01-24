class Player extends Droppable {
	TreeMap<String, Character> keyBinds;
	int index;
	color colour;
		
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
					triangle
						.transpose(
							new PVector(0,10)
						)
						.merge(
							triangle
								.roundRotate(PI)
								.transpose(
									new PVector(10,10)
								)
						)
						.merge(
							triangle
								.roundRotate(PI)
								.transpose(
									new PVector(-10,10)
								)
						)
						.scale(
							new PVector(1,0.6)
						)
			)
			.add(
				"body", rectangle
						.transpose(
							new PVector(0,-10)
						)
						.scale(
							new PVector(3,1)
						)
			)
		);
		this.keyBinds = new TreeMap<String, Character>();
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
		if (!colliding) {	
			if (checkKey(keyBinds.get("up"))) {
				spin += 0.035;
				this.spinoffset += 0.035;
			}
			if (checkKey(keyBinds.get("down"))) {
				spin -= 0.035;
				this.spinoffset -= 0.035;
			}
		}
		else {
			if (checkKey(keyBinds.get("right")) && speed.mag() < speedlimit) {
				speed.x -= (cos(spinoffset)) * movementspeed;
				speed.y -= (sin(spinoffset)) * movementspeed;
			}
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
		
		super.update();    
	}
}

class Player extends Droppable {
	TreeMap<String, Character> keyBinds;
	int index;
	color colour;
		
	Player() {
		super(
			new PVector(width / 2, height / 2),
			new Shape().add("windows",
					triangle
						.transpose(
							new PVector(0,10)
						)
						.merge(
							triangle
								.rotate(PI)
								.transpose(
									new PVector(10,10)
								)
						)
						.merge(
							triangle
								.rotate(PI)
								.transpose(
									new PVector(-10,10)
								)
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
		stroke(colour);
		
		super.display();
	}
	
	void update() {		
		if (checkKey(keyBinds.get("up"))) {
			spin += HALF_PI;
		}
		if (checkKey(keyBinds.get("down"))) {
			spin -= HALF_PI;
		}  
		if (checkKey(keyBinds.get("right"))) {
			speed.x -= (HALF_PI - cos(spin));
			speed.y -= (HALF_PI - sin(spin));
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

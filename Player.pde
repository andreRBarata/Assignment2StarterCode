class Player extends Droppable {
	TreeMap<String, Character> keyBinds;
	int index;
	color colour;
		
	Player() {
		super(
			new PVector(width / 2, height / 2),
			triangle.scale(1.5)
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
	
	
	void draw() {
		stroke(colour);
		
		super.draw();
	}
	
	void update() {
		if (checkKey(keyBinds.get("up"))) {
			speed.y -= 2;
		}
		if (checkKey(keyBinds.get("down"))) {
			speed.y += 2;
		}
		if (checkKey(keyBinds.get("left"))) {
			speed.x -= 2;
		}    
		if (checkKey(keyBinds.get("right"))) {
			speed.x += 2;
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
	}
}

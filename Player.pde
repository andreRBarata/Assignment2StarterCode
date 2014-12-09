interface CallBack {
	public void run();
}

class Shape {
	PVector[][] shape;
	
	Shape(PVector[][] shape) {
		this.shape = shape;
	}
	
	int size() {
		return shape.length;
	}
	
	PVector[] get(int i) {
		return shape[i];
	}
	
	float maxWidth() {
		float maxWidth = 0;
		for (int i = 0; i < shape.length; i++) {
			if (i == 0 || shape[i][0].x > maxWidth) {
				maxWidth = shape[i][0].x;
			}
			if (i == 0 || shape[i][1].x > maxWidth) {
				maxWidth = shape[i][1].x;
			}
		}
		
		return maxWidth;
	}
	
	float maxHeight() {
		float maxHeight = 0;
		for (int i = 0; i < shape.length; i++) {
			if (i == 0 || shape[i][0].y > maxHeight) {
				maxHeight = shape[i][0].y;
			}
			if (i == 0 || shape[i][1].y > maxHeight) {
				maxHeight = shape[i][1].y;
			}
		}
		
		return maxHeight;
	}
}

class Drawable {
	PVector position;
	Shape shape;
	
	Drawable(PVector position, PVector[][] shape) {
		this.position = position;
		this.shape = new Shape(shape);
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

class Button extends Drawable {
	CallBack callback;
	String text;
	
	Button(PVector position, String text, PVector[][] shape, CallBack callback) {
		super(position, shape);
		
		
		this.callback = callback;
		this.text = text;
	}
	
	void draw() {
		fill(color(255));

		super.draw();
		
		fill(color(0));
		
		text(
			text,
			position.x + this.shape.maxWidth()/2 - textWidth(text)/2,
			position.y + (this.shape.maxHeight() + 10)/2
		);
	}
}

class Player extends Drawable {
	TreeMap<String, Character> keyBinds;
	int index;
	color colour;
		
	Player() {
		super(
			new PVector(width / 2, height / 2),
			rectangle
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
	
	void update() {
		if (checkKey(keyBinds.get("up"))) {
		  position.y -= 1;
		}
		if (checkKey(keyBinds.get("down"))) {
		  position.y += 1;
		}
		if (checkKey(keyBinds.get("left"))) {
		  position.x -= 1;
		}    
		if (checkKey(keyBinds.get("right"))) {
		  position.x += 1;
		}
		if (checkKey(keyBinds.get("start"))) {
		  println("Player " + index + " start");
		}
		if (checkKey(keyBinds.get("button1"))) {
		  println("Player " + index + " button 1");
		}
		if (checkKey(keyBinds.get("button2"))) {
		  println("Player " + index + " butt2");
		}    
	}
}

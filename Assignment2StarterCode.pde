/*
		DIT OOP Assignment 2 Starter Code
		=================================
		
		Loads player properties from an xml file
		See: https://github.com/skooter500/DT228-OOP 
*/
import java.util.*;

Drawable map;

ArrayList<Player> players;
ArrayList<Button> buttons;
boolean[] keys = new boolean[526];

void setup() {
	players = new ArrayList<Player>();
	buttons = new ArrayList<Button>();
	size(700, 500);
	
	shapes();
	drawMap();

	background(255);
	setUpPlayerControllers();
	
	Button button = new Button(
		new PVector(width/2, height/2),
		"button",
		rectangle
			.scale(
				new PVector(2.5, 1)
			)
			.transpose(
				new PVector(25,10)
			),
		new CallBack() {
			public void run() {
				println("teste");
			}
		}
	);
	
	buttons.add(button);
}

void draw() {
	background(255);
	
	for(Player player: players) {
		stroke(0);
		player.update();
		player.draw();
	}
	
	for (int i = 0; i < buttons.size(); i++) {
		stroke(0);
		Button button = buttons.get(i);
		button.draw();
	}
	
	println(
		collider(players.get(0), players.get(1)).size()
	);
	
	map.draw();
}

void drawMap() {
	Shape shape = new Shape();
	PVector point = new PVector(0,0);
	float theta = 0;
	float thetaInc = TWO_PI / 10000;
	float noiseScale = 0.2;

	while (theta - PI < HALF_PI) {
		float radius = 10 + 3 * noise(	
			noiseScale * point.x, 
			noiseScale * point.y
		);
		
		point = new PVector(
			sin(theta) * radius,
	    		cos(theta) * radius
	    	);
		
		shape.add(point);
		
		theta += thetaInc;
	}
	
	map = new Drawable(
		new PVector(width/2,height/2),
		shape.scale(70)
	);
}

void keyPressed() {
	keys[keyCode] = true;
}

void keyReleased() {
	keys[keyCode] = false;
}

boolean checkKey(char theKey) {
	return keys[Character.toUpperCase(theKey)];
}

char buttonNameToKey(XML xml, String buttonName) {
	String value =  xml.getChild(buttonName).getContent();
	if ("LEFT".equalsIgnoreCase(value)) {
		return LEFT;
	}
	if ("RIGHT".equalsIgnoreCase(value)) {
		return RIGHT;
	}
	if ("UP".equalsIgnoreCase(value)) {
		return UP;
	}
	if ("DOWN".equalsIgnoreCase(value)) {
		return DOWN;
	}
	//.. Others to follow
	return value.charAt(0);  
}

void setUpPlayerControllers() {
	XML xml = loadXML("arcade.xml");
	XML[] children = xml.getChildren("player");
	int gap = width / (children.length + 1);
	
	for(int i = 0; i < children.length; i ++) {
		XML playerXML = children[i];
		Player p = new Player(
			i,
			color(random(0, 255), random(0, 255), random(0, 255)),
			playerXML
		);
		int x = (i + 1) * gap;
		p.position.x = x;
		p.position.y = 300;
		players.add(p);         
	}
}

void mousePressed() {
	for (int i = 0; i < buttons.size(); i++) {
		Button button = buttons.get(i);
		
		if (mouseX > button.position.x && mouseX < button.position.x + button.shape.maxWidth()) {
			if (mouseY > button.position.y && mouseY < button.position.y + button.shape.maxHeight()) {
				button.clicked = true;
				button.callback.run();
			}
		}
	}
}

void mouseReleased() {
	for (int i = 0; i < buttons.size(); i++) {
		Button button = buttons.get(i);
		
		button.clicked = false;
	}
}

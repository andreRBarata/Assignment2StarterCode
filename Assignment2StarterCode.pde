/*
		DIT OOP Assignment 2 Starter Code
		=================================
		
		Loads player properties from an xml file
		See: https://github.com/skooter500/DT228-OOP 
*/
import java.util.*;

float gravity;
float planetScale;
float hillsize;

Drawable map;

ArrayList<Player> players;
ArrayList<Button> buttons;
boolean[] keys = new boolean[526];

void setup() {
	players = new ArrayList<Player>();
	buttons = new ArrayList<Button>();
	size(700, 500);

	gravity = 5;
	planetScale = 900;
	hillsize = 5;
	
	Poligons();
	createMap();
	
	background(255);
	setUpPlayerControllers();
}

void draw() {
	background(255);
	
	pushMatrix();
	translate(
		width/2, height + planetScale -10
	);
	rotate(
		PI - (atan2(players.get(0).position.y, players.get(0).position.x) -
			atan2(map.position.y, map.position.x)) + HALF_PI
	);
	
	stroke(0);
	
	map.display();
	
	fill(255,255,0);
	
	for(Player player: players) {
		stroke(0);
		player.update();
		player.display();
	}
	
	fill(0);
	
	for (int i = 0; i < buttons.size(); i++) {
		stroke(0);
		Button button = buttons.get(i);
		button.display();
	}
	
	popMatrix();
}

void createMap() {
	Poligon Poligon = new Poligon();
	PVector point = new PVector(0,0);
	float theta = 0;
	float thetaInc = TWO_PI / 500;
	float noiseScale = 0.1;

	while (theta - PI < HALF_PI) {
		float radius = 10 + hillsize * noise(	
			noiseScale * point.x, 
			noiseScale * point.y
		);
		
		point = new PVector(
			sin(theta) * radius,
	    		cos(theta) * radius
	    	);
		
		Poligon.add(point);
		
		theta += thetaInc;
	}
	
	map = new Drawable(
		new PVector(0,0),
		//new PVector(width/2, height/2),
		Poligon.scale(planetScale / 10)
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
		int x = width/2 - (i + 1) * gap;
		p.position.x = x;
		p.position.y = planetScale + hillsize * 100 + 50;
		players.add(p);
	}
}

/*void mousePressed() {
	for (int i = 0; i < buttons.size(); i++) {
		Button button = buttons.get(i);
		
		if (mouseX > button.position.x && mouseX < button.position.x + button.Poligon.maxWidth()) {
			if (mouseY > button.position.y && mouseY < button.position.y + button.Poligon.maxHeight()) {
				button.clicked = true;
				button.callback.run();
			}
		}
	}
}*/

void mouseReleased() {
	for (int i = 0; i < buttons.size(); i++) {
		Button button = buttons.get(i);
		
		button.clicked = false;
	}
}

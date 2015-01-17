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
float spinradius;
float movementspeed;
float minimapscale;

Drawable map;

ArrayList<Player> players;
ArrayList<Button> buttons;
boolean[] keys = new boolean[526];

void setup() {
	players = new ArrayList<Player>();
	buttons = new ArrayList<Button>();
	size(700, 700);

	gravity = 8;
	planetScale = 1100;
	hillsize = 550;
	spinradius = 1000;
	movementspeed = 2;
	minimapscale = 60;
	
	Poligons();
	createMap();
	//map.sprite = triangle.roundRotate(PI).scale(110);
	background(255);
	setUpPlayerControllers();
}

void draw() {
	/*noLoop();
	players.get(0).position = new PVector(489.15033, 132.2817);
	println("collision", collider(players.get(0), map));*/
	
	PVector avgPlayer = new PVector();
	Drawable minimap = (Drawable)map.clone();
	
	background(51);
	
	for (Player player: players) {
		avgPlayer.add(player.position);
	}
	
	avgPlayer.div(players.size());
	//Game map processing		
	pushMatrix();
		translate(
			width/2, height/2 + avgPlayer.y
		);
		rotate(
			- (HALF_PI + PVector.angleBetween(
				avgPlayer,
				new PVector(1, 0)
			))
		);
		
		stroke(0);
		fill(255);
		map.display();
		
		for(Player player: players) {
			player.update();
			player.display();
		}
		
	popMatrix();
	
	
	fill(0);
	
	for (int i = 0; i < buttons.size(); i++) {
		stroke(0);
		Button button = buttons.get(i);
		button.display();
	}
	
	for (Player player: players) {
		PVector location = minimap
			.position
			.get();
		
		location = PVector.sub(
			location,
			PVector.mult(
				player.position,
				minimapscale/planetScale
			)
		);
			
		ellipse(location.x, location.y, 10, 10);
	}
	
	fill(255);
	stroke(0);
	
	minimap.sprite = minimap
		.sprite
		.rotate(
			PI - (atan2(avgPlayer.y, avgPlayer.x) -
				atan2(map.position.y, map.position.x)) + HALF_PI
		)
		.scale(minimapscale/planetScale);

	minimap.position = new PVector(
		width - (minimapscale + 20),
		(minimapscale + 20)
	);
	
	minimap.display();
}

void createMap() {
	Poligon poligon = new Poligon();
	PVector point = new PVector(0,0);
	float theta = 0;
	float thetaInc = TWO_PI / 350;
	float noiseScale = 0.0029;
	int start = 0;

	while (theta < TWO_PI) {
		float radius = map(
			noise(	
				noiseScale * point.x, 
				noiseScale * point.y
			),
			0,
			1,
			planetScale,
			planetScale + hillsize
		) * start;
		
		if (start == 0) {
			radius += planetScale + hillsize/2;
		}
		
		point = new PVector(
			sin(theta) * radius,
	    		cos(theta) * radius
	    	);
		
		poligon.add(point);
		
		theta += thetaInc;
		
		if (theta > HALF_PI/6) {
			start = 1;
		}
		
		if (theta > TWO_PI - HALF_PI/6) {
			start = 0;
		}
	}
	
	map = new Drawable(
		new PVector(0,0),
		poligon
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
	
	for(int i = 0; i < 1/*children.length*/; i ++) {
		XML playerXML = children[i];
		Player p = new Player(
			i,
			color(random(0, 255), random(0, 255), random(0, 255)),
			playerXML
		);
		int x = width/2 - (i + 1) * gap;
		p.position.x = x;
		p.position.y = planetScale + hillsize + 100;
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

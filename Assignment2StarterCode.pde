/*
		DIT OOP Assignment 2 Starter Code
		=================================
		
		Loads player properties from an xml file
		See: https://github.com/skooter500/DT228-OOP 
*/
import java.util.*;
import java.awt.*;
import java.awt.geom.*;

boolean devMode = true;
boolean start = false;
boolean multiplayer = false;

float gravity;
float planetScale;
float hillsize;
float spinradius;
float movementspeed;
float minimapscale;
float speedlimit;

int timer;
int startMilis;

Drawable map;

ArrayList<Player> players;
ArrayList<Button> buttons;
boolean[] keys = new boolean[526];
boolean written = false;
boolean sketchFullScreen() {
	return !devMode;
}

void setup() {
	players = new ArrayList<Player>();
	buttons = new ArrayList<Button>();
	if (devMode) {
		size(800, 600);
	}
	else {
		size(displayWidth, displayHeight);
	}

	gravity = 900;
	planetScale = 1100;
	hillsize = 550;
	spinradius = 1000;
	movementspeed = 150;
	minimapscale = 60;
	speedlimit = 500;
	timer = 0;
	
	poligons();
	createMap();
	//map.sprite = triangle.roundRotate(PI).scale(110);
	background(255);
	setUpPlayerControllers();
	textAlign(CENTER,CENTER);
}

void draw() {
	/*noLoop();
	players.get(0).position = new PVector(69.24815, 1374.2155);
	((Shape)players.get(0).sprite).updateOutline();
	println(collider(players.get(0),map));*/
	
	background(51);
	
	if (!start) {
		startMessage();
	}
	else {
		PVector avgPlayer = new PVector();
	
		for (Player player: players) {
			avgPlayer.add(player.position);
			println(((Shape)player.sprite).getOutline());
		}
	
		avgPlayer.div(players.size());
		//Game map processing		
		pushMatrix();
			float angle = -(HALF_PI + atan2(
					avgPlayer.y, avgPlayer.x
				)
			);
			translate(
				width/2, -(avgPlayer.y/cos(angle)) + height/2
			);
			rotate(
				angle
			);
		
			stroke(0);
			fill(255);
			map.display();
		
			for(Player player: players) {
				player.update();
				player.display();
			}
		
		popMatrix();
	
		hud(avgPlayer);
	}
	
	for (int i = 0; i < buttons.size(); i++) {
		Button button = buttons.get(i);
		button.display();
	}
}

void hud(PVector avgPlayer) {
	Drawable minimap = (Drawable)map.clone();
	
	fill(255);
	stroke(0);
	
	if (!multiplayer) {
		text(timer,
			width/2,
			40
		);
	}
	
	
	minimap.sprite = minimap
		.sprite
		.rotate(
			PI
		)
		.scale(minimapscale/planetScale);

	minimap.position = new PVector(
		width - (minimapscale + 30),
		(minimapscale + 30)
	);
	
	minimap.display();
	
	for (Player player: players) {
		PVector location = minimap
			.position
			.get();
		
		location = PVector.sub(
			minimap.position,
			PVector.mult(
				player.position,
				minimapscale/planetScale
			)
		);
		
		if (!multiplayer) {
			text(
				((int)(player.pathtraveled / TWO_PI * 100)) + "%",
				width/3,
				40
			);
			if (devMode) {
				text(
					"Speed: " + player.speed.mag(),
					100,50
				);
			
				text(
					"Angle: " + player.spinoffset,
					100,100
				);
			
				text(
					"Altitude: " + (PVector.dist(player.position, new PVector(0,0)) / TWO_PI) * 360,
					100,150
				);
			
				text(
					"isColliding: " + player.isColliding,
					100,200
				);
			}
		}

		fill(player.colour);
		
		ellipse(location.x, location.y, 10, 10);
		
		if (player.pathtraveled > TWO_PI) {
			gameOver(player);
		}
		else {
			if (!multiplayer) {
				timer = (millis() - startMilis)/1000;
			}
		}
	}
}

void startMessage() {
	ArrayList<Button> menubuttons =
		new ArrayList<Button>();

	fill(255);

	rect(
		20,20,
		width - 40,
		height - 40,
		20
	);
	
	fill(0);
	textSize(32);
	
	text(
		"Welcome to Asteroid Racer",
		width/2, 80
	);
	
	for (Player player: players) {
		int playerNum = players.indexOf(player) + 1;
		
		
		textSize(25);
	
		if (checkKey(player.keyBinds.get("right"))) {
			int i = 0;
			
			while (i < 3 && player.name[i] == 'Z') {
				i++;
			}
			
			if (i < 3) {
				player.name[i]++;
			}
			else {
				player.name = "AAA".toCharArray();
			}
		}
		if (checkKey(player.keyBinds.get("left"))) {
			int i = 0;
			
			while (i < 3 && player.name[i] == 'A') {
				i++;
			}
			
			if (i < 3) {
				player.name[i]--;
			}
			else {
				player.name = "ZZZ".toCharArray();
			}
		}
	
		text("Set name of player "
			+ playerNum
			+ ": "
			+ String.valueOf(player.name),
			width/2, height/(players.size() + 1) * playerNum
		);
	}
	menubuttons.add(
		new Button(
			new PVector(width/2, height - 80),
			"Start Game",
			rectangle
				.scale(
					new PVector(10, 2.5)
				),
			new CallBack() {
				public void run() {
					start = true;
					buttons = new ArrayList<Button>();
					startMilis = millis();
				}
			}
		)
	);
	
	if (buttons != menubuttons) {
		buttons = menubuttons;
	}
}

void gameOver(Player player) {
	ArrayList<Button> menubuttons =
			new ArrayList<Button>();
	String[] file = loadStrings("scores.txt");
	ArrayList<String> data;
	
	fill(255);

	rect(
		20,20,
		width - 40,
		height - 40,
		20
	);
	
	fill(0);
	textSize(32);
	
	text(
		"Congratulations, you won the game",
		width/2, 80
	);
	
	textSize(25);
	
	text(
		"I am as suprised as you are! " + String.valueOf(player.name),
		width/2, 120
	);
	
	text(
		"It only took you " + timer + " seconds",
		width/2, 145
	);
	
	if (file != null) {
		int i;
		
		data = new ArrayList<String>(
			Arrays.asList(file)
		);
		
		i = data.size() - 1;
		
		if (data.size() >= 1) {
			int score = Integer.valueOf(
				split(data.get(i), ",")[0]
			);
		
			while (i > 0 && score > timer) {
				i--;
				String[] line = split(data.get(i), ",");
				score = Integer.valueOf(line[0]);		
			}
			
			i++;
		}
		else {
			i = 0;
		}
		
		data.add(i,
			String.valueOf(timer) + "," +
			String.valueOf(player.name)
		);
		
		for (String line: data) {
			text(
				line.replace(",", ": "),
				width/2,
				200 + 30 * data.indexOf(line)
			);
		}
	}
	else {
		data = new ArrayList<String>();
		
		data.add(
			String.valueOf(timer) + "," +
			String.valueOf(player.name)
		);
	}
	
	if (!written) {
		saveStrings("scores.txt", 
			data
				.subList(0,min(10, data.size()))
				.toArray(new String[min(10, data.size())])
		);
		
		written = true;
	}

	player.speed = new PVector(0,0);
	
	
	menubuttons.add(
		new Button(
			new PVector(width/2, height - 80),
			"Go again",
			rectangle
				.scale(
					new PVector(10, 2.5)
				),
			new CallBack() {
				public void run() {
					start = false;
					setup();
				}
			}
		)
	);
	
	if (buttons != menubuttons) {
		buttons = menubuttons;
	}
}

void createMap() {
	Poligon poligon = new Poligon();
	PVector point = new PVector(0,0);
	float theta = 0;
	float thetaInc = TWO_PI / 100;
	float noiseScale = 0.0039;
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
		
		p.pathtraveled = -(
			- HALF_PI + atan2(
				p.position.y,
				p.position.x
			)
		);
		
		players.add(p);
	}
}

void mousePressed() {
	for (Button button: buttons) {
		Vectorial spriteInSpace = button
			.sprite
			.transpose(button.position);
		
		if (pointInVectorial(new PVector(mouseX,mouseY), spriteInSpace)) {
			button.clicked = true;
			button.callback.run();
		}
	}
}

void mouseReleased() {
	for (int i = 0; i < buttons.size(); i++) {
		Button button = buttons.get(i);
		
		button.clicked = false;
	}
}

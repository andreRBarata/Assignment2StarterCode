import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import java.util.*; 
import java.awt.*; 
import java.awt.geom.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class Assignment2StarterCode extends PApplet {

/*
		DIT OOP Assignment 2 Starter Code
		=================================
		
		Loads player properties from an xml file
		See: https://github.com/skooter500/DT228-OOP 
*/




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
public boolean sketchFullScreen() {
	return !devMode;
}

public void setup() {
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

public void draw() {
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

public void hud(PVector avgPlayer) {
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

public void startMessage() {
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
					new PVector(10, 2.5f)
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

public void gameOver(Player player) {
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
					new PVector(10, 2.5f)
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

public void createMap() {
	Poligon poligon = new Poligon();
	PVector point = new PVector(0,0);
	float theta = 0;
	float thetaInc = TWO_PI / 100;
	float noiseScale = 0.0039f;
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

public void keyPressed() {
	keys[keyCode] = true;
}

public void keyReleased() {
	keys[keyCode] = false;
}

public boolean checkKey(char theKey) {
	return keys[Character.toUpperCase(theKey)];
}

public char buttonNameToKey(XML xml, String buttonName) {
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

public void setUpPlayerControllers() {
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

public void mousePressed() {
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

public void mouseReleased() {
	for (int i = 0; i < buttons.size(); i++) {
		Button button = buttons.get(i);
		
		button.clicked = false;
	}
}
Poligon rectangle;
Poligon triangle;
Poligon circle;
Poligon trapezium;

public void poligons() {
	rectangle = new Poligon(
		new PVector[] {
			new PVector(-10,-10),
			new PVector(10,-10),
			new PVector(10,10),
			new PVector(-10,10)
		}
	);
	
	triangle = new Poligon(
		new PVector[] {
			new PVector(0,-10),
			new PVector(-10,10),
			new PVector(10,10)
		}
	);
	
	float theta = 0;
	float thetaInc = TWO_PI / 10;
	circle = new Poligon();
	while (theta < TWO_PI) {
		circle.add(
			new PVector(
		    		sin(theta) * 10,
		    		cos(theta) * 10
		    	)
		);
		
		theta += thetaInc;
	}
	
	trapezium = triangle
			.transpose(
				new PVector(0,0)
			)
			.merge(
				triangle
					.roundRotate(PI)
					.transpose(
						new PVector(10,0)
					)
			)
			.merge(
				triangle
					.roundRotate(PI)
					.transpose(
						new PVector(-10,0)
					)
			);
}
interface CallBack {
	public void run();
}

interface Vectorial {
	public void draw();
	public int size();
	public Vectorial clone();

	public PVector[] getLine(int i);
	public PVector center();
	public float getRadius();
	public float getArea();
	public boolean contains(Object point);
	public int count();

	public Vectorial transpose(PVector val);
	public Vectorial rotate(float degrees);
	public Vectorial scale(float val);
	public Vectorial scale(PVector val);
}

public PVector gravity(Droppable p, Drawable m) {
	return new PVector(
		- sin(
			HALF_PI + atan2(
				p.position.y - m.position.y,
				p.position.x - m.position.x
			)
		) * (gravity/frameRate),
		- cos(
			HALF_PI + atan2(
				p.position.y - m.position.y,
				p.position.x - m.position.x
			)
		) * (gravity/frameRate)
	);
}

public PVector yToProcessing(PVector point) {
	return new PVector(
		point.x,
		-point.y
	);
}

public boolean isNull(Object o1, Object o2) {
	return (o1 == null || o2 == null);
}

public boolean equalApproximately(PVector p1, PVector p2) {
	return (
		(abs(p1.x - p2.x) < 0.0001f) &&
		(abs(p1.y - p2.y) < 0.0001f)
	);
}


public boolean inBox(PVector point, PVector[] line) {
	/*print("Starting in box", point);
	//println(line );
	//println("y >= min", point.y, min(line[0].y, line[1].y));*/
	if (point.y >= min(line[0].y, line[1].y)-0.00001f) {
		//println("y <=max", point.y, max(line[0].y, line[1].y));
		if (point.y <= max(line[0].y, line[1].y)+0.00001f) {
			//println("x >= min", point.x, min(line[0].x, line[1].x));
			if (point.x >= min(line[0].x, line[1].x)-0.00001f) {
				//println("x <= max", point.x, max(line[0].x, line[1].x));
				if (point.x <= max(line[0].x, line[1].x)+0.00001f) {
					return true;
				}
			}
		}
	}
	
	return false;
}

/*boolean inBox(PVector point, PVector[] line) {
	return inLine(point, line);
}*/

public boolean inLine(PVector point, PVector[] line) {
	float m = lineSlope(line);
	float b = line[0].y - m * line[0].x;
	//print("point",point);
	//print("line");
	//println(line);
	if ((Float.isNaN(m) || Float.isInfinite(m))) {
		return (
			(abs(point.x - line[0].x) <= 0.0000001f) &&
			(point.y <= max(line[0].y, line[1].y)) &&
			(point.y >= min(line[0].y, line[1].y))
		);
	}
	else {
		return  (	
				((m * point.x + b) == point.y) &&
				(point.y <= max(line[0].y, line[1].y)) &&
				(point.y >= min(line[0].y, line[1].y))
			);
	}	
}

public float lineSlope(PVector[] line) {
	return (
		(line[1].y - line[0].y) /
		(line[1].x - line[0].x)
	);
}

public PVector getIntersection(PVector[] line1, PVector[] line2) {
	float m1 = lineSlope(line1);
	float m2 = lineSlope(line2);
	//////println("ms",m1,m2);
	if ((Float.isNaN(m1) || Float.isInfinite(m1)) && (Float.isNaN(m2) || Float.isInfinite(m2)) ) {
		//////println("NaN1");
		return null;
	}
	else if (Float.isNaN(m1) || Float.isInfinite(m1)) {
		float b2 = line2[0].y - m2 * line2[0].x;
		//////println("NaN2", b2,m2);
		//////println(line2);
		return new PVector(
			line1[0].x,
			m2 * line1[0].x + b2
		);
	}
	else if (Float.isNaN(m2) || Float.isInfinite(m2)) {
		float b1 = line1[0].y - m1 * line1[0].x;
		//////println("NaN3");
		return new PVector(
			line2[0].x,
			m1 * line2[0].x + b1
		);
	}
	else {
		//////println("NaN4");
		if (abs(m1 - m2) > 0.00001f) {
			PVector intersection;
			//////println("NaN5");
			float b1 = line1[0].y - m1 * line1[0].x;
			float b2 = line2[0].y - m2 * line2[0].x;
			
			intersection = new PVector(
				(b2 - b1) / (m1 - m2),
				m1 * ((b2 - b1) / (m1 - m2)) + b1
			);
			//////println(intersection);
			return intersection;
		}
		else {
			//////println("NaN6");
			return null;
		}
	}
}

public PVector intersectionInline(PVector[] line1, PVector[] line2) {
	PVector intersection = getIntersection(line1, line2);
	
	if (intersection != null) {
		//println("point", intersection);
		if (inBox(intersection, line1) && inBox(intersection, line2)) {
			return intersection;
		}
		else {
			return null;
		}
	}
	else {
		return null;
	}
}

public float lineAngle(PVector[] line1, PVector[] line2) {
	return (
		atan2((line2[1].y - line2[0].y), (line2[1].x - line2[0].x)) -
		atan2((line1[1].y - line1[0].y), (line1[1].x - line1[0].x))
	);
}

public boolean pointInVectorial(PVector point, Vectorial sprite) {
	ArrayList<PVector> tested = new ArrayList<PVector>();
	int count = 0;
	boolean isInside = false;
	
	//println("Begining pointInVectorial: is point", point, "in polygon", sprite);
	
	if (sprite.contains(point)) {
		//println("point is one of the vertices of the poligon");
		isInside = true;
	}
	else {
		int i = 0;
		while (i < sprite.count() && !isInside) {
			PVector[] side = sprite.getLine(i);
			
			////print("testing side", i);
			//println(side);
			
			if (inLine(point, side)) {
				//println("point is over side");
				isInside = true;
			}
			else {
				//println("point is not over side, testing further");
				PVector[] probe = new PVector[] {
					point.get(),
					new PVector(new Float(point.x), 0)
				};
				PVector intersection = getIntersection(
					probe,
					side
				);
				//println("found intersection", intersection);
				if (intersection != null) {
					//println("testing",intersection.y, point.y);
					//println("testing2",intersection);
					//println("testing3", (intersection.y < point.y), !tested.contains(intersection));
					//println(side);
					if (intersection.y < point.y && !tested.contains(intersection)) {
						//if (inLine(intersection, side)) {
						//println("testing y>min:", intersection.y , min(side[0].y, side[1].y));
						if (inBox(intersection, side)) {
							//println("Counting", point);
							tested.add(intersection);
							count++;
							//println("Count is now", count);		
						}
					}
				}
			}
			i++;
		}
		if (!isInside) {
			isInside = ((count % 2) == 1);
		}
		//println("count", count);
	}
	
	//println("is inside", isInside);
	return (isInside);
}

public Shape collider(Drawable p1, Drawable p2) {
	Vectorial spriteInSpace1 = p1.sprite;
	Vectorial spriteInSpace2 = p2.sprite;
		
		
	if (spriteInSpace1 instanceof Shape) {
		spriteInSpace1 = ((Shape)spriteInSpace1).getOutline();
	}
	
	if (spriteInSpace2 instanceof Shape) {
		spriteInSpace2 = ((Shape)spriteInSpace2).getOutline();
	}
	
	spriteInSpace1 = spriteInSpace1.transpose(p1.position);
	spriteInSpace2 = spriteInSpace2.transpose(p2.position);

	return collider(
		(Poligon)spriteInSpace1,
		(Poligon)spriteInSpace2
	);
}

public Shape collider(Poligon p1, Poligon p2) {
	Shape toReturn = new Shape();
	Poligon intersection = new Poligon();
	Polygon l1 = new Polygon();
	Polygon l2 = new Polygon();
	Area a1;
	Area a2;
	PathIterator pi;
	
	for (PVector point: p1) {
		l1.addPoint(
			(int)(point.x * 10000),
			(int)(point.y * 10000)
		);
	}
	
	for (PVector point: p2) {
		l2.addPoint(
			(int)(point.x * 10000),
			(int)(point.y * 10000)
		);
	}
	
	a1 = new Area(l1);
	a2 = new Area(l2);
	
	a1.intersect(a2);
	
	pi = a1.getPathIterator(new AffineTransform());
	
	while (!pi.isDone()) {
		float[] coords = new float[2];
		
		pi.currentSegment(coords);
		intersection.add(new PVector(coords[0]/10000, coords[1]/10000));
		pi.next();
	}
	
	for (int i = 0; i < p1.count(); i++) {
		for (int e = 0; e < p2.count(); e++) {
			PVector tmp = intersectionInline(
				p1.getLine(i),
				p2.getLine(e)
			);

			if (tmp != null) {
				if (!equalApproximately(p1.get(i), tmp)) {
					if (!equalApproximately(p2.get(e), tmp)) {
						toReturn.put("obj1_line",
							new Poligon(
								p1.getLine(i)
							)
						);
						toReturn.put("obj2_line",
							new Poligon(
								p2.getLine(e)
							)
						);
					}
				}
			}

		}
	}
	
	toReturn.put("intersection", intersection);

	return toReturn;
}

public Shape collider(Vectorial spriteInSpace1, Vectorial spriteInSpace2) {
	if (spriteInSpace1 instanceof Shape) {
		spriteInSpace1 = ((Shape)spriteInSpace1).getOutline();
	}
	
	if (spriteInSpace2 instanceof Shape) {
		spriteInSpace2 = ((Shape)spriteInSpace2).getOutline();
	}

	return collider(
		(Poligon)spriteInSpace1,
		(Poligon)spriteInSpace2
	);
}

public void rotateToSurface(Droppable p, Drawable m) {
	Shape collision = collider(p, m);
	float factor = 1;
	float oldrotation = new Float(p.spinoffset);
	float nextrotation = lineAngle(
		collision
			.get("obj2_line")
			.toArray(new PVector[2]),
		new PVector[] {
			new PVector(0,0),
			new PVector(1,0)
		}
	);
	
	println("while starting");
	while (collision.get("intersection").getArea() > 20 && factor > 0.0001f) {
		Droppable copy = p.clone();
		
		copy.sprite = copy.sprite.rotate(
			(nextrotation - copy.spinoffset) * factor
		);
		copy.spinoffset += (nextrotation - copy.spinoffset) * factor;
		
		collision = collider(copy, m);
		
		factor /= 2;
	}
	println("new angle", nextrotation * (factor * 2), factor);
	//if (factor > 0.0001) {
		p.sprite = p.sprite.rotate(
			 (nextrotation - p.spinoffset) * (factor * 2)
		);
		p.spinoffset += (nextrotation - p.spinoffset) * (factor * 2);
	//}
}

public void adjustToSurface(Droppable p1, Drawable p2) {
	float lerp = 1;
	Droppable copy = p1.clone();
	Shape collision = null;
	PVector nextposition = PVector.add(
		p1.position,
		PVector.mult(
			yToProcessing(p1.speed),
			1 / frameRate
		)
	);
	
	do {
		copy.position = PVector.lerp(
			p1.position,
			nextposition,
			lerp
		);
		
		collision = collider(copy, p2);
		//println("position", copy.position,"area", area, "lerp", lerp, collision.get("intersection"));
		
		lerp /= 2;
	}
	while (collision.get("intersection").getArea() > 20 && lerp > 0.01f);

	if (collision.get("intersection").getArea() > 40) {
		PVector preposition = copy.position;
		lerp = 0.001f;
	
		nextposition = PVector.lerp(
			p2.position,
			p1.position,
			2
		);


		while (collision.get("intersection").getArea() > 40 && lerp <= 1) {
			copy.position = PVector.lerp(
				p1.position,
				nextposition,
				lerp
			);
		
			collision = collider(copy, p2);
	
			lerp *= 2;
		}
		
		if (collision.get("intersection").getArea() > 40) {
			copy.position = preposition;
		}
	}
	
	p1.position = copy.position.get();
}
/*
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
*/

class Button extends Drawable {
	CallBack callback;
	boolean clicked;
	String text;
	
	Button(PVector position, String text, Poligon Poligon, CallBack callback) {
		super(position, Poligon);
		
		this.clicked = false;
		this.callback = callback;
		this.text = text;
	}
	
	public void display() {
		if (!clicked) {
			fill(color(255));
		}
		else {
			fill(color(20,20,200));
		}

		super.display();
		
		fill(0);
		
		text(
			text,
			position.x,
			position.y
		);
	}
}
class Drawable {
	PVector position;
	Vectorial sprite;
	
	Drawable(PVector position, Vectorial sprite) {
		this.position = position;
		this.sprite = sprite;
	}
	
	public Drawable clone() {
		return new Drawable(
			position.get(),
			sprite.clone()
		);
	}
	
	public void display() {
		Vectorial spriteInSpace = sprite.transpose(position);
		
		spriteInSpace.draw();
	}
}
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
	
	public Droppable clone() {
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
	
	public void update() {
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
			
			magnitude /= 1.5f;
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
class Player extends Droppable {
	TreeMap<String, Character> keyBinds;
	int index;
	int colour;
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
							new PVector(1,0.6f)
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
							new PVector(1.8f,1)
						)
			)
		
		);
		
		this.keyBinds = new TreeMap<String, Character>();
		this.pathtraveled = 0;
		this.name = "AAA".toCharArray();
	}
	
	Player(int index, int colour) {
		this();
		this.index = index;
		this.colour = colour;
	}
	
	Player(int index, int colour, TreeMap keyBinds) {
		this();
		this.index = index;
		this.colour = colour;
		this.keyBinds = keyBinds;
	}
	
	Player(int index, int colour, XML xml) {
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
	
	public void display() {
		fill(colour);
		super.display();
		
	}
	
	public void update() {
		float startAngle;
		float endAngle;
		
		if (!isColliding) {	
			if (checkKey(keyBinds.get("up"))) {
				spin += 0.15f;
			}
			else if (checkKey(keyBinds.get("down"))) {
				spin -= 0.15f;
			}
			else if (spin != 0) {
				spin -= 0.15f * (abs(spin) / spin);
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
class Poligon extends ArrayList<PVector> implements Vectorial {
	
	Poligon() {
		super();
	}
	
	Poligon(Poligon poligon) {
		super(poligon);
	}
	
	Poligon(PVector[] poligon) {
		super(Arrays.asList(poligon));
	}
	
	public int count() {
		return this.size();
	}
	
	public boolean containsApproximately(PVector point) {
		int i = 0;
		
		while (i < this.size() && !equalApproximately(this.get(i),point)) {
			i++;
		}
		return (i != this.size());
	}
	
	public float getRadius() {
		float total = 0;
		PVector center = this.center();
		
		for (int i = 0; i < this.size(); i++) {
			total += PVector.dist(center, this.get(i));
		}
		
		return total/this.size();
	}
	
	public float getArea() {
		float sum = 0;
		
		for (int i = 0; i < this.size(); i++) {
			PVector l1 = this.get(i);
			PVector l2 = this.get(
				(i + 1) % this.size()
			);
			
			sum += (l1.x * l2.y) - (l1.y * l2.x);
		}
		
		return abs(sum/2);
	}
	
	public void draw() {
		beginShape();
		
		for (int i = 0; i < this.size(); i++) {
			vertex(
				this.get(i).x,
				this.get(i).y
			);
		}

		endShape(CLOSE);
	}
	
	public PVector[] getLine(int i) {
		return new PVector[] {
			this.get(i).get(),
			this.get((i + 1) % this.size())
		};
	}
	
	public Poligon clone() {
		Poligon poligon = new Poligon();
		
		for (PVector point: this) {
			poligon.add(point.get());
		}
		
		return poligon;
	}
	
	public PVector center() {
		float sumx = 0;
		float sumy = 0;
		
		for (int i = 0; i < this.size(); i++) {
			sumx += this.get(i).x;
			sumy += this.get(i).y;
		}
		
		return new PVector(
			sumx / this.size(),
			sumy / this.size()
		);
	}
	
	public Poligon union(Poligon union) {
		Poligon poligon = new Poligon();
		Poligon ori1 = this.clone();
		Poligon ori2 = union.clone();
		
		PVector coords = ori1.get(0);
		
		//println("before", ori1, ori2);
		
		for (int i = 0; i < ori1.count(); i++) {
			for (int e = 0; e < ori2.count(); e++) {
				PVector tmp = intersectionInline(
					ori1.getLine(i),
					ori2.getLine(e)
				);
				//print("lines");
				//print(ori1.getLine(i));
				//println(ori2.getLine(e));
				if (tmp != null) {
					coords = tmp;
					//println("tmp", tmp);
					if (!ori1.containsApproximately(coords)) {
						if (!equalApproximately(ori1.get(i), coords)) {
							//println("tmp2", coords);
							ori1.add(i + 1, coords);
						}
					}
			
					if (!ori2.containsApproximately(coords)) {
						if (!equalApproximately(ori2.get(e), coords)) {
							ori2.add(e + 1, coords);
						}
					}
				}
			}
		}
		
		Collections.rotate(ori1, -ori1.indexOf(coords));
		
		/*print("poligons");
		print(ori1);
		println(ori2);*/
		
		do {
			poligon.add(ori1.get(0));
			//println("adding", ori1.get(0));
			if (ori2.contains(ori1.get(0))) {
				Poligon tmp = ori1;
				
				ori1 = ori2;
				ori2 = tmp;
				//println("switched");
				
				Collections.rotate(ori1, -ori1.indexOf(ori2.get(0)));
				
				if (pointInVectorial(ori1.get(1), ori2)) {
					Collections.reverse(ori1);
					//println("reversed");
					
					Collections.rotate(ori1, -ori1.indexOf(ori2.get(0)));
				}
			}
			
			Collections.rotate(ori1, -1);
		}
		while (!poligon.contains(ori1.get(0)));
		
		//println("poligon", poligon);
		return poligon;
	}
	
	public Poligon merge(Poligon merge) {
		Poligon poligon = new Poligon();
		Poligon ori1 = this.clone();
		Poligon ori2 = merge.clone();
		
		PVector coords = ori1.get(0);
		
		while (coords != null) {
			poligon.add(coords);
			ori1.remove(coords);
			
			if (ori2.contains(coords) || ori1.size() == 0) {
				Poligon tmp = ori1;
				
				ori2.remove(coords);
				
				ori1 = ori2;
				ori2 = tmp;
				Collections.rotate(ori1, -ori1.indexOf(coords));
				
				if (ori2.contains(coords) && ori2.contains(ori1.get(0))) {
					Collections.reverse(ori1);
				}
			}
			
			if (ori1.size() > 0) {
				coords = ori1.get(0);
			}
			else {
				coords = null;
			}
		}
		//println("return", poligon);
		return poligon;	
	}
	
	public Poligon rotate(float degrees) {
		Poligon poligon = (Poligon)this.clone();
		
		for (int i = 0; i < poligon.size(); i++) {
			poligon.get(i).rotate(degrees);
		}
		
		return poligon;
	}
	
	public Poligon roundRotate(float degrees) {
		Poligon poligon = (Poligon)this.clone();
		
		for (int i = 0; i < poligon.size(); i++) {
			poligon.get(i).rotate(degrees);
			poligon.get(i).x = round(poligon.get(i).x);
			poligon.get(i).y = round(poligon.get(i).y);
		}
		
		return poligon;
	}
	
	public Poligon transpose(PVector val) {
		Poligon poligon = (Poligon)this.clone();
		
		for (int i = 0; i < poligon.size(); i++) {
			poligon.get(i).add(val);
		}
		
		return poligon;
	}
	
	public Poligon scale(PVector val) {
		Poligon poligon = (Poligon)this.clone();
	
		for (int i = 0; i < poligon.size(); i++) {
			poligon.get(i).x *= val.x;
			poligon.get(i).y *= val.y;
		}
		
		return poligon;
	}
	
	public Poligon scale(float val) {
		Poligon poligon = (Poligon)this.clone();
	
		for (int i = 0; i < poligon.size(); i++) {
			poligon.get(i).mult(val);
		}
		
		return poligon;
	}
	
	public PVector highestX() {
		PVector highest = new PVector();
		
		for (int i = 0; i < this.size(); i++) {
			if (i == 0 || this.get(i).x > highest.x) {
				highest = this.get(i);
			}
		}
		
		return highest;
	}
	
	public PVector lowestX() {
		PVector lowest = new PVector();
		
		for (int i = 0; i < this.size(); i++) {
			if (i == 0 || this.get(i).x < lowest.x) {
				lowest = this.get(i);
			}
		}
		
		return lowest;
	}
	
	public float maxWidth() {
		float minWidth = 0;
		float maxWidth = 0;
		for (int i = 0; i < this.size(); i++) {
			if (i == 0 || this.get(i).x > maxWidth) {
				maxWidth = this.get(i).x;
			}
			
			if (i == 0 || this.get(i).x < minWidth) {
				minWidth = this.get(i).x;
			}
		}
		
		return maxWidth - minWidth;
	}
	
	public float maxHeight() {
		float minHeight = 0;
		float maxHeight = 0;
		for (int i = 0; i < this.size(); i++) {
			if (i == 0 || this.get(i).y > maxHeight) {
				maxHeight = this.get(i).y;
			}
			
			if (i == 0 || this.get(i).y < minHeight) {
				minHeight = this.get(i).y;
			}
		}
		
		return maxHeight - minHeight;
	}
}
class Shape extends TreeMap<String,Poligon> implements Vectorial {
	public Shape clone() {
		Shape shape = new Shape();
		
		for (String key: this.keySet()) {
			shape.put(
				new String((String) key),
				this.get(key).clone()
			);
		}
		
		return shape;
	}
	
	public Shape add(String key, Poligon poligon) {
		super.put(key, poligon);
		
		return this;
	}
	
	public PVector center() {
		Poligon composite = new Poligon();
	
		for (String key: this.keySet()) {	
			composite.addAll(this.get(key));
		}
		
		return composite.center();
	}
	
	public boolean contains(Object point) {
		String[] keys = this
			.keySet()
			.toArray(
				new String[this.size()]
			);
		int i = 0;
		boolean contains = false;
		
		while (i < keys.length && !contains) {
			contains = this.get(keys[i]).contains(point);
		}
		
		return contains;
	}
	
	public int count() {
		int size = 0;

		for (String key: this.keySet()) {
			size += this.get(key).count();
		}
		
		return size;
	}
	
	public float getRadius() {
		Poligon composite = new Poligon();
	
		for (String key: this.keySet()) {	
			composite.addAll(this.get(key));
		}
		
		return composite.getRadius();
	}
	
	public float getArea() {
		float area = 0;
		
		for (String key: this.keySet()) {
			area += this.get(key).getArea();
		}
		
		for (String outerkey: this.keySet()) {
			for (String innerkey: this.keySet()) {
				area -= collider(
					this.get(outerkey), this.get(innerkey)
				).get("intersection").getArea();
			}
		}
		
		return area;
	}
	
	public Poligon getOutline() {
		Polygon l1 = new Polygon();
		Polygon l2 = new Polygon();
		Area a1;
		Area a2;
		PathIterator pi;
	
		String[] keys = this
			.keySet()
			.toArray(
				new String[this.size()]
			);
		Poligon outline = null;
	
		for (PVector point: this.get(keys[0])) {
			l1.addPoint(
				(int)(point.x * 10000),
				(int)(point.y * 10000)
			);
		}
	
		a1 = new Area(l1);
	
		if (this.size() >= 1) {
			outline = this.get(keys[0]).clone();
		}
	
		if (this.size() > 1) {
			for (int i = 1; i < keys.length; i++) {
				for (PVector point: this.get(keys[i])) {
					l2.addPoint(
						(int)(point.x * 10000),
						(int)(point.y * 10000)
					);
				}
			
				a2 = new Area(l2);
			
				a1.add(a2);
			}
		
			pi = a1.getPathIterator(null);
			outline = new Poligon();
		
			while (!pi.isDone()) {
				float[] coords = new float[2];
	
				pi.currentSegment(coords);
				outline.add(new PVector(coords[0]/10000, coords[1]/10000));
				pi.next();
			}
		}	
	
		return outline;
	}
	
	public Shape transpose(PVector val) {
		Shape shape = this.clone();
	
		for (String key: this.keySet()) {
			shape.put(key, shape.get(key).transpose(val));
		}
		
		return shape;
	}
	
	public Shape scale(PVector val) {
		Shape shape = this.clone();
	
		for (String key: this.keySet()) {
			shape.put(key, shape.get(key).scale(val));
		}
		
		return shape;
	}
	
	public Shape scale(float val) {
		Shape shape = this.clone();
	
		for (String key: this.keySet()) {
			shape.put(key, shape.get(key).scale(val));
		}
		
		return shape;
	}
	
	public Shape rotate(float degrees) {
		Shape shape = this.clone();
	
		for (String key: this.keySet()) {
			shape.put(key,
				shape.get(key).rotate(degrees)
			);
		}
		
		return shape;
	}
	
	public void draw() {
		
		for (String key: this.keySet()) {
			this.get(key).draw();
		}
	}
	
	public PVector[] getLine(int i) {
		String[] keys = this
			.keySet()
			.toArray(
				new String[this.size()]
			);
		int e = 0;
		
		while (e < this.size() && i >= this.get(keys[e]).size()) {
			i -= this.get(keys[e]).count();
			e++;
		}
		
		
		if (i < this.get(keys[e]).size()) {
			Poligon poligon = this.get(keys[e]);
		
			return poligon.getLine(i);
		}
		else {
			return null;
		}
	}
}
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "Assignment2StarterCode" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}

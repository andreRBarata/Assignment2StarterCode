import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import java.util.*; 

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

public void setup() {
	players = new ArrayList<Player>();
	buttons = new ArrayList<Button>();
	size(700, 700);

	gravity = 8;
	planetScale = 1100;
	hillsize = 550;
	spinradius = 1000;
	movementspeed = 10;
	minimapscale = 60;
	
	Poligons();
	createMap();
	//map.sprite = triangle.roundRotate(PI).scale(110);
	background(255);
	setUpPlayerControllers();
}

public void draw() {
	/*noLoop();
	players.get(0).position = new PVector(489.15033, 132.2817);
	((Shape)players.get(0).sprite).getOutline();*/
	
	PVector avgPlayer = new PVector();
	
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
	
	hud(avgPlayer);
}

public void hud(PVector avgPlayer) {
	Drawable minimap = (Drawable)map.clone();

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

public void createMap() {
	Poligon poligon = new Poligon();
	PVector point = new PVector(0,0);
	float theta = 0;
	float thetaInc = TWO_PI / 350;
	float noiseScale = 0.0029f;
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

public void Poligons() {
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
	println(line );
	println("y >= min", point.y, min(line[0].y, line[1].y));*/
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
		spriteInSpace1 = ((Shape)spriteInSpace1).outline.clone();
	}
	
	if (spriteInSpace2 instanceof Shape) {
		spriteInSpace2 = ((Shape)spriteInSpace2).outline.clone();
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
	Poligon p1clone = p1.clone();
	Poligon p2clone = p2.clone();
	Poligon intersection = new Poligon();
	PVector pointception = null;
	
	//////print("Calculating intersection between");
	//////print(p1clone);
	//////print(" and ");
	////println(p2clone);
	
	////println("== Adding intersections to polygons");
	for (int i = 0; i < p1clone.count(); i++) {
		for (int e = 0; e < p2clone.count(); e++) {
			////println("Cycle: i=", i, "e=", e);
			PVector tmp = intersectionInline(
				p1clone.getLine(i),
				p2clone.getLine(e)
			);
			//////print("Calculating intersection of");
			//////print(p1clone.getLine(i));
			//////print(" and ");
			////println(p2clone.getLine(e));
			////println("found intersection:", tmp);
			if (tmp != null) {
				////println("intersection is not null");
				pointception = tmp;
				toReturn.put("obj1_line",
					new Poligon(
						p1clone.getLine(i)
					)
				);
				toReturn.put("obj2_line",
					new Poligon(
						p2clone.getLine(e)
					)
				);
				////println("testing p1clone: ", p1clone, "does not contain", pointception);
				if (!p1clone.contains(pointception)) {
					////println("testing p1clone: ", p1clone.get(i), "is not equal to", pointception);
					if (!equalApproximately(p1clone.get(i), pointception)) {
						////println("adding ",pointception, "to p1clone:", p1clone);
						p1clone.add(i + 1, pointception);
					}
				}
				
				////println("testing p2clone: ", p2clone, "does not contain", pointception);
				if (!p2clone.contains(pointception)) {
					////println("testing p2clone: ", p2clone.get(e), "is not equal to", pointception);
					if (!equalApproximately(p2clone.get(e), pointception)) {
						////println("adding ",pointception, "to p2clone:", p2clone);
						p2clone.add(e + 1, pointception);
					}
				}
				
			}

		}
	}

	//println("p1clone with intersections", p1clone);
	//println("p2clone with intersections", p2clone);
	
	//println("== Creating intersection polygon");
	Collections.rotate(p1clone, -p1clone.indexOf(pointception));
	Collections.rotate(p2clone, -p2clone.indexOf(pointception));
	//println("pointception", pointception);
	if (pointception == null) {
		if (pointInVectorial(p1clone.get(0), p2)) {
			intersection = p1.clone();
		}
		else if (pointInVectorial(p2clone.get(0), p1)) {
			intersection = p2.clone();
		}
	}
	else {
		//println("cicle");
		do {
			//println("P1=",p1clone);
			//println("P2=",p2clone);
			intersection.add(p1clone.get(0));
			//println("adding", p1clone.get(0));
			//println("considering", p1clone.get(1), p2clone);
			if (!pointInVectorial(p1clone.get(1), p2clone)) {
				Poligon tmp = p1clone;
				//println("does not contain 1");
				p1clone = p2clone;
				p2clone = tmp;
				
				//println("switched polygon, now considering", p1clone.get(1));
			}
			
			if (!pointInVectorial(p1clone.get(1), p2clone)) {
				//println("does not contain 2");
				Collections.reverse(p1clone);
				//println("rotated polygon, now considering", p1clone.get(1));
			}
			
			//println("In", p1clone, p1clone.size());
			Collections.rotate(p1clone, -1);
			Collections.rotate(p2clone, -p2clone.indexOf(p1clone.get(0)));
			//println("Intersection now contains",intersection);
			//println("p1clone now contains",p1clone);
			//println("testing: ",p1clone.get(0), "in", p2, p1clone.get(0), "not in", intersection);
			//println( "results: ", pointInVectorial(p1clone.get(0), p2) , !intersection.contains(p1clone.get(0) ));
		}
		while (pointInVectorial(p1clone.get(0), p2clone) && !intersection.contains(p1clone.get(0)));
	}
	
	toReturn.put("intersection", intersection);

	return toReturn;
}

public Shape collider(Vectorial spriteInSpace1, Vectorial spriteInSpace2) {
	if (spriteInSpace1 instanceof Shape) {
		spriteInSpace1 = ((Shape)spriteInSpace1).outline.clone();
	}
	
	if (spriteInSpace2 instanceof Shape) {
		spriteInSpace2 = ((Shape)spriteInSpace2).outline.clone();
	}

	return collider(
		(Poligon)spriteInSpace1,
		(Poligon)spriteInSpace2
	);
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
	
	public void draw() {
		fill(color(255));

		super.display();
		
		if (!clicked) {
			fill(color(0));
		}
		else {
			fill(color(20,20,200));
		}
		
		text(
			text,
			position.x + this.sprite.getRadius()/2 - textWidth(text)/2,
			position.y + (this.sprite.getRadius()/2 + 10)
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
	boolean colliding;
	float spin;
	float spinoffset;
	float momentum;
	
	Droppable(PVector position, Vectorial sprite) {
		super(position, sprite);
		this.speed = new PVector(0,0);
		this.colliding = false;
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
		clone.colliding = new Boolean(this.colliding);
		clone.spin = new Float(this.spin);
		clone.spinoffset = new Float(this.spinoffset);
		clone.momentum = new Float(this.momentum);
		
		return clone;
	}
	
	public void update() {
		if (this.sprite instanceof Shape) {
			((Shape)this.sprite).updateOutline();
		}
	
		Shape collision = collider(this, map);
		float area = collision
			.get("intersection")
			.getArea();
		//Droppable sink avoidence
		float lerp = 1;
		Droppable copy = this.clone();
		PVector nextposition = PVector.add(
			this.position,
			this.speed
		);
		
		do {
			copy.position = PVector.lerp(
				this.position,
				nextposition,
				lerp
			);
			
			collision = collider(copy, map);
			
			area = collision
				.get("intersection")
				.getArea();
				
			//println("position", copy.position,"area", area, "lerp", lerp, collision.get("intersection"));
			
			lerp /= 2;
		}
		while (area > 0 && lerp > 0.01f);
		
		//println("intersection P",collision.get("intersection") ,"speed", speed,"colliding", collision.get("obj2_line"));
		
		this.position = copy.position.get();
		copy.position = nextposition.get();
		
		collision = collider(copy, map);
		
		if (collision.get("intersection").size() > 2) {
			float magnitude = speed.mag()/1.5f;
			
			Vectorial spriteInSpace = copy
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
			
			this.spin -= (
				(
					transfer.mag()/(
						TWO_PI * spriteInSpace.getRadius() * spinradius
					)
				)
			);
			
			this.spin *= 0.5f;
			
			this.speed = PVector.fromAngle(
				PI - lineAngle(
					new PVector[] {
						PVector.sub(position, speed),
						copy.position
					},
					collision
						.get("obj2_line")
						.toArray(new PVector[2])
				)
			);
			
			this.speed.mult(magnitude);
			
			colliding = true;
		}
		else {
			colliding = false;	
		}
		
		this.sprite = this.sprite.rotate(
			spin
		);
		
		this.speed.x -= sin(
			HALF_PI + atan2(copy.position.y, copy.position.x) -
				atan2(map.position.y, map.position.x)
		) * (gravity/frameRate);
	
		this.speed.y += cos(
			HALF_PI + atan2(copy.position.y, copy.position.x) -
				atan2(map.position.y, map.position.x)
		) * (gravity/frameRate);
		
		this.spinoffset = (this.spinoffset + this.spin) % TWO_PI;
	}
}
class Player extends Droppable {
	TreeMap<String, Character> keyBinds;
	int index;
	int colour;
		
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
							new PVector(1,0.6f)
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
		if (!colliding) {	
			if (checkKey(keyBinds.get("up"))) {
				spin += 0.035f;
			}
			if (checkKey(keyBinds.get("down"))) {
				spin -= 0.035f;
			}
		}
		else {
			if (checkKey(keyBinds.get("right"))) {
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
		
		while (!equalApproximately(this.get(i),point) && i > this.size()) {
			i++;
		}
		return (equalApproximately(this.get(i),point));
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
		
		print("poligons");
		print(ori1);
		println(ori2);
		
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
				}
			}
			
			Collections.rotate(ori1, 1);
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
	Poligon outline;

	public Shape clone() {
		Shape shape = new Shape();
		
		shape.outline = outline.clone();
		
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
	
	public void updateOutline() {
		this.outline = getOutline();
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
		String[] keys = this
			.keySet()
			.toArray(
				new String[this.size()]
			);
		Poligon outline = null;
		
		if (this.size() >= 1) {
			outline = this.get(keys[0]).clone();
		}
		
		if (this.size() > 1) {
			for (int i = 1; i < keys.length; i++) {
				outline.union(this.get(keys[i]).clone());
			}
		}
		
		/*print("outline");
		println(outline);*/
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

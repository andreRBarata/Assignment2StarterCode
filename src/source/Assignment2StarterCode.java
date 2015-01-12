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
	size(700, 500);

	gravity = 8;
	planetScale = 1100;
	hillsize = 550;
	spinradius = 1000;
	movementspeed = 2;
	minimapscale = 60;
	
	Poligons();
	createMap();
	
	background(255);
	setUpPlayerControllers();
}

public void draw() {
	PVector avgPlayer = new PVector();
	Drawable minimap = (Drawable)map.clone();
	
	background(255);
	
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
			PI - (atan2(avgPlayer.y, avgPlayer.x) -
				atan2(map.position.y, map.position.x)) + HALF_PI
		);
	
		stroke(0);
		fill(139,69,19);
		map.display();
	
		for(Player player: players) {
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
	
	for (Player player: players) {
		PVector location = minimap
			.position
			.get();
			
		location.sub(
			PVector.mult(
				player.position,
				minimapscale
			)
		);
		
		println(location);
			
		ellipse(location.x, location.y, 10, 10);
	}
	
	fill(255);
	stroke(0);
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
		float radius = planetScale + hillsize * noise(	
			noiseScale * point.x, 
			noiseScale * point.y
		) * start;
		
		if (start == 0) {
			radius += hillsize/2;
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
	
	for(int i = 0; i < children.length; i ++) {
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
	public int lineCount();

	public Vectorial transpose(PVector val);
	public Vectorial rotate(float degrees);
	public Vectorial scale(float val);
	public Vectorial scale(PVector val);
}

public boolean isNull(Object o1, Object o2) {
	return (o1 == null || o2 == null);
}

public boolean inLine(PVector point, PVector[] line) {
	return  (
			(point.x < max(line[0].x, line[1].x)) &&
			(point.x > min(line[0].x, line[1].x))
		);	
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
	
	if (Float.isNaN(m1)) {
		float b2 = line2[0].y - m2 * line2[0].x;
		
		return new PVector(
			line1[0].x,
			m2 * line1[0].x + b2
		);
	}
	else if (Float.isNaN(m2)) {
		float b1 = line1[0].y - m2 * line1[0].x;
		
		return new PVector(
			line2[0].x,
			m1 * line2[0].x + b1
		);
	}
	else {
		if (m1 != m2) {
			float b1 = line1[0].y - m1 * line1[0].x;
			float b2 = line2[0].y - m2 * line2[0].x;
		
			float x = (
				(b2 - b1) / (m1 - m2)
			);
			float y = m1 * x + b1;
		
			return new PVector(
				x,
				y
			);
		}
		else {
			return null;
		}
	}
}

public PVector intersectionInline(PVector[] line1, PVector[] line2) {
	PVector intersection = getIntersection(line1, line2);
	
	if (intersection != null) {	
		if (inLine(intersection, line1) && inLine(intersection, line2)) {
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

public Shape collider(Drawable p1, Drawable p2) {
	Shape toReturn = new Shape();
	Vectorial spriteInSpace1 = p1.sprite
		.transpose(p1.position);
	Vectorial spriteInSpace2 = p2.sprite
		.transpose(p2.position);
	Poligon intersection = new Poligon();
	
	for (int i = 0; i < spriteInSpace1.lineCount(); i++) {
		PVector[] line1 = spriteInSpace1.getLine(i);
	
		for (int e = 0; e < spriteInSpace2.lineCount(); e++) {
			PVector[] line2 = spriteInSpace2.getLine(e);

			PVector intersectionPoint = intersectionInline(
				line1,
				line2
			);
			
			if (intersectionPoint != null) {
				intersection.add(intersectionPoint);
				toReturn.put("obj1_line", new Poligon(line1));
				toReturn.put("obj2_line", new Poligon(line2));
			}
		}
	}
	
	toReturn.put("intersection", intersection);
	
	
	return toReturn;
}

public Shape collider(Vectorial p1, Vectorial p2) {
	Shape toReturn = new Shape();
	Poligon intersection = new Poligon();
	
	for (int i = 0; i < p1.lineCount(); i++) {
		PVector[] line1 = p1.getLine(i);
	
		for (int e = 0; e < p2.lineCount(); e++) {
			PVector[] line2 = p2.getLine(e);

			PVector intersectionPoint = intersectionInline(
				line1,
				line2
			);
			
			if (intersectionPoint != null) {
				intersection.add(intersectionPoint);
				toReturn.put("obj1_line", new Poligon(line1));
				toReturn.put("obj2_line", new Poligon(line2));
			}
		}
	}
	
	toReturn.put("intersection", intersection);
	
	
	return toReturn;
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
	
	/*void draw() {
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
			position.x + this.Poligon.maxWidth()/2 - textWidth(text)/2,
			position.y + (this.Poligon.maxHeight() + 10)/2
		);
	}*/
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
	
	public void update() {
		Shape collision = collider(this, map);
		
		if (collision.get("intersection").size() > 1) {
			PVector old_position = this.position.get();
			old_position.sub(this.speed);
			PVector edgeVector = collision
				.get("obj2_line")
				.get(1)
				.get();
				
			edgeVector.sub(
				collision
					.get("obj2_line")
					.get(0)
			);
			
			float baseAngle = lineAngle(
				collision
					.get("obj2_line")
					.toArray(new PVector[2]),
				new PVector[] {
					new PVector(0,0),
					new PVector(1,0)
				}
			);
			
			float magnitude = speed.mag()/1.5f;
			float angle = lineAngle(
				new PVector[] {
					old_position,
					this.position
				},
				collision
					.get("obj2_line")
					.toArray(new PVector[2])
			);

			if (angle != -1) {
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
				
				this.spin -= (
					(
						transfer.mag()/(
							TWO_PI * spriteInSpace.getRadius() * spinradius
						)
					)
				);
				
				this.spin *= 0.5f;
				
				this.speed = PVector.fromAngle(
					lineAngle(
						collision
							.get("obj1_line")
							.toArray(new PVector[2]),
						collision
							.get("obj2_line")
							.toArray(new PVector[2])
					) - HALF_PI
				);
				
				/*this.speed = PVector.fromAngle(
					PI - (angle + baseAngle)
				);*/
				
				this.speed.mult(magnitude);
				
				colliding = true;
			}
			else {
				colliding = false;
			}
		}
		else {
			colliding = false;	
		}
		
		this.speed.x -= sin(
			HALF_PI + atan2(this.position.y, this.position.x) -
				atan2(map.position.y, map.position.x)
		) * (gravity/frameRate);
	
		this.speed.y += cos(
			HALF_PI + atan2(this.position.y, this.position.x) -
				atan2(map.position.y, map.position.x)
		) * (gravity/frameRate);
		
		this.position.add(this.speed);
		
		this.sprite = this.sprite.rotate(
			spin
		);
		this.spinoffset += spin % TWO_PI;
	}
}
class Player extends Droppable {
	TreeMap<String, Character> keyBinds;
	int index;
	int colour;
		
	Player() {
		super(
			new PVector(width / 2, height / 2),
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
		stroke(color(255));
		
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
	
	public int lineCount() {
		return this.size();
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
			this.get(i),
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
				Collections.rotate(ori1, ori1.indexOf(coords));
				
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
	
	public int lineCount() {
		int size = 0;

		for (String key: this.keySet()) {
			size += this.get(key).lineCount();
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
			i -= this.get(keys[e]).lineCount();
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

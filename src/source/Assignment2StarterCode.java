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

Drawable map;

ArrayList<Player> players;
ArrayList<Button> buttons;
boolean[] keys = new boolean[526];

public void setup() {
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

public void draw() {
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

public void createMap() {
	Poligon Poligon = new Poligon();
	PVector point = new PVector(0,0);
	float theta = 0;
	float thetaInc = TWO_PI / 500;
	float noiseScale = 0.1f;

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

	public PVector[] getLine(int i);
	public PVector center();
	public float getRadius();
	public int lineCount();

	public Vectorial transpose(PVector val);
	public Vectorial rotate(float degrees);
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
	
	for (int i = 0; i < spriteInSpace1.size(); i++) {
		for (int e = 0; e < spriteInSpace2.size(); e++) {
			PVector[] line1 = spriteInSpace1.getLine(i);
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
	
	public void display() {
		Vectorial spriteInSpace = sprite.transpose(position);

		spriteInSpace.draw();
	}
}
class Droppable extends Drawable {
	PVector speed;
	boolean colliding;
	float spin;
	float momentum;
	
	Droppable(PVector position, Poligon Poligon) {
		super(position, Poligon);
		this.speed = new PVector(0,0);
		this.colliding = false;
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

			if (angle != -1 && !colliding) {
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
				
				this.spin += (
					transfer.mag()/(
						TWO_PI * spriteInSpace.getRadius()
					)
				);
				this.spin *= 0.5f;
				this.speed.sub(transfer);
			
				this.speed = PVector.fromAngle(
					PI - angle + baseAngle
				);
				speed.mult(magnitude);
				
				colliding = true;
			}
			else {
				colliding = false;
			}
		}
		else {
			colliding = false;
			
			this.speed.x -= sin(
				HALF_PI + atan2(this.position.y, this.position.x) -
					atan2(map.position.y, map.position.x)
			) * (gravity/frameRate);
		
			this.speed.y += cos(
				HALF_PI + atan2(this.position.y, this.position.x) -
					atan2(map.position.y, map.position.x)
			) * (gravity/frameRate);
		}
		
		this.position.add(this.speed);
		
		this.sprite = this.sprite.rotate(
			spin *= 0.95f
		);

	}
}
class Player extends Droppable {
	TreeMap<String, Character> keyBinds;
	int index;
	int colour;
		
	Player() {
		super(
			new PVector(width / 2, height / 2),
			triangle.scale(1.5f)
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
	
	
	public void draw() {
		stroke(colour);
		
		super.display();
	}
	
	public void update() {		
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
class Poligon extends ArrayList<PVector> implements Vectorial {
	
	Poligon() {
		super();
	}
	
	Poligon(Poligon Poligon) {
		super(Poligon);
	}
	
	Poligon(PVector[] Poligon) {
		super(Arrays.asList(Poligon));
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
		Poligon Poligon = new Poligon();
		
		for (PVector point: this) {
			Poligon.add(point.get());
		}
		
		return Poligon;
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
		Poligon Poligon = (Poligon)this.clone();
	
		for (int i = 0; i < Poligon.size(); i++) {
			for (int e = 0; e < merge.size(); e++) {
				if (Poligon.get(i) == merge.get(e)) {
					if (e + 1 < merge.size()) {
						Poligon.add(i + 1, Poligon.get(e));
					}
				}
			}
		}
		
		return Poligon;	
	}
	
	public Poligon rotate(float degrees) {
		Poligon Poligon = (Poligon)this.clone();
		
		for (int i = 0; i < Poligon.size(); i++) {
			Poligon.get(i).rotate(degrees);
		}
		
		return Poligon;
	}
	
	public Poligon transpose(PVector val) {
		Poligon Poligon = (Poligon)this.clone();
		
		for (int i = 0; i < Poligon.size(); i++) {
			Poligon.get(i).add(val);
		}
		
		return Poligon;
	}
	
	public Poligon scale(PVector val) {
		Poligon Poligon = (Poligon)this.clone();
	
		for (int i = 0; i < Poligon.size(); i++) {
			Poligon.get(i).x *= val.x;
			Poligon.get(i).y *= val.y;
		}
		
		return Poligon;
	}
	
	public Poligon scale(float val) {
		Poligon Poligon = (Poligon)this.clone();
	
		for (int i = 0; i < Poligon.size(); i++) {
			Poligon.get(i).mult(val);
		}
		
		return Poligon;
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
		String[] keys = this
			.keySet()
			.toArray(
				new String[this.size()]
			);
		
		for (int i = 0; i < this.size(); i++) {
			shape.put(
				new String((String) keys[i]),
				this.get(keys[i]).clone()
			);
		}
		
		return shape;
	}
	
	public PVector center() {
		float sumx = 0;
		float sumy = 0;
		String[] keys = this
			.keySet()
			.toArray(
				new String[this.size()]
			);
	
		for (int i = 0; i < keys.length; i++) {
			PVector center = this.get(keys[i]).center();
		
			sumx += center.x;
			sumy += center.y;
		}
		
		return new PVector(
			sumx/this.size(),
			sumy/this.size()	
		);
	}
	
	public int lineCount() {
		String[] keys = this
			.keySet()
			.toArray(
				new String[this.size()]
			);

		int size = 0;
	
		for (int i = 0; i < keys.length; i++) {
			size += this.get(keys[i]).size();
		}
		
		return size;
	}
	
	public float getRadius() {
		PVector center = this.center();
		float total = 0;
		
		String[] keys = this
			.keySet()
			.toArray(
				new String[this.size()]
			);
	
		for (int i = 0; i < keys.length; i++) {	
			total += PVector.dist(
				this.get(keys[i]).center(),
				center
			);
		}
		
		return total/this.size();
	}
	
	public Shape transpose(PVector val) {
		Shape shape = this.clone();
		String[] keys = this
			.keySet()
			.toArray(
				new String[this.size()]
			);
	
		for (int i = 0; i < keys.length; i++) {
			shape.get(keys[i]).transpose(val);
		}
		
		return shape;
	}
	
	public Shape rotate(float degrees) {
		Shape shape = this.clone();
		String[] keys = this
			.keySet()
			.toArray(
				new String[this.size()]
			);
	
		for (int i = 0; i < keys.length; i++) {
			shape.get(keys[i]).rotate(degrees);
		}
		
		return shape;
	}
	
	public void draw() {
		String[] keys = this
			.keySet()
			.toArray(
				new String[this.size()]
			);
	
		for (int i = 0; i < keys.length; i++) {
			this.get(keys[i]).draw();
		}
	}
	
	public PVector[] getLine(int i) {
		String[] keys = this
			.keySet()
			.toArray(
				new String[this.size()]
			);
		int e = 0;
		PVector[] line = new PVector[2];
		
		while (i > this.get(keys[e]).lineCount()) {
			if (i <= this.get(keys[e]).lineCount()) {
				Poligon poligon = this.get(keys[e]);
			
				line = new PVector[] {
					poligon.get(i),
					poligon.get((i + 1) % this.lineCount())
				};
			}
			else {
			
			}
		
			e++;
		}
		
		return line;
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

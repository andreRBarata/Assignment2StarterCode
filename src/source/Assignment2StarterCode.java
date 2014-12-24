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


Drawable map;

ArrayList<Player> players;
ArrayList<Button> buttons;
boolean[] keys = new boolean[526];

public void setup() {
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
				new PVector(2.5f, 1)
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

public void draw() {
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
	
	map.draw();
}

public void drawMap() {
	Shape shape = new Shape();
	PVector point = new PVector(0,0);
	float theta = 0;
	float thetaInc = TWO_PI / 500;
	float noiseScale = 0.2f;

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
	
	/*shape.add(new PVector(-width/2, height/2));
	shape.add(new PVector(width/2, -height/2));*/
	
	map = new Drawable(
		new PVector(width/2,height + 800),
		//new PVector(width/2, height/2),
		shape.scale(80)
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
		int x = (i + 1) * gap;
		p.position.x = x;
		p.position.y = 50;
		players.add(p);         
	}
}

public void mousePressed() {
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

public void mouseReleased() {
	for (int i = 0; i < buttons.size(); i++) {
		Button button = buttons.get(i);
		
		button.clicked = false;
	}
}
interface CallBack {
	public void run();
}

public boolean isNull(Object o1, Object o2) {
	return (o1 == null || o2 == null);
}

class Shape extends ArrayList<PVector> {
	
	Shape() {
		super();
	}
	
	Shape(Shape shape) {
		super(shape);
	}
	
	Shape(PVector[] shape) {
		super(Arrays.asList(shape));
	}
	
	public Shape clone() {
		Shape shape = new Shape();
		
		for (PVector point: this) {
			shape.add(point.get());
		}
		
		return shape;
	}
	
	public Shape rotate(float degrees) {
		Shape shape = (Shape)this.clone();
		
		for (int i = 0; i < shape.size(); i++) {
			shape.get(i).rotate(degrees);
		}
		
		return shape;
	}
	
	public Shape transpose(PVector val) {
		Shape shape = (Shape)this.clone();
		
		for (int i = 0; i < shape.size(); i++) {
			shape.get(i).add(val);
		}
		
		return shape;
	}
	
	public Shape scale(PVector val) {
		Shape shape = (Shape)this.clone();
	
		for (int i = 0; i < shape.size(); i++) {
			shape.get(i).x *= val.x;
			shape.get(i).y *= val.y;
		}
		
		return shape;
	}
	
	public Shape scale(float val) {
		Shape shape = (Shape)this.clone();
	
		for (int i = 0; i < shape.size(); i++) {
			shape.get(i).mult(val);
		}
		
		return shape;
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

class Drawable {
	PVector position;
	Shape shape;
	
	Drawable(PVector position, Shape shape) {
		this.position = position;
		this.shape = shape;
	}
	
	public void draw() {
		int i = 0;

		Shape shapeInSpace = shape.transpose(position);

		while (i < shape.size()) {
			PVector current1;
			PVector current2;
			
			current1 = shapeInSpace.get(
				i % shape.size()
			);	
			
			i++;

			current2 = shapeInSpace.get(
				i % shape.size()
			);
			
			line(
				current1.x,
				current1.y,
				current2.x,
				current2.y
			);
		}
	}
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

public Poligon collider(Drawable p1, Drawable p2) {
	Poligon toReturn = new Poligon();
	Shape shapeInSpace1 = (Shape)(p1.shape)
		.transpose(p1.position);
	Shape shapeInSpace2 = (Shape)(p2.shape)
		.transpose(p2.position);
	Shape intersection = new Shape();
	
	for (int i = 0; i < shapeInSpace1.size(); i++) {
		for (int e = 0; e < shapeInSpace2.size(); e++) {
			PVector[] line1 = {
				shapeInSpace1.get(i),
				shapeInSpace1.get(
					(i + 1) % shapeInSpace1.size()
				)
			};
			PVector[] line2 = {
				shapeInSpace2.get(e),
				shapeInSpace2.get(
					(e + 1) % shapeInSpace2.size()
				)
			};
		
			PVector intersectionPoint = intersectionInline(
				line1,
				line2
			);
			
			if (intersectionPoint != null) {
				line(0, intersectionPoint.y, width, intersectionPoint.y);
				line(intersectionPoint.x, 0, intersectionPoint.x, height);
			
				intersection.add(intersectionPoint);
				toReturn.put("obj1_line", new Shape(line1));
				toReturn.put("obj2_line", new Shape(line2));
			}
		}
	}
	
	toReturn.put("intersection", intersection);
	
	
	return toReturn;
}
class Button extends Drawable {
	CallBack callback;
	boolean clicked;
	String text;
	
	Button(PVector position, String text, Shape shape, CallBack callback) {
		super(position, shape);
		
		this.clicked = false;
		this.callback = callback;
		this.text = text;
	}
	
	public void draw() {
		fill(color(255));

		super.draw();
		
		if (!clicked) {
			fill(color(0));
		}
		else {
			fill(color(20,20,200));
		}
		
		text(
			text,
			position.x + this.shape.maxWidth()/2 - textWidth(text)/2,
			position.y + (this.shape.maxHeight() + 10)/2
		);
	}
}
class Droppable extends Drawable {
	PVector speed;
	
	Droppable(PVector position, Shape shape) {
		super(position, shape);
		speed = new PVector(0,0);
	}
	
	public void draw() {
		Poligon collision = collider(this, map);
		
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
		
			println("speedBefore:", this.speed);
		
			float baseAngle = lineAngle(
				collision
					.get("obj2_line")
					.toArray(new PVector[2]),
				new PVector[] {
					new PVector(0,0),
					new PVector(1,0)
				}
			);
			
			println("baseAngle:", baseAngle);
			
			float magnitude = speed.mag();
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
		
				this.speed = PVector.fromAngle(
					PI - angle + baseAngle
				);
				speed.mult(magnitude);
			}		

		}
		
		this.speed.y += cos(
			atan2(map.position.x, map.position.y) -
				atan2(this.position.x, this.position.y)
		) * 9.1f/frameRate;
		
		this.position.add(this.speed);

		super.draw();
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
		
		super.draw();
	}
	
	public void update() {
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
class Poligon extends TreeMap<String,Shape> {

}
Shape rectangle;
Shape triangle;
Shape circle;

public void shapes() {
	rectangle = new Shape(
		new PVector[] {
			new PVector(-10,-10),
			new PVector(10,-10),
			new PVector(10,10),
			new PVector(-10,10)
		}
	);
	
	triangle = new Shape(
		new PVector[] {
			new PVector(0,-10),
			new PVector(-10,10),
			new PVector(10,10)
		}
	);
	
	float theta = 0;
	float thetaInc = TWO_PI / 10;
	circle = new Shape();
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
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "Assignment2StarterCode" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}

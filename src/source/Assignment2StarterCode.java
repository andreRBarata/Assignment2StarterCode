import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import java.util.TreeMap; 
import java.util.Arrays; 

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



Shape map;

ArrayList<Player> players;
ArrayList<Button> buttons;
boolean[] keys = new boolean[526];

public void setup() {
	players = new ArrayList<Player>();
	buttons = new ArrayList<Button>();
	
	background(255);
	size(500, 500);
	setUpPlayerControllers();
	
	Button button = new Button(
		new PVector(width/2, height/2),
		"button",
		new Shape(rectangle)
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
		p.position.y = 300;
		players.add(p);         
	}
}

public boolean collider(Drawable p1, Drawable p2) {
	Shape shapeInSpace1 = (Shape)(p1.shape).clone();
	Shape shapeInSpace2 = (Shape)(p2.shape).clone();
	
	shapeInSpace1.transpose(p1.position);
	shapeInSpace2.transpose(p2.position);
	
	for (int i = 0; i < shapeInSpace1.size(); i++) {
		
	}
	
	return false;
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

class Shape extends ArrayList<PVector[]> {
	ArrayList<PVector> points;
	
	Shape(PVector[][] shape) {
		points = new ArrayList<PVector>();
		
		for (int i = 0; i < shape.length; i++) {
			PVector[] line = new PVector[2];
			
			for (int e = 0; e < 2; e++) {
				if (!points.contains(shape[i][e])) {
					points.add(shape[i][e]);
					line[e] = shape[i][e];
				}
				else {
					line[e] = points.get(points.indexOf(shape[i][e]));
				}
			}
			this.add(line);
		}
	}
	
	public Shape transpose(PVector val) {
		for (int i = 0; i < this.points.size(); i++) {
			points.get(i).add(val);
		}
		
		return this;
	}
	
	//To be altered later
	public Shape scale(PVector val) {
		for (int i = 0; i < this.points.size(); i++) {
			points.get(i).x *= val.x;
			points.get(i).y *= val.y;
		}
		
		return this;
	}
	
	public Shape scale(float val) {
		for (int i = 0; i < this.points.size(); i++) {
			points.get(i).mult(val);
		}
		
		return this;
	}
	
	public float maxWidth() {
		float minWidth = 0;
		float maxWidth = 0;
		for (int i = 0; i < this.points.size(); i++) {
			if (i == 0 || points.get(i).x > maxWidth) {
				maxWidth = points.get(i).x;
			}
			
			if (i == 0 || points.get(i).x < minWidth) {
				minWidth = points.get(i).x;
			}
		}
		
		return maxWidth - minWidth;
	}
	
	public float maxHeight() {
		float minHeight = 0;
		float maxHeight = 0;
		for (int i = 0; i < this.points.size(); i++) {
			if (i == 0 || points.get(i).y > maxHeight) {
				maxHeight = points.get(i).y;
			}
			
			if (i == 0 || points.get(i).y < minHeight) {
				minHeight = points.get(i).y;
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
class Player extends Drawable {
	TreeMap<String, Character> keyBinds;
	int index;
	int colour;
		
	Player() {
		super(
			new PVector(width / 2, height / 2),
			new Shape(triangle)
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
		  println("Player " + index + " button 2");
		}    
	}
}
PVector[][] rectangle = {
	{new PVector(-10,-10), new PVector(10,-10)},
	{new PVector(-10,10), new PVector(10,10)},
	{new PVector(10,-10), new PVector(10,10)},
	{new PVector(-10,-10), new PVector(-10,10)}
};

PVector[][] triangle = {
	{new PVector(0,-10), new PVector(-10,10)},
	{new PVector(0,-10), new PVector(10,10)},
	{new PVector(-10,10), new PVector(10,10)}
};
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "Assignment2StarterCode" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}

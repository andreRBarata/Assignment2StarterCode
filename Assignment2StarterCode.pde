/*
		DIT OOP Assignment 2 Starter Code
		=================================
		
		Loads player properties from an xml file
		See: https://github.com/skooter500/DT228-OOP 
*/
import java.util.TreeMap;

PVector[][] rectangle = {
	{new PVector(0,0), new PVector(50,0)},
	{new PVector(0,20), new PVector(50,20)},
	{new PVector(50,0), new PVector(50,20)},
	{new PVector(0,0), new PVector(0,20)}
};

ArrayList<Player> players;
ArrayList<Button> buttons;
boolean[] keys = new boolean[526];

void setup() {
	players = new ArrayList<Player>();
	buttons = new ArrayList<Button>();
	
	background(255);
	size(500, 500);
	setUpPlayerControllers();
	
	Button button = new Button(
		new PVector(width/2, height/2),
		"button",
		rectangle,
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
		player.update();
		player.draw();
	}
	
	for (int i = 0; i < buttons.size(); i++) {
		Button button = buttons.get(i);
		button.draw();
	}
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

void mouseClicked() {
	for (int i = 0; i < buttons.size(); i++) {
		Button button = buttons.get(i);
		
		if (mouseX > button.position.x && mouseX < button.position.x + button.shape.maxWidth()) {
			if (mouseY > button.position.y && mouseY < button.position.y + button.shape.maxHeight()) {
				fill(color(50));
				button.callback.run();
				fill(color(0));
			}
		}
	}
}

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

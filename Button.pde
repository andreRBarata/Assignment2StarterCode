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
	
	void draw() {
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

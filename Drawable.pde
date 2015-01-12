class Drawable {
	PVector position;
	Vectorial sprite;
	
	Drawable(PVector position, Vectorial sprite) {
		this.position = position;
		this.sprite = sprite;
	}
	
	Drawable clone() {
		return new Drawable(
			position.get(),
			sprite.clone()
		);
	}
	
	void display() {
		Vectorial spriteInSpace = sprite.transpose(position);
		
		spriteInSpace.draw();
	}
}

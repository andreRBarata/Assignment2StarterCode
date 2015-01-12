class Drawable {
	PVector position;
	Vectorial sprite;
	
	Drawable(PVector position, Vectorial sprite) {
		this.position = position;
		this.sprite = sprite;
	}
	
	void display() {
		Vectorial spriteInSpace = sprite.transpose(position);
		
		spriteInSpace.draw();
	}
}

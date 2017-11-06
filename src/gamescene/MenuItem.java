package gamescene;

public class MenuItem {
	private String string;
	private int coordinateX;
	private int coordinateY;
	private int cursorPosition;

	public MenuItem(String string, int coordinateX, int coordinateY, int cursorPosition){
		this.string = string;
		this.coordinateX = coordinateX;
		this.coordinateY = coordinateY;
		this.cursorPosition = cursorPosition;
	}

	public String getString(){
		return this.string;
	}

	public int getCoordinateX(){
		return this.coordinateX;
	}

	public int getCoordinateY(){
		return this.coordinateY;
	}

	public int getCursorPosition(){
		return this.cursorPosition;
	}
}

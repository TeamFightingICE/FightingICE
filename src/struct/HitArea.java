package struct;

public class HitArea {

	private int left;

	private int right;

	private int top;

	private int bottom;

	public void move(int speedX,int speedY){
		left += speedX;
		right += speedX;
		top += speedY;
		bottom += speedY;
	}

	public int getleft() {
		return left;
	}

	public int getright() {
		return right;
	}

	public int gettop() {
		return top;
	}

	public int getbottom() {
		return bottom;
	}

}

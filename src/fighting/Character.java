package fighting;

import java.util.ArrayList;

import enumerate.Action;
import enumerate.State;

public class Character {
	private int hp;

	private int energy;

	private int x;

	private int y;

	private int left;

	private int right;

	private int top;

	private int bottom;

	private int speedX;

	private int speedY;

	private State state;

	private Action action;

	private boolean front;

	private boolean control;

	private Attack attack;

	private int remainingFrame;

	private int graphicSizeX;

	private int graphicSizeY;

	private int graphicCenterX;

	private int graphicCenterY;

	private ArrayList<Action> currentCombo = new ArrayList<Action>();

	private int lastCombo;

	public boolean isFront() {
		return this.front;
	}

	public boolean isControl() {
		return this.control;
	}

	// getter
	public int getHp() {
		return this.hp;
	}

	public int getEnergy() {
		return this.energy;
	}

	public int getX() {
		return this.x;
	}

	public int getY() {
		return this.y;
	}

	public int getLeft() {
		return this.left;
	}

	public int getRight() {
		return this.right;
	}

	public int getTop() {
		return this.top;
	}

	public int getBottom() {
		return this.bottom;
	}

	public int getSpeedX() {
		return this.speedX;
	}

	public int getSpeedY() {
		return this.speedY;
	}

	public State getState() {
		return this.state;
	}

	public Action getAction() {
		return this.action;
	}

	public int getRemainingFrame() {
		return this.remainingFrame;
	}

	/*
	 * public Attack getAttack() { // create the deep copy of the attack to
	 * ensure the attack object is // immutable return new Attack(attack); }
	 */

	public int getGraphicCenterX() {
		return this.graphicCenterX;
	}

	public int getGraphicCenterY() {
		return this.graphicCenterY;
	}

	public int getGraphicSizeX() {
		return this.graphicSizeX;
	}

	public int getGraphicSizeY() {
		return this.graphicSizeY;
	}

	public ArrayList<Action> getCurrentCombo() {
		return this.currentCombo;
	}

	public int getLastCombo() {
		return this.lastCombo;
	}

	public int getComboState() {
		return this.currentCombo.size();
	}

	// setter
	public void setHp(int hp) {
		this.hp = hp;
	}

	public void setEnergy(int energy) {
		this.energy = energy;
	}

	public void setX(int x) {
		this.x = x;
	}

	public void setY(int y) {
		this.y = y;
	}

	public void setSpeedX(int speedX) {
		this.speedX = speedX;
	}

	public void setSpeedY(int speedY) {
		this.speedY = speedY;
	}

	public void setState(State state) {
		this.state = state;
	}

	public void setAction(Action action) {
		this.action = action;
	}

	public void setFront(boolean front) {
		this.front = front;
	}

	public void setControl(boolean control) {
		this.control = control;
	}

	public void setRemainingFrame(int remainingFrame) {
		this.remainingFrame = remainingFrame;
	}

	public void setTop(int top) {
		this.top = top;
	}

	public void setBottom(int bottom) {
		this.bottom = bottom;
	}

	public void setLeft(int left) {
		this.left = left;
	}

	public void setRight(int right) {
		this.right = right;
	}

	public void setAttack(Attack attack) {
		// this.attack = (attack != null) ? new Attack(attack) : null;
	}

	public void setGraphicCenterX(int graphicCenterX) {
		this.graphicCenterX = graphicCenterX;
	}

	public void setGraphicCenterY(int graphicCenterY) {
		this.graphicCenterY = graphicCenterY;
	}

	public void setGraphicSizeX(int graphicSizeX) {
		this.graphicSizeX = graphicSizeX;
	}

	public void setGraphicSizeY(int graphicSizeY) {
		this.graphicSizeY = graphicSizeY;
	}

}

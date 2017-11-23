package util;

import java.awt.image.BufferedImage;
import java.util.Deque;
import java.util.LinkedList;

import fighting.Character;
import fighting.HitEffect;
import fighting.LoopEffect;

public class ResourceDrawer {

	private static ResourceDrawer resourceDrawer = new ResourceDrawer();

	private ResourceDrawer() {
		System.out.println("Create instance: " + ResourceDrawer.class.getName());
	}

	public static ResourceDrawer getInstance() {
		return resourceDrawer;
	}

	public void drawResource(Character[] characters, Deque<LoopEffect> projectiles,
			LinkedList<LinkedList<HitEffect>> hitEffects, BufferedImage screen, int remainingFrame, int round) {

	}

}

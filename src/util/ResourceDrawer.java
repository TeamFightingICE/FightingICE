package util;

import java.util.Deque;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

import fighting.Attack;
import fighting.Character;
import fighting.HitEffect;
import fighting.LoopEffect;
import image.Image;
import manager.GraphicManager;
import setting.FlagSetting;
import setting.GameSetting;
import setting.LaunchSetting;
import struct.HitArea;

/**
 * キャラクターや攻撃などの画像表示を扱うクラス．
 */
public class ResourceDrawer {

	/**
	 * クラスコンストラクタ．
	 */
	private ResourceDrawer() {
		Logger.getAnonymousLogger().log(Level.INFO, "Create instance: " + ResourceDrawer.class.getName());
	}

	/**
	 * ResourceDrawerクラスの唯一のインスタンスを取得する．
	 *
	 * @return ResourceDrawerクラスの唯一のインスタンス
	 */
	public static ResourceDrawer getInstance() {
		return ResourceDrawerHolder.instance;
	}

	/**
	 * getInstance()が呼ばれたときに初めてインスタンスを生成するホルダークラス．
	 */
	private static class ResourceDrawerHolder {
		private static final ResourceDrawer instance = new ResourceDrawer();
	}

	/**
	 * 引数で渡された情報を用いて画面を描画する．<br>
	 *
	 * @param characters
	 *            P1とP2のキャラクターデータを格納した配列
	 * @param projectiles
	 *            波動拳のループエフェクトを格納した両端キュー
	 * @param hitEffects
	 *            ヒットエフェクトのリストを格納したリスト
	 * @param remainingTime
	 *            ラウンドの残り時間
	 * @param round
	 *            ラウンド
	 */
	public void drawResource(Character[] characters, Deque<LoopEffect> projectiles,
			LinkedList<LinkedList<HitEffect>> hitEffects, int remainingTime, int round) {
		this.drawResource(characters, projectiles, hitEffects, remainingTime, round, true);
	}
	
	public void drawResource(Character[] characters, Deque<LoopEffect> projectiles,
			LinkedList<LinkedList<HitEffect>> hitEffects, int remainingTime, int round, boolean shouldRender) {
		drawBackGroundImage(shouldRender);
		drawCharacterImage(characters, shouldRender);
		drawAttackImage(projectiles, characters, shouldRender);
		drawHPGaugeImage(characters, shouldRender);
		drawEnergyGaugeImage(characters, shouldRender);
		drawTimeImage(remainingTime, shouldRender);
		drawPlayerDetail(characters, shouldRender);
		drawRoundNumber(round, shouldRender);
		drawHitCounter(characters, shouldRender);
		drawHitArea(characters, projectiles, shouldRender);
		drawHitEffects(hitEffects, shouldRender);
	}

	/**
	 * Draws the background image.
	 */
	public void drawBackGroundImage(boolean shouldRender) {
		Image bg = GraphicManager.getInstance().getBackgroundImage().get(0);
		GraphicManager.getInstance().drawImage(bg, 0, 0, Image.DIRECTION_RIGHT, shouldRender);
	}

	/**
	 * Draws both characters' images.
	 *
	 * @param playerCharacters
	 *            P1とP2のキャラクターデータを格納した配列
	 */
	public void drawCharacterImage(Character[] playerCharacters, boolean shouldRender) {
		String[] names = { "P1", "P2" };

		// draw players name
		for (int i = 0; i < 2; ++i) {
			// Draw a character to match the direction
			// BufferedImage image = playerCharacters[i].getNowImage().getBufferedImage();
			// キャラクターの向いている方向に応じて,画像を反転させる
			//image = flipImage(image, playerCharacters[i].isFront());

			int positionX = playerCharacters[i].getHitAreaLeft()
					+ (playerCharacters[i].getHitAreaRight() - playerCharacters[i].getHitAreaLeft()) / 3;
			int positionY = playerCharacters[i].getHitAreaTop() - 50;

			GraphicManager.getInstance().drawString(names[i], positionX, positionY, shouldRender);
			GraphicManager.getInstance().drawImage(playerCharacters[i].getNowImage(), playerCharacters[i].getX(),
					playerCharacters[i].getY(), playerCharacters[i].getGraphicSizeX(),
					playerCharacters[i].getGraphicSizeY(), playerCharacters[i].isFront(),
					-playerCharacters[i].getGraphicSizeX()/2, 0, shouldRender);
		}
	}

	/**
	 * Draws attack's images.
	 *
	 * @param projectiles
	 *            波動拳のループエフェクトを格納した両端キュー
	 * @param characters
	 *            P1とP2のキャラクターデータを格納した配列
	 */
	private void drawAttackImage(Deque<LoopEffect> projectiles, Character[] characters, boolean shouldRender) {
		// Is displayed according to the orientation image attack.
		for (LoopEffect projectile : projectiles) {
			Attack attack = projectile.getAttack();

			if (attack.getCurrentFrame() > attack.getStartUp()) {
				Image image = projectile.getImage();
				HitArea area = attack.getCurrentHitArea();

				int positionX;
				if (attack.getSpeedX() >= 0) {
					positionX = area.getRight() - (image.getWidth() * 5 / 6);
				} else {
					positionX = area.getLeft() - (image.getWidth() * 1 / 6);
				}
				int positionY = area.getTop() - ((image.getHeight() - (area.getBottom() - area.getTop())) / 2);

				GraphicManager.getInstance().drawImage(image, positionX, positionY, attack.getSpeedX() >= 0, shouldRender);
			}
		}
	}

	/**
	 * Draws both characters' HP information.
	 *
	 * @param playerCharacters
	 *            P1とP2のキャラクターデータを格納した配列
	 */
	private void drawHPGaugeImage(Character[] playerCharacters, boolean shouldRender) {
		if (FlagSetting.limitHpFlag) {
			int p1Hp = (int) ((double) playerCharacters[0].getHp() / LaunchSetting.maxHp[0] * 300 * -1);
			int p2Hp = (int) ((double) playerCharacters[1].getHp() / LaunchSetting.maxHp[1] * 300);

			GraphicManager.getInstance().drawQuad(480 - 50, 75, -300, 20, 0.2f, 0.2f, 0.2f, 0.0f, shouldRender);
			GraphicManager.getInstance().drawQuad(480 + 50, 75, 300, 20, 0.2f, 0.2f, 0.2f, 0.0f, shouldRender);
			GraphicManager.getInstance().drawQuad(480 - 50, 75, Math.max(-300, Math.min(0, p1Hp)), 20, 0, 1.0f, 0, 0.0f, shouldRender);
			GraphicManager.getInstance().drawQuad(480 + 50, 75, Math.min(300, Math.max(0, p2Hp)), 20, 1.0f, 0.65f, 0, 0.0f, shouldRender);
		}
	}

	/**
	 * Draws both characters' energy information.
	 *
	 * @param playerCharacters
	 *            P1とP2のキャラクターデータを格納した配列
	 */
	private void drawEnergyGaugeImage(Character[] playerCharacters, boolean shouldRender) {
		if (FlagSetting.limitHpFlag) {
			float[] red = { 1.0f, 1.0f };
			float[] green = { 0.0f, 0.0f };
			float[] blue = { 0.0f, 0.0f };

			for (int i = 0; i < 2; i++) {
				int energy = playerCharacters[i].getEnergy();

				if (energy >= 50 && energy < LaunchSetting.maxEnergy[i]) {
					red[i] = 1.0f;
					green[i] = 1.0f;
					blue[i] = 0.0f;
				} else if (energy >= LaunchSetting.maxEnergy[i]) {
					red[i] = 0.0f;
					green[i] = 0.0f;
					blue[i] = 1.0f;
				}
			}

			int p1Energy = (int) ((float) playerCharacters[0].getEnergy() / LaunchSetting.maxEnergy[0] * 300 * -1);
			int p2Energy = (int) ((float) playerCharacters[1].getEnergy() / LaunchSetting.maxEnergy[1] * 300);

			GraphicManager.getInstance().drawQuad(480 - 50, 75 + 20, p1Energy, 8, red[0], green[0], blue[0], 0.0f, shouldRender);
			GraphicManager.getInstance().drawQuad(480 + 50, 75 + 20, p2Energy, 8, red[1], green[1], blue[1], 0.0f, shouldRender);
		}
	}
	
	/**
	 * Draws time.
	 *
	 * @param remainingTime
	 *            the remaining time
	 */
	private void drawTimeImage(int remainingTime, boolean shouldRender) {
		if (FlagSetting.trainingModeFlag) {
			GraphicManager.getInstance().drawString("Training Mode", GameSetting.STAGE_WIDTH / 2 - 80, 10, shouldRender);
		} else {
			GraphicManager.getInstance().drawString(String.format("%.3f", remainingTime / 1000.0), GameSetting.STAGE_WIDTH / 2 - 35, 10, shouldRender);
		}
	}

	private void drawPlayerDetail(Character[] playerCharacters, boolean shouldRender) {
		if (FlagSetting.limitHpFlag) {
			GraphicManager.getInstance().drawString("P1 HP: " + Math.max(0, playerCharacters[0].getHp()), 130 + 30, 45, shouldRender);
			GraphicManager.getInstance().drawString("Energy: " + playerCharacters[0].getEnergy(), 260 + 30, 45, shouldRender);
			
			GraphicManager.getInstance().drawString("P2 HP: " + Math.max(0, playerCharacters[1].getHp()), 590 - 30, 45, shouldRender);
			GraphicManager.getInstance().drawString("Energy: " + playerCharacters[1].getEnergy(), 720 - 30, 45, shouldRender);
		} else {
			GraphicManager.getInstance().drawString("P1 HP: " + playerCharacters[0].getHp(), 100, 50, shouldRender);
			GraphicManager.getInstance().drawString("P1 Energy: " + playerCharacters[0].getEnergy(), 100, 90, shouldRender);
			
			GraphicManager.getInstance().drawString("P2 HP: " + playerCharacters[1].getHp(), 760, 50, shouldRender);
			GraphicManager.getInstance().drawString("P2 Energy: " + playerCharacters[1].getEnergy(), 760, 90, shouldRender);
		}
	}
	
	/**
	 * Draws round number.
	 *
	 * @param round
	 *            現在のラウンド
	 */
	private void drawRoundNumber(int round, boolean shouldRender) {
		GraphicManager.getInstance().drawString("Round: " + round, 850, 10, shouldRender);
	}

	/**
	 * Draws the combo hit counter.
	 *
	 * @param playerCharacters
	 *            P1とP2のキャラクターデータを格納した配列
	 */
	private void drawHitCounter(Character[] playerCharacters, boolean shouldRender) {
		for (int i = 0; i < 2; ++i) {
			int comboState = Math.min(playerCharacters[i].getHitCount(), 9);

			if (comboState > 0) {
				Image counterImage = GraphicManager.getInstance().getCounterTextImageContainer().get(comboState);
				GraphicManager.getInstance().drawImage(counterImage, i == 0 ? 100 : 760, 150, Image.DIRECTION_RIGHT, shouldRender);

				Image hitTextImage = GraphicManager.getInstance().getHitTextImageContainer().get(0);
				GraphicManager.getInstance().drawImage(hitTextImage, i == 0 ? 170 : 830, 150, Image.DIRECTION_RIGHT, shouldRender);
			}
		}
	}

	/**
	 * Draws character's hit area.
	 *
	 * @param playerCharacters
	 *            P1とP2のキャラクターデータを格納した配列
	 * @param projectiles
	 *            波動拳のループエフェクトを格納した両端キュー
	 */
	private void drawHitArea(Character[] playerCharacters, Deque<LoopEffect> projectiles, boolean shouldRender) {
		for (int i = 0; i < 2; ++i) {

			// キャラクターの当たり判定ボックスの描画
			// P1とP2で色を変える
			GraphicManager.getInstance().drawLineQuad(playerCharacters[i].getHitAreaLeft(),
					playerCharacters[i].getHitAreaTop(),
					playerCharacters[i].getHitAreaRight() - playerCharacters[i].getHitAreaLeft(),
					playerCharacters[i].getHitAreaBottom() - playerCharacters[i].getHitAreaTop(), 0.0f + i,
					1.0f - i * 0.35f, 0.0f, 0.0f, shouldRender);

			// 攻撃の当たり判定ボックスの描画
			if (playerCharacters[i].getAttack() != null) {
				HitArea area = playerCharacters[i].getAttack().getCurrentHitArea();

				GraphicManager.getInstance().drawLineQuad(area.getLeft(), area.getTop(),
						area.getRight() - area.getLeft(), area.getBottom() - area.getTop(), 1.0f, 0.0f, 0.0f, 0.0f, shouldRender);
			}
		}

		// 波動拳の当たり判定ボックスの描画
		for (LoopEffect loopEffect : projectiles) {
			Attack temp = loopEffect.getAttack();

			if (temp.getCurrentFrame() > temp.getStartUp()) {
				HitArea area = temp.getCurrentHitArea();

				GraphicManager.getInstance().drawLineQuad(area.getLeft(), area.getTop(),
						area.getRight() - area.getLeft(), area.getBottom() - area.getTop(), 1.0f, 0.0f, 0.0f, 0.0f, shouldRender);
			}
		}
	}

	/**
	 * ヒットエフェクトを描画する．<br>
	 * 攻撃がヒットしているかを確認し，ヒットしていればそのヒットエフェクトを描画する．
	 *
	 * @param hitEffects
	 *            ヒットエフェクトのリストを格納したリスト
	 */
	private void drawHitEffects(LinkedList<LinkedList<HitEffect>> hitEffects, boolean shouldRender) {
		for (int i = 0; i < 2; i++) {
			for (int j = 0; j < hitEffects.get(i).size(); ++j) {
				HitEffect hitEffect = hitEffects.get(i).get(j);

				if (hitEffect.isHit()) {
					HitArea area = hitEffect.getAttack().getCurrentHitArea();
					Image image = hitEffect.getImage();

					int positionX = area.getLeft() - (image.getWidth() - area.getRight() + area.getLeft()) / 2
							+ hitEffect.getVariationX();
					int positionY = area.getTop() - (image.getHeight() - area.getBottom() + area.getTop()) / 2
							+ hitEffect.getVariationY();

					if (hitEffect.getVariationX() == 0 && hitEffect.getVariationY() == 0) {
						positionX += 30;
					}
					GraphicManager.getInstance().drawImage(image, positionX, positionY, i == 0 ? Image.DIRECTION_RIGHT : Image.DIRECTION_LEFT, shouldRender);
				}
			}
		}
	}

}

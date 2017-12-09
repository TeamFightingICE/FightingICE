package util;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
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

public class ResourceDrawer {

	private ResourceDrawer() {
		Logger.getAnonymousLogger().log(Level.INFO, "Create instance: " + ResourceDrawer.class.getName());
	}

	/**
	 * ResourceDrawerクラスの唯一のインスタンスを取得するgetterメソッド．
	 *
	 * @return ResourceDrawerクラスの唯一のインスタンス
	 */
	public static ResourceDrawer getInstance() {
		return ResourceDrawerHolder.instance;
	}

	/** getInstance()が呼ばれたときに初めてインスタンスを生成するホルダークラス */
	private static class ResourceDrawerHolder {
		private static final ResourceDrawer instance = new ResourceDrawer();
	}

	/**
	 * 引数で渡された情報を用いて画面を描画するメソッド．<br>
	 *
	 * @param characters
	 *            P1とP2のキャラクターデータを格納した配列
	 * @param projectiles
	 *            波動拳のループエフェクトを格納した両端キュー
	 * @param hitEffects
	 *            ヒットエフェクトのリストを格納したリスト
	 * @param screen
	 *            背景として描画する画像
	 * @param remainingTime
	 *            ラウンドの残り時間
	 * @param round
	 *            ラウンド
	 */
	public void drawResource(Character[] characters, Deque<LoopEffect> projectiles,
			LinkedList<LinkedList<HitEffect>> hitEffects, BufferedImage screen, int remainingTime, int round) {

		Graphics2D screenGraphic = screen.createGraphics();

		drawBackGroundImage(screenGraphic);

		drawCharacterImage(characters, screenGraphic);

		drawAttackImage(projectiles, characters, screenGraphic);

		drawHPGaugeImage(characters);

		drawEnergyGaugeImage(characters);

		drawTimeImage(remainingTime);

		drawRoundNumber(round);

		drawHitCounter(characters);

		drawHitArea(characters, projectiles);

		drawHitEffects(hitEffects, screenGraphic);

		screenGraphic.dispose();
	}

	/**
	 * Draws the background image.
	 *
	 * @param screenGraphic
	 *            The screen graphics
	 */
	public void drawBackGroundImage(Graphics2D screenGraphic) {
		Image bg = GraphicManager.getInstance().getBackgroundImage().get(0);
		screenGraphic.drawImage(bg.getBufferedImage(), 0, 0, GameSetting.STAGE_WIDTH, GameSetting.STAGE_HEIGHT,
				Color.black, null);

		GraphicManager.getInstance().drawImage(bg, 0, 0, GameSetting.STAGE_WIDTH, GameSetting.STAGE_HEIGHT,
				Image.DIRECTION_RIGHT);
	}

	/**
	 * Draws both characters' images.
	 *
	 * @param playerCharacters
	 *            P1とP2のキャラクターデータを格納した配列
	 * @param screenGraphic
	 *            The screen graphics
	 */
	public void drawCharacterImage(Character[] playerCharacters, Graphics2D screenGraphic) {

		String[] names = { "P1", "P2" };

		// draw players name
		for (int i = 0; i < 2; ++i) {
			// Draw a character to match the direction
			BufferedImage image = playerCharacters[i].getNowImage().getBufferedImage();
			// キャラクターの向いている方向に応じて,画像を反転させる
			image = flipImage(image, playerCharacters[i].isFront());

			int positionX = playerCharacters[i].getHitAreaLeft()
					+ (playerCharacters[i].getHitAreaRight() - playerCharacters[i].getHitAreaLeft()) / 3;
			int positionY = playerCharacters[i].getHitAreaTop() - 50;

			GraphicManager.getInstance().drawString(names[i], positionX, positionY);

			screenGraphic.drawImage(image, playerCharacters[i].getX(), playerCharacters[i].getY(),
					playerCharacters[i].getGraphicSizeX(), playerCharacters[i].getGraphicSizeY(), null);

			GraphicManager.getInstance().drawImage(playerCharacters[i].getNowImage(), playerCharacters[i].getX(),
					playerCharacters[i].getY(), playerCharacters[i].getGraphicSizeX(),
					playerCharacters[i].getGraphicSizeY(), playerCharacters[i].isFront());
		}
	}

	/**
	 * Draws attack's images.
	 *
	 * @param projectiles
	 *            波動拳のループエフェクトを格納した両端キュー
	 * @param characters
	 *            P1とP2のキャラクターデータを格納した配列
	 * @param screenGraphic
	 *            The screen graphics
	 */
	private void drawAttackImage(Deque<LoopEffect> projectiles, Character[] characters, Graphics2D screenGraphic) {

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

				BufferedImage tmpImage = image.getBufferedImage();
				tmpImage = flipImage(tmpImage, attack.getSpeedX() >= 0);

				screenGraphic.drawImage(tmpImage, positionX, positionY, image.getWidth(), image.getHeight(), null);

				GraphicManager.getInstance().drawImage(image, positionX, positionY, image.getWidth(), image.getHeight(),
						attack.getSpeedX() >= 0);
			}
		}
	}

	/**
	 * Draws both characters' HP information.
	 *
	 * @param playerCharacters
	 *            P1とP2のキャラクターデータを格納した配列
	 */
	private void drawHPGaugeImage(Character[] playerCharacters) {
		if (FlagSetting.limitHpFlag) {
			int p1Hp = (int) ((double) playerCharacters[0].getHp() / LaunchSetting.maxHp[0] * 300 * -1);
			int p2Hp = (int) ((double) playerCharacters[1].getHp() / LaunchSetting.maxHp[1] * 300);

			GraphicManager.getInstance().drawQuad(480 - 50, 75, -300, 20, 0.2f, 0.2f, 0.2f, 0.0f);
			GraphicManager.getInstance().drawQuad(480 + 50, 75, 300, 20, 0.2f, 0.2f, 0.2f, 0.0f);
			GraphicManager.getInstance().drawQuad(480 - 50, 75, p1Hp, 20, 0, 1.0f, 0, 0.0f);
			GraphicManager.getInstance().drawQuad(480 + 50, 75, p2Hp, 20, 1.0f, 0.65f, 0, 0.0f);

			GraphicManager.getInstance().drawString("P1 HP:" + playerCharacters[0].getHp(), 130 + 30, 50);
			GraphicManager.getInstance().drawString("P2 HP:" + playerCharacters[1].getHp(), 590 - 30, 50);

		} else {
			GraphicManager.getInstance().drawString("P1 HP:" + playerCharacters[0].getHp(), 100, 50);
			GraphicManager.getInstance().drawString("P2 HP:" + playerCharacters[1].getHp(), 760, 50);
		}
	}

	/**
	 * Draws both characters' energy information.
	 *
	 * @param playerCharacters
	 *            P1とP2のキャラクターデータを格納した配列
	 */
	private void drawEnergyGaugeImage(Character[] playerCharacters) {
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

			GraphicManager.getInstance().drawQuad(480 - 50, 75 + 20, p1Energy, 8, red[0], green[0], blue[0], 0.0f);
			GraphicManager.getInstance().drawQuad(480 + 50, 75 + 20, p2Energy, 8, red[1], green[1], blue[1], 0.0f);
			GraphicManager.getInstance().drawString("ENERGY:" + playerCharacters[0].getEnergy(), 250 + 30, 50);
			GraphicManager.getInstance().drawString("ENERGY:" + playerCharacters[1].getEnergy(), 710 - 30, 50);

		} else {
			GraphicManager.getInstance().drawString("P1 ENERGY:" + playerCharacters[0].getEnergy(), 100, 100);
			GraphicManager.getInstance().drawString("P2 ENERGY:" + playerCharacters[1].getEnergy(), 760, 100);
		}
	}

	/**
	 * Draws time.
	 *
	 * @param remainingTime
	 *            The remaining time.
	 */
	private void drawTimeImage(int remainingTime) {
		if (FlagSetting.trainingModeFlag) {
			GraphicManager.getInstance().drawString("Training Mode", GameSetting.STAGE_WIDTH / 2 - 80, 10);
		} else {
			GraphicManager.getInstance().drawString(Integer.toString(remainingTime), GameSetting.STAGE_WIDTH / 2 - 30,
					10);
		}

	}

	/**
	 * Draws round number.
	 *
	 * @param round
	 *            ラウンド
	 */
	private void drawRoundNumber(int round) {
		GraphicManager.getInstance().drawString("ROUND:" + round, 850, 10);
	}

	/**
	 * Draws the combo hit counter.
	 *
	 * @param playerCharacters
	 *            P1とP2のキャラクターデータを格納した配列
	 */
	private void drawHitCounter(Character[] playerCharacters) {
		for (int i = 0; i < 2; ++i) {
			int comboState = Math.min(playerCharacters[i].getHitCount(), 9);

			if (comboState > 0) {
				Image counterImage = GraphicManager.getInstance().getCounterTextImageContainer().get(comboState);
				GraphicManager.getInstance().drawImage(counterImage, i == 0 ? 100 : 760, 150, Image.DIRECTION_RIGHT);

				Image hitTextImage = GraphicManager.getInstance().getHitTextImageContainer().get(0);
				GraphicManager.getInstance().drawImage(hitTextImage, i == 0 ? 170 : 830, 150, Image.DIRECTION_RIGHT);

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
	private void drawHitArea(Character[] playerCharacters, Deque<LoopEffect> projectiles) {
		for (int i = 0; i < 2; ++i) {

			// キャラクターの当たり判定ボックスの描画
			// P1とP2で色を変える
			GraphicManager.getInstance().drawLineQuad(playerCharacters[i].getHitAreaLeft(),
					playerCharacters[i].getHitAreaTop(),
					playerCharacters[i].getHitAreaRight() - playerCharacters[i].getHitAreaLeft(),
					playerCharacters[i].getHitAreaBottom() - playerCharacters[i].getHitAreaTop(), 0.0f + i,
					1.0f - i * 0.35f, 0.0f, 0.0f);

			// 攻撃の当たり判定ボックスの描画
			if (playerCharacters[i].getAttack() != null) {
				HitArea area = playerCharacters[i].getAttack().getCurrentHitArea();

				GraphicManager.getInstance().drawLineQuad(area.getLeft(), area.getTop(),
						area.getRight() - area.getLeft(), area.getBottom() - area.getTop(), 1.0f, 0.0f, 0.0f, 0.0f);
			}
		}

		// 波動拳の当たり判定ボックスの描画
		for (LoopEffect loopEffect : projectiles) {
			Attack temp = loopEffect.getAttack();

			if (temp.getCurrentFrame() > temp.getStartUp()) {
				HitArea area = temp.getCurrentHitArea();

				GraphicManager.getInstance().drawLineQuad(area.getLeft(), area.getTop(),
						area.getRight() - area.getLeft(), area.getBottom() - area.getTop(), 1.0f, 0.0f, 0.0f, 0.0f);
			}
		}
	}

	/**
	 * ヒットエフェクトを描画するメソッド．<br>
	 * 攻撃がヒットしているかを確認し，ヒットしていればそのヒットエフェクトを描画する．
	 *
	 * @param hitEffects
	 *            ヒットエフェクトのリストを格納したリスト
	 * @param screenGraphic
	 *            The screen graphics
	 */
	private void drawHitEffects(LinkedList<LinkedList<HitEffect>> hitEffects, Graphics2D screenGraphic) {
		for (int i = 0; i < 2; i++) {
			for (int j = 0; j < hitEffects.get(i).size(); ++j) {
				HitEffect hitEffect = hitEffects.get(i).get(j);

				if (hitEffect.isHit()) {
					HitArea area = hitEffect.getAttack().getCurrentHitArea();
					Image image = hitEffect.getImage();

					BufferedImage tmpImage = image.getBufferedImage();
					tmpImage = flipImage(tmpImage, i != 0);

					int positionX = area.getLeft() - (image.getWidth() - area.getRight() + area.getLeft()) / 2
							+ hitEffect.getXVariation();
					int positionY = area.getTop() - (image.getHeight() - area.getBottom() + area.getTop()) / 2
							+ hitEffect.getYVariation();
					screenGraphic.drawImage(tmpImage, positionX, positionY, image.getWidth(), image.getHeight(), null);

					if (hitEffect.getXVariation() == 0 && hitEffect.getYVariation() == 0) {
						positionX += 30;
					}
					GraphicManager.getInstance().drawImage(image, positionX, positionY, image.getWidth(),
							image.getHeight(), i == 0 ? Image.DIRECTION_RIGHT : Image.DIRECTION_LEFT);
				}
			}
		}
	}

	/**
	 * 画像を左右反転させるメソッド．<br>
	 * 画像データは右向きで用意されているため，引数の右向きかどうかを表す変数がfalseなら画像を左右反転させる．
	 *
	 * @param image
	 *            画像
	 * @param isFront
	 *            右向きかどうかを表す変数
	 *
	 * @return 左右反転画像(isFrontがfalse)か，元の画像(isFrontがtrue)
	 */
	private BufferedImage flipImage(BufferedImage image, boolean isFront) {
		// Flip the image if we need to
		if (!isFront) {
			AffineTransform tx = AffineTransform.getScaleInstance(-1, 1);
			tx.translate(-image.getWidth(null), 0);

			AffineTransformOp flip = new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
			image = flip.filter(image, null);
		}

		return image;
	}

}

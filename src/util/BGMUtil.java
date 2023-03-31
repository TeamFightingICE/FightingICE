package util;

import struct.CharacterData;
import struct.FrameData;

public class BGMUtil {

	private static float calculateGainFromHp(float hp) {
		float gain;
		// gain = (float) (hp / 400.0 * 0.65 + 0.1);
		if (hp <= 400 && hp > 300) {
			gain = 0.75f;
        } else if (hp > 250) {
        	gain = 0.6f;
        } else if (hp > 200) {
        	gain = 0.55f;
        } else if (hp > 150) {
        	gain = 0.4f;
        } else if (hp > 100) {
        	gain = 0.35f;
        } else if (hp > 50) {
        	gain = 0.25f;
        } else {
        	gain = 0.1f;
        }
		return gain;
	}
	
	private static float calculateGainFromEnergy(float energy) {
		float gain;
		// gain = (float) (energy / 300.0 * 0.65 + 0.1);
		if (energy <= 300 && energy > 250) {
			gain = 0.75f;
        } else if (energy > 200) {
        	gain = 0.6f;
        } else if (energy > 150) {
        	gain = 0.55f;
        } else if (energy > 100) {
        	gain = 0.4f;
        } else if (energy > 70) {
        	gain = 0.35f;
        } else if (energy > 50) {
        	gain = 0.25f;
        } else {
        	gain = 0.1f;
        }
		return gain;
	}
	
	private static float calculateGainFromDistance(int distance) {
		float gain;
		// gain = (float) ((1 - (Math.min(distance, 750) / 750.0)) * 0.65 + 0.1);
		if (distance < 750 && distance > 600) {
			gain = 0.1f;
		} else if (distance > 500) {
			gain = 0.2f;
		} else if (distance > 400) {
			gain = 0.3f;
		} else if (distance > 300) {
			gain = 0.4f;
		} else if (distance > 60) {
			gain = 0.5f;
		} else if (distance > 30) {
			gain = 0.7f;
		} else {
			gain = 0.75f;
		}
		return gain;
	}
	
	public static float[] getAudioGains(FrameData fd) {
		CharacterData p1 = fd.getCharacter(true);
		CharacterData p2 = fd.getCharacter(false);
		
		float[] audioGains = new float[5];
		audioGains[0] = calculateGainFromHp(p1.getHp());
		audioGains[1] = calculateGainFromEnergy(p1.getEnergy());
		audioGains[2] = calculateGainFromHp(p2.getHp());
		audioGains[3] = calculateGainFromEnergy(p2.getEnergy());
		audioGains[4] = calculateGainFromDistance(fd.getDistanceX());
		
		return audioGains;
	}
	
}

package util;

import struct.CharacterData;
import struct.FrameData;

public class BGMUtil {

	private static float calculateGainFromHpPlayer1(float hp) {
		float gain;
		// gain = (float) (hp / 400.0 * 0.65 + 0.1);
		if (hp > 250) {
        	gain = 0.852f;
        } else if (hp > 200) {
        	gain = 0.652f;
        } else if (hp > 150) {
        	gain = 0.502f;
        } else if (hp > 100) {
        	gain = 0.452f;
        } else if (hp > 50) {
        	gain = 0.352f;
        } else {
        	gain = 0.102f;
        }
		return gain;
	}
	
	private static float calculateGainFromHpPlayer2(float hp) {
		float gain;
		// gain = (float) (hp / 400.0 * 0.65 + 0.1);
		if (hp > 250) {
        	gain = 0.853f;
        } else if (hp > 200) {
        	gain = 0.653f;
        } else if (hp > 150) {
        	gain = 0.503f;
        } else if (hp > 100) {
        	gain = 0.453f;
        } else if (hp > 50) {
        	gain = 0.353f;
        } else {
        	gain = 0.103f;
        }
		return gain;
	}
	
//	private static float calculateGainFromEnergy(float energy) {
//		float gain;
//		// gain = (float) (energy / 300.0 * 0.65 + 0.1);
//		if (energy <= 300 && energy > 250) {
//			gain = 0.75f;
//        } else if (energy > 200) {
//        	gain = 0.6f;
//        } else if (energy > 150) {
//        	gain = 0.55f;
//        } else if (energy > 100) {
//        	gain = 0.4f;
//        } else if (energy > 70) {
//        	gain = 0.35f;
//        } else if (energy > 50) {
//        	gain = 0.25f;
//        } else {
//        	gain = 0.1f;
//        }
//		return gain;
//	}
	
	private static float calculateGainFromDistance(int distance) {
		float gain;
		// gain = (float) ((1 - (Math.min(distance, 750) / 750.0)) * 0.65 + 0.1);
		if (distance < 750 && distance > 600) {
			gain = 0.101f;
		} else if (distance > 500) {
			gain = 0.301f;
		} else if (distance > 400) {
			gain = 0.401f;
		} else if (distance > 300) {
			gain = 0.501f;
		} else if (distance > 60) {
			gain = 0.601f;
		} else if (distance > 30) {
			gain = 0.751f;
		} else {
			gain = 0.851f;
		}
		return gain;
	}
	
	public static float[] getAudioGains(FrameData fd) {
		CharacterData p1 = fd.getCharacter(true);
		CharacterData p2 = fd.getCharacter(false);
		
		float[] audioGains = new float[3];
		audioGains[0] = calculateGainFromHpPlayer1(p1.getHp());
		audioGains[1] = calculateGainFromHpPlayer2(p2.getHp());
		audioGains[2] = calculateGainFromDistance(fd.getDistanceX());
		
		return audioGains;
	}
	
}

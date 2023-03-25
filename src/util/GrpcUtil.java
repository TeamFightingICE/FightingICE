package util;

import java.util.Arrays;

import com.google.protobuf.ByteString;

import informationcontainer.RoundResult;
import protoc.MessageProto.GrpcAttackData;
import protoc.MessageProto.GrpcAudioData;
import protoc.MessageProto.GrpcCharacterData;
import protoc.MessageProto.GrpcFftData;
import protoc.MessageProto.GrpcFrameData;
import protoc.MessageProto.GrpcGameData;
import protoc.MessageProto.GrpcHitArea;
import protoc.MessageProto.GrpcKey;
import protoc.MessageProto.GrpcRoundResult;
import protoc.MessageProto.GrpcScreenData;
import struct.AttackData;
import struct.AudioData;
import struct.CharacterData;
import struct.FFTData;
import struct.FrameData;
import struct.GameData;
import struct.HitArea;
import struct.Key;
import struct.ScreenData;

public class GrpcUtil {

	public static GrpcHitArea convertHitArea(HitArea hitArea) {
		return GrpcHitArea.newBuilder()
				.setLeft(hitArea.getLeft())
				.setRight(hitArea.getRight())
				.setTop(hitArea.getTop())
				.setBottom(hitArea.getBottom())
				.build();
	}
	
	public static GrpcAttackData convertAttackData(AttackData attackData) {
  		return GrpcAttackData.newBuilder()
  				.setSettingHitArea(convertHitArea(attackData.getSettingHitArea()))
  				.setSettingSpeedX(attackData.getSettingSpeedX())
  				.setSettingSpeedY(attackData.getSettingSpeedY())
  				.setCurrentHitArea(convertHitArea(attackData.getCurrentHitArea()))
  				.setCurrentFrame(attackData.getCurrentFrame())
  				.setPlayerNumber(attackData.getPlayerNumber() == 0)
  				.setSpeedX(attackData.getSpeedX())
  				.setSpeedY(attackData.getSpeedY())
  				.setStartUp(attackData.getStartUp())
  				.setActive(attackData.getActive())
  				.setHitDamage(attackData.getHitDamage())
  				.setGuardDamage(attackData.getGuardDamage())
  				.setStartAddEnergy(attackData.getStartAddEnergy())
  				.setHitAddEnergy(attackData.getHitAddEnergy())
  				.setGuardAddEnergy(attackData.getGuardAddEnergy())
  				.setGiveEnergy(attackData.getGiveEnergy())
  				.setImpactX(attackData.getImpactX())
  				.setImpactY(attackData.getImpactY())
  				.setGiveGuardRecov(attackData.getGiveGuardRecov())
  				.setAttackType(attackData.getAttackType())
  				.setDownProp(attackData.isDownProp())
  				.setIsProjectile(attackData.isProjectile())
  				.build();
  	}
  	
  	public static GrpcCharacterData convertCharacterData(CharacterData characterData) {
  		return GrpcCharacterData.newBuilder()
  				.setPlayerNumber(characterData.isPlayerNumber())
  				.setHp(characterData.getHp())
  				.setEnergy(characterData.getEnergy())
  				.setX(characterData.getCenterX())
  				.setY(characterData.getCenterY())
  				.setLeft(characterData.getLeft())
  				.setRight(characterData.getRight())
  				.setTop(characterData.getTop())
  				.setBottom(characterData.getBottom())
  				.setSpeedX(characterData.getSpeedX())
  				.setSpeedY(characterData.getSpeedY())
  				.setStateValue(characterData.getState().ordinal())
  				.setActionValue(characterData.getAction().ordinal())
  				.setFront(characterData.isFront())
  				.setControl(characterData.isControl())
  				.setAttackData(convertAttackData(characterData.getAttack()))
  				.setRemainingFrame(characterData.getRemainingFrame())
  				.setHitConfirm(characterData.isHitConfirm())
  				.setGraphicSizeX(characterData.getGraphicSizeX())
  				.setGraphicSizeY(characterData.getGraphicSizeY())
  				.setGraphicAdjustX(characterData.getGraphicAdjustX())
  				.setHitCount(characterData.getHitCount())
  				.setLastHitFrame(characterData.getLastHitFrame())
  				.build();
  	}
  	
  	public static GrpcFrameData convertFrameData(FrameData frameData) {
  		GrpcFrameData.Builder builder = GrpcFrameData.newBuilder();
  		builder = builder.addCharacterData(GrpcCharacterData.getDefaultInstance())
  				.addCharacterData(GrpcCharacterData.getDefaultInstance())
  				.addFront(false)
  				.addFront(false);
  		if (frameData.getCharacter(true) != null) {
  			builder = builder.setCharacterData(0, convertCharacterData(frameData.getCharacter(true)));
  		}
  		if (frameData.getCharacter(false) != null) {
  			builder = builder.setCharacterData(1, convertCharacterData(frameData.getCharacter(false)));
  		}
  		for (AttackData proj : frameData.getProjectiles()) {
  			builder.addProjectileData(convertAttackData(proj));
  		}
  		builder.setFront(0, frameData.isFront(true));
  		builder.setFront(1, frameData.isFront(false));
  		return builder.setCurrentFrameNumber(frameData.getFramesNumber())
  				.setCurrentRound(frameData.getRound())
  				.setEmptyFlag(frameData.getEmptyFlag())
  				.build();
  	}
  	
  	public static GrpcFftData convertFftData(FFTData fftData) {
  		return GrpcFftData.newBuilder()
  				.setRealDataAsBytes(ByteString.copyFrom(fftData.getRealAsBytes()))
  				.setImaginaryDataAsBytes(ByteString.copyFrom(fftData.getImagAsBytes()))
  				.build();
  	}
  	
  	public static GrpcScreenData convertScreenData(ScreenData screenData, int width, int height, boolean grayscale) {
  		if (screenData == null) {
  			return GrpcScreenData.getDefaultInstance();
  		}
  		
  		GrpcScreenData.Builder builder = GrpcScreenData.newBuilder();
  		if (screenData.getDisplayBytes() != null) {
  			builder.setDisplayBytes(ByteString.copyFrom(screenData.getDisplayByteBufferAsBytes(width, height, grayscale)));
  		}
  		return builder.build();
  	}
  	
  	public static GrpcAudioData convertAudioData(AudioData audioData) {
  		if (audioData == null) {
  			return GrpcAudioData.getDefaultInstance();
  		}
  		
  		return GrpcAudioData.newBuilder()
  				.setRawDataAsBytes(ByteString.copyFrom(audioData.getRawDataAsBytes()))
  				.addAllFftData(Arrays.stream(audioData.getFftData()).map(x -> convertFftData(x)).toList())
  				.setSpectrogramDataAsBytes(ByteString.copyFrom(audioData.getSpectrogramDataAsBytes()))
  				.build();
  	}
  	
  	public static Key fromGrpcKey(GrpcKey grpcKey) {
  		Key key = new Key();
  		key.A = grpcKey.getA();
  		key.B = grpcKey.getB();
  		key.C = grpcKey.getC();
  		key.U = grpcKey.getU();
  		key.D = grpcKey.getD();
  		key.L = grpcKey.getL();
  		key.R = grpcKey.getR();
  		return key;
  	}
  	
    public static GrpcKey convertKey(Key key) {
    	if (key == null) key = new Key();
    	return GrpcKey.newBuilder()
    			.setA(key.A)
    			.setB(key.B)
    			.setC(key.C)
    			.setD(key.D)
    			.setL(key.L)
    			.setR(key.R)
    			.setU(key.U)
    			.build();
    }
    
    public static GrpcGameData convertGameData(GameData gameData) {
    	return GrpcGameData.newBuilder()
    			.addMaxHps(gameData.getMaxHP(true))
    			.addMaxHps(gameData.getMaxHP(false))
    			.addMaxEnergies(gameData.getMaxEnergy(true))
    			.addMaxEnergies(gameData.getMaxEnergy(false))
    			.addCharacterNames(gameData.getCharacterName(true))
    			.addCharacterNames(gameData.getCharacterName(false))
    			.addAiNames(gameData.getAiName(true))
    			.addAiNames(gameData.getAiName(false))
    			.build();
    }
    
    public static GrpcRoundResult convertRoundResult(RoundResult roundResult) {
    	return GrpcRoundResult.newBuilder()
    			.setCurrentRound(roundResult.getRound())
    			.addRemainingHps(roundResult.getRemainingHPs()[0])
    			.addRemainingHps(roundResult.getRemainingHPs()[1])
    			.setElapsedFrame(roundResult.getElapsedFrame())
    			.build();
    }
	
}

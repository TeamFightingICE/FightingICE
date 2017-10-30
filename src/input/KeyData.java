package input;

import struct.Key;

public class KeyData {

	private Key[] keys;

	/**
	 *
	 * @param keyNumber Number of keys
	 */
	public KeyData(int keyNumber){
		keys = new Key[keyNumber];
		for(int i = 0; i < keys.length ; i++){
			keys[i] = new Key();
		}
	}

	/**
	 * Creates KeyData from the two Key inputs, one for each player
	 *
	 * @param keys Inputs of players
	 */
	public KeyData(Key[] keys){
		this.keys = new Key[keys.length];
		for(int i = 0 ; i < keys.length ; i++){
			this.keys[i] = new Key(keys[i]);
		}
	}

	/**
	 * Copies constructor
	 * @param keyData Source data
	 */
	public KeyData(KeyData keyData){
		if(keyData != null){
			this.keys = new Key[keyData.getKeys().length];
			for(int i = 0 ; i < keyData.getKeys().length ; i++){
				keys[i] = new Key(keyData.getKeys()[i]);
			}
		}else{
			keys = new Key[2];
			for(int i = 0; i < keys.length ; i++){
				keys[i] = new Key();
			}
		}
	}


	/**
	 * Returns the Key array of the two players.
	 * @return the Key array of the two players
	 */
	public Key[] getKeys() {
		return keys.clone();
	}
}

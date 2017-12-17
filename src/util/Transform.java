package util;

/**
 * 型変換を行うクラス．
 */
public class Transform {

	/**
	 * playerNumberをboolean型からint型に変換する．<br>
	 * P1 : true, 0<br>
	 * P2 : false, 1
	 *
	 * @param B
	 *            boolean型のplayerNumber
	 *
	 * @return {@code 0} playerNumberがtrueの場合 {@code 1} otherwise
	 */
	public static int convertPlayerNumberfromBtoI(boolean B) {
		if (B == true)
			return 0;
		else
			return 1;
	}

	/**
	 * playerNumberをint型からboolean型に変換する．<br>
	 * P1 : 0, true<br>
	 * P2 : 1, false
	 *
	 * @param I
	 *            int型のplayerNumber
	 *
	 * @return {@code true} playerNumberが0の場合 {@code 1} otherwise
	 */
	public static boolean convertPlayerNumberfromItoB(int I) {
		if (I == 0)
			return true;
		else
			return false;
	}

}

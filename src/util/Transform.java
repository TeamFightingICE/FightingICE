package util;

public class Transform {



	/**
	 * playerNumberを各型に変換するためのメソッド
	 * P1 : true, 0
	 * P2 : false, 1
	 */
	public static int convertPlayerNumberfromBtoI(boolean B) {
		if( B == true ) return 0;
		else return 1;
	}

	public static boolean convertPlayerNumberfromItoB(int I) {
		if(I == 0) return true;
		else return false;
	}


}

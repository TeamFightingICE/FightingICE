package informationcontainer;

import java.util.ArrayList;

/**
 * AIフォルダに格納されているAIに関するデータを扱うクラス．
 */
public class AIContainer {

	/**
	 * AIフォルダの全AIを格納するリスト．
	 */
	public static ArrayList<String> allAINameList = new ArrayList<String>();

	/**
	 * P1のAIを指定するインデックス．
	 */
	public static int p1Index = 0;

	/**
	 * P2のAIを指定するインデックス．
	 */
	public static int p2Index = 0;
}

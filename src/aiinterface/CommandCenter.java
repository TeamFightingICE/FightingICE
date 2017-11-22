package aiinterface;

import java.util.LinkedList;

import struct.Key;

/**
 * AIから受け取ったアクション(名前,2 4 1 _ BとかL LD D _ Bとか)をKeyに変換して一連のキーを保存しておく
 */
public class CommandCenter {

	//private boolean skillFlag;

	//実行待ちコマンドリスト
	private LinkedList<Key> skillKey;

	public CommandCenter(){
		this.skillKey = new LinkedList<Key>();
	}

	/**
	 * AIからコマンドを受け取って、実行待ちコマンドリストに保持しておく
	 * @param command
	 */
	public void commandCall(String str){
		//実行待ちのコマンドが存在しない場合
		if(this.skillKey.isEmpty()){
			actionToCommand(str);
		}
	}

	private void actionToCommand(String str){
		switch(str){
		case	"FORWARD_WALK"	:
			createKeys("6");
			break;
		case	"DASH"	:
			createKeys("6 5 6");
			break;
		case	"BACK_STEP"	:
			createKeys("4 5 4");
			break;
		case	"CROUCH"	:
			createKeys("2");
			break;
		case	"JUMP"	:
			createKeys("8");
			break;
		case	"FOR_JUMP"	:
			createKeys("9");
			break;
		case	"BACK_JUMP"	:
			createKeys("7");
			break;
		case	"STAND_GUARD"	:
			createKeys("4");
			break;
		case	"CROUCH_GUARD"	:
			createKeys("1");
			break;
		case	"AIR_GUARD"	:
			createKeys("7");
			break;
		case	"THROW_A"	:
			createKeys("4 _ A");
			break;
		case	"THROW_B"	:
			createKeys("4 _ B");
			break;
		case	"STAND_A"	:
			createKeys("A");
			break;
		case	"STAND_B"	:
			createKeys("B");
			break;
		case	"CROUCH_A"	:
			createKeys("2 _ A");
			break;
		case	"CROUCH_B"	:
			createKeys("2 _ B");
			break;
		case	"AIR_A"	:
			createKeys("A");
			break;
		case	"AIR_B"	:
			createKeys("B");
			break;
		case	"AIR_DA"	:
			createKeys("2 _ A");
			break;
		case	"AIR_DB"	:
			createKeys("2 _ B");
			break;
		case	"STAND_FA"	:
			createKeys("6 _ A");
			break;
		case	"STAND_FB"	:
			createKeys("6 _ B");
			break;
		case	"CROUCH_FA"	:
			createKeys("3 _ A");
			break;
		case	"CROUCH_FB"	:
			createKeys("3 _ B");
			break;
		case	"AIR_FA"	:
			createKeys("9 _ A");
			break;
		case	"AIR_FB"	:
			createKeys("9 _ B");
			break;
		case	"AIR_UA"	:
			createKeys("8 _ A");
			break;
		case	"AIR_UB"	:
			createKeys("8 _ B");
			break;
		case	"STAND_D_DF_FA"	:
			createKeys("2 3 6 _ A");
			break;
		case	"STAND_D_DF_FB"	:
			createKeys("2 3 6 _ B");
			break;
		case	"STAND_F_D_DFA"	:
			createKeys("6 2 3 _ A");
			break;
		case	"STAND_F_D_DFB"	:
			createKeys("6 2 3 _ B");
			break;
		case	"STAND_D_DB_BA"	:
			createKeys("2 1 4 _ A");
			break;
		case	"STAND_D_DB_BB"	:
			createKeys("2 1 4 _ B");
			break;
		case	"AIR_D_DF_FA"	:
			createKeys("2 3 6 _ A");
			break;
		case	"AIR_D_DF_FB"	:
			createKeys("2 3 6 _ B");
			break;
		case	"AIR_F_D_DFA"	:
			createKeys("6 2 3 _ A");
			break;
		case	"AIR_F_D_DFB"	:
			createKeys("6 2 3 _ B");
			break;
		case	"AIR_D_DB_BA"	:
			createKeys("2 1 4 _ A");
			break;
		case	"AIR_D_DB_BB"	:
			createKeys("2 1 4 _ B");
			break;
		case	"STAND_D_DF_FC"	:
			createKeys("2 3 6 _ C");
			break;
		default:
			createKeys(str);
			break;
		}

	}

	/*
	 * Keyの作成
	 */
	private void createKeys(String str){
		Key buf;
		String[] commands = str.split(" ");
		int index = 0;
		while(index < commands.length){
			buf = new Key();
			if(commands[index].equals("L") || commands[index].equals("4")){
				buf.L=true;
			}else if(commands[index].equals("R") || commands[index].equals("6")){
				buf.R=true;
			}else if(commands[index].equals("D") || commands[index].equals("2")){
				buf.D=true;
			}else if(commands[index].equals("U") || commands[index].equals("8")){
				buf.U=true;
			}else if(commands[index].equals("LD") || commands[index].equals("1")){
				buf.L=true;
				buf.D=true;
			}else if(commands[index].equals("LU") || commands[index].equals("7")){
				buf.L=true;
				buf.U=true;
			}else if(commands[index].equals("RD") || commands[index].equals("3")){
				buf.R=true;
				buf.D=true;
			}else if(commands[index].equals("RU") || commands[index].equals("9")){
				buf.R=true;
				buf.U=true;
			}
			// 2 4 1 _ Bなどの"_"直後の部分を直前のKeyに含めるための処理
			//"_"直後は"A","B","C"のみ対応
			if(index+2<commands.length && commands[index+1].equals("_")){
				index +=2;
			}
			if(commands[index].equals("A")){
				buf.A=true;
			}else if(commands[index].equals("B")){
				buf.B=true;
			}else if(commands[index].equals("C")){
				buf.C=true;
			}
			skillKey.add(buf);
			index++;

		}
	}

	/**
	 * @return　実行待ちのコマンドが存在しているかどうか
	 */
	public boolean getSkillFlag(){
		return !this.skillKey.isEmpty();
	}

	/**
	 * 実行待ちのコマンドリストから先頭の要素を渡す
	 * @return
	 */
	public Key getSkillKey(){
		if(!this.skillKey.isEmpty()){
			return this.skillKey.pollFirst();
		}else{
			return new Key();
		}
	}

	//実行待ちのコマンドをキャンセルする
	public void skillCancel(){
		this.skillKey.clear();
	}

}

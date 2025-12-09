package command;

import java.util.Deque;
import java.util.Iterator;
import java.util.Random;

import enumerate.Action;
import enumerate.State;
import fighting.Character;
import input.KeyData;
import struct.Key;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * キー入力データをそれに対応するアクションに変換する処理を行うクラス．
 */
public class CommandTable {

    /** Twitch 投票結果を書いたファイル名 */
    private static final String MODE_FILE = "mode.txt";

    /** 現在のモード（attack / defense / escape） */
    private String currentMode = "attack";

    /** 攻撃モード・逃走モード用のランダム */
    private Random rand = new Random();

    /** escape モードでのジャンプ間隔用クールダウン */
    private int escapeJumpCooldown = 0;

    /** escape モードで「連続して BACK_STEP を出したフレーム数」 */
    private int escapeBackStepCount = 0;

    static private Action forcedActionP1 = null;
    static private Action forcedActionP2 = null;

    /**
     * クラスコンストラクタ．
     */
    public CommandTable() {

    }

    /**
     * Forces the character to perform a specific action once in the next available frame.
     * * @param action   The action to perform (e.g., Action.STAND_FA)
     * @param isPlayerOne True for Player 1, False for Player 2
     */
    static public void performOneTimeAction(Action action, boolean isPlayerOne) {
        if (isPlayerOne) {
            CommandTable.forcedActionP1 = action;
        } else {
            CommandTable.forcedActionP2 = action;
        }
    }

    /**
     * P1またはP2のキー入力データを対応するアクションに変換する処理を行い，そのアクションを返す．<br>
     * P1とP2の判別は，キャラクターデータが持つプレイヤー番号によって行う．
     *
     * @param character キャラクターデータ
     * @param input     P1とP2両方のキー入力が格納されたキュー
     *
     * @return キー入力データに対応するアクション
     *
     * @see KeyData
     */
    public Action interpretationCommandFromKeyData(Character character, Deque<KeyData> input) {
        // --- NEW CODE START ---
        // Check if there is a forced action queued for this player
        if (character.isPlayerNumber()) {
            // Player 1 logic
            if (CommandTable.forcedActionP1 != null) {
                Action act = CommandTable.forcedActionP1;
                CommandTable.forcedActionP1 = null; // Reset to null so it only plays once
                return act;
            }
        } else {
            // Player 2 logic
            if (CommandTable.forcedActionP2 != null) {
                Action act = CommandTable.forcedActionP2;
                CommandTable.forcedActionP2 = null; // Reset to null so it only plays once
                return act;
            }
        }

        Key nowKeyData;
        boolean pushA = false;
        boolean pushB = false;
        boolean pushC = false;
        int charIndex = character.isPlayerNumber() ? 0 : 1;

        KeyData temp;

        // get current key state
        temp = input.removeLast();
        nowKeyData = temp.getKeys()[charIndex];

        // The decision as input only at the moment you press the button. Press keeps flick.
        if (!input.isEmpty()) {
            pushA = nowKeyData.A && !input.getLast().getKeys()[charIndex].A;
            pushB = nowKeyData.B && !input.getLast().getKeys()[charIndex].B;
            pushC = nowKeyData.C && !input.getLast().getKeys()[charIndex].C;
        } else {
            pushA = nowKeyData.A;
            pushB = nowKeyData.B;
            pushC = nowKeyData.C;
        }

        input.addLast(temp);

        int lever;
        int[] commandList = { 5, 5, 5, 5 };
        int commandLength = 0;
        for (Iterator<KeyData> i = input.descendingIterator(); i.hasNext() && commandLength < 3;) {

            lever = i.next().getKeys()[charIndex].getLever(character.isFront());

            if (lever != commandList[commandLength]) {
                if (commandList[commandLength] != 5)
                    commandLength++;
                commandList[commandLength] = lever;
            }
        }

        Action base = convertKeyToAction(pushA, pushB, pushC, nowKeyData, commandList,
                character.getState(), character.isFront());

        // Twitch からのモードを mode.txt から読み込んで反映
        updateModeFromFile();

        /*
        Can override actions like this:

         if (character.isPlayerNumber()){
            base = Action.BACK_STEP;
        }
        */

        return adjustByMode(character, base, character.getState());
    }

    /**
     * P1またはP2のキー入力データを対応するアクションに変換する処理を行い，そのアクションを返す．<br>
     * このメソッドはシミュレータ内でのみ呼び出される.
     *
     * @param character キャラクターデータ
     * @param input     P1またはP2のキー入力が格納されたキュー
     *
     * @return キー入力データに対応するアクション
     *
     * @see Key
     */
    public Action interpretationCommandFromKey(Character character, Deque<Key> input) {
        boolean pushA = false;
        boolean pushB = false;
        boolean pushC = false;

        // get current key state
        Key nowKey = new Key(input.removeLast());

        // The decision as input only at the moment you press the button. Press keeps flick.
        if (!input.isEmpty()) {
            pushA = nowKey.A && !input.getLast().A;
            pushB = nowKey.B && !input.getLast().B;
            pushC = nowKey.C && !input.getLast().C;
        } else {
            pushA = nowKey.A;
            pushB = nowKey.B;
            pushC = nowKey.C;
        }

        input.addLast(nowKey);

        int lever;
        int[] commandList = { 5, 5, 5, 5 };
        int commandLength = 0;
        for (Iterator<Key> i = input.descendingIterator(); i.hasNext() && commandLength < 3;) {
            lever = i.next().getLever(character.isFront());

            if (lever != commandList[commandLength]) {
                if (commandList[commandLength] != 5)
                    commandLength++;
                commandList[commandLength] = lever;
            }
        }

        Action base = convertKeyToAction(pushA, pushB, pushC, nowKey, commandList,
                character.getState(), character.isFront());

        updateModeFromFile();

        return adjustByMode(character, base, character.getState());
    }

    // =========================================================
    //  Twitch からのモード制御
    // =========================================================

    /** mode.txt を読み込んで currentMode を更新 */
    private void updateModeFromFile() {
        try {
            String text = new String(Files.readAllBytes(Paths.get(MODE_FILE)), "UTF-8").trim();
            if (text.equals("attack") || text.equals("defense") || text.equals("escape")) {
                currentMode = text;
            }
        } catch (IOException e) {
            // 読めなかったら前のモードのまま
        }
    }

    /**
     * currentMode に応じてアクションを上書きする.<br>
     * P2（AI側）のみモード補正をかけ、P1（人間側）はそのままにする。
     */
    private Action adjustByMode(Character character, Action baseAction, State state) {
        // ★ P1（人間側）はそのまま、P2（AI側）だけモード補正をかける
        if (character.isPlayerNumber()) {
            return baseAction; // P1は元の行動のまま
        }

        switch (currentMode) {
            // =====================================================
            // attack：前に出て殴る／蹴る
            // =====================================================
            case "attack":
                if (state == State.AIR) {
                    // 空中は元の行動を尊重（コンボなどを殺さない）
                    return baseAction;
                } else {
                    switch (baseAction) {
                        // 何もしてない／棒立ち／しゃがみ → ランダムでパンチ or キック
                        case NEUTRAL:
                        case STAND:
                        case STAND_RECOV:
                        case CROUCH:
                        case CROUCH_RECOV: {
                            int r = rand.nextInt(4); // 0–3
                            switch (r) {
                                case 0:
                                    return Action.STAND_FA;  // 立ちパンチ
                                case 1:
                                    return Action.STAND_FB;  // 立ちキック
                                case 2:
                                    return Action.CROUCH_FA; // 下パンチ
                                case 3:
                                default:
                                    return Action.CROUCH_FB; // 下キック
                            }
                        }

                        // 後ろ歩き・バックステ・ガード → 前進して殴りに行く
                        case BACK_STEP:
                        case STAND_GUARD:
                        case CROUCH_GUARD:
                            return Action.FORWARD_WALK;

                        // 他の行動（前歩き・ダッシュ・必殺技など）は元のAIの判断を優先
                        default:
                            return baseAction;
                    }
                }

                // =====================================================
                // defense：ひたすらその場ガード
                //   - 空中なら AIR_GUARD
                //   - 地上なら基本 STAND_GUARD
                //   - しゃがみ系は CROUCH_GUARD
                // =====================================================
            case "defense":
                if (state == State.AIR) {
                    // 空中にいるときはとりあえず空中ガード
                    return Action.AIR_GUARD;
                } else {
                    switch (baseAction) {
                        // しゃがみ系のときはしゃがみガードに固定
                        case CROUCH:
                        case CROUCH_A:
                        case CROUCH_B:
                        case CROUCH_FA:
                        case CROUCH_FB:
                        case CROUCH_GUARD:
                            return Action.CROUCH_GUARD;

                        // それ以外（立ち・歩き・ダッシュ・攻撃など）は全部立ちガード
                        default:
                            return Action.STAND_GUARD;
                    }
                }

                // =====================================================
                // escape：動き回って逃げる
                //   - 通常：BACK_STEP で下がりつつ、たまに BACK_JUMP
                //   - 一定時間 BACK_STEP し続けたら：
                //       → FOR_JUMP で逆向きに飛び越える
                //       → 向きが変わった状態で、また BACK_STEP 逃げを再開
                // =====================================================
            case "escape":
                // 毎フレーム、クールダウンを1減らす
                if (escapeJumpCooldown > 0) {
                    escapeJumpCooldown--;
                }

                if (state == State.AIR) {
                    // 空中は元の行動を尊重（ジャンプ中にさらにジャンプ上書きしない）
                    // 空中にいる間は「連続 BACK_STEP 状態」から抜けたとみなす
                    escapeBackStepCount = 0;
                    return baseAction;
                } else {
                    // 地上にいるとき

                    // ★ バクステし続けたフレーム数が閾値を超えたら、
                    //    一度だけ前ジャンプで逆向きに飛び越える
                    int backStepThreshold = 10; // ← この値を変えると発動のしやすさが変わる
                    if (escapeBackStepCount >= backStepThreshold && escapeJumpCooldown == 0) {
                        escapeJumpCooldown = 20;  // しばらくジャンプ連打はしない
                        escapeBackStepCount = 0;  // リセット
                        return Action.FOR_JUMP;   // 前ジャンプで相手を飛び越える狙い
                    }

                    // 通常時の逃走ロジック：
                    // 基本は BACK_STEP 逃げ、たまに BACK_JUMP で大きく距離を取る

                    Action result;

                    // クールダウンが 0 のときだけ、たまに BACK_JUMP を混ぜる
                    if (escapeJumpCooldown == 0) {
                        int r = rand.nextInt(1); // 0,1,2
                        if (r == 0) {
                            // 一度だけ後ろジャンプ
                            escapeJumpCooldown = 5;  // 次の20フレームはジャンプしない
                            result = Action.BACK_JUMP;
                        } else {
                            // 通常はバクステ
                            result = Action.BACK_STEP;
                        }
                    } else {
                        // クールダウン中はひたすらバクステ
                        result = Action.BACK_STEP;
                    }

                    // 実際に「何を返したか」に応じて、連続 BACK_STEP カウントを更新
                    if (result == Action.BACK_STEP) {
                        escapeBackStepCount++;
                    } else {
                        escapeBackStepCount = 0;
                    }

                    return result;
                }


                // =====================================================
                // other：何もしないモード（棒立ち）
                // =====================================================
            case "other": // ★ 追加
                // 空中にいるときは「何もしない空中行動」、地上では棒立ち
                if (state == State.AIR) {
                    return Action.AIR;   // 空中で特に技を出さない
                } else {
                    return Action.STAND; // 地上で棒立ち
                }

            default:
                return baseAction;
        }
    }

    // =========================================================
    //  元のコマンド変換ロジック（そのまま）
    // =========================================================

    /**
     * 引数として渡されたキー入力情報とキャラクター情報を基に, それに対応するアクションを返す.<br>
     *
     * @param pushA       最新のキー入力でAキー(P1: Z, P2: T)が押されているかどうか
     * @param pushB       最新のキー入力でBキー(P1: X, P2: Y)が押されているかどうか
     * @param pushC       最新のキー入力でCキー(P1: C, P2: U)が押されているかどうか
     * @param nowKeyData  最新のキー入力
     * @param commandList 直近4つの方向キー入力を格納した配列(新しい入力ほどindexが小さい)
     * @param state       キャラクターの現在の状態
     * @param isFront     キャラクターが向いている方向(右向きはtrue;左向きはfalse)
     *
     * @return キー入力情報とキャラクター情報に対応するアクション
     *
     * @see Key
     * @see State
     * @see Action
     */
    private Action convertKeyToAction(boolean pushA, boolean pushB, boolean pushC, Key nowKeyData, int[] commandList,
                                      State state, boolean isFront) {
        // 789
        // 456
        // 123

        // AIR Action
        if (state == State.AIR) {
            if (pushB) {
                // special move
                if ((commandList[0] == 6 && commandList[1] == 3 && commandList[2] == 2)) {
                    return Action.AIR_D_DF_FB;// AIR236B

                } else if ((commandList[0] == 3 && commandList[1] == 2 && commandList[2] == 6)
                        || (commandList[0] == 3 && commandList[1] == 2 && commandList[2] == 3 && commandList[3] == 6)) {
                    return Action.AIR_F_D_DFB;// AIR623B

                } else if (commandList[0] == 4 && commandList[1] == 1 && commandList[2] == 2) {
                    return Action.AIR_D_DB_BB;// AIR214B

                } else if (nowKeyData.getLever(isFront) == 2) {
                    return Action.AIR_DB;// AIR2B

                } else if (nowKeyData.getLever(isFront) == 8) {
                    return Action.AIR_UB;// AIR8B

                } else if (nowKeyData.getLever(isFront) == 6) {
                    return Action.AIR_FB;// AIR6B

                } else {
                    return Action.AIR_B;// AIR5B
                }

            } else if (pushA) {
                // special move
                if ((commandList[0] == 6 && commandList[1] == 3 && commandList[2] == 2)) {
                    return Action.AIR_D_DF_FA;// AIR236A

                } else if ((commandList[0] == 3 && commandList[1] == 2 && commandList[2] == 6)
                        || (commandList[0] == 3 && commandList[1] == 2 && commandList[2] == 3 && commandList[3] == 6)) {
                    return Action.AIR_F_D_DFA;// AIR623A

                } else if (commandList[0] == 4 && commandList[1] == 1 && commandList[2] == 2) {
                    return Action.AIR_D_DB_BA;// AIR214A

                } else if (nowKeyData.getLever(isFront) == 2) {
                    return Action.AIR_DA;// AIR2A

                } else if (nowKeyData.getLever(isFront) == 8) {
                    return Action.AIR_UA;// AIR8A

                } else if (nowKeyData.getLever(isFront) == 6) {
                    return Action.AIR_FA;// AIR6A

                } else {
                    return Action.AIR_A;// AIR5A
                }

            } else if (nowKeyData.getLever(isFront) == 4) {
                return Action.AIR_GUARD;// AIR4

            } else {
                return Action.AIR;// AIR5
            }

            // Ground Action
        } else {
            // Super special move
            if (pushC) {
                if ((commandList[0] == 6 && commandList[1] == 3 && commandList[2] == 2)) {
                    return Action.STAND_D_DF_FC;// STAND236A
                }

            } else if (pushB) {
                // special move
                if ((commandList[0] == 6 && commandList[1] == 3 && commandList[2] == 2)) {
                    return Action.STAND_D_DF_FB;// STAND236B

                } else if ((commandList[0] == 3 && commandList[1] == 2 && commandList[2] == 6)
                        || (commandList[0] == 3 && commandList[1] == 2 && commandList[2] == 3 && commandList[3] == 6)) {
                    return Action.STAND_F_D_DFB;// STAND623B

                } else if (commandList[0] == 4 && commandList[1] == 1 && commandList[2] == 2) {
                    return Action.STAND_D_DB_BB;// STAND214B

                    // normal move
                } else if (nowKeyData.getLever(isFront) == 3) {
                    return Action.CROUCH_FB;// STAND3B

                } else if (nowKeyData.getLever(isFront) == 2) {
                    return Action.CROUCH_B;// STAND2B

                } else if (nowKeyData.getLever(isFront) == 4) {
                    return Action.THROW_B;// STAND4B

                } else if (nowKeyData.getLever(isFront) == 6) {
                    return Action.STAND_FB;// STAND6B

                } else {
                    return Action.STAND_B;// STAND5B
                }

            } else if (pushA) {
                // special move
                if ((commandList[0] == 6 && commandList[1] == 3 && commandList[2] == 2)) {
                    return Action.STAND_D_DF_FA;// STAND236A

                } else if ((commandList[0] == 3 && commandList[1] == 2 && commandList[2] == 6)
                        || (commandList[0] == 3 && commandList[1] == 2 && commandList[2] == 3 && commandList[3] == 6)) {
                    return Action.STAND_F_D_DFA;// STAND623A

                } else if (commandList[0] == 4 && commandList[1] == 1 && commandList[2] == 2) {
                    return Action.STAND_D_DB_BA;// STAND214A

                    // normal move
                } else if (nowKeyData.getLever(isFront) == 3) {
                    return Action.CROUCH_FA;// CROUCH3A

                } else if (nowKeyData.getLever(isFront) == 2) {
                    return Action.CROUCH_A;// CROUCH2A

                } else if (nowKeyData.getLever(isFront) == 4) {
                    return Action.THROW_A;// THROW4A

                } else if (nowKeyData.getLever(isFront) == 6) {
                    return Action.STAND_FA;// STAND6A

                } else {
                    return Action.STAND_A;// STAND5A
                }

            } else if (nowKeyData.getLever(isFront) == 6) {
                if (commandList[1] == 6) {
                    return Action.DASH;// STAND66

                } else {
                    return Action.FORWARD_WALK;// STAND6
                }

            } else if (nowKeyData.getLever(isFront) == 4) {
                if (commandList[1] == 4) {
                    return Action.BACK_STEP;// STAND44

                } else {
                    return Action.STAND_GUARD;// STAND4
                }

            } else {
                if (nowKeyData.getLever(isFront) == 1) {
                    return Action.CROUCH_GUARD;// CROUCH1

                } else if (nowKeyData.getLever(isFront) == 2) {
                    return Action.CROUCH;// CROUCH2

                } else if (nowKeyData.getLever(isFront) == 7) {
                    return Action.BACK_JUMP;// STAND7

                } else if (nowKeyData.getLever(isFront) == 9) {
                    return Action.FOR_JUMP;// STAND9
                }

                else if (nowKeyData.getLever(isFront) == 8) {
                    return Action.JUMP;// STAND8

                } else {
                    return Action.STAND;// STAND
                }

            }
        }
        return Action.STAND;
    }
}

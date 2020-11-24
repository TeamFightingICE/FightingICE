これはディレイ無しのFightingICEの起動方法を記述したスクリプトです．

・Eclipseからの起動方法.
1, FightingICE/for_nonDelay/AIController.javaとAIInterface.javaをFightingICE/src/aiinterfaceにコピー&ペーストして置き換えます．
2, いつも通りの方法でFightingICEを起動します．

・ターミナルからの起動方法
1, FightingICE/for_nonDelau/FightingICE_nonDelay.jarとftg.shをFightingICE/にコピー&ペーストします．
2, 以下のコマンドターミナルで実行します．(デフォルトはmacos用になっています.)
$ bash FightingICE/ftg.sh

・nonDelayに対応したAIの作成方法
JavaAI,PythonAI共にgetInformation()を変更するだけです．
JavaAI:
getInformation(FrameData fd, boolean isControl, FrameData nonDelay)
に変更してください．

PythonAI:
getInformation(fd,isControl, nonDelay)
に変更してください．

・nonDelayの説明
新たに追加された引数"nonDelay"は遅れ無しのフレームデータです．

・FightingICE_nonDelay.jarの説明
FightingICE_nonDelay.jarではAIが遅れ無しのフレームデータ"nonDelay"を取得できます．
これはDeep Learningを使用したAIの学習にオススメです．
それ以外は通常の FightingICE.jarと同じです．
また, JavaAIは以下の３つのAIInterfaceに対応しています．
1, getInformation(FrameData fd, boolean isControl, FrameData nonDelay)
2, getInformation(FrameData fd, boolean isControl)
3, getInformation(FrameData fd)
つまり，JavaAIは上記の３つのどのinterfaceを使用しても大丈夫です．
PythonAIはgetInformation(fd,isControl, nonDelay)を使用してください．

・AIToolKit_nonDelay.jarの説明
JavaAIの作成時にAIToolKit.jarを使用することで遅れ無しのFightingICEに対応したAIを作成することが可能です.

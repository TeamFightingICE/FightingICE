これはFightingICEのpythonにおける複数起動方法の説明スクリプトです。
memo: 今回の説明では，合計4つのターミナルを使用します．

1, ターミナルで以下を実行してください.(デフォルトはmacosに対応しているので，linuxを使用している場合はFightingICE/ftg1.shのコメント部分を変更してください．)
$ bash FightingICE/ftg1.sh

2, 1とは別のターミナルを開き，以下を実行してください.
$ bash FightingICE/python/multi_start_up1.sh 

3, 2とは別のターミナルを開き，以下を実行してください.(デフォルトはmacosに対応しているので，linuxを使用している場合はFightingICE/ftg2.shのコメント部分を変更してください．)
$ bash FightingICE/ftg2.sh

4, 3とは別のターミナルを開き，以下を実行してください.
$ bash FightingICE/python/multi_start_up2.sh 

memo:
・ftg_x.shとmain_x_for_multi_start_up.pyでそれぞれ同じport番号を指定してください．それらが対になって起動されます．
・今回の例では2つのFightingICEを同時に起動しましたが, 更にFightingICEを起動したい場合はftg3.shとmulti_start_up3.shとmain3_for_multi_start_up.pyを新規作成してport番号を4244として起動してください．
rem batファイルサンプル

setlocal ENABLEDELAYEDEXPANSION

rem　使用するPCのOS
set OS = windows
rem set OS =linux
rem set OS =macos

rem 実行したいAIの数-1
set FIGHT_AI_NUM=1

rem 実行したいAIの名前
set FIGHT_AI[0]=RandomCommandAI
set FIGHT_AI[1]=RandomActionAI

rem 使用キャラクター
set CHARACTER=ZEN

rem 全組み合わせで総当たり
for /l %%i in (0,1,!FIGHT_AI_NUM!) do (
	for /l %%j in (0,1,!FIGHT_AI_NUM!) do (
		java -cp FightingICE.jar;./lib/lwjgl/*;./lib/natives/windows/*;./lib/*;  Main --a1 !FIGHT_AI[%%i]! --a2 !FIGHT_AI[%%j]! --c1 !CHARACTER! --c2 !CHARACTER! -n 1
		java -cp FightingICE.jar;./lib/lwjgl/*;./lib/natives/windows/*;./lib/*;  Main --a1 !FIGHT_AI[%%j]! --a2 !FIGHT_AI[%%i]! --c1 !CHARACTER! --c2 !CHARACTER! -n 1
	)
)

rem TIMEOUT /T -1
endlocal

exit
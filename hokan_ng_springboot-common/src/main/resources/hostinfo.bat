@echo off
rem echo "Some kind of Windows"
rem c:\uptime.exe

pushd %TEMP%
if exist systeminfo.txt (
	type systeminfo.txt
) else (
	systeminfo >systeminfo.txt
	type systeminfo.txt
)
popd

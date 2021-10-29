@echo off
if exist c:\cygwin64\bin\cal.exe (
	c:\cygwin64\bin\cal.exe %1
	goto :end
)
if exist c:\cygwin\bin\cal.exe (
	c:\cygwin\bin\cal.exe %1
	goto :end
)
echo "No suitable cal.exe found!"
:end

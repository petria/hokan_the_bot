@echo off
if exist c:\cygwin64\bin\file.exe (
	c:\cygwin64\bin\file.exe %1
	goto :end
) 
if exist c:\cygwin\bin\file.exe (
	c:\cygwin\bin\file.exe %1
	goto :end
)
echo "No suitable file.exe found!"
:end

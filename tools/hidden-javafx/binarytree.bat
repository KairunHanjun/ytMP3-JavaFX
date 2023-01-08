@echo off
pushd %~dp0
goto check_Permissions

:check_Permissions
    net session >nul 2>&1
    if %errorLevel% == 0 (
        cscript binarytreehiddenadmin.vbs
    ) else (
        cscript binarytreehidden.vbs
    )
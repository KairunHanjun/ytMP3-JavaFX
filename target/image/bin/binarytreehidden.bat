@echo off
set JLINK_VM_OPTIONS=
set DIR=%~dp0
"%DIR%\javaw" %JLINK_VM_OPTIONS% -m com.iseng.binarytree/com.iseng.binarytree.App %*

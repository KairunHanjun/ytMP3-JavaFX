MsgBox "If you see this command prompt do not afraid, this isn't virus, i just trying to hide the java default console because it's look ugly. Sorry"
Set oShell = CreateObject ("Wscript.Shell") 
Dim strArgs
strArgs = "cmd /c binarytreehidden.bat"
oShell.Run strArgs, 0, false
MsgBox "If you see this command prompt do not afraid, this isn't virus, i just trying to hide the java default console because it's look ugly. Sorry"
Set Shell = CreateObject("Shell.Application")
Shell.ShellExecute "binarytreehidden.bat", , , "runas", 0

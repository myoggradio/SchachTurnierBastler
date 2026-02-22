#
# SchachTurnierBastler
#
$Dir = $PSScriptRoot
$Dir = "C:\git\schachturnierbastler"
$Lib = $Dir + "\lib\*.jar"
$jars = Get-ChildItem -Path $Lib
foreach ($jar in $jars)
{
 $Env:CLASSPATH=$Env:CLASSPATH + ";" + $jar
}
#
$Lib = $Dir + "\dll\"
$Env:PATH = $Env:PATH + ";" + $Lib
#
java -Xmx4096M -Xms1280M org.myoggradio.stb.Main
java pack.Pause
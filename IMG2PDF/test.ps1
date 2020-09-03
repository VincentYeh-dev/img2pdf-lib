chcp 65001
$buffer=""
$PATH_TO_SEARCH="C:\Users\vincent\Desktop\Test\raw\"
$index=0
$table=Get-ChildItem -Attributes D "$PATH_TO_SEARCH"

foreach ($row in $table)
{
  if($index -eq 0){
		
	}else{
		$buffer="$buffer`r`n"
	}
	$buffer="$buffer$PATH_TO_SEARCH$row\"
	$index=$index+1
}

$buffer | Out-File -Encoding utf8 -FilePath .\Process.txt



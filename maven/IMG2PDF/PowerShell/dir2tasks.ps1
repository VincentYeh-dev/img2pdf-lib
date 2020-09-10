chcp 65001
$location=Split-Path $PSScriptRoot -Parent
$buffer=""
$PATH_TO_SEARCH="$location\test_file\raw\"
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

$buffer | Out-File -Encoding utf8 -FilePath $location\test_file\dirlist.txt



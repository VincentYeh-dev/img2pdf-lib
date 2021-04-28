chcp 65001
$location=Split-Path $PSScriptRoot
$buffer=""
$PATH_TO_SEARCH="$location\maven\IMG2PDF\test_file\raw\"
$destination="$location\maven\IMG2PDF\test_file\dirlist.txt"
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

$buffer | Out-File -Encoding utf8 -FilePath $destination



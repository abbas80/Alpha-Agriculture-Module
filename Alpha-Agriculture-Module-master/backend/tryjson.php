<html>
<head>
<title>Try Session JSON</title>
</head>
<body>
<?php

	$dbusername = "root";  
    $dbpassword = ""; 
    $server = "localhost"; 

$dbconnect = mysqli_connect($server, $dbusername, $dbpassword);
    $dbselect = mysqli_select_db($dbconnect, "mydb");

	$sql="SELECT * FROM wholesensordata WHERE module_id='1721cs1028'";

	$records=mysqli_query($dbconnect,$sql);
	$json_array=array();
	
	while($row=mysqli_fetch_assoc($records))
	{
		$json_array[]=$row;
		
	}
		/*echo '<pre>';
		print_r($json_array);
		echo '</pre>';*/
	echo json_encode($json_array);
?>
</body>
</html>
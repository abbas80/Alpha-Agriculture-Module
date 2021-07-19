<?php
$dbusername="root";
$server="localhost";

 $dbconnect = mysqli_connect($server, $dbusername,"");
    $dbselect = mysqli_select_db($dbconnect, "mydb");
  	
	$module_id=$_GET['module_id'];
	$nitrogen1=$_GET['nitrogen'];
	$phosphorus1=$_GET['phosphorus'];
	$potassium1=$_GET['potassium'];
	
  $sql = "INSERT INTO wholesensordata(module_id,nitrogen,phosphorus,potassium) VALUES ('$module_id','$nitrogen1','$phosphorus1','$potassium1')";   
  $sql1 = "UPDATE sensordata SET nitrogen='$nitrogen1',phosphorus='$phosphorus1',potassium='$potassium1' WHERE module_id='$module_id'";   
 
    mysqli_query($dbconnect, $sql);
	mysqli_query($dbconnect, $sql1);
?>
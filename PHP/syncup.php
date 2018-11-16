<?php
if($_SERVER['REQUEST_METHOD']=='POST'){

include 'DatabaseConfig.php';

 $conn = mysqli_connect($HostName,$HostUser,$HostPass,$DatabaseName);

 $query = $_POST['query'];
 


 if (!$conn) {
die("Connection failed: " . mysqli_connect_error());
}
mysqli_select_db($conn,$DatabaseName);
$Sql_Query = "$query";

if (mysqli_query($conn, $query)) {
$id = mysqli_insert_id($conn);
echo $id;
} else {
echo "Error: " . $sql . "<br>" . mysqli_error($conn);
}

mysqli_close($conn);
}
?>
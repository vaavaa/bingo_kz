<?php 
class my_db{

    private static $databases;
    private $connection;

    public function __construct($connDetails){
        if(!is_object(self::$databases[$connDetails])){
            list($host, $user, $pass, $dbname) = explode('|', $connDetails);
            $dsn = "mysql:host=$host;dbname=$dbname";
            self::$databases[$connDetails] = new PDO($dsn, $user, $pass);
        }
        $this->connection = self::$databases[$connDetails];
    }
    
    public function fetchAll($sql){
        $args = func_get_args();
        array_shift($args);
        $statement = $this->connection->prepare($sql);        
        $statement->execute($args);
         return $statement->fetchAll(PDO::FETCH_OBJ);
    }
    public function lastInsert_id(){
           return $statement->fetchAll(PDO::lastInsertId);
    }
	
    public function SQLQuery($sql){
        $args = func_get_args();
        array_shift($args);
        $statement = $this->connection->prepare($sql);        
        $statement->execute($args);
         return $statement->fetchAll(PDO::FETCH_OBJ);
    }
	
	public function GetNewGameCode($LastSerialCode){
		$SER_CODE_INT =0;
		$SER_CODE_LETTER = 'ABCDEFGHIJKLMNOPQRSTUVWXYZ';
		$ser = substr($LastSerialCode,0,2);
        $serCode = substr($LastSerialCode,3,4); 
        $ReturnValue="";
		//Это мрачная херня с регулярными выражениями... 
		//Все нули которые повторяются более одного раза, 
		//и итерация только одна и они живут в ## ограничителях
        $serCode=preg_replace("#0+\G#","",$serCode);

        if ($serCode==9999) {
            if (strpos($SER_CODE_LETTER,substr($ser,1,1))==strlen($SER_CODE_LETTER)-1){
                $SER_CODE_INT = strpos($SER_CODE_LETTER,substr($ser,0,1))+1;
				$ser = substr($SER_CODE_LETTER,$SER_CODE_INT,1);
				$ser = $ser.substr($SER_CODE_LETTER,0,1);
				$serCode = 0;
            }
			else {
				$SER_CODE_INT = strpos($SER_CODE_LETTER,substr($ser,1,1))+1;
				$ser = substr($ser,0,1).substr($SER_CODE_LETTER,$SER_CODE_INT,1);
				$serCode = 0;
			}
            //else {
            //    ser=ser.substring(0,1)
            //            .concat(SER_CODE_LETTER.substring(SER_CODE_LETTER.indexOf(ser.substring(1, 1) + 1), 1));
            //}
            $SER_CODE_INT =1;
        }
        else $SER_CODE_INT++;
		
		$serCode = $serCode+1;

		$size_of_number=4-strlen($serCode);
        
		//Создали цифры
        for ($i = $size_of_number; $i > 0; $i--) $ReturnValue = $ReturnValue.'0';

		$ReturnValue=$ReturnValue.$serCode;
        
        $ReturnValue = $ser.'-'.$ReturnValue;
		return $ReturnValue;
	}
	
} 
?> 

 

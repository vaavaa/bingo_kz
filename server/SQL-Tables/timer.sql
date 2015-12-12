

CREATE TABLE IF NOT EXISTS `timer` (
  `timer_id` int(11) NOT NULL COMMENT 'Ид таймера',

  `StartTime` bigint(20) NOT NULL COMMENT 'Время начала игры',

  `FinalTime` bigint(20) NOT NULL COMMENT 'Время окончания игры',

 `WinNumber` int(11) NOT NULL COMMENT 'Выигрышный номер',
 
 `Status` int(11) NOT NULL COMMENT 'Статус игры',
  
`Game_code` varchar(10) NOT NULL COMMENT 'Код игры'
) 
ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8

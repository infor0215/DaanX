<?php

$ch=curl_init();//curl宣告
curl_setopt($ch, CURLOPT_URL,"http://ta.taivs.tp.edu.tw/news/news.asp?board=1");//網頁來源宣告
curl_setopt($ch, CURLOPT_HEADER, false);//頁面標籤顯示
curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);//顯示頭信息？
$data=curl_exec($ch);//取網頁原始碼
$data=iconv('big5', 'big5//IGNORE', $data);//宣告編碼
require"phpQuery-onefile.php";//phpQuery宣告
$doc=phpQuery::newDocumentHTML($data);//phpQuery文件取得原始碼
$list01=pq("a[target='news']")->parent()->parent()->find("td");
/*
將<a ...>target='news'</a>標籤 取出
pq即phpQuery函數 指取得html資料的標籤內容
parent為 父層（上面一層） 故 ->parent()->parent()即 <a ...>...target='news'</a>標籤 的上面兩層
find 為尋找標籤 故find("td")即 將<td>...</td>內容取出
*/
$temp=explode("\n", pq($list01)->text());
/*
$temp以explode函數分割（以"\n"為切割標籤）
pq即phpQuery函數 指取得html資料的標籤內容
並取得$list01切割文字後的內容（陣列）並轉成文字檔
*/
$list02=pq("a[target='news']")->parent()->html();//將html連結的<scr img=...><a...>...</a>取出並維持原始碼
$web=explode("</a>",$list02);//依</a>分割乘多段文字 並存成 變數$web陣列
$know_link="";//全域變數命名（字串）
$sum=0;//全域變數命名（記數）
for ($g=0; $g <250 ; $g++) {
	$Address=strpos($web[$g],"KEY=",0);
	$output=substr($web[$g],$Address+4,5);
	$link='http://ta.taivs.tp.edu.tw/news/'."news.asp?KEY=".$output."&amp;PageNo=1&amp;board=1&amp;SearchWay=no0";
	$know_link=$know_link." ".$link;
	$sum=$sum+1;
	if($Address==""){
		break;
	}
/*
$g為區域變數 基於學校單一頁面 所存資料數總數為215左右 故限制
$Address是指位址取得   strpos函數為尋$web陣列的"KEY="的所在位址 而"0"是從第幾個數字開始 此分析後面的substr函數會用到
而$output是指取值5個數字  substr函數為 剛才找到的 "KEY="的位址右移四位後（去除 "KEY="） 取得五個數字
基於學校網站的消息資料為'http://ta.taivs.tp.edu.tw/news/'."news.asp?KEY=".自定義五位數."&amp;PageNo=1&amp;board=1&amp;SearchWay=no0"
故可將$output取的5個數字放入 自定義五位數的位置 且學校消息網址使用相對路徑 故前面要先加'http://ta.taivs.tp.edu.tw/news/' 故$link是將資料整合網址檔
$know_link則為將陣列轉成文字（在迴圈下取得） 並以" "作為下次資料分割的判斷
$sum為計數用
if的判斷式 是當未尋找到資料時強制終止處理
*/
}
$temp_link=explode(" ", $know_link);//將資料分回陣列

for ($i=0; ($i+5)<count($list01); $i+=5) {
	$post_text[]=array("name"=>$temp[$i],"class"=>$temp[$i+1],"writer"=>$temp[$i+2],"date"=>$temp[$i+3],"hot"=>$temp[$i+4]);
}
/*
分析$temp的資料 由於當時找到的資料為 是依照: 主題名 文章種類 來源 發文日期 觀看人數 一維陣列排列
故用 for將其轉成二維陣列 使其能自動分類
*/


for ($s=0; $s <$sum ; $s++) { 
	$post_web[]=$temp_link[$s+1];
}
/*
網址的一維陣列產生
"$s+1"是取值的修正
*/

for ($z=0; $z <$sum ; $z++) { 
	if($post_text[$z]==""||$post_web[$z]==""){
		break;
	}
	else{
		$post_output=$post_text[$z]["name"]."<br>".$post_text[$z]["class"]."<br>".$post_text[$z]["writer"]."<br>".$post_text[$z]["date"]."<br>".$post_web[$z]."<br>"."<br>"."\n";
		echo $post_output;
	}
}
/*
輸出結果 主題名+文章種類+來源+發文日期+網址
（由於在app中 無法將手機用戶的點擊次數 加入學校網頁 故學校官網的觀看人數不列入輸出）
if的判斷式 是當未尋找到資料時強制終止處理 有bug 必然顯示一次：
Notice: Undefined offset: 212 in /var/www/html/index8.php on line 68
*/


curl_close($ch);//結束php_curl

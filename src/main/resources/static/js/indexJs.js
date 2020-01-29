/**
 * 
 */
let today = "";
let nowTime = "";
function showClock() {
	   let date = new Date();
	   let year = date.getFullYear();
	   let month = date.getMonth();
	   let day = date.getDate();
	   let nowHour = date.getHours();
	   let nowMin  = date.getMinutes();
	   let nowSec  = date.getSeconds();
	   
	   today = year + "-" + month +1 + "-" + day;
	   nowTime = nowHour + ":" + nowMin + ":" + nowSec;
	   document.getElementById("todayDisp").innerHTML = today;
	   document.getElementById("nowtimeDisp").innerHTML = nowTime;
}
setInterval('showClock()',1000);

$(function(){
	$(".btn-in").on('click', function() {
		if($(".btn-in").text() == "IN"){
				$(".btn-in").text(nowTime);
				let date = today + "," + nowTime;
				$(".btn-in").val(date);
;
			}
	});
	$(".btn-out").on('click', function() {
		if($(".btn-out").text() == "OUT"){
				$(".btn-out").text(nowTime);
				$(".btn-out").val(nowTime);
			}
	});
})
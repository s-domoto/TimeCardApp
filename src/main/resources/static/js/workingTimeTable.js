/**
 * 
 */

$(function(){
	$(".btn-date").on('click', function() {
		if($(".btn-date").text() == "IN"){
				$(".btn-in").text(nowTime);
				let date = today + "," + nowTime;
				$(".btn-in").val(date);
;
			}
	});
	$(".btn-out").on('click', function() {
		if($(".btn-out").text() == "OUT"){
				$(".btn-out").text(nowTime);
				let date = today + "," + nowTime;
				$(".btn-out").val(date);
			}
	});
})
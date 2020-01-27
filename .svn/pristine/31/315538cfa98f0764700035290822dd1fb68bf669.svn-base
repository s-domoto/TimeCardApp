/**
 * 
 */
$(function(){
	$(".submit").on('click', function() {
		let idStr = $(".id").val().toString();
		let passStr = $(".pass").val().toString();
		if(idStr == "" || passStr == ""){
			$(".id").val(idStr);
			$(".pass").val(passStr)
			window.location.href = 'http://localhost:8080/login';
			alert("IDとパスワードを入力してください。");
			return false;
		}
	});
})
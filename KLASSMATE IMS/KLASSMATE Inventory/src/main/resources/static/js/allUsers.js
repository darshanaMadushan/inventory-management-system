$(document).ready(function(){
	$("#selectedUsersDetails").addClass("d-none")
});

$(".btn-warning").on("click", function(){
	var userId = $(this).parent().attr("id");
	var name = $(this).parent().attr("name");
	if(confirm("Do You want to delete this User: " + name + " \nOnce Deleted, Cant' be reversed")) {
		$.ajax({
			type: "DELETE",
			url: "api/deleteUser",
			data: {userId : userId},
			success: function() {
				$("#deleteSuccess").removeClass("d-none").addClass("d-block");
				setTimeout(function(){
					$("#deleteSuccess").removeClass("d-block").addClass("d-none");
				}, 5000);
			},
			error: function (){
				console.log("Error");
				$("#deleteError").removeClass("d-none").addClass("d-block");
				setTimeout(function(){
					$("#deleteError").removeClass("d-block").addClass("d-none");
				}, 5000);
			},
		});
	}
});

$("#submitButton").on("click", function(){
	var username = $("#username").val();
	var name = $("#name").val();
	var designation = $("#designation").val();
	var email = $("#email").val();
	var password = $("#password").val();
	
	if(username !== "" || password !== "" || name !== "" || email !== "" || designation !== "") {
		var user = {
			username : username,
			name : name, 
			designation : designation, 
			email : email,
			password: password,
			role : "ADMIN"
		};
		$.ajax({
			type: "POST",
			url: "api/createUser",
			data: JSON.stringify(user),
			contentType : "application/json",
			success: function() {
				$("#adminSuccess").removeClass("d-none").addClass("d-block");
				setTimeout(function(){
					$("#adminSuccess").removeClass("d-block").addClass("d-none");
				}, 5000);
			},
			error: function() {
				$("#adminError").removeClass("d-none").addClass("d-block");
				setTimeout(function(){
					$("#adminError").removeClass("d-block").addClass("d-none");
				}, 5000);
			}
		});	
	}
});

$("#userSearch").on("keyup", function(){
	var value = $(this).val();
	console.log(value);
});

$("#username").on("change", function(){
	var value = $(this).val();
	$.ajax({
		type: "GET",
		url: "/api/checkUsername",
		data: {username : value},
		success: function(){
			$("#usernameHelp").addClass("text-danger");
			$("#submitButton").attr("disabled", "disabled");
		},
		error: function(){
			$("#usernameHelp").removeClass("text-danger");
			$("#submitButton").attr("disabled", false);
		}
	});
});

$("#repassword").on("change", function(){
	var password = $("#password").val();
	var rePass = $(this).val();
	if(password !== rePass) {
		$("#passwordIncorrect").removeClass("d-none").addClass("d-block");
		$("#submitButton").attr("disabled", "disabled");
	} else {
		$("#passwordIncorrect").removeClass("d-block").addClass("d-none");
		$("#submitButton").attr("disabled", false);
	}
});
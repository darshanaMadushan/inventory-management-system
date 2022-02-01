window.onload = function() {
	document.getElementById("username").focus();
}

function checkPasswords(pass, rePass, error, pageName =null) {
	const errorPara = document.getElementById(error);
	const password = document.getElementById(pass);
	const rePassword = document.getElementById(rePass);
	
	if(pageName === "signup"){
		const signupButton = document.getElementById("signup");
		if(password.value === rePassword.value){
			signupButton.disabled = false;
			errorPara.classList.remove("text-danger");		
			errorPara.classList.add("text-success");
			errorPara.innerText = "Passwords are good";
		} else {
			signupButton.disabled = true;
			errorPara.classList.remove("text-success");
			errorPara.classList.add("text-danger");	
			errorPara.innerText = "Passwords are not compatible.";
		}
		
	} else {
		const updateButton = document.getElementById("updateButton");
		if(password.value === rePassword.value){
			updateButton.disabled = false;
			errorPara.classList.remove("text-danger");		
			errorPara.classList.add("text-success");
			errorPara.innerText = "Passwords are good";
		} else {
			updateButton.disabled = true;
			errorPara.classList.remove("text-success");
			errorPara.classList.add("text-danger");	
			errorPara.innerText = "Passwords are not compatible.";
		}
		
	}
	
}

function updateUserProfile(){
	var userId = $("#userId").val();
	var newEmail = $("#updateEmail").val();
	var newName = $("#updateName").val();
	var newDesignation = $("#updateDesignation").val();
	var newPassword = $("#updatePassword").val();	
	if( newEmail !== "" && newName !== "" && newDesignation !== "") {
		var newPerson = {
			id: userId,
			name: newName,
			email: newEmail,
			password: newPassword,
			designation: newDesignation,
		};
		
		$.ajax({
			type: "PUT",
			url: "/api/updateUser?userId=" + userId,
			contentType: "application/json", 
			data: JSON.stringify(newPerson),
			success: function(data){
				$("#alertsRow").removeClass("d-none").addClass("d-block");
				$("#updateSuccessAlert").removeClass("d-none").addClass("d-block");
				$("#updateErrorAlert").removeClass("d-block").addClass("d-none");
				$("#updateErrorAlert").text("");
				if(data === "password") {
					document.getElementById("updateSuccessAlert").innerHTML += "<strong>You have updated Password. Use newer password when Login</strong>";
				}
				setTimeout(function(){
					$("#alertsRow").removeClass("d-block").addClass("d-none");
					$("#updateSuccessAlert").removeClass("d-block").addClass("d-none");
					location.reload();
					if(data === "password") {
						$("#updateSuccessAlert").last().remove();
					}
				},3000);
			},
			error: function(error){
				console.log(error);
				$("#alertsRow").removeClass("d-none").addClass("d-block");
				$("#updateSuccessAlert").removeClass("d-block").addClass("d-none");
				$("#updateErrorAlert").removeClass("d-none").addClass("d-block");
				$("#updateErrorAlert").text("Request Failed: " +error.responseJSON.error + " - " + error.responseJSON.status);
				setTimeout(function(){
					$("#alertsRow").removeClass("d-block").addClass("d-none");
					$("#updateErrorAlert").text();
				},3000);
			}
		});
	} else {
		alert(`Username, Name, Email, Designation Fields should not be blank..`);
	}
}
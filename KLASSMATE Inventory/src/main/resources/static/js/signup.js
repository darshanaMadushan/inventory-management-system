/*alert("jsfile is working")*/

const password = document.getElementById("password");
const rePassword = document.getElementById("re_pass");
const signupButton = document.getElementById("signup");
const errorPara = document.getElementById("error");
const resetButton = document.getElementById("reset");

function checkPasswords(pass, rePass) {
	if(pass === rePass){
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
}


rePassword.addEventListener("change", function () {
	checkPasswords(password.value, rePassword.value);
});

resetButton.addEventListener("click", function(){
	errorPara.innerText = "";
})
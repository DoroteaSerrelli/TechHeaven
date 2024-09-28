/**
 * 
 */

function validate() {
	if (!validateEmail() || !validateUsername()) return false;
}

function validateEmail() {
	let n = document.forms["client"]["email"].value;
	var pattern = /^\S+@\S+\.\S+$/;

	if (!n.match(pattern)) {
		const element = document.getElementById('errorSession');
		if (element.style.display === 'block') {
			element.style.display = "none";
			element.innerText = "";
		}
		document.getElementById("error").innerHTML = "L’email deve essere scritta nel formato nomeutente@dominio (es. mario.rossi10@gmail.com).";
		error.classList.add("invalid");

		return false;
	} else {
		document.getElementById("error").innerHTML = "";
		error.classList.remove("invalid");

		return true;
	}
}

function validateUsername() {
	let n = document.forms["client"]["username"].value;
	var pattern = "^[a-zA-Z]{5,}$";

	if (!n.match(pattern)) {
		const element = document.getElementById('errorSession');
		if (element.style.display === 'block') {
			element.style.display = "none";
			element.innerText = "";
		}
		document.getElementById("error").innerHTML = "Username o email non valide";
		error.classList.add("invalid");

		return false;

	} else {
		document.getElementById("error").innerHTML = "";
		error.classList.remove("invalid");

		return true;
	}
}

function validateFormPassword(){
	if(!validatePassword) return false;
}

function validatePassword() {
	let n = document.forms["client"]["password"].value;
	var pattern = /^(?=.*[a-zA-Z])(?=.*[0-9]).{5,}$/;

	if (!n.match(pattern)) {
		const element = document.getElementById('errorSession');
		if (element.style.display === 'block') {
			element.style.display = "none";
			element.innerText = "";
		}
		document.getElementById("error").innerHTML = "La password deve avere almeno 5 caratteri che siano lettere e numeri.";
		error.classList.add("invalid");

		return false;

	} else {
		document.getElementById("error").innerHTML = "";
		error.classList.remove("invalid");

		return true;
	}
}


function validate() {
	if (!validateEmail() || !validateName() || !validateSurname() || !validateAddress() || !validatePhoneNumber() || !validatePassword()) return false;
}

function validateEmail() {
	let n = document.forms["client"]["email"].value;
	var pattern = /^\S+@\S+\.\S+$/;
	
	if (!n.match(pattern)) {
		document.getElementById("error").innerHTML = "L’email deve essere scritta nel formato nomeutente@dominio (es. mario.rossi10@gmail.com).";
		error.classList.add("invalid");
		
		return false;
	}else {
		document.getElementById("error").innerHTML = "";
		error.classList.remove("invalid");

		return true;
	}
}

function validateName() {
	let n = document.forms["client"]["name"].value;
	var pattern = /^[A-Za-z\s]+$/;
	
	if (!n.match(pattern)) {
		document.getElementById("error").innerHTML = "Il nome deve contenere solo lettere e, eventualmente, spazi.";
		error.classList.add("invalid");
		
		return false;
		
	}else {
		document.getElementById("error").innerHTML = "";
		error.classList.remove("invalid");

		return true;
	}
}
function validateSurname() {
	let n = document.forms["client"]["surname"].value;
	var pattern = /^[A-Za-z\s]+$/;
	
	if (!n.match(pattern)) {
		document.getElementById("error").innerHTML = "Il cognome deve contenere solo lettere e, eventualmente, spazi.";
		error.classList.add("invalid");
		
		return false;
		
	}else {
		document.getElementById("error").innerHTML = "";
		error.classList.remove("invalid");
		
		return true;
	}
}

function validateAddress() {
	const elements = {
		road: document.getElementById('road'),
		cv: document.getElementById('cv'),
		cap: document.getElementById('cap'),
		province: document.getElementById('province'),
		city: document.getElementById('city')
	};

	const patterns = {
		road: /^[A-Za-z\s]+$/,
		cv: /^(([0-9])|(([0-9]+|\w)(\w|[0-9]+)))$/,
		cap: /^\d{5}$/,
		province: /^[A-Za-z]{2}$/,
		city: /^[A-Za-z\s]+$/
	};

	const errorMessages = {
		road: "La via deve contenere solo lettere e spazi",
		cv: "Il numero civico è composto da numeri e, eventualmente, una lettera",
		cap: "Il CAP è formato da 5 numeri",
		province: "La provincia è composta da due lettere maiuscole",
		city: "La città deve essere composta solo da lettere e spazi"
	};

	let anyInvalid = false;

	for (const field in elements) {
		const element = elements[field];
		const value = element.value.trim(); // Remove leading/trailing spaces
		const errorElement = document.getElementById("error " + field); // Get the error div

		if (!value.match(patterns[field])) {
			anyInvalid = true;
			element.classList.add("invalid");
			element.classList.remove("valid");
			errorElement.textContent = errorMessages[field]; // Display the appropriate error message
			errorElement.classList.add("invalid-message");
			errorElement.classList.remove("valid-message");
		} else {
			element.classList.remove("invalid");
			element.classList.add("valid");
			errorElement.textContent = " "; // Clear error message when input is valid
			errorElement.classList.remove("invalid-message");
			errorElement.classList.add("valid-message");
		}
	}
	return !anyInvalid; // Return true if no fields are invalid, false otherwise
}

function validatePhoneNumber() {
	let n = document.forms["client"]["phoneNumber"].value;
	var pattern = "^[0-9]{3}-[0-9]{3}-[0-9]{4}$";
	
	if (!n.match(pattern)) {
		document.getElementById("error").innerHTML = "Il formato del numero di telefono deve essere xxx-xxx-xxxx";
		error.classList.add("invalid");
		
		return false;
		
	}else{
		document.getElementById("error").innerHTML = "";
		error.classList.remove("invalid");
		
		return true;
	}
}

function validateUsername() {
	let n = document.forms["client"]["username"].value;
	var pattern = "^[a-zA-Z]{5,}$";
	
	if (!n.match(pattern)) {
		document.getElementById("error").innerHTML = "L'username deve avere almeno lunghezza pari a 5 e contenere solo lettere.";
		error.classList.add("invalid");
		
		return false;
		
	}else{
		document.getElementById("error").innerHTML = "";
		error.classList.remove("invalid");
		
		return true;
	}
}

function validatePassword() {
	let n = document.forms["client"]["password"].value;
	var pattern = /^(?=.*[a-zA-Z])(?=.*[0-9]).{5,}$/;
	
	if (!n.match(pattern)) {
		document.getElementById("error").innerHTML = "La password deve avere almeno 5 caratteri che siano lettere e numeri.";
		error.classList.add("invalid");
		
		return false;
		
	}else {
		document.getElementById("error").innerHTML = "";
		error.classList.remove("invalid");

		return true;
	}
}

/**
 * La funzione validateForm() è progettata per verificare se i dati inseriti in un modulo web sono validi prima di inviarlo.
 * All'inizio, si assume che il modulo non sia valido. Solo se tutte le verifiche vanno a buon fine, isValid diventerà true.
 */
function validateForm() {
	var isValid = false;
	
	/**
	 * Se ci sono dei campi nascosti (quelli con ID updateEmail e updateTelefono), vengono svuotati. 
	 * Questi campi potrebbero contenere informazioni precompilate che non devono interferire 
	 * con la nuova immissione.
	 */
	var emailField = document.getElementById('updateEmail');
	if (emailField.style.display === 'none') {
		document.getElementById('email').value = '';
	}

	var phoneField = document.getElementById('updateTelefono');
	if (phoneField.style.display === 'none') {
		document.getElementById('phoneNumber').value = '';
	}

	/**
	 * Se il campo dell'email è visibile (non nascosto), chiama la funzione validateEmail() per controllare se l'indirizzo è corretto.
	 * Numero di telefono: Stessa cosa per il campo del numero di telefono, chiamando la funzione validatePhoneNumber().
	 * Indirizzo: Se presente, verifica anche l'indirizzo con la funzione validateAddress().
	 * */
	
	if (emailField.style.display !== 'none') {
		isValid = validateEmail();

	}

	if (phoneField.style.display !== 'none') {
		isValid = validatePhoneNumber();
	}

	var addressField = document.getElementById('updateAddress');
	if (addressField.style.display !== 'none') {
		isValid = validateAddress();
	}

	if (isValid) {
		document.getElementById("updateInfoForm").submit();
	}
}
function openSidebar() {
  document.getElementById("navbar").style.right = "0";
  
}

function closeSidebar() {
  document.getElementById("navbar").style.right = "-300px";
}


function addInvalidMessage(msg, errorField){
    var errField = document.getElementById(errorField);
    errField.innerHTML=msg;
    errField.style.display= "block";
    errField.style.color= "red";
    errField.classList.remove("valid");
    errField.classList.add("invalid");	
}

function removeInvalidMessage(errorField){
    var errField = document.getElementById(errorField);
    errField.style.display= "none";
    errField.classList.remove("invalid");									
    errField.classList.add("valid");	
}
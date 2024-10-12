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

function filterProducts() {
    // Get the value from the input field and convert it to lowercase
    var input = document.getElementById("productFilter");
    var filter = input.value.toLowerCase();
    
    // Get the table and all its rows
    var table = document.getElementById("showpr");
    var tr = table.getElementsByTagName("tr");

    // Loop through all table rows (starting from the second row because the first is the header)
    for (var i = 1; i < tr.length; i++) {
        // Get the product name from the third column (index 2)
        var td = tr[i].getElementsByClassName("productName")[0];
        if (td) {
            var txtValue = td.textContent || td.innerText;
            // If the product name contains the filter text, display the row, otherwise hide it
            if (txtValue.toLowerCase().indexOf(filter) > -1) {
                tr[i].style.display = "";
            } else {
                tr[i].style.display = "none";
            }
        }       
    }
}
function redirectToRolePage(selectElement) {
    var selectedRole = selectElement.value;
    switch (selectedRole) {
        case 'Cliente':
            window.location.href = '/AreaRiservata.jsp';
            break;
        case 'GestoreOrdini':
            window.location.href = 'protected/gestoreOrdini/GestioneOrdini.jsp';
            break;
        case 'GestoreCatalogo':
            window.location.href = 'protected/gestoreCatalogo/GestioneCatalogo.jsp';           
        // Add more cases for other roles as needed
        default:
            // Handle default case or unknown roles
            break;
    }
}

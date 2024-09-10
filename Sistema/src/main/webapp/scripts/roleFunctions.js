function redirectToRolePage(selectElement) {
    var selectedRole = selectElement.value;
    var contextPath = window.location.pathname.substring(0, window.location.pathname.indexOf("/",2));
    console.log('Context Path:', contextPath); // Debug line
    console.log('Selected Role:', selectedRole); // Debug line
    switch (selectedRole) {
        case 'Cliente':
            console.log('Redirecting to Cliente'); // Debug line
            window.location.assign(contextPath + '/AreaRiservata');
            break;
        case 'GestoreOrdini':
            console.log('Redirecting to GestoreOrdini'); // Debug line
            window.location.assign(contextPath + '/GestioneOrdini');
            break;
        case 'GestoreCatalogo':
            console.log('Redirecting to GestoreCatalogo'); // Debug line
            window.location.assign(contextPath + '/GestioneCatalogo');
            break;
        // Add more cases for other roles as needed
        default:
            // Handle default case or unknown roles
            console.log('Unknown role'); // Debug line
            break;
    }
}
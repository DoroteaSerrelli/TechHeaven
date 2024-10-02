/* 
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/JavaScript.js to edit this template
 */
function moveToSidebar(selectedBoxId, formId) {
    const sidebar = document.getElementById('options_sidebar');
    const mainContent = document.getElementById('mainContent');
    const form = document.getElementById(formId);
    const centerMenu = document.getElementById('centerMenu');
    const boxes = centerMenu.querySelectorAll('.fe-box');
    
    // Hide the center menu
    centerMenu.style.display = 'none';
    
    // Show the sidebar and main content
    sidebar.classList.remove('hidden');
    sidebar.classList.add('visible');
    mainContent.classList.add('visible');
    
      // Move all boxes to the sidebar
    boxes.forEach(box => {
        sidebar.appendChild(box);
    });

    // Highlight the selected option and remove highlight from others
    const allBoxes = sidebar.querySelectorAll('.fe-box');
    allBoxes.forEach(box => {
        box.classList.remove('selected');
    });
    document.getElementById(selectedBoxId).classList.add('selected');
    
    // Show the relevant form in the main content area
    const forms = document.querySelectorAll('.form-section');
    forms.forEach(f => {
        f.classList.add('hidden');
    });
    form.classList.remove('hidden');
}

function toggleSidebar(){
    var options_sidebar = document.getElementById("options_sidebar"); 
    if(options_sidebar.classList.contains("visible")){
        options_sidebar.classList.remove("visible");         
    }
    else{
        options_sidebar.classList.add("visible");
    }
 }
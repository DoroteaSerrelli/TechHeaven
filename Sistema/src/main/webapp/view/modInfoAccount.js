// Add event listener to the toggle button
    const toggleButton = document.getElementById('editInfoButton');
    const editEmailInput = document.getElementById('editEmailInput');
    const editPhoneInput = document.getElementById('editPhoneInput');
    const editConfirmInput = document.getElementById('editConfirmInput');

    toggleButton.addEventListener('click', () => {
      editEmailInput.style.display = editEmailInput.style.display === 'none' ? 'block' : 'none';
      editPhoneInput.style.display = editPhoneInput.style.display === 'none' ? 'block' : 'none';
      editConfirmInput.style.display = editConfirmInput.style.display === 'none' ? 'block' : 'none';
    });
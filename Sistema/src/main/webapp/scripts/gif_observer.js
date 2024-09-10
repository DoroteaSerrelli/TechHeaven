// Get all GIF container elements
const gifContainers = document.querySelectorAll('.icon');

// Swap image on mouse hover for each GIF container
gifContainers.forEach(container => {
  const staticImage = container.querySelector('.static-image');
  const gifImage = container.querySelector('.gif-image');
  gifImage.style.display = 'none';
  
  container.addEventListener('mouseenter', () => {
    gifImage.style.display = 'block';
    staticImage.style.display = 'none';
  });
  
  container.addEventListener('mouseleave', () => {
    gifImage.style.display = 'none';
    staticImage.style.display = 'block';
  });
});
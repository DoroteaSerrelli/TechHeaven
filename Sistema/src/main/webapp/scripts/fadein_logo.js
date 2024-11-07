// Get the GIF container element
const pngImageContainer = document.querySelector('.footer .logo img');

// Function to start PNG animation when scrolled into view
function startPngAnimation() {
  pngImageContainer.classList.add('animate-png');
}
// Function to check if the GIF container is in view
function isInView(element) {
  const rect = element.getBoundingClientRect();
  return (
    rect.top >= 0 &&
    rect.left >= 0 &&
    rect.bottom <= (window.innerHeight || document.documentElement.clientHeight) &&
    rect.right <= (window.innerWidth || document.documentElement.clientWidth)
  );
}

// Event listener to trigger animation when scrolled into view
window.addEventListener('scroll', () => {
  if (isInView(pngImageContainer)) {
    startPngAnimation();
  }
});
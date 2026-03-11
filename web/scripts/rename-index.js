const fs = require('fs');
const path = require('path');

const distDir = path.join(__dirname, '..', 'dist', 'lead-web', 'browser');
const csrIndex = path.join(distDir, 'index.csr.html');
const index = path.join(distDir, 'index.html');

try {
  if (fs.existsSync(csrIndex)) {
    fs.copyFileSync(csrIndex, index);
    console.log('Copied index.csr.html to index.html');
  } else {
    console.log('index.csr.html not found; nothing to do');
  }
} catch (err) {
  console.error('Error copying index file:', err);
  process.exit(1);
}

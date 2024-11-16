const express = require('express');
const fs = require('fs');
const path = require('path');
const cors = require('cors');
const app = express();

// Enable CORS for all origins
app.use(cors());
app.use(express.json());

// Serve static files from the "public" directory
app.use(express.static(path.join(__dirname, 'public')));

// Ensure the data folder exists
const dataDir = path.join(__dirname, 'data');
if (!fs.existsSync(dataDir)) {
    fs.mkdirSync(dataDir);
}

// Route to fetch data from a file based on the tab name
app.get('/view-data', (req, res) => {
    const { tab } = req.query;

    // Define file paths for each tab
    const filePaths = {
        clients: path.join(dataDir, 'clients.txt'),
        AdmWorkHours: path.join(dataDir, 'AdmWorkHours.txt'),
        video: path.join(dataDir, 'video.txt'),
    };

    // Check if the tab exists
    if (!filePaths[tab]) {
        return res.status(400).json('Invalid tab name');
    }

    // Read the file and send back its content
    fs.readFile(filePaths[tab], 'utf8', (err, data) => {
        if (err) {
            return res.status(500).json('Error reading file');
        }
        res.json(data || 'No data available');  // Return file content or a fallback message
    });
});

// Start the server
const port = 3000;
app.listen(port, () => {
    console.log(`Server is running on http://localhost:${port}`);
});






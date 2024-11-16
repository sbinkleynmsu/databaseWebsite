const express = require('express');
const fs = require('fs');
const path = require('path');
const cors = require('cors');
const app = express();

// Enable CORS for all origins
app.use(cors());

// Serve static files from the "public" directory
app.use(express.static(path.join(__dirname, 'public')));

// Your existing code for the '/save-data' route
app.use(express.json());

const dataDir = path.join(__dirname, 'data');
if (!fs.existsSync(dataDir)) {
    fs.mkdirSync(dataDir);
}

app.post('/save-data', (req, res) => {
    const { tab, data } = req.body;

    const filePaths = {
        clients: path.join(dataDir, 'clients.txt'),
        AdmWorkHours: path.join(dataDir, 'AdmWorkHours.txt'),
        video: path.join(dataDir, 'video.txt'),
    };

    if (!filePaths[tab]) {
        return res.status(400).send('Invalid tab');
    }

    const dataString = JSON.stringify(data) + '\n';

    fs.appendFile(filePaths[tab], dataString, (err) => {
        if (err) {
            return res.status(500).send('Failed to save data');
        }
        res.send('Data saved successfully');
    });
});

// Start the server
const port = 3000;
app.listen(port, () => {
    console.log(`Server is running on http://localhost:${port}`);
});





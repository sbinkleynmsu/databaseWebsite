const express = require('express');
const mysql = require('mysql2');
const bodyParser = require('body-parser');

// Create an Express application
const app = express();
const port = 3000;

// Parse incoming request bodies in JSON format
app.use(bodyParser.json());

// Set up MySQL connection
const db = mysql.createConnection({
  host: 'localhost',          // Hostname (usually localhost)
  user: 'root',      // Replace with your MySQL username
  password: '218023.Copiper$1',  // Replace with your MySQL password
  database: 'abcmediabinkleypfeiffer',  // The name of your database
  port: 3301
});

// Establish the connection
db.connect((err) => {
  if (err) {
    console.error('Error connecting to the database:', err);
    process.exit(1); // Exit if there is an error connecting to the database
  } else {
    console.log('Connected to the MySQL database.');
  }
});

// Example route to insert a new display
app.post('/displays', (req, res) => {
  const { serialNo, schedulerSystem, modelNo } = req.body;

  // Insert query
  const query = 'INSERT INTO DigitalDisplay (serialNo, schedulerSystem, modelNo) VALUES (?, ?, ?)';

  db.query(query, [serialNo, schedulerSystem, modelNo], (err, result) => {
    if (err) {
      console.error('Error inserting display:', err);
      res.status(500).send('Failed to insert digital display.');
    } else {
      console.log('Display inserted:', result);
      res.status(200).send('Digital display inserted successfully.');
    }
  });
});

// Route to fetch all digital displays
app.get('/displays', (req, res) => {
  const query = 'SELECT * FROM DigitalDisplay';

  db.query(query, (err, results) => {
    if (err) {
      console.error('Error fetching displays:', err);
      res.status(500).send('Failed to fetch digital displays.');
    } else {
      res.status(200).json(results);
    }
  });
});

// Route to search digital displays by scheduler system
app.get('/displays/search', (req, res) => {
  const { schedulerSystem } = req.query;

  const query = 'SELECT * FROM DigitalDisplay WHERE schedulerSystem = ?';

  db.query(query, [schedulerSystem], (err, results) => {
    if (err) {
      console.error('Error searching displays:', err);
      res.status(500).send('Failed to search digital displays.');
    } else {
      res.status(200).json(results);
    }
  });
});

// Route to delete a digital display by serial number
app.delete('/displays/:serialNo', (req, res) => {
  const { serialNo } = req.params;

  const query = 'DELETE FROM DigitalDisplay WHERE serialNo = ?';

  db.query(query, [serialNo], (err, result) => {
    if (err) {
      console.error('Error deleting display:', err);
      res.status(500).send('Failed to delete digital display.');
    } else {
      res.status(200).send(`Digital display with serialNo ${serialNo} deleted successfully.`);
    }
  });
});

// Route to update a digital display by serial number
app.put('/displays/:serialNo', (req, res) => {
  const { serialNo } = req.params;
  const { schedulerSystem, modelNo } = req.body;

  const query = 'UPDATE DigitalDisplay SET schedulerSystem = ?, modelNo = ? WHERE serialNo = ?';

  db.query(query, [schedulerSystem, modelNo, serialNo], (err, result) => {
    if (err) {
      console.error('Error updating display:', err);
      res.status(500).send('Failed to update digital display.');
    } else {
      res.status(200).send(`Digital display with serialNo ${serialNo} updated successfully.`);
    }
  });
});

// Start the server
app.listen(port, () => {
  console.log(`Server running on http://localhost:${port}`);
});

const path = require('path');
app.use(express.static(path.join(__dirname, 'public')));

  








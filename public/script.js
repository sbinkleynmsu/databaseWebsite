// Wait for the document to be ready before adding event listeners
document.addEventListener('DOMContentLoaded', function() {
    const form = document.getElementById('insertDisplayForm'); // Get the form
    const resultContainer = document.getElementById('resultContainer'); // Div for result message (optional)

    form.addEventListener('submit', function(event) {
        event.preventDefault(); // Prevent the default form submission

        const serialNo = document.getElementById('serialNo').value;
        const schedulerSystem = document.getElementById('schedulerSystem').value;
        const modelNo = document.getElementById('modelNo').value;

        // Prepare the data to be sent to the server
        const formData = {
            serialNo,
            schedulerSystem,
            modelNo
        };

        // Send a POST request to the server with the form data
        fetch('/displays', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(formData)
        })
        .then(response => response.text())
        .then(data => {
            // Display the response from the server (success or error message)
            displayMessage(data, resultContainer);
        })
        .catch(error => {
            console.error('Error:', error);
            displayMessage('An error occurred while inserting the display.', resultContainer, 'error');
        });
    });

    // Function to display success or error messages
    function displayMessage(message, container, type = 'success') {
        // Clear any previous messages
        container.innerHTML = '';

        // Create a new message element
        const messageElement = document.createElement('div');
        messageElement.classList.add(type);
        messageElement.innerText = message;

        // Append the message to the result container
        container.appendChild(messageElement);
    }
});


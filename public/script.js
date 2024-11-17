document.addEventListener('DOMContentLoaded', function () {
    const resultContainer = document.getElementById('resultsContent');

    // Handle Insert Form
    document.getElementById('insertForm').addEventListener('submit', function (event) {
        event.preventDefault();

        const serialNo = document.getElementById('insertSerialNo').value;
        const schedulerSystem = document.getElementById('insertSchedulerSystem').value;
        const modelNo = document.getElementById('insertModelNo').value;

        fetch('/displays', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ serialNo, schedulerSystem, modelNo }),
        })
            .then((res) => res.json())
            .then((data) => {
                displayResults([data], resultContainer, 'Inserted Display');
            })
            .catch((err) => {
                console.error(err);
                resultContainer.innerHTML = '<div class="error">Failed to insert display.</div>';
            });
    });

    // Handle Search Form
    document.getElementById('searchForm').addEventListener('submit', function (event) {
        event.preventDefault();

        const schedulerSystem = document.getElementById('searchSchedulerSystem').value;

        fetch(`/displays/search?schedulerSystem=${encodeURIComponent(schedulerSystem)}`)
            .then((res) => res.json())
            .then((data) => {
                if (data.length > 0) {
                    displayResults(data, resultContainer, `Displays for Scheduler: ${schedulerSystem}`);
                } else {
                    resultContainer.innerHTML = '<div class="error">No results found.</div>';
                }
            })
            .catch((err) => {
                console.error(err);
                resultContainer.innerHTML = '<div class="error">Search failed.</div>';
            });
    });

    // Handle Delete Form
    document.getElementById('deleteForm').addEventListener('submit', function (event) {
        event.preventDefault();

        const serialNo = document.getElementById('deleteSerialNo').value;

        fetch(`/displays/${serialNo}`, {
            method: 'DELETE',
        })
            .then((res) => res.text())
            .then((data) => {
                resultContainer.innerHTML = `<div class="success">${data}</div>`;
            })
            .catch((err) => {
                console.error(err);
                resultContainer.innerHTML = '<div class="error">Failed to delete display.</div>';
            });
    });

    // Function to display results dynamically
    function displayResults(data, container, title) {
        container.innerHTML = `<h3>${title}</h3>`;
        const list = document.createElement('ul');

        data.forEach((item) => {
            const listItem = document.createElement('li');
            listItem.textContent = JSON.stringify(item, null, 2);
            list.appendChild(listItem);
        });

        container.appendChild(list);
    }
});



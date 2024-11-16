document.addEventListener("DOMContentLoaded", () => {
    const tabs = document.querySelectorAll(".tabs button");
    const tabContents = document.querySelectorAll(".tab-content");

    tabs.forEach(tab => {
        tab.addEventListener("click", () => {
            // Remove active class from all tabs and contents
            tabs.forEach(t => t.classList.remove("active"));
            tabContents.forEach(content => content.classList.remove("active"));

            // Add active class to clicked tab and corresponding content
            tab.classList.add("active");
            document.getElementById(tab.getAttribute("data-tab")).classList.add("active");
        });
    });

    // Activate the first tab and content by default
    tabs[0].click();

    // Handle the form submission for View Data tab
    document.getElementById('viewData-form').addEventListener('submit', (e) => {
        e.preventDefault();

        const tabName = document.getElementById('tabName').value.trim();

        // Fetch data based on the entered tab name
        fetch(`http://localhost:3000/view-data?tab=${tabName}`)
            .then(response => response.json())
            .then(data => {
                // Display the fetched data
                document.getElementById('dataOutput').textContent = data;
            })
            .catch(error => {
                console.error('Error fetching data:', error);
                document.getElementById('dataOutput').textContent = 'Error fetching data.';
            });
    });
});


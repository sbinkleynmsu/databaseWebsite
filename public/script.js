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

    // Handle form submissions for each tab
    const handleFormSubmit = (formId, tabName) => {
        const form = document.getElementById(formId);
        form.addEventListener('submit', (e) => {
            e.preventDefault();
        
            const formData = new FormData(form);
            const data = Object.fromEntries(formData.entries());
        
            fetch('/save-data', { // Assuming your server has a route '/save-data'
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({ tab: tabName, data: data }),
            })
            .then(response => response.text())
            .then(message => {
                alert(message);
            })
            .catch(error => {
                console.error('Error:', error);
            });
        });
    };

    // Initialize form submit handlers for each tab
    handleFormSubmit('clients-form', 'clients');
    handleFormSubmit('AdmWorkHours-form', 'AdmWorkHours');
    handleFormSubmit('products-form', 'video');
});



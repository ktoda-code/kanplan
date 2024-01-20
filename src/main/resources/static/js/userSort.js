"use strict";

document.getElementById('tableContainer').addEventListener('click',
    function (event) {
        if (event.target.matches('#idHeader, #emailHeader, #createdHeader')) {
            sortBy(event.target.id);
        }
    });

function sortBy(column) {
    let header = document.getElementById(column);
    const sortDir = header.getAttribute('data-sortdir');
    const sortBy = header.getAttribute('data-sortby');
    const newSortDir = sortDir === 'ASC' ? 'DESC' : 'ASC';

    const urlParams = new URLSearchParams(window.location.search);
    let currentPage = urlParams.get('pageNumber') || 0;
    currentPage = parseInt(currentPage, 10);
    const pageElements = urlParams.get('pageElements') || 3;

    // console.log(`SortBy: [${sortBy},${sortDir},${currentPage},${pageElements},${column}]`)
    fetchTable(sortBy, newSortDir, currentPage, pageElements, column);
}

function updatePaginationControls(sortBy, sortDir, pageElements, totalPages, currentPage) {
    const paginationContainer = document.querySelector('.pagination');
    paginationContainer.innerHTML = ''; // Clear existing pagination

    // 'Previous' link
    const prevItem = document.createElement('li');
    prevItem.className = `page-item ${currentPage === 0 ? 'disabled' : ''}`;
    const prevLink = document.createElement('a');
    prevLink.className = 'page-link';
    prevLink.href = '#';
    prevLink.textContent = 'Previous';
    prevLink.onclick = () => currentPage > 0 && fetchTable(sortBy, sortDir, currentPage - 1, pageElements);
    prevItem.appendChild(prevLink);
    paginationContainer.appendChild(prevItem);

    // Numbered page links
    for (let i = 0; i < totalPages; i++) {
        const pageLink = document.createElement('a');
        pageLink.className = 'page-link';
        pageLink.href = '#';
        pageLink.textContent = i + 1;
        pageLink.onclick = () => fetchTable(sortBy, sortDir, i, pageElements);

        const listItem = document.createElement('li');
        listItem.className = `page-item ${i === currentPage ? 'active' : ''}`;
        listItem.appendChild(pageLink);

        paginationContainer.appendChild(listItem);
    }

    // 'Next' link
    const nextItem = document.createElement('li');
    nextItem.className = `page-item ${currentPage === totalPages - 1 ? 'disabled' : ''}`;
    const nextLink = document.createElement('a');
    nextLink.className = 'page-link';
    nextLink.href = '#';
    nextLink.textContent = 'Next';
    nextLink.onclick = () => currentPage < totalPages - 1 && fetchTable(sortBy, sortDir, currentPage + 1, pageElements);
    nextItem.appendChild(nextLink);
    paginationContainer.appendChild(nextItem);
}

function changePageElements(pageElements) {
    const urlParams = new URLSearchParams(window.location.search);
    const sortBy = urlParams.get('sortBy') || 'id'; // Default to 'id' if not present
    const sortDir = urlParams.get('sortDir') || 'ASC'; // Default to 'ASC' if not present
    const currentPage = urlParams.get('pageNumber') || 0; // Default to 0 if not present

    fetchTable(sortBy, sortDir, currentPage, pageElements);
}

function changePageElementsCustom() {
    const customPageElements = parseInt(document.getElementById('customPageElements').value, 10);
    if (customPageElements > 0) {
        changePageElements(customPageElements);
    } else {
        changePageElements(3);
    }
}

function fetchTable(sortBy, sortDir, currentPage, pageElements, column = null) {
    fetch(`/users?sortBy=${sortBy}&sortDir=${sortDir}&pageNumber=${currentPage}&pageElements=${pageElements}`, {
        headers: {
            'X-Requested-With': 'XMLHttpRequest'
        }
    })
        .then(response => {
            const totalPages = parseInt(response.headers.get('X-Total-Pages'), 10);
            return Promise.all([response.text(), totalPages]);
        })
        .then(([table, totalPages]) => {
            document.getElementById('tableContainer').innerHTML = table;

            if (column) {
                const header = document.getElementById(column);
                header.setAttribute('data-sortdir', sortDir);
            }

            // Update pagination controls
            updatePaginationControls(sortBy, sortDir, pageElements, totalPages, currentPage);
        })
        .catch(error => console.error('Error:', error));
}
var db = require("./db-management");
var express = require('express');
var cors = require('cors');
var server = express();

// Enabling CORS (All requests)
server.use(cors())

// Logging
server.use((req, res, next) => {
    console.log(req.method + " " + req.url)
    next();
});

// Setting up GET request handlers
server.get('/api/books', handleGetBooks);
server.get('/api/books/:id', handleGetBookById);

// Middleware for returning 400 status for wrong requests
server.use((req, res) => {
    return res.status(404).send();
});

// Start the server listening on port 8080
server.listen(8080);
console.log('Node server is running on port 8080');

/** Handles GET books. By default returns paginated list of all books. Also can be used for searching.
 * 
 * @urlParam {string} search optional Search books based on title, author or ISBN.
 * @urlParam {int} page optinal Defaults to 1. Used to for choosing the page to return.
 * 
 */
async function handleGetBooks(req, res) {
    // Get all books if search query is empty, else get filtered data based on the search query
    if (req.query.search == null || req.query.search === '') {
        var data = await db.getAllBooks();
    } else {
        var data = await db.searchAllBooks(req.query.search);
    }

    // Set total number of books in the data
    let total_count = data.length

    // Pagination
    if (req.query.page == null || req.query.page === '') {
        req.query.page = "1";
    }
    let page = parseInt(req.query.page);
    let per_page = 12;
    let paginatedData = paginate(data, page, per_page);
    let page_count = Math.ceil(total_count / per_page);

    // Prepare return data
    let returnData = {
        page: page,
        per_page: per_page,
        page_count: page_count,
        total_count: total_count,
        data: paginatedData,
    };
    
    res.json(returnData);
}

/** Gets a book by ID. 
 * 
 * @urlParam id required ID of a book.
 * 
*/
async function handleGetBookById(req, res) {
    var data = await db.getBookById(req.params.id);

    // Prepare return data
    let returnData = {
        data: data
    };

    // give error if the book does not exist
    if (returnData.data.length === 0) {
        res.status(404).send(`Book with id=${req.params.id} not found`);
        return;
    }

    res.json(returnData);
}

// Splits data into pages and returns required page
function paginate(jsonData, page, per_page) {
    startIndex = (page - 1) * per_page;
    endIndex = page * per_page
    let paginated = jsonData.slice(startIndex, endIndex);

    return paginated;
}
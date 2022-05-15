// Import the mysql module
var mysql = require('mysql2/promise');

// Create a connection object with the user details
var connectionPool = mysql.createPool({
    connectionLimit: 1,
    host: "localhost",
    user: "root",
    password: "",
    database: "price_comparison",
    debug: false
});

// Gets all books from the database and returns in pre-processed format
module.exports.getAllBooks = async () => {
    let sqlQuery = `SELECT book.id as book_id, book.title, book.author, format.id as format_id, format.name as format_name, format.isbn, format.image_url, comparison.id as comparison_id, price, url
        FROM book
        INNER JOIN format ON format.book_id = book.id
        INNER JOIN comparison ON comparison.format_id = format.id;`

    // Get the data from database
    let [sqlData, sqlFields] = await connectionPool.query(sqlQuery);

    // Process the data
    let processedData = processSqlData(sqlData);

    return processedData;
};

// Gets books based on the search query and returns in pre-processed format
module.exports.searchAllBooks = async (searchQuery) => {
    // escape to avoid different issues such as SQL injection
    let escapedSearchQuery = mysql.escape(`%${searchQuery}%`);

    var sqlQuery = `SELECT book.id as book_id, book.title, book.author, format.id as format_id, format.name as format_name, format.isbn, format.image_url, comparison.id as comparison_id, price, url
    FROM book
    INNER JOIN format ON format.book_id = book.id
    INNER JOIN comparison ON comparison.format_id = format.id
    WHERE (book.title LIKE ${escapedSearchQuery}) OR (book.author LIKE ${escapedSearchQuery}) OR (format.isbn LIKE ${escapedSearchQuery});`

    // Get the data from database
    let [sqlData, sqlFields] = await connectionPool.query(sqlQuery);

    // Process the data
    let processedData = processSqlData(sqlData);

    return processedData;
};

// Get a book by ID and returns in pre-processed format
module.exports.getBookById = async (bookId) => {
    // escape to avoid different issues such as SQL injection
    let escapedBookID = mysql.escape(bookId);

    // Get the data from database
    let sqlQuery = `SELECT book.id as book_id, book.title, book.author, format.id as format_id, format.name as format_name, format.isbn, format.image_url, comparison.id as comparison_id, price, url
    FROM book
    INNER JOIN format ON format.book_id = book.id
    INNER JOIN comparison ON comparison.format_id = format.id
    WHERE book.id = ${escapedBookID};`

    // Get the data from database
    let [sqlData, sqlFields] = await connectionPool.query(sqlQuery);

    // Process the data
    let processedData = processSqlData(sqlData);

    return processedData;
};

// Converts data from SQL database into JSON friendly format
function processSqlData(sqlData) {
    let processedData = [];
    for (book of sqlData) {
        let bookFound = false;
        for (processedBook of processedData) {
            // check if similar book exists
            if (book['book_id'] === processedBook['book_id']) {
                bookFound = true;

                // check if the similar format exists
                let formatFound = false;
                for (processedBookFormat of processedBook['formats']) {
                    if (book['isbn'] === processedBookFormat['isbn']) {
                        formatFound = true;

                        // check if similar comparison exists
                        let comparisonFound = false;
                        for (processedBookFormatComaprison of processedBookFormat['comparisons']) {
                            if (book['url'] == processedBookFormatComaprison['url']) {
                                comparisonFound = true;
                                // no need to change anything
                            }
                        }

                        // if similar comparison not found
                        if (!comparisonFound) {
                            processedBookFormat['comparisons'].push(
                                {
                                    price: book['price'],
                                    url: book['url'],
                                }
                            )
                        }
                    }
                }

                // if similar format not found
                if (!formatFound) {
                    processedBook['formats'].push(
                        {
                            name: book['format_name'],
                            isbn: book['isbn'],
                            image_url: book['image_url'],
                            comparisons: [
                                {
                                    price: book['price'],
                                    url: book['url'],
                                }
                            ],
                        }
                    )
                }
            }
        }

        // if similar book not found
        if (!bookFound) {
            processedData.push(
                {
                    book_id: book['book_id'],
                    title: book['title'],
                    author: book['author'],
                    formats: [
                        {
                            name: book['format_name'],
                            isbn: book['isbn'],
                            image_url: book['image_url'],
                            comparisons: [
                                {
                                    price: book['price'],
                                    url: book['url'],
                                }
                            ],
                        }
                    ]
                }
            );
        }
    }

    return processedData;
}
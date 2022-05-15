// Importing server and database manager
var server = require("../server");
var db = require("../db-management");

// Importing chai
var chai = require("chai");
var chaiHttp = require("chai-http");
var assert = require("chai").assert;
var expect = require("chai").expect;
chai.use(chaiHttp);

// base url of the server, required for test requests
var serverBaseUrl = "http://localhost:8080";

// Testing server
describe('Server', () => {

    describe('Check database is not empty', () => {
        it("database should not be empty", async () => {
            let allBooks = await db.getAllBooks();
            assert.isAbove(allBooks.length, 0);
        });
    });

    describe('Check handleGetBooks', () => {
        it("Should return response", (done) => {
            chai.request(serverBaseUrl).get("/api/books").end((err, res) => {
                expect(res).to.have.status(200);
                done();
            });
        });

        it("Response data should not be empty", (done) => {
            chai.request(serverBaseUrl).get("/api/books").end((err, res) => {
                assert.notEqual(res.body.data.length, 0);
                done();
            });
        });
    });

    describe('Check handleGetBooks with search', () => {
        it("Should return response", (done) => {
            chai.request(serverBaseUrl).get("/api/books").query({search: 'Test',}).end((err, res) => {
                expect(res).to.have.status(200);
                done();
            });
        });
    });

    describe('Check handleGetBookById', () => {
        it("Should return 404 when non-existent book is called", (done) => {
            chai.request(serverBaseUrl).get("/api/books/9999999999999999999999").end((err, res) => {
                expect(res).to.have.status(404);
                done();
            });
        });
    });
});
const app = new Vue({
    el: '#app',
    data: {
        sitename: "Price Comparison Website",
        searchQuery: "",
        loadedData: [],
        showBookPage: false,

        // Data to display on book page
        bookPageInfo: {
            selectedFormat: {},
            data: {},
        },
    },
    methods: {
        loadAllBooks(){
            fetch("http://localhost:8080/api/books").then(response => response.json()).then(
                data => {
                    this.loadedData = data;
                });
        },

        searchBooks(){
            this.showBookPage = false;
            this.loadedData = [];

            fetch("http://localhost:8080/api/books?search=" + this.searchQuery).then(response => response.json()).then(
                data => {
                    this.loadedData = data;
                });
        },
        getBook(book_id){
            this.showBookPage = true;
            this.loadedData = [];

            fetch("http://localhost:8080/api/books/" + book_id).then(response => response.json()).then(
                data => {
                    this.loadedData = data;
                    this.bookPageInfo.data = data.data[0];
                    this.bookPageInfo.selectedFormat = this.bookPageInfo.data.formats[0];
                });
        },
        goToPage(page){
            this.showBookPage = false;
            this.loadedData = [];

            fetch("http://localhost:8080/api/books?page=" + page).then(response => response.json()).then(
                data => {
                    this.loadedData = data;
                });
        },
        // method to parse hostname from url
        getUrlHostname(url){
            return new URL(url).hostname;
        },
    },
    computed: {
        
    },
    created(){
        // Called when page is loaded
        this.loadAllBooks();
    }
})
# BOOK-STORE
## Book Store Application

### DB Tables
![DB Image 1](https://github.com/qametmammadli/book-store/blob/master/book-store-DATABASE.PNG)

## Tech Stack
- Maven
- Java 11
- Spring BOOT (Web, Security, Data JPA)
- JPA Specification
- MySQL 5.7
- Liquibase
- JUnit & Mockito
- ModelMapper
- Apache Kafka
- Docker
- Kubernetes, Helm


## Installation and Running
##### After Cloning project, Open your favorite Terminal in root directory and run these commands.
 First step:
 
    mvn clean install
 
 Second step:
    
    docker build -t book-store-app:0.0.1 .
 Third step (can be need run again, first time mysql could't be installed yet):

    docker-compose up
    
## Postman Collections to import [click here](https://github.com/qametmammadli/book-store/blob/master/Book%20Store.postman_collection.json)
#### By importing `Book Store.postman_collection.json` file into Postman (which located in root directory),  You can quickly test an application 

##### default base url: 
      
      localhost:8002/api/book_store
     
## REST API
##### When Application running, firstly by default Admin user will be created AS ADMIN Role

### Features

- User can be registered
- Admin can add publisher role to registered users
- Publisher can add authors, their books and edit books (he or she published)
  * Note: for adding a book, firstly need to add an author 
- Publisher and User can search specific book by filter

### Authentication
##### Get access_token to send other requests
`POST {{base_url}}/authentication`

``` 
{
    "username":"admin",
    "password":"admin1"
}
```      

### User APIs

| Request | About |
| ------ | ----------- |
| `POST /users`  | Create user. |
| `GET /users` | Get all users. |
| `PUT /users/{userId}/add_publisher_role`| Add publisher role to user (Only Admin can add) |
| `DELETE /users/{userId}` | Delete user. |


### Book Author APIs

| Request | About |
| ------ | ----------- |
| `POST /authors`  | Create book author. |
| `GET /authors` | Get all book authors. |
| `DELETE /authors/{authorId}` | Delete author. |

### Book APIs

| Request | About |
| ------ | ----------- |
| `POST /books`  | Create book. |
| `GET /books` | Get all books. |
| `GET /books/{bookId}` | Get one book by id. |
| `GET /books/published_by_me` | Get books published by me. |
| `GET /books/by_publisher/{userId}` | Get books by publisher. |
| `GET /books/filter?` | Get books by filter (book_name, author_name, price, publisher_name, book_description) |
| `PUT /books/{bookId}`| Edit book (Only that book created by publisher himself or herself) |
| `DELETE /books/{bookId}` | Delete book. |
    


    


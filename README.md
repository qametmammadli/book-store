# BOOK-STORE
## Book Store Application

### DB Tables
![DB Image 1](https://github.com/qametmammadli/book-store/blob/master/book-store-DATABASE.PNG)

## Tech Stack
- Maven
- Java 11
- Spring BOOT (Web, Security, Data JPA)
- JPA Specification
- Docker
- MySQL 5.7
- Liquibase
- JUnit & Mockito
- ModelMapper


## Installation and Running
##### After Cloning project, Open your favorite Terminal in root directory and run these commands.
 First step:

    docker build -t book-store-app:0.0.1 .
 Second step:

    docker-compose up
    
## Postman Collections to import [click here](https://github.com/qametmammadli/book-store/blob/master/Book%20Store.postman_collection.json)
#### By importing `Book Store.postman_collection.json` file into Postman (which located in root directory),  You can quickly test an application 

##### default base url: 
      
      localhost:8002/api
     
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

    


    


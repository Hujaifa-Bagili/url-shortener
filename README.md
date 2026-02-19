# URL Shortener

A REST API built with Spring Boot that shortens long URLs.

## Features
- Shorten any long URL
- Custom alias support
- Redirect to original URL
- Click count tracking
- Analytics API
- Proper error handling

## Tech Stack
- Java 21
- Spring Boot 4.0
- MySQL
- JPA/Hibernate
- Maven

## API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | /api/shorten | Shorten a URL |
| GET | /{shortCode} | Redirect to original URL |
| GET | /api/analytics/{shortCode} | Get URL analytics |

## How to Run
1. Clone the repository
2. Create MySQL database: `CREATE DATABASE urlshortener_db;`
3. Update `application.properties` with your MySQL password
4. Run `UrlshortenerApplication.java`
5. API runs on `http://localhost:8080`

## Example

Request:
```json
POST /api/shorten
{
  "originalUrl": "https://www.flipkart.com/very-long-url"
}
```

Response:
```json
{
  "shortCode": "1",
  "shortUrl": "http://localhost:8080/1"
}

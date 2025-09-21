# 🎬 Movie Streaming Platform (Netflix Clone)

A **Netflix-like movie streaming platform** with secure authentication, subscription plans, and an admin dashboard.  

---

## ✨ Features

- 🔑 User Authentication with Clerk (Google & Facebook login)  
- 💳 Subscription Payments via PayHere (users must buy a plan before accessing movies)  
- 🎥 Movie Library powered by TheMovieDB (metadata) and VidSrc (streaming)  
- 📌 **My List** feature for saving favorite movies  

---

## 🛠️ Admin Features

- ➕ Add, ✏️ Edit, ❌ Delete movies  
- 📝 Upload subtitles  
- 👥 Manage users (grant/revoke admin access, delete accounts)  
- 💰 Track subscription payments  

---

## 🧑‍💻 Tech Stack

**Client:** HTML, CSS, JavaScript  
**Server:** Spring Boot  
**Auth:** Clerk (Social Logins, JWT)  
**Payments:** PayHere  
**Movies API:** TheMovieDB (data), VidSrc (streaming)  

---

## 📸 Screenshots

![Home](https://raw.githubusercontent.com/AshenIndeewara/Neflix-Clone/refs/heads/master/screenshots/Screenshot%202025-09-21%20175715.png)  
![Player](https://raw.githubusercontent.com/AshenIndeewara/Neflix-Clone/refs/heads/master/screenshots/Screenshot%202025-09-21%20175738.png)
![Admin Home](https://raw.githubusercontent.com/AshenIndeewara/Neflix-Clone/refs/heads/master/screenshots/Screenshot%202025-09-21%20175814.png)  
![Admin Add Movie](https://raw.githubusercontent.com/AshenIndeewara/Neflix-Clone/refs/heads/master/screenshots/Screenshot%202025-09-21%20175833.png)  

---

## ⚙️ Environment Variables

To run this project, add the following environment variables in your `application.properties` file (or `.env`).  

### General
```
SPRING_APPLICATION_NAME=Netflix
```

### Database Settings
```
SPRING_DATASOURCE_DRIVER_CLASS_NAME=com.mysql.cj.jdbc.Driver
SPRING_DATASOURCE_USERNAME=your_db_username
SPRING_DATASOURCE_PASSWORD=your_db_password
SPRING_DATASOURCE_URL=jdbc:mysql://<host>:<port>/<database>
SPRING_DATASOURCE_HIKARI_MAXIMUM_POOL_SIZE=10
```

### JPA Settings
```
SPRING_JPA_GENERATE_DDL=true
SPRING_JPA_SHOW_SQL=true
SPRING_JPA_DATABASE_PLATFORM=org.hibernate.dialect.MySQLDialect
SPRING_JPA_HIBERNATE_DDL_AUTO=update
```

### Clerk (Authentication)
```
CLERK_BACKEND_API=https://api.clerk.com
CLERK_SECRET_KEY=your_clerk_secret_key
CLERK_PEM_PUBLIC_KEY=your_clerk_public_key
```

### PayHere (Payments)
```
PAYHERE_MERCHANT_ID=your_merchant_id
PAYHERE_MERCHANT_SECRET=your_merchant_secret
```

---

## 🚀 Deployment

You can deploy this project in multiple ways:  

### 🐳 Run with Docker
Make sure you have [Docker](https://docs.docker.com/get-docker/) installed.  

```bash
# Build Docker image
docker build -t netflix-app .

# Run container
docker run -p 8080:8080 netflix-app
```

The app will be available at 👉 [http://localhost:8080](http://localhost:8080)  

---

### ☕ Run as a Spring Boot App

Using Maven:
```bash
mvn spring-boot:run
```

Build and run jar:
```bash
mvn clean package
java -jar target/netflix-0.0.1-SNAPSHOT.jar
```

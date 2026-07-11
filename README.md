# Campus Notification System

A scalable Campus Notification System developed as part of the AffordMed Campus Hiring Assessment.

---

## Project Overview

The objective of this project is to design and implement a notification system capable of:

- Managing notifications for students
- Exposing REST APIs
- Designing scalable database architecture
- Optimizing database performance
- Designing scalable notification delivery architecture
- Implementing a Priority Inbox algorithm

The project is completed in multiple stages as per the assignment requirements.

---

# Technology Stack

- Java 17
- Spring Boot 3.5
- Maven
- PostgreSQL (Design)
- REST APIs
- JSON
- Git & GitHub

---

# Project Structure

```
studentdemo
│
├── src
│   ├── main
│   │   ├── java
│   │   │   └── com.student.studentdemo
│   │   │       ├── controller
│   │   │       ├── model
│   │   │       ├── service
│   │   │       ├── StudentdemoApplication.java
│   │   │       └── Stage6PriorityInbox.java
│   │   │
│   │   └── resources
│   │
│   └── Stage1
│       └── Notification_System_Design.md
│
├── pom.xml
└── README.md
```

---

# Stage 1 – REST API Design

Implemented REST API design for the notification system.

### APIs

- Create Notification
- Retrieve Notifications
- Get Notification by ID
- Mark Notification as Read
- Delete Notification
- Get Notification Categories
- Get Notification Preferences
- Update Notification Preferences

Also included:

- WebSocket design
- Error handling
- API versioning
- Security considerations

---

# Stage 2 – Database Design

Designed persistent storage using PostgreSQL.

Included:

- Database schema
- SQL table design
- Indexing strategy
- SQL queries for APIs
- Scalability discussion

---

# Stage 3 – Query Optimization

Optimized notification retrieval query for millions of records.

Covered:

- Composite indexing
- Pagination
- Avoiding SELECT *
- Table partitioning
- Archiving strategy
- Redis caching
- Read replicas

---

# Stage 4 – Scalability Improvements

Designed architecture improvements including:

- Redis Cache
- Pagination
- Lazy Loading
- WebSocket Notifications
- Read Replicas
- Notification Archiving

---

# Stage 5 – Notification Processing Architecture

Improved notification delivery architecture using:

- Message Queue (Kafka/RabbitMQ)
- Asynchronous Processing
- Retry Mechanism
- Worker-based Architecture
- Fault Tolerance
- Bulk Database Operations

---

# Stage 6 – Priority Inbox

Implemented a Priority Inbox algorithm using Java.

### Features

- Fetch notifications from REST API
- Calculate notification priority
- Priority based on:
  - Placement
  - Result
  - Event
- Consider notification recency
- Maintain Top 10 notifications using PriorityQueue

### Algorithm

Priority Score:

```
(typeWeight × 100) + recencyScore
```

Time Complexity:

| Operation | Complexity |
|-----------|------------|
| Insert | O(log n) |
| Remove | O(log n) |
| Top 10 Maintenance | O(n log 10) |

---

# How to Run

Clone repository

```bash
git clone https://github.com/19Chandrika/my.ac.p2mca25210.git
```

Go to project

```bash
cd my.ac.p2mca25210
```

Compile

```bash
./mvnw clean compile
```

Run Spring Boot Application

```bash
./mvnw spring-boot:run
```

Run Stage 6

```bash
./mvnw exec:java -Dexec.mainClass=com.student.studentdemo.Stage6PriorityInbox
```

---

# Documentation

Project documentation is available in:

```
Notification_System_Design.md
```

The document contains:

- Stage 1
- Stage 2
- Stage 3
- Stage 4
- Stage 5
- Stage 6

---

# Notes

- The Priority Inbox implementation is complete.
- The provided Notification API is protected and requires valid authentication credentials.
- Without valid credentials, the API returns **HTTP 401 Unauthorized**, which is expected behavior.

---

# License

This project was developed as part of the AffordMed Campus Hiring Assessment.

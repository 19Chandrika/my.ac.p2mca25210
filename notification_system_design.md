# Stage 1: Notification System REST API Design

## Overview

This document presents the REST API contract for a real-time notification system designed to deliver notifications to logged-in users. The system supports multiple notification types, user preferences, and real-time delivery mechanisms.

---

## Core Notification Actions

The notification platform should support the following core actions:

1. Retrieve Notifications
2. Mark Notifications as Read
3. Delete Notifications
4. Get Notification Preferences
5. Update Notification Preferences
6. Create Notifications
7. Get Notification Categories
8. Subscribe to Real-Time Updates

---

# REST API Endpoints

## 1. Retrieve Notifications

### Endpoint

```http
GET /api/v1/notifications
```

### Description

Fetch all notifications for the authenticated user.

### Response

```json
{
  "statusCode": 200,
  "message": "Notifications retrieved successfully"
}
```

---

## 2. Get Notification by ID

### Endpoint

```http
GET /api/v1/notifications/{notificationId}
```

### Description

Retrieve a specific notification using notification ID.

---

## 3. Mark Notification as Read

### Endpoint

```http
PATCH /api/v1/notifications/{notificationId}/read
```

### Description

Mark a notification as read.

---

## 4. Delete Notification

### Endpoint

```http
DELETE /api/v1/notifications/{notificationId}
```

### Description

Delete a notification.

---

## 5. Get Notification Preferences

### Endpoint

```http
GET /api/v1/notifications/preferences
```

### Description

Retrieve user notification preferences.

---

## 6. Update Notification Preferences

### Endpoint

```http
PUT /api/v1/notifications/preferences
```

### Description

Update user notification preferences.

---

## 7. Get Notification Categories

### Endpoint

```http
GET /api/v1/notifications/categories
```

### Description

Retrieve supported notification categories.

---

## 8. Create Notification

### Endpoint

```http
POST /api/v1/notifications
```

### Description

Create and send a notification.

### Request Body

```json
{
  "recipients": {
    "type": "user",
    "targetIds": [
      "user_789"
    ]
  },
  "title": "Course Registration Open",
  "message": "Spring 2026 course registration is available",
  "category": "academic",
  "type": "course_registration",
  "priority": "high"
}
```

---

# Real-Time Notification Mechanism

## WebSocket Endpoint

```text
wss://api.yourdomain.com/ws/notifications
```

### WebSocket Event Example

```json
{
  "type": "notification:new",
  "data": {
    "id": "notif_12345",
    "title": "New Assignment Posted",
    "message": "Assignment 5 is now available"
  }
}
```

---

# Error Handling

| Status Code | Description |
|---|---|
| 200 | Success |
| 201 | Created |
| 400 | Bad Request |
| 401 | Unauthorized |
| 404 | Not Found |
| 500 | Internal Server Error |

---

# Security

- JWT Authentication
- HTTPS/WSS encryption
- Rate limiting
- Input validation
- Audit logging

---

# Pagination Defaults

- Default page size: 20
- Maximum page size: 100

---

# Versioning Strategy

- Current Version: v1
- Endpoint Prefix: /api/v1

---

# Stage 2

## Persistent Storage Choice

PostgreSQL is selected as the persistent database for the notification system.

### Reasons for Choosing PostgreSQL

- Supports ACID transactions
- Reliable and production ready
- Strong indexing support
- Handles structured relational data efficiently
- Supports high concurrency
- Scalable for large notification systems

---

# Database Schema

## notifications Table

| Column Name | Data Type | Constraints |
|---|---|---|
| id | BIGSERIAL | PRIMARY KEY |
| user_id | VARCHAR(100) | NOT NULL |
| title | VARCHAR(255) | NOT NULL |
| message | TEXT | NOT NULL |
| category | VARCHAR(100) | NOT NULL |
| type | VARCHAR(100) | NOT NULL |
| priority | VARCHAR(20) | NOT NULL |
| status | VARCHAR(20) | DEFAULT 'unread' |
| action_url | VARCHAR(500) | NULL |
| created_at | TIMESTAMP | DEFAULT CURRENT_TIMESTAMP |
| read_at | TIMESTAMP | NULL |

---

# SQL Schema

```sql
CREATE TABLE notifications (
    id BIGSERIAL PRIMARY KEY,
    user_id VARCHAR(100) NOT NULL,
    title VARCHAR(255) NOT NULL,
    message TEXT NOT NULL,
    category VARCHAR(100) NOT NULL,
    type VARCHAR(100) NOT NULL,
    priority VARCHAR(20) NOT NULL,
    status VARCHAR(20) DEFAULT 'unread',
    action_url VARCHAR(500),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    read_at TIMESTAMP NULL
);
```

---

# Indexing Strategy

```sql
CREATE INDEX idx_user_notifications
ON notifications(user_id);

CREATE INDEX idx_user_status
ON notifications(user_id, status);

CREATE INDEX idx_created_at
ON notifications(created_at DESC);
```

### Benefits

- Faster unread notification queries
- Faster sorting
- Better performance for large datasets

---

# Problems with Increasing Data Volume

## 1. Slow Query Performance

### Problems

Queries become slower with millions of notifications.

### Solutions

- Add indexes
- Use pagination
- Optimize queries

---

## 2. Database Storage Growth

### Problems

Notification table size increases rapidly.

### Solutions

- Archive old notifications
- Partition tables
- Apply retention policies

---

## 3. High Concurrent Traffic

### Problems

Large concurrent requests overload database.

### Solutions

- Redis caching
- Read replicas
- Connection pooling

---

## 4. Real-Time Notification Delays

### Problems

High notification traffic may delay delivery.

### Solutions

- Kafka or RabbitMQ
- Asynchronous processing
- WebSocket scaling

---

# SQL Queries for REST APIs

## 1. Retrieve Notifications

### REST API

```http
GET /api/v1/notifications
```

### SQL Query

```sql
SELECT *
FROM notifications
WHERE user_id = 'user_789'
ORDER BY created_at DESC
LIMIT 20 OFFSET 0;
```

---

## 2. Get Notification by ID

### REST API

```http
GET /api/v1/notifications/{notificationId}
```

### SQL Query

```sql
SELECT *
FROM notifications
WHERE id = 1;
```

---

## 3. Mark Notification as Read

### REST API

```http
PATCH /api/v1/notifications/{notificationId}/read
```

### SQL Query

```sql
UPDATE notifications
SET status = 'read',
    read_at = CURRENT_TIMESTAMP
WHERE id = 1;
```

---

## 4. Delete Notification

### REST API

```http
DELETE /api/v1/notifications/{notificationId}
```

### SQL Query

```sql
DELETE FROM notifications
WHERE id = 1;
```

---

## 5. Create Notification

### REST API

```http
POST /api/v1/notifications
```

### SQL Query

```sql
INSERT INTO notifications
(user_id, title, message, category, type, priority, status)
VALUES
(
    'user_789',
    'Course Registration Open',
    'Spring 2026 registration is available',
    'academic',
    'course_registration',
    'high',
    'unread'
);
```

---

# Scalability Improvements

- Redis caching
- Kafka/RabbitMQ queues
- Database partitioning
- Read replicas
- Horizontal WebSocket scaling

---

# Final Architecture

Client Application  
↓  
REST API Gateway  
↓  
Notification Service  
↓  
PostgreSQL Database  
↓  
Redis Cache  
↓  
Kafka/RabbitMQ  
↓  
WebSocket Gateway
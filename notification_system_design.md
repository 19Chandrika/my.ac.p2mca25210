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


# Stage 3

## Problem Statement

The notification system currently stores:

- 50,000 students
- 5,000,000 notifications

The following query is becoming slow as data volume increases:

```sql
SELECT * FROM notifications
WHERE studentID = 1042
AND isRead = false
ORDER BY createdAt DESC;
```

---

# Why the Query is Slow

As the notification table grows to millions of records, the database performs large table scans to find unread notifications for a student.

Problems include:

- Full table scan on large dataset
- Sorting overhead using `ORDER BY`
- Increased disk I/O
- Higher query execution time
- Increased memory consumption

---

# Root Cause Analysis

The query filters using:

```sql
studentID
isRead
```

and sorts using:

```sql
createdAt DESC
```

If indexes are missing or improperly designed, PostgreSQL/MySQL cannot efficiently locate the required rows.

---

# Optimized Solution

## 1. Add Composite Index

The best optimization is to create a composite index matching the query pattern.

### Optimized Index

```sql
CREATE INDEX idx_notifications_student_read_created
ON notifications(studentID, isRead, createdAt DESC);
```

---

# Why This Index Helps

The database can now:

1. Quickly locate rows for a specific student
2. Filter unread notifications efficiently
3. Return rows already sorted by `createdAt DESC`
4. Avoid full table scans
5. Reduce sorting cost

---

# Optimized Query

```sql
SELECT *
FROM notifications
WHERE studentID = 1042
AND isRead = false
ORDER BY createdAt DESC
LIMIT 50;
```

---

# Additional Improvements

## 2. Pagination

Returning all unread notifications is inefficient.

### Solution

Use pagination:

```sql
SELECT *
FROM notifications
WHERE studentID = 1042
AND isRead = false
ORDER BY createdAt DESC
LIMIT 50 OFFSET 0;
```

### Benefits

- Smaller result set
- Reduced memory usage
- Faster API response time

---

## 3. Avoid SELECT *

Fetching unnecessary columns increases disk reads.

### Optimized Query

```sql
SELECT id, title, message, createdAt
FROM notifications
WHERE studentID = 1042
AND isRead = false
ORDER BY createdAt DESC
LIMIT 50;
```

### Benefits

- Reduced network transfer
- Faster query execution
- Better cache utilization

---

## 4. Database Partitioning

With millions of notifications, table partitioning improves performance.

### Suggested Partitioning

Partition by:

- studentID
or
- createdAt (monthly partitions)

### Example

```text
notifications_2026_01
notifications_2026_02
notifications_2026_03
```

### Benefits

- Smaller searchable partitions
- Faster queries
- Easier archival

---

## 5. Archiving Old Notifications

Old notifications rarely accessed should be archived.

### Strategy

- Move notifications older than 1 year to archive tables
- Keep active notifications in main table

### Benefits

- Smaller active dataset
- Faster queries
- Lower storage overhead

---

## 6. Redis Caching

Unread notification counts are frequently requested.

### Solution

Store unread counts in Redis.

### Example

```text
student:1042:unread_count = 12
```

### Benefits

- Faster response time
- Reduced database load
- Better scalability

---

## 7. Read Replicas

High read traffic can overload primary database.

### Solution

Use PostgreSQL/MySQL read replicas.

### Benefits

- Distribute read traffic
- Improve scalability
- Better availability

---

# Expected Performance Improvements

| Optimization | Improvement |
|---|---|
| Composite Index | Major query speed improvement |
| Pagination | Reduced response time |
| Column Selection | Reduced memory usage |
| Partitioning | Faster searches |
| Archiving | Smaller active dataset |
| Redis Cache | Reduced DB load |
| Read Replicas | Better scalability |

---

# Final Optimized Query

```sql
SELECT id, title, message, createdAt
FROM notifications
WHERE studentID = 1042
AND isRead = false
ORDER BY createdAt DESC
LIMIT 50 OFFSET 0;
```

---

# Recommended Final Index

```sql
CREATE INDEX idx_notifications_student_read_created
ON notifications(studentID, isRead, createdAt DESC);
```

---

# Final Recommendation

For large-scale notification systems:

- Use composite indexes
- Avoid `SELECT *`
- Use pagination
- Archive old data
- Use Redis caching
- Add database partitioning
- Use read replicas for scalability

These optimizations ensure the notification system remains fast and scalable even with millions of notifications.


# Stage 4

## Problem Statement

Notifications are currently fetched from the database on every page load for every student.

This causes:

- Heavy database load
- Increased response time
- Poor user experience
- High server resource usage
- Scalability problems during peak traffic

---

# Recommended Solutions

## 1. Redis Caching

### Solution

Store frequently accessed notifications and unread counts in Redis.

### Example Cache

```text
student:1042:notifications
student:1042:unread_count
```

### Workflow

1. User requests notifications
2. Application checks Redis first
3. If cache exists → return cached data
4. If cache miss → fetch from DB and update Redis

### Benefits

- Extremely fast reads
- Reduced database load
- Better response time

### Tradeoffs

| Pros | Cons |
|---|---|
| Fast access | Cache invalidation complexity |
| Reduced DB traffic | Additional infrastructure |
| Better scalability | Memory usage |

---

## 2. Pagination

### Problem

Fetching all notifications at once is expensive.

### Solution

Load notifications page by page.

### Example

```sql
SELECT id, title, message, createdAt
FROM notifications
WHERE studentID = 1042
ORDER BY createdAt DESC
LIMIT 20 OFFSET 0;
```

### Benefits

- Smaller response size
- Faster API response
- Reduced memory usage

### Tradeoffs

| Pros | Cons |
|---|---|
| Faster loading | More API calls |
| Lower DB load | Pagination handling required |

---

## 3. Lazy Loading / Infinite Scroll

### Solution

Load additional notifications only when user scrolls.

### Benefits

- Better user experience
- Reduced initial API load
- Lower bandwidth usage

### Tradeoffs

| Pros | Cons |
|---|---|
| Faster initial page load | Additional frontend logic |
| Reduced server load | Complex UI handling |

---

## 4. WebSocket-Based Real-Time Notifications

### Problem

Repeated polling overloads database.

### Solution

Use WebSockets to push notifications only when new notifications arrive.

### Workflow

1. Client opens WebSocket connection
2. Server pushes new notifications instantly
3. No repeated polling required

### Benefits

- Real-time updates
- Reduced unnecessary API calls
- Lower database load

### Tradeoffs

| Pros | Cons |
|---|---|
| Instant updates | Persistent connection management |
| Lower polling traffic | Higher server memory usage |

---

## 5. Read Replicas

### Solution

Use database read replicas for notification fetching.

### Benefits

- Distributes read traffic
- Reduces primary DB load
- Better scalability

### Tradeoffs

| Pros | Cons |
|---|---|
| Better scalability | Replication lag |
| Improved availability | Additional infrastructure cost |

---

## 6. Notification Archiving

### Solution

Archive old notifications into separate storage.

### Benefits

- Smaller active dataset
- Faster query execution
- Better indexing efficiency

### Tradeoffs

| Pros | Cons |
|---|---|
| Improved query performance | Archive management complexity |
| Reduced storage pressure | Extra maintenance |

---

# Recommended Final Architecture

Client Application  
↓  
API Gateway  
↓  
Notification Service  
↓  
Redis Cache  
↓  
PostgreSQL Primary DB  
↓  
Read Replicas  
↓  
Archive Storage

---

# Final Recommendation

For best performance:

- Use Redis caching
- Implement pagination
- Use lazy loading
- Push notifications via WebSocket
- Add read replicas
- Archive old notifications

This architecture significantly reduces database load and improves scalability.

---

# Stage 5

## Problems in Existing Implementation

Current pseudocode:

```python
function notify_all(student_ids: array, message: string):
    for student_id in student_ids:
        send_email(student_id, message)
        save_to_db(student_id, message)
        push_to_app(student_id, message)
```

### Issues

1. Sequential processing is very slow
2. One email failure may affect entire flow
3. No retry mechanism
4. High latency for 50,000 users
5. Tight coupling between services
6. No fault tolerance
7. Poor scalability
8. No asynchronous processing

---

# Why Email Failures Happened

Logs show 200 email failures because:

- Email APIs may timeout
- Network issues
- Rate limiting from email provider
- Temporary SMTP failures
- Sequential processing increases delays

---

# Recommended Architecture

Use:

- Message Queue (Kafka/RabbitMQ)
- Asynchronous workers
- Retry mechanism
- Independent services
- Batch processing

---

# Improved Design

## Key Improvements

### 1. Save Notification to DB First

Notification persistence should happen first.

Reason:

- Ensures notification is never lost
- Email delivery can retry later
- In-app notifications can still work even if email fails

---

## 2. Use Message Queue

Instead of directly calling services:

```text
Notification Service
        ↓
Message Queue
        ↓
Email Worker
Push Worker
Analytics Worker
```

### Benefits

- Asynchronous execution
- Better scalability
- Retry support
- Fault isolation

---

## 3. Retry Mechanism

Failed emails should retry automatically.

### Example Strategy

- Retry 3 times
- Exponential backoff
- Dead letter queue for permanent failures

---

# Revised Pseudocode

```python
function notify_all(student_ids: array, message: string):

    notification_ids = []

    # Step 1: Save notifications in bulk
    for student_id in student_ids:

        notification_id = save_to_db(
            student_id,
            message,
            status="PENDING"
        )

        notification_ids.append(notification_id)

    # Step 2: Publish events asynchronously
    for notification_id in notification_ids:

        publish_to_queue(
            topic="notification-events",
            notification_id=notification_id
        )
```

---

# Email Worker

```python
function email_worker(event):

    try:
        send_email(event.student_id, event.message)

        update_status(
            event.notification_id,
            "EMAIL_SENT"
        )

    except Exception:

        retry_event(event)
```

---

# Push Notification Worker

```python
function push_worker(event):

    push_to_app(
        event.student_id,
        event.message
    )
```

---

# Why This Design Is Better

| Improvement | Benefit |
|---|---|
| Async Processing | Faster execution |
| Queue-Based Architecture | Better scalability |
| Retry Mechanism | Improved reliability |
| Bulk Inserts | Faster DB writes |
| Independent Workers | Fault isolation |
| Event-Driven Design | Easier scaling |

---

# Should Saving to DB and Sending Email Happen Together?

## Recommendation

No, they should not happen synchronously together.

### Reason

Email delivery is an external operation and may fail temporarily.

If DB save and email are tightly coupled:

- Entire transaction may fail
- Notifications may be lost
- User experience degrades

### Better Approach

1. Save notification to DB
2. Publish async event
3. Workers process email/push separately

This guarantees durability and improves system resilience.

---

# Final Recommended Architecture

Client/Admin  
↓  
Notification API  
↓  
PostgreSQL Database  
↓  
Kafka/RabbitMQ Queue  
↓  
Email Workers  
Push Workers  
Analytics Workers  
↓  
Redis Cache  
↓  
WebSocket Gateway

---

# Final Recommendation

For large-scale notification delivery systems:

- Use asynchronous queues
- Avoid synchronous email sending
- Save notifications before delivery
- Use retries and dead letter queues
- Use worker-based processing
- Use bulk DB operations
- Scale workers horizontally

This architecture provides:

- High reliability
- Better fault tolerance
- Faster processing
- Enterprise scalability

# Stage 6

## Objective

Implement a Priority Inbox that always displays the top 10 most important unread notifications based on:

- Notification Type Weight
- Recency

---

# Priority Rules

| Notification Type | Weight |
|---|---|
| Placement | 3 |
| Result | 2 |
| Event | 1 |

Priority score is calculated using:

```text
(typeWeight * 100) + recencyScore
```

More recent notifications receive higher scores.

---

# Approach Used

The implementation uses:

- Java
- PriorityQueue (Min Heap)
- REST API Integration
- JSON Parsing

The Notification API is used to fetch notifications dynamically instead of hardcoding data.

---

# Algorithm

## Steps

1. Fetch notifications from API
2. Parse JSON response
3. Calculate priority score
4. Maintain Top 10 using PriorityQueue
5. Display highest priority unread notifications

---

# Why PriorityQueue?

PriorityQueue provides:

- Efficient Top-K retrieval
- Faster insertion
- Automatic ordering

Time Complexity:

| Operation | Complexity |
|---|---|
| Insert | O(log n) |
| Remove | O(log n) |
| Top 10 Maintenance | O(n log 10) |

---

# Efficient Maintenance of Top 10

As new notifications arrive continuously:

- Insert notification into Min Heap
- If heap size exceeds 10:
  - Remove lowest priority notification

This ensures:

- Constant memory usage
- Efficient processing
- Real-time Top 10 maintenance

---

# Technologies Used

- Java 17
- Maven
- org.json library
- PriorityQueue
- HttpURLConnection

---

# Screenshots

Screenshots of terminal output displaying top priority notifications are uploaded in the GitHub repository.

---

# Conclusion

The solution efficiently maintains the top 10 unread notifications using a Min Heap PriorityQueue and dynamically calculates importance based on notification type and recency.
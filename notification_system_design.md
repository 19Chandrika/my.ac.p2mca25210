# Stage 1: Notification System REST API Design

## Overview
This document presents the REST API contract for a real-time notification system designed to deliver notifications to logged-in users. The system supports multiple notification types, user preferences, and real-time delivery mechanisms.

---

## Core Notification Actions

The notification platform should support the following core actions:

1. **Retrieve Notifications** - Fetch user notifications with filtering and pagination
2. **Mark as Read** - Update notification read status
3. **Delete Notifications** - Remove notifications
4. **Get Notification Preferences** - Retrieve user notification settings
5. **Update Notification Preferences** - Modify notification delivery preferences
6. **Create Notification** - Send notifications to users (internal/admin use)
7. **Get Notification Categories** - Retrieve available notification types
8. **Subscribe to Real-Time Updates** - Establish WebSocket connection for live notifications

---

## REST API Endpoints

### 1. Retrieve User Notifications

**Endpoint:** `GET /api/v1/notifications`

**Description:** Fetch all notifications for the authenticated user with pagination and filtering support.

**Request Headers:**
```
Authorization: Bearer {JWT_TOKEN}
Accept: application/json
Content-Type: application/json
```

**Query Parameters:**
```
?page=0&size=20&status=unread&category=account&sortBy=createdAt&sortOrder=desc
```

**Response (200 OK):**
```json
{
  "statusCode": 200,
  "message": "Notifications retrieved successfully",
  "data": {
    "notifications": [
      {
        "id": "notif_12345",
        "userId": "user_789",
        "title": "Account Security Alert",
        "message": "New login detected from a new device",
        "category": "account",
        "type": "security_alert",
        "priority": "high",
        "status": "unread",
        "actionUrl": "/account/security",
        "timestamp": "2026-05-06T10:30:00Z",
        "createdAt": "2026-05-06T10:30:00Z",
        "readAt": null,
        "metadata": {
          "deviceInfo": "Chrome on macOS",
          "location": "San Francisco, CA"
        }
      },
      {
        "id": "notif_12346",
        "userId": "user_789",
        "title": "Course Enrollment Confirmation",
        "message": "You have successfully enrolled in CS 101",
        "category": "academic",
        "type": "enrollment",
        "priority": "normal",
        "status": "read",
        "actionUrl": "/courses/cs-101",
        "timestamp": "2026-05-05T14:15:00Z",
        "createdAt": "2026-05-05T14:15:00Z",
        "readAt": "2026-05-05T14:20:00Z",
        "metadata": {}
      }
    ],
    "pagination": {
      "currentPage": 0,
      "pageSize": 20,
      "totalElements": 45,
      "totalPages": 3,
      "hasNextPage": true,
      "hasPreviousPage": false
    }
  }
}
```

**Response (401 Unauthorized):**
```json
{
  "statusCode": 401,
  "message": "Unauthorized - Invalid or missing authentication token",
  "error": "UNAUTHORIZED",
  "timestamp": "2026-05-06T10:30:00Z"
}
```

---

### 2. Get Notification by ID

**Endpoint:** `GET /api/v1/notifications/{notificationId}`

**Description:** Retrieve a specific notification by its ID.

**Request Headers:**
```
Authorization: Bearer {JWT_TOKEN}
Accept: application/json
```

**Path Parameters:**
```
notificationId: string (required) - The unique notification identifier
```

**Response (200 OK):**
```json
{
  "statusCode": 200,
  "message": "Notification retrieved successfully",
  "data": {
    "id": "notif_12345",
    "userId": "user_789",
    "title": "Account Security Alert",
    "message": "New login detected from a new device",
    "category": "account",
    "type": "security_alert",
    "priority": "high",
    "status": "unread",
    "actionUrl": "/account/security",
    "timestamp": "2026-05-06T10:30:00Z",
    "createdAt": "2026-05-06T10:30:00Z",
    "readAt": null,
    "metadata": {
      "deviceInfo": "Chrome on macOS",
      "location": "San Francisco, CA"
    }
  }
}
```

**Response (404 Not Found):**
```json
{
  "statusCode": 404,
  "message": "Notification not found",
  "error": "NOT_FOUND",
  "timestamp": "2026-05-06T10:30:00Z"
}
```

---

### 3. Mark Notification as Read

**Endpoint:** `PATCH /api/v1/notifications/{notificationId}/read`

**Description:** Mark a single notification as read.

**Request Headers:**
```
Authorization: Bearer {JWT_TOKEN}
Content-Type: application/json
```

**Path Parameters:**
```
notificationId: string (required) - The unique notification identifier
```

**Request Body:**
```json
{
  "readAt": "2026-05-06T10:35:00Z"
}
```

**Response (200 OK):**
```json
{
  "statusCode": 200,
  "message": "Notification marked as read",
  "data": {
    "id": "notif_12345",
    "status": "read",
    "readAt": "2026-05-06T10:35:00Z"
  }
}
```

---

### 4. Mark Multiple Notifications as Read

**Endpoint:** `PATCH /api/v1/notifications/bulk/read`

**Description:** Mark multiple notifications as read in a single request.

**Request Headers:**
```
Authorization: Bearer {JWT_TOKEN}
Content-Type: application/json
```

**Request Body:**
```json
{
  "notificationIds": [
    "notif_12345",
    "notif_12346",
    "notif_12347"
  ],
  "readAt": "2026-05-06T10:35:00Z"
}
```

**Response (200 OK):**
```json
{
  "statusCode": 200,
  "message": "Notifications marked as read",
  "data": {
    "successCount": 3,
    "failureCount": 0,
    "processedIds": [
      "notif_12345",
      "notif_12346",
      "notif_12347"
    ]
  }
}
```

---

### 5. Delete Notification

**Endpoint:** `DELETE /api/v1/notifications/{notificationId}`

**Description:** Delete a specific notification.

**Request Headers:**
```
Authorization: Bearer {JWT_TOKEN}
```

**Path Parameters:**
```
notificationId: string (required) - The unique notification identifier
```

**Response (204 No Content):**
```
(No response body)
```

**Response (404 Not Found):**
```json
{
  "statusCode": 404,
  "message": "Notification not found",
  "error": "NOT_FOUND",
  "timestamp": "2026-05-06T10:30:00Z"
}
```

---

### 6. Delete Multiple Notifications

**Endpoint:** `DELETE /api/v1/notifications/bulk`

**Description:** Delete multiple notifications in a single request.

**Request Headers:**
```
Authorization: Bearer {JWT_TOKEN}
Content-Type: application/json
```

**Request Body:**
```json
{
  "notificationIds": [
    "notif_12345",
    "notif_12346"
  ]
}
```

**Response (200 OK):**
```json
{
  "statusCode": 200,
  "message": "Notifications deleted successfully",
  "data": {
    "deletedCount": 2,
    "failedCount": 0
  }
}
```

---

### 7. Get Notification Preferences

**Endpoint:** `GET /api/v1/notifications/preferences`

**Description:** Retrieve notification delivery preferences for the authenticated user.

**Request Headers:**
```
Authorization: Bearer {JWT_TOKEN}
Accept: application/json
```

**Response (200 OK):**
```json
{
  "statusCode": 200,
  "message": "Notification preferences retrieved successfully",
  "data": {
    "userId": "user_789",
    "globalSettings": {
      "enabled": true,
      "emailNotifications": true,
      "pushNotifications": true,
      "inAppNotifications": true,
      "smsNotifications": false,
      "quietHoursEnabled": true,
      "quietHoursStart": "22:00",
      "quietHoursEnd": "08:00",
      "timezone": "America/Los_Angeles"
    },
    "categoryPreferences": [
      {
        "category": "account",
        "enabled": true,
        "emailEnabled": true,
        "pushEnabled": true,
        "frequency": "immediate"
      },
      {
        "category": "academic",
        "enabled": true,
        "emailEnabled": true,
        "pushEnabled": false,
        "frequency": "daily_digest"
      },
      {
        "category": "promotions",
        "enabled": false,
        "emailEnabled": false,
        "pushEnabled": false,
        "frequency": "off"
      },
      {
        "category": "system",
        "enabled": true,
        "emailEnabled": false,
        "pushEnabled": true,
        "frequency": "immediate"
      }
    ]
  }
}
```

---

### 8. Update Notification Preferences

**Endpoint:** `PUT /api/v1/notifications/preferences`

**Description:** Update notification delivery preferences for the authenticated user.

**Request Headers:**
```
Authorization: Bearer {JWT_TOKEN}
Content-Type: application/json
```

**Request Body:**
```json
{
  "globalSettings": {
    "enabled": true,
    "emailNotifications": true,
    "pushNotifications": false,
    "inAppNotifications": true,
    "smsNotifications": false,
    "quietHoursEnabled": true,
    "quietHoursStart": "22:00",
    "quietHoursEnd": "08:00",
    "timezone": "America/Los_Angeles"
  },
  "categoryPreferences": [
    {
      "category": "promotions",
      "enabled": false,
      "emailEnabled": false,
      "pushEnabled": false,
      "frequency": "off"
    },
    {
      "category": "academic",
      "enabled": true,
      "emailEnabled": true,
      "pushEnabled": true,
      "frequency": "immediate"
    }
  ]
}
```

**Response (200 OK):**
```json
{
  "statusCode": 200,
  "message": "Notification preferences updated successfully",
  "data": {
    "userId": "user_789",
    "globalSettings": {
      "enabled": true,
      "emailNotifications": true,
      "pushNotifications": false,
      "inAppNotifications": true,
      "smsNotifications": false,
      "quietHoursEnabled": true,
      "quietHoursStart": "22:00",
      "quietHoursEnd": "08:00",
      "timezone": "America/Los_Angeles"
    },
    "updatedAt": "2026-05-06T10:40:00Z"
  }
}
```

---

### 9. Get Notification Categories

**Endpoint:** `GET /api/v1/notifications/categories`

**Description:** Retrieve all available notification categories and types supported by the system.

**Request Headers:**
```
Authorization: Bearer {JWT_TOKEN}
Accept: application/json
```

**Response (200 OK):**
```json
{
  "statusCode": 200,
  "message": "Notification categories retrieved successfully",
  "data": {
    "categories": [
      {
        "id": "account",
        "displayName": "Account",
        "description": "Notifications related to account security and activity",
        "types": [
          {
            "type": "security_alert",
            "displayName": "Security Alert",
            "description": "Login attempts, password changes, etc.",
            "priority": "high"
          },
          {
            "type": "profile_update",
            "displayName": "Profile Update",
            "description": "Changes to profile information",
            "priority": "normal"
          }
        ]
      },
      {
        "id": "academic",
        "displayName": "Academic",
        "description": "Notifications related to courses and academic activities",
        "types": [
          {
            "type": "enrollment",
            "displayName": "Enrollment Confirmation",
            "description": "Course enrollment updates",
            "priority": "normal"
          },
          {
            "type": "grade_posted",
            "displayName": "Grade Posted",
            "description": "When a grade is published",
            "priority": "high"
          }
        ]
      },
      {
        "id": "system",
        "displayName": "System",
        "description": "System maintenance and important updates",
        "types": [
          {
            "type": "maintenance",
            "displayName": "Maintenance Alert",
            "description": "Scheduled maintenance notifications",
            "priority": "normal"
          }
        ]
      },
      {
        "id": "promotions",
        "displayName": "Promotions",
        "description": "Special offers and promotional content",
        "types": [
          {
            "type": "offer",
            "displayName": "Special Offer",
            "description": "Limited-time promotions",
            "priority": "low"
          }
        ]
      }
    ]
  }
}
```

---

### 10. Create Notification (Admin/Internal Use)

**Endpoint:** `POST /api/v1/notifications`

**Description:** Create and send a notification to users. Typically used by internal services or admin users.

**Request Headers:**
```
Authorization: Bearer {JWT_TOKEN}
Content-Type: application/json
X-Request-ID: {UNIQUE_REQUEST_ID}
```

**Request Body:**
```json
{
  "recipients": {
    "type": "user",
    "targetIds": ["user_789", "user_790"]
  },
  "title": "Course Registration Now Open",
  "message": "Spring 2026 course registration is now available",
  "category": "academic",
  "type": "course_registration",
  "priority": "high",
  "actionUrl": "/courses/register",
  "templateId": "course_registration_v1",
  "variables": {
    "semester": "Spring 2026",
    "deadline": "2026-05-31"
  },
  "deliveryChannels": {
    "inApp": true,
    "email": true,
    "push": true,
    "sms": false
  },
  "scheduledFor": "2026-05-06T12:00:00Z",
  "expiresAt": "2026-05-13T23:59:59Z"
}
```

**Response (201 Created):**
```json
{
  "statusCode": 201,
  "message": "Notification created and queued for delivery",
  "data": {
    "campaignId": "campaign_5678",
    "notificationIds": [
      "notif_12349",
      "notif_12350"
    ],
    "recipientCount": 2,
    "status": "queued",
    "createdAt": "2026-05-06T10:50:00Z"
  }
}
```

**Response (400 Bad Request):**
```json
{
  "statusCode": 400,
  "message": "Invalid notification payload",
  "error": "VALIDATION_ERROR",
  "details": {
    "field": "recipients.targetIds",
    "message": "At least one recipient ID must be provided"
  },
  "timestamp": "2026-05-06T10:50:00Z"
}
```

---

### 11. Get Notification Delivery Status

**Endpoint:** `GET /api/v1/notifications/{notificationId}/status`

**Description:** Retrieve the delivery status of a notification across different channels.

**Request Headers:**
```
Authorization: Bearer {JWT_TOKEN}
Accept: application/json
```

**Path Parameters:**
```
notificationId: string (required) - The unique notification identifier
```

**Response (200 OK):**
```json
{
  "statusCode": 200,
  "message": "Notification delivery status retrieved successfully",
  "data": {
    "notificationId": "notif_12345",
    "deliveryStatus": [
      {
        "channel": "inApp",
        "status": "delivered",
        "deliveredAt": "2026-05-06T10:30:05Z",
        "readAt": "2026-05-06T10:35:00Z"
      },
      {
        "channel": "email",
        "status": "delivered",
        "deliveredAt": "2026-05-06T10:30:10Z",
        "bounced": false
      },
      {
        "channel": "push",
        "status": "failed",
        "failureReason": "Device token expired",
        "attemptedAt": "2026-05-06T10:30:08Z"
      }
    ],
    "overallStatus": "partially_delivered"
  }
}
```

---

## Real-Time Notifications Mechanism

### WebSocket Connection for Live Updates

**Protocol:** WebSocket over TLS (WSS)

**Endpoint:** `wss://api.yourdomain.com/ws/notifications`

**Description:** Establishes a persistent WebSocket connection to receive real-time notification updates.

#### Connection Handshake

**Client Connection Request:**
```
GET /ws/notifications HTTP/1.1
Host: api.yourdomain.com
Upgrade: websocket
Connection: Upgrade
Sec-WebSocket-Key: {BASE64_ENCODED_KEY}
Sec-WebSocket-Version: 13
Authorization: Bearer {JWT_TOKEN}
X-User-ID: user_789
```

**Server Response:**
```
HTTP/1.1 101 Switching Protocols
Upgrade: websocket
Connection: Upgrade
Sec-WebSocket-Accept: {CALCULATED_ACCEPT_KEY}
```

#### WebSocket Message Format

**Connection Confirmation (Server → Client):**
```json
{
  "type": "connection_established",
  "data": {
    "connectionId": "conn_abc123",
    "userId": "user_789",
    "timestamp": "2026-05-06T10:55:00Z"
  }
}
```

**Heartbeat/Ping (Server → Client):**
```json
{
  "type": "ping",
  "data": {
    "timestamp": "2026-05-06T11:00:00Z"
  }
}
```

**Client Heartbeat Response (Client → Server):**
```json
{
  "type": "pong",
  "data": {
    "timestamp": "2026-05-06T11:00:00Z"
  }
}
```

**New Notification Received (Server → Client):**
```json
{
  "type": "notification:new",
  "data": {
    "id": "notif_12351",
    "userId": "user_789",
    "title": "New Assignment Posted",
    "message": "CS 101: Assignment 5 has been posted",
    "category": "academic",
    "type": "assignment",
    "priority": "normal",
    "status": "unread",
    "actionUrl": "/courses/cs-101/assignments/5",
    "timestamp": "2026-05-06T11:05:00Z",
    "metadata": {}
  }
}
```

**Notification Status Update (Server → Client):**
```json
{
  "type": "notification:updated",
  "data": {
    "notificationId": "notif_12351",
    "status": "read",
    "readAt": "2026-05-06T11:10:00Z"
  }
}
```

**Multiple Notifications (Server → Client):**
```json
{
  "type": "notifications:batch",
  "data": {
    "notifications": [
      {
        "id": "notif_12352",
        "title": "Notification 1",
        "message": "Content 1",
        "category": "academic"
      },
      {
        "id": "notif_12353",
        "title": "Notification 2",
        "message": "Content 2",
        "category": "system"
      }
    ]
  }
}
```

**Subscribe to Notification Categories (Client → Server):**
```json
{
  "type": "subscribe",
  "data": {
    "categories": ["academic", "account"],
    "includeRead": false
  }
}
```

**Server Acknowledgment (Server → Client):**
```json
{
  "type": "subscribe_ack",
  "data": {
    "status": "success",
    "subscribedCategories": ["academic", "account"],
    "message": "Successfully subscribed to categories"
  }
}
```

**Connection Error (Server → Client):**
```json
{
  "type": "error",
  "data": {
    "code": "AUTH_FAILED",
    "message": "Authentication token expired",
    "action": "reconnect_with_new_token"
  }
}
```

**Disconnect (Server → Client):**
```json
{
  "type": "connection_closed",
  "data": {
    "connectionId": "conn_abc123",
    "reason": "user_logout",
    "timestamp": "2026-05-06T11:15:00Z"
  }
}
```

---

## WebSocket Implementation Details

### Connection Management

- **Heartbeat Interval:** Server sends ping every 30 seconds
- **Heartbeat Timeout:** Connection closes if no pong received within 60 seconds
- **Reconnection Strategy:** Client should implement exponential backoff (1s, 2s, 4s, 8s max)
- **Max Reconnection Attempts:** 5 attempts before notifying user
- **Connection Timeout:** 30 seconds to establish handshake

### Message Queue and Offline Delivery

- **Offline Messages:** Messages are stored for 7 days
- **Sync on Reconnect:** Client can request missed messages by timestamp
- **Message Deduplication:** Messages include unique `messageId` to prevent duplicates

**Request Missed Messages (Client → Server):**
```json
{
  "type": "sync",
  "data": {
    "sinceTimestamp": "2026-05-06T11:00:00Z",
    "limit": 50
  }
}
```

**Missed Messages Response (Server → Client):**
```json
{
  "type": "sync_response",
  "data": {
    "missedNotifications": [
      {
        "id": "notif_12354",
        "title": "Missed Notification",
        "message": "You missed this notification",
        "timestamp": "2026-05-06T11:05:00Z"
      }
    ],
    "totalMissed": 1,
    "syncComplete": true
  }
}
```

---

## Error Handling

### Common HTTP Status Codes

| Status Code | Scenario |
|---|---|
| 200 | Successful GET or PATCH request |
| 201 | Resource created successfully |
| 204 | Successful deletion |
| 400 | Bad request or validation error |
| 401 | Unauthorized - invalid/missing token |
| 403 | Forbidden - insufficient permissions |
| 404 | Resource not found |
| 409 | Conflict - resource state conflict |
| 429 | Rate limit exceeded |
| 500 | Internal server error |

### Standard Error Response Format

```json
{
  "statusCode": 400,
  "message": "Human-readable error message",
  "error": "ERROR_CODE",
  "details": {
    "field": "fieldName",
    "message": "Specific error details"
  },
  "timestamp": "2026-05-06T10:30:00Z",
  "requestId": "req_12345"
}
```

---

## Security Considerations

1. **Authentication:** All endpoints require valid JWT token in Authorization header
2. **Rate Limiting:** Maximum 100 requests per minute per user
3. **CORS:** Specify allowed origins in response headers
4. **Data Encryption:** All data in transit over HTTPS/WSS
5. **Token Refresh:** JWT tokens expire in 1 hour, refresh tokens valid for 7 days
6. **Audit Logging:** All notification actions logged for compliance

### Recommended Headers

```
Strict-Transport-Security: max-age=31536000; includeSubDomains
X-Content-Type-Options: nosniff
X-Frame-Options: DENY
X-XSS-Protection: 1; mode=block
Content-Security-Policy: default-src 'self'
```

---

## Implementation Best Practices

### Client-Side

1. Always validate JWT token expiration before making requests
2. Implement automatic reconnection for WebSocket with exponential backoff
3. Cache notification preferences locally to minimize API calls
4. Use pagination when fetching large notification lists
5. Implement local notification deduplication

### Server-Side

1. Validate all input against defined JSON schemas
2. Implement request ID tracking for debugging
3. Use message queues (Redis, RabbitMQ) for reliable notification delivery
4. Cache frequently accessed preferences in Redis
5. Archive old notifications after 90 days
6. Monitor WebSocket connection health and clean up stale connections

---

## Pagination and Filtering Defaults

- **Default Page Size:** 20 notifications
- **Max Page Size:** 100 notifications
- **Default Sort:** By creation date (descending)
- **Filterable Fields:** status, category, priority, type

---

## Versioning Strategy

- **Current Version:** v1
- **Deprecation Policy:** APIs deprecated for 6 months before removal
- **Version in Path:** `/api/v1/notifications`
- **Backward Compatibility:** Maintained for at least 2 major versions


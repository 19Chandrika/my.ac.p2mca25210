package com.student.studentdemo;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.PriorityQueue;

import org.json.JSONArray;
import org.json.JSONObject;

public class Stage6PriorityInbox {

    private static final String API_URL =
            "http://20.207.122.201/evaluation-service/notifications";

    private static final String TOKEN =
    "actual_full_token_here";
    public static void main(String[] args) {

        try {

            String response = fetchNotifications();

            JSONObject jsonObject = new JSONObject(response);

            JSONArray notifications =
                    jsonObject.getJSONArray("notifications");

            PriorityQueue<NotificationData> topNotifications =
                    new PriorityQueue<>(
                            10,
                            Comparator.comparingDouble(
                                    NotificationData::getPriorityScore
                            )
                    );

            for (int i = 0; i < notifications.length(); i++) {

                JSONObject object =
                        notifications.getJSONObject(i);

                String id =
                        object.getString("ID");

                String type =
                        object.getString("Type");

                String message =
                        object.getString("Message");

                String timestamp =
                        object.getString("Timestamp");

                double score =
                        calculatePriority(type, timestamp);

                NotificationData notification =
                        new NotificationData(
                                id,
                                type,
                                message,
                                timestamp,
                                score
                        );

                topNotifications.offer(notification);

                if (topNotifications.size() > 10) {
                    topNotifications.poll();
                }
            }

            System.out.println(
                    "\n===== TOP 10 PRIORITY NOTIFICATIONS =====\n"
            );

            while (!topNotifications.isEmpty()) {

                NotificationData notification =
                        topNotifications.poll();

                System.out.println(
                        "ID: " + notification.id
                );

                System.out.println(
                        "Type: " + notification.type
                );

                System.out.println(
                        "Message: " + notification.message
                );

                System.out.println(
                        "Timestamp: " + notification.timestamp
                );

                System.out.println(
                        "Priority Score: "
                                + notification.priorityScore
                );

                System.out.println(
                        "-----------------------------------"
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String fetchNotifications() throws Exception {

        URL url = new URL(API_URL);

        HttpURLConnection connection =
                (HttpURLConnection) url.openConnection();

        connection.setRequestMethod("GET");

        connection.setRequestProperty(
                "Authorization",
                "Bearer " + TOKEN
        );

        BufferedReader reader =
                new BufferedReader(
                        new InputStreamReader(
                                connection.getInputStream()
                        )
                );

        StringBuilder response =
                new StringBuilder();

        String line;

        while ((line = reader.readLine()) != null) {
            response.append(line);
        }

        reader.close();

        return response.toString();
    }

    private static double calculatePriority(
            String type,
            String timestamp
    ) {

        double typeWeight = 0;

        switch (type.toLowerCase()) {

            case "placement":
                typeWeight = 3;
                break;

            case "result":
                typeWeight = 2;
                break;

            case "event":
                typeWeight = 1;
                break;

            default:
                typeWeight = 0;
        }

        DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern(
                        "yyyy-MM-dd HH:mm:ss"
                );

        LocalDateTime notificationTime =
                LocalDateTime.parse(
                        timestamp,
                        formatter
                );

        LocalDateTime now =
                LocalDateTime.now();

        long hours =
                java.time.Duration.between(
                        notificationTime,
                        now
                ).toHours();

        double recencyScore =
                Math.max(0, 100 - hours);

        return (typeWeight * 100)
                + recencyScore;
    }

    static class NotificationData {

        String id;
        String type;
        String message;
        String timestamp;
        double priorityScore;

        public NotificationData(
                String id,
                String type,
                String message,
                String timestamp,
                double priorityScore
        ) {

            this.id = id;
            this.type = type;
            this.message = message;
            this.timestamp = timestamp;
            this.priorityScore = priorityScore;
        }

        public double getPriorityScore() {
            return priorityScore;
        }
    }
}
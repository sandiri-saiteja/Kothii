package com.greencampus;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;

public class ApiServer {

    private static final int PORT = Integer.getInteger("green.campus.api.port", 8080);

    private final UserDAO userDAO = new UserDAO();
    private final ReportDAO reportDAO = new ReportDAO();

    public static void main(String[] args) throws Exception {
        new ApiServer().start();
    }

    public void start() throws Exception {
        HttpServer server = HttpServer.create(
                new InetSocketAddress("0.0.0.0", PORT),
                0
        );

        server.createContext("/api/health", this::health);
        server.createContext("/api/register", this::register);
        server.createContext("/api/login", this::login);
        server.createContext("/api/report", this::report);

        server.setExecutor(Executors.newFixedThreadPool(8));
        server.start();

        System.out.println();
        System.out.println("====================================================");
        System.out.println("Green Campus Web API started");
        System.out.println("API:    http://localhost:" + PORT);
        System.out.println("Health: http://localhost:" + PORT + "/api/health");
        System.out.println("====================================================");
        System.out.println("Keep this terminal running while using Register/Login/Reports.");
    }

    private void health(HttpExchange exchange) throws IOException {
        if (handleOptions(exchange)) {
            return;
        }

        if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) {
            sendJson(exchange, 405,
                    errorJson("Method not allowed."));
            return;
        }

        sendJson(exchange, 200,
                "{\"success\":true,\"message\":\"Green Campus API is running.\"}");
    }

    private void register(HttpExchange exchange) throws IOException {
        if (handleOptions(exchange)) {
            return;
        }

        if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
            sendJson(exchange, 405,
                    errorJson("Method not allowed."));
            return;
        }

        try {
            Map<String, String> form = parseForm(exchange);

            String name = required(form, "name");
            String userCode = required(form, "id");
            String email = required(form, "email").toLowerCase();
            String department = required(form, "department");
            String campus = required(form, "campus");
            String password = required(form, "password");

            if (userDAO.emailExists(email)) {
                sendJson(exchange, 409,
                        errorJson("An account with this email already exists."));
                return;
            }

            if (userDAO.userCodeExists(userCode)) {
                sendJson(exchange, 409,
                        errorJson("This Student / Employee ID is already registered."));
                return;
            }

            User user = new User(
                    name,
                    userCode,
                    email,
                    department,
                    campus,
                    SecurityUtil.sha256(password)
            );

            userDAO.createUser(user);

            String response = "{\"success\":true"
                    + ",\"message\":" + json("Registration saved in MySQL successfully.")
                    + ",\"user\":{\"userId\":" + user.getUserId()
                    + ",\"name\":" + json(user.getFullName())
                    + ",\"id\":" + json(user.getUserCode())
                    + ",\"email\":" + json(user.getEmail())
                    + ",\"department\":" + json(user.getDepartment())
                    + ",\"campus\":" + json(user.getCampus())
                    + "}}";

            sendJson(exchange, 201, response);

        } catch (IllegalArgumentException e) {
            sendJson(exchange, 400, errorJson(e.getMessage()));
        } catch (Exception e) {
            sendJson(exchange, 500,
                    errorJson("Database error: " + safeMessage(e)));
        }
    }

    private void login(HttpExchange exchange) throws IOException {
        if (handleOptions(exchange)) {
            return;
        }

        if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
            sendJson(exchange, 405,
                    errorJson("Method not allowed."));
            return;
        }

        try {
            Map<String, String> form = parseForm(exchange);
            String email = required(form, "email").toLowerCase();
            String password = required(form, "password");

            User user = userDAO.findByEmail(email);

            if (user == null
                    || !SecurityUtil.sha256(password).equals(user.getPasswordHash())) {
                sendJson(exchange, 401,
                        errorJson("Invalid email or password."));
                return;
            }

            String response = "{\"success\":true"
                    + ",\"message\":" + json("Login successful.")
                    + ",\"user\":{\"userId\":" + user.getUserId()
                    + ",\"name\":" + json(user.getFullName())
                    + ",\"id\":" + json(user.getUserCode())
                    + ",\"email\":" + json(user.getEmail())
                    + ",\"department\":" + json(user.getDepartment())
                    + ",\"campus\":" + json(user.getCampus())
                    + "}}";

            sendJson(exchange, 200, response);

        } catch (IllegalArgumentException e) {
            sendJson(exchange, 400, errorJson(e.getMessage()));
        } catch (Exception e) {
            sendJson(exchange, 500,
                    errorJson("Database error: " + safeMessage(e)));
        }
    }

    private void report(HttpExchange exchange) throws IOException {
        if (handleOptions(exchange)) {
            return;
        }

        if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
            sendJson(exchange, 405,
                    errorJson("Method not allowed."));
            return;
        }

        try {
            Map<String, String> form = parseForm(exchange);

            String email = required(form, "email").toLowerCase();
            String metric = required(form, "metric");
            double value = Double.parseDouble(required(form, "value"));
            LocalDate date = LocalDate.parse(required(form, "date"));
            String description = required(form, "description");

            if (value <= 0) {
                throw new IllegalArgumentException(
                        "Metric value must be greater than zero."
                );
            }

            int reportId = reportDAO.createReport(
                    email,
                    metric,
                    value,
                    date,
                    description
            );

            String response = "{\"success\":true"
                    + ",\"message\":" + json("Report saved in MySQL successfully.")
                    + ",\"reportId\":" + reportId + "}";

            sendJson(exchange, 201, response);

        } catch (NumberFormatException | DateTimeParseException e) {
            sendJson(exchange, 400,
                    errorJson("Invalid report value or date."));
        } catch (IllegalArgumentException e) {
            sendJson(exchange, 400, errorJson(e.getMessage()));
        } catch (Exception e) {
            sendJson(exchange, 500,
                    errorJson("Database error: " + safeMessage(e)));
        }
    }

    private Map<String, String> parseForm(HttpExchange exchange)
            throws IOException {

        Headers headers = exchange.getRequestHeaders();
        String contentType = headers.getFirst("Content-Type");

        if (contentType == null
                || !contentType.toLowerCase()
                .startsWith("application/x-www-form-urlencoded")) {

            throw new IllegalArgumentException(
                    "Request must use application/x-www-form-urlencoded data."
            );
        }

        try (InputStream input = exchange.getRequestBody()) {
            String body = new String(
                    input.readAllBytes(),
                    StandardCharsets.UTF_8
            );

            Map<String, String> form = new HashMap<>();

            if (body.isBlank()) {
                return form;
            }

            for (String pair : body.split("&")) {
                int equals = pair.indexOf('=');

                if (equals < 0) {
                    continue;
                }

                String key = URLDecoder.decode(
                        pair.substring(0, equals),
                        StandardCharsets.UTF_8
                );

                String value = URLDecoder.decode(
                        pair.substring(equals + 1),
                        StandardCharsets.UTF_8
                );

                form.put(key, value);
            }

            return form;
        }
    }

    private String required(Map<String, String> form, String key) {
        String value = form.get(key);

        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(
                    "Missing required field: " + key
            );
        }

        return value.trim();
    }

    private boolean handleOptions(HttpExchange exchange) throws IOException {
        if (!"OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
            return false;
        }

        Headers headers = exchange.getResponseHeaders();
        addCorsHeaders(headers);
        exchange.sendResponseHeaders(204, -1);
        exchange.close();
        return true;
    }

    private void sendJson(HttpExchange exchange, int status, String body)
            throws IOException {

        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
        Headers headers = exchange.getResponseHeaders();

        addCorsHeaders(headers);
        headers.set("Content-Type", "application/json; charset=UTF-8");

        exchange.sendResponseHeaders(status, bytes.length);

        try (OutputStream output = exchange.getResponseBody()) {
            output.write(bytes);
        }
    }

    private void addCorsHeaders(Headers headers) {
        headers.set("Access-Control-Allow-Origin", "*");
        headers.set("Access-Control-Allow-Headers", "Content-Type");
        headers.set("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
    }

    private String errorJson(String message) {
        return "{\"success\":false,\"message\":"
                + json(message == null ? "Unknown error." : message)
                + "}";
    }

    private String json(String value) {
        if (value == null) {
            return "null";
        }

        return "\""
                + value
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\r", "\\r")
                .replace("\n", "\\n")
                .replace("\t", "\\t")
                + "\"";
    }

    private String safeMessage(Exception e) {
        String message = e.getMessage();
        return (message == null || message.isBlank())
                ? e.getClass().getSimpleName()
                : message;
    }
}

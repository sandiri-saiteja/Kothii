package com.greencampus;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Locale;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Servlet-based controller for the Green Campus Sustainability Analytics
 * Portal.
 *
 * Base URL: /api/resources /api/consumption-records /api/sustainability-metrics
 *
 * The servlet keeps HTTP routing/controller responsibilities separate from the
 * existing DAO implementation used by the standalone Java CRUD experiment.
 */
@WebServlet(
        name = "GreenCampusControllerServlet",
        urlPatterns = "/api/*",
        loadOnStartup = 1
)
public class GreenCampusControllerServlet extends HttpServlet {

    private final GreenCampusServletService service
            = new GreenCampusServletService();
    private final UserDAO userDAO = new UserDAO();
    private final ReportDAO reportDAO = new ReportDAO();

    @Override
    protected void doOptions(
            HttpServletRequest request,
            HttpServletResponse response) throws IOException {

        applyCors(response);
        response.setStatus(HttpServletResponse.SC_NO_CONTENT);
    }

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response) throws IOException {

        applyCors(response);

        try {
            String[] path = pathParts(request);

            if (path.length == 1 && "health".equals(path[0])) {
                sendJson(response, HttpServletResponse.SC_OK,
                        "{\"success\":true,\"message\":\"Green Campus Servlet is running.\"}");
                return;
            }

            if (path.length == 1 && "resources".equals(path[0])) {
                sendJson(response, HttpServletResponse.SC_OK,
                        resourcesJson(service.listResources()));
                return;
            }

            if (path.length == 2 && "resources".equals(path[0])) {
                Resource resource = service.getResource(parseId(path[1], "resourceId"));
                if (resource == null) {
                    notFound(response, "Resource not found.");
                    return;
                }
                sendJson(response, HttpServletResponse.SC_OK, resourceJson(resource));
                return;
            }

            if (path.length == 1 && "consumption-records".equals(path[0])) {
                sendJson(response, HttpServletResponse.SC_OK,
                        consumptionJson(service.listConsumptionRecords()));
                return;
            }

            if (path.length == 2 && "consumption-records".equals(path[0])) {
                ConsumptionRecord record = service.getConsumptionRecord(
                        parseId(path[1], "recordId")
                );
                if (record == null) {
                    notFound(response, "Consumption record not found.");
                    return;
                }
                sendJson(response, HttpServletResponse.SC_OK, consumptionRecordJson(record));
                return;
            }

            if (path.length == 1 && isMetricCollection(path[0])) {
                sendJson(response, HttpServletResponse.SC_OK,
                        metricsJson(service.listMetrics()));
                return;
            }

            if (path.length == 2 && isMetricCollection(path[0])) {
                SustainabilityMetric metric = service.getMetric(
                        parseId(path[1], "metricId")
                );
                if (metric == null) {
                    notFound(response, "Sustainability metric not found.");
                    return;
                }
                sendJson(response, HttpServletResponse.SC_OK, metricJson(metric));
                return;
            }

            notFound(response, "API endpoint not found.");

        } catch (IllegalArgumentException e) {
            sendError(response, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        } catch (SQLException e) {
            serverError(response, e);
        }
    }

    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response) throws IOException {

        applyCors(response);

        try {
            String[] path = pathParts(request);
            FormData form = new FormData(request);

            if (path.length == 1 && "register".equals(path[0])) {
                registerUser(form, response);
                return;
            }

            if (path.length == 1 && "login".equals(path[0])) {
                loginUser(form, response);
                return;
            }

            if (path.length == 1 && "report".equals(path[0])) {
                saveReport(form, response);
                return;
            }

            if (path.length == 1 && "resources".equals(path[0])) {
                Resource resource = resourceFromForm(form);
                Resource created = service.createResource(resource);
                sendJson(response, HttpServletResponse.SC_CREATED,
                        successObject("Resource created successfully.", resourceJson(created)));
                return;
            }

            if (path.length == 1 && "consumption-records".equals(path[0])) {
                ConsumptionRecord record = consumptionFromForm(form);
                ConsumptionRecord created = service.createConsumptionRecord(record);
                sendJson(response, HttpServletResponse.SC_CREATED,
                        successObject("Consumption record created successfully.",
                                consumptionRecordJson(created)));
                return;
            }

            if (path.length == 1 && isMetricCollection(path[0])) {
                SustainabilityMetric metric = metricFromForm(form);
                SustainabilityMetric created = service.createMetric(metric);
                sendJson(response, HttpServletResponse.SC_CREATED,
                        successObject("Sustainability metric created successfully.",
                                metricJson(created)));
                return;
            }

            notFound(response, "API endpoint not found.");

        } catch (IllegalArgumentException e) {
            sendError(response, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        } catch (SQLException e) {
            serverError(response, e);
        }
    }

    private void registerUser(
        FormData form,
        HttpServletResponse response) throws IOException {

    try {
        String name = form.required("name");
        String userCode = form.required("id");
        String email = form.required("email").toLowerCase();
        String department = form.required("department");
        String campus = form.required("campus");
        String password = form.required("password");

        if (userDAO.emailExists(email)) {
            sendError(response,
                    HttpServletResponse.SC_CONFLICT,
                    "An account with this email already exists.");
            return;
        }

        if (userDAO.userCodeExists(userCode)) {
            sendError(response,
                    HttpServletResponse.SC_CONFLICT,
                    "This Student / Employee ID is already registered.");
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

        String json =
                "{\"success\":true"
                + ",\"message\":" + quote(
                        "Registration saved in MySQL successfully.")
                + ",\"user\":{"
                + "\"userId\":" + user.getUserId()
                + ",\"name\":" + quote(user.getFullName())
                + ",\"id\":" + quote(user.getUserCode())
                + ",\"email\":" + quote(user.getEmail())
                + ",\"department\":" + quote(user.getDepartment())
                + ",\"campus\":" + quote(user.getCampus())
                + "}}";

        sendJson(response, HttpServletResponse.SC_CREATED, json);

    } catch (IllegalArgumentException e) {
        sendError(response,
                HttpServletResponse.SC_BAD_REQUEST,
                e.getMessage());

    } catch (SQLException e) {
        serverError(response, e);
    }
}

private void loginUser(
        FormData form,
        HttpServletResponse response) throws IOException {

    try {
        String email = form.required("email").toLowerCase();
        String password = form.required("password");

        User user = userDAO.findByEmail(email);

        if (user == null ||
                !SecurityUtil.sha256(password)
                        .equals(user.getPasswordHash())) {

            sendError(response,
                    HttpServletResponse.SC_UNAUTHORIZED,
                    "Invalid email or password.");
            return;
        }

        String json =
                "{\"success\":true"
                + ",\"message\":" + quote("Login successful.")
                + ",\"user\":{"
                + "\"userId\":" + user.getUserId()
                + ",\"name\":" + quote(user.getFullName())
                + ",\"id\":" + quote(user.getUserCode())
                + ",\"email\":" + quote(user.getEmail())
                + ",\"department\":" + quote(user.getDepartment())
                + ",\"campus\":" + quote(user.getCampus())
                + "}}";

        sendJson(response, HttpServletResponse.SC_OK, json);

    } catch (IllegalArgumentException e) {
        sendError(response,
                HttpServletResponse.SC_BAD_REQUEST,
                e.getMessage());

    } catch (SQLException e) {
        serverError(response, e);
    }
}

private void saveReport(
        FormData form,
        HttpServletResponse response) throws IOException {

    try {
        String email = form.required("email").toLowerCase();
        String metric = form.required("metric");
        double value = form.doubleValue("value");

        LocalDate date = GreenCampusServletService.parseDate(
                form.required("date"),
                "date"
        );

        String description = form.required("description");

        GreenCampusServletService.requirePositive(
                value,
                "value"
        );

        int reportId = reportDAO.createReport(
                email,
                metric,
                value,
                date,
                description
        );

        String json =
                "{\"success\":true"
                + ",\"message\":" + quote(
                        "Report saved in MySQL successfully.")
                + ",\"reportId\":" + reportId
                + "}";

        sendJson(response, HttpServletResponse.SC_CREATED, json);

    } catch (NumberFormatException | DateTimeParseException e) {
        sendError(response,
                HttpServletResponse.SC_BAD_REQUEST,
                "Invalid report value or date.");

    } catch (IllegalArgumentException e) {
        sendError(response,
                HttpServletResponse.SC_BAD_REQUEST,
                e.getMessage());

    } catch (SQLException e) {
        serverError(response, e);
    }
}

    @Override
    protected void doPut(
            HttpServletRequest request,
            HttpServletResponse response) throws IOException {

        applyCors(response);

        try {
            String[] path = pathParts(request);
            if (path.length != 2) {
                sendError(response, HttpServletResponse.SC_BAD_REQUEST,
                        "PUT requires an ID in the URL.");
                return;
            }

            FormData form = new FormData(request);

            if ("resources".equals(path[0])) {
                Resource resource = resourceFromForm(form);
                resource.setResourceId(parseId(path[1], "resourceId"));

                if (!service.updateResource(resource)) {
                    notFound(response, "Resource not found.");
                    return;
                }

                sendJson(response, HttpServletResponse.SC_OK,
                        successObject("Resource updated successfully.",
                                resourceJson(resource)));
                return;
            }

            if ("consumption-records".equals(path[0])) {
                ConsumptionRecord record = consumptionFromForm(form);
                record.setRecordId(parseId(path[1], "recordId"));

                if (!service.updateConsumptionRecord(record)) {
                    notFound(response, "Consumption record not found.");
                    return;
                }

                sendJson(response, HttpServletResponse.SC_OK,
                        successObject("Consumption record updated successfully.",
                                consumptionRecordJson(record)));
                return;
            }

            if (isMetricCollection(path[0])) {
                SustainabilityMetric metric = metricFromForm(form);
                metric.setMetricId(parseId(path[1], "metricId"));

                if (!service.updateMetric(metric)) {
                    notFound(response, "Sustainability metric not found.");
                    return;
                }

                sendJson(response, HttpServletResponse.SC_OK,
                        successObject("Sustainability metric updated successfully.",
                                metricJson(metric)));
                return;
            }

            notFound(response, "API endpoint not found.");

        } catch (IllegalArgumentException e) {
            sendError(response, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        } catch (SQLException e) {
            serverError(response, e);
        }
    }

    @Override
    protected void doDelete(
            HttpServletRequest request,
            HttpServletResponse response) throws IOException {

        applyCors(response);

        try {
            String[] path = pathParts(request);
            if (path.length != 2) {
                sendError(response, HttpServletResponse.SC_BAD_REQUEST,
                        "DELETE requires an ID in the URL.");
                return;
            }

            int id;

            if ("resources".equals(path[0])) {
                id = parseId(path[1], "resourceId");
                if (!service.deleteResource(id)) {
                    notFound(response, "Resource not found.");
                    return;
                }
                sendJson(response, HttpServletResponse.SC_OK,
                        successMessage("Resource deleted successfully."));
                return;
            }

            if ("consumption-records".equals(path[0])) {
                id = parseId(path[1], "recordId");
                if (!service.deleteConsumptionRecord(id)) {
                    notFound(response, "Consumption record not found.");
                    return;
                }
                sendJson(response, HttpServletResponse.SC_OK,
                        successMessage("Consumption record deleted successfully."));
                return;
            }

            if (isMetricCollection(path[0])) {
                id = parseId(path[1], "metricId");
                if (!service.deleteMetric(id)) {
                    notFound(response, "Sustainability metric not found.");
                    return;
                }
                sendJson(response, HttpServletResponse.SC_OK,
                        successMessage("Sustainability metric deleted successfully."));
                return;
            }

            notFound(response, "API endpoint not found.");

        } catch (IllegalArgumentException e) {
            sendError(response, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        } catch (SQLException e) {
            serverError(response, e);
        }
            }

    private Resource resourceFromForm(FormData form) {
        String name = form.required("resourceName");
        String type = form.required("resourceType");
        String unit = form.required("unit");
        String description = form.optional("description");

        return new Resource(name, type, unit, description);
    }

    private ConsumptionRecord consumptionFromForm(FormData form) {
        int resourceId = GreenCampusServletService.positiveId(
                form.intValue("resourceId"), "resourceId"
        );
        double value = form.doubleValue("consumptionValue");
        GreenCampusServletService.requirePositive(value, "consumptionValue");
        LocalDate date = GreenCampusServletService.parseDate(
                form.required("consumptionDate"), "consumptionDate"
        );
        String location = form.required("campusLocation");

        return new ConsumptionRecord(resourceId, value, date, location);
    }

    private SustainabilityMetric metricFromForm(FormData form) {
        String name = form.required("metricName");
        double value = form.doubleValue("metricValue");
        GreenCampusServletService.requirePositive(value, "metricValue");
        String unit = form.required("unit");
        LocalDate date = GreenCampusServletService.parseDate(
                form.required("metricDate"), "metricDate"
        );
        String description = form.optional("description");

        return new SustainabilityMetric(name, value, unit, date, description);
    }

    private boolean isMetricCollection(String value) {
        return "sustainability-metrics".equals(value)
                || "metrics".equals(value);
    }

    private int parseId(String value, String field) {
        try {
            return GreenCampusServletService.positiveId(
                    Integer.parseInt(value), field
            );
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(
                    field + " must be a positive integer."
            );
        }
    }

    private String[] pathParts(HttpServletRequest request) {
        String path = request.getPathInfo();

        if (path == null || path.isBlank() || "/".equals(path)) {
            return new String[0];
        }

        return path.substring(1).split("/");
    }

    private void applyCors(HttpServletResponse response) {
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type");
        response.setHeader("Access-Control-Allow-Methods",
                "GET, POST, PUT, DELETE, OPTIONS");
    }

    private void sendJson(HttpServletResponse response, int status, String body)
            throws IOException {
        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
        response.setStatus(status);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType("application/json");
        response.getOutputStream().write(bytes);
    }

    private void sendError(HttpServletResponse response, int status, String message)
            throws IOException {
        sendJson(response, status,
                "{\"success\":false,\"message\":" + quote(message) + "}");
    }

    private void notFound(HttpServletResponse response, String message)
            throws IOException {
        sendError(response, HttpServletResponse.SC_NOT_FOUND, message);
    }

    private void serverError(HttpServletResponse response, SQLException e)
            throws IOException {
        String message = e.getMessage();
        if (message == null || message.isBlank()) {
            message = e.getClass().getSimpleName();
        }
        sendError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                "Database error: " + message);
    }

    private String successMessage(String message) {
        return "{\"success\":true,\"message\":" + quote(message) + "}";
    }

    private String successObject(String message, String objectJson) {
        return "{\"success\":true,\"message\":" + quote(message)
                + ",\"data\":" + objectJson + "}";
    }

    private String resourcesJson(List<Resource> resources) {
        StringBuilder json = new StringBuilder("[");
        for (int i = 0; i < resources.size(); i++) {
            if (i > 0) {
                json.append(',');
            }
            json.append(resourceJson(resources.get(i)));
        }
        return json.append(']').toString();
    }

    private String resourceJson(Resource resource) {
        return "{"
                + "\"resourceId\":" + resource.getResourceId() + ","
                + "\"resourceName\":" + quote(resource.getResourceName()) + ","
                + "\"resourceType\":" + quote(resource.getResourceType()) + ","
                + "\"unit\":" + quote(resource.getUnit()) + ","
                + "\"description\":" + quote(resource.getDescription())
                + "}";
    }

    private String consumptionJson(List<ConsumptionRecord> records) {
        StringBuilder json = new StringBuilder("[");
        for (int i = 0; i < records.size(); i++) {
            if (i > 0) {
                json.append(',');
            }
            json.append(consumptionRecordJson(records.get(i)));
        }
        return json.append(']').toString();
    }

    private String consumptionRecordJson(ConsumptionRecord record) {
        return "{"
                + "\"recordId\":" + record.getRecordId() + ","
                + "\"resourceId\":" + record.getResourceId() + ","
                + "\"consumptionValue\":" + number(record.getConsumptionValue()) + ","
                + "\"consumptionDate\":" + quote(record.getConsumptionDate()) + ","
                + "\"campusLocation\":" + quote(record.getCampusLocation())
                + "}";
    }

    private String metricsJson(List<SustainabilityMetric> metrics) {
        StringBuilder json = new StringBuilder("[");
        for (int i = 0; i < metrics.size(); i++) {
            if (i > 0) {
                json.append(',');
            }
            json.append(metricJson(metrics.get(i)));
        }
        return json.append(']').toString();
    }

    private String metricJson(SustainabilityMetric metric) {
        return "{"
                + "\"metricId\":" + metric.getMetricId() + ","
                + "\"metricName\":" + quote(metric.getMetricName()) + ","
                + "\"metricValue\":" + number(metric.getMetricValue()) + ","
                + "\"unit\":" + quote(metric.getUnit()) + ","
                + "\"metricDate\":" + quote(metric.getMetricDate()) + ","
                + "\"description\":" + quote(metric.getDescription())
                + "}";
    }

    private String number(double value) {
        return BigDecimal.valueOf(value).stripTrailingZeros().toPlainString();
    }

    private String quote(Object value) {
        if (value == null) {
            return "null";
        }

        String text = String.valueOf(value);
        return "\""
                + text.replace("\\", "\\\\")
                        .replace("\"", "\\\"")
                        .replace("\r", "\\r")
                        .replace("\n", "\\n")
                        .replace("\t", "\\t")
                + "\"";
    }

    /**
     * Small form-url-encoded parser shared by POST and PUT.
     */
    private static final class FormData {

        private final java.util.Map<String, String> values = new java.util.HashMap<>();

        FormData(HttpServletRequest request) throws IOException {
            String contentType = request.getContentType();

            if (contentType == null
                    || !contentType.toLowerCase(Locale.ROOT)
                            .startsWith("application/x-www-form-urlencoded")) {
                throw new IllegalArgumentException(
                        "Use application/x-www-form-urlencoded request data."
                );
            }

            String body = new String(
                    request.getInputStream().readAllBytes(),
                    StandardCharsets.UTF_8
            );

            if (body.isBlank()) {
                return;
            }

            for (String pair : body.split("&")) {
                int equals = pair.indexOf('=');
                if (equals < 0) {
                    continue;
                }

                try {
                    String key = java.net.URLDecoder.decode(
                            pair.substring(0, equals), StandardCharsets.UTF_8
                    );
                    String value = java.net.URLDecoder.decode(
                            pair.substring(equals + 1), StandardCharsets.UTF_8
                    );
                    values.put(key, value);
                } catch (IllegalArgumentException e) {
                    throw new IllegalArgumentException("Invalid form encoding.");
                }
            }
        }

        String required(String key) {
            String value = values.get(key);
            if (value == null || value.isBlank()) {
                throw new IllegalArgumentException(key + " is required.");
            }
            return value.trim();
        }

        String optional(String key) {
            String value = values.get(key);
            return value == null ? "" : value.trim();
        }

        int intValue(String key) {
            try {
                return Integer.parseInt(required(key));
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException(key + " must be a valid integer.");
            }
        }

        double doubleValue(String key) {
            try {
                return Double.parseDouble(required(key));
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException(key + " must be a valid number.");
            }
        }
    }
}

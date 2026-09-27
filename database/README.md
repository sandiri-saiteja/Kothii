# Green Campus Database Setup

This folder recreates the complete MySQL database needed by the existing Green Campus project.

## 1. Create the database

Open MySQL Workbench or the MySQL command line and run:

```sql
SOURCE C:/path/to/Kothii/database/green_campus.sql;
```

Or open `green_campus.sql` in MySQL Workbench and execute the complete script.

The script creates:

- `users` — web registration/login accounts
- `resources` — Java CRUD resources
- `consumption_records` — Java CRUD consumption records
- `sustainability_metrics` — Java CRUD sustainability metrics
- `sustainability_reports` — web report submissions linked to users

It also inserts sample rows for the existing Java CRUD stage.

## 2. Configure the MySQL password

From PowerShell, inside `Kothii/java`:

```powershell
$env:GREEN_CAMPUS_DB_PASSWORD="YOUR_MYSQL_PASSWORD"
```

The project connects with MySQL user `root` and database `green_campus`.

## 3. Compile

```powershell
mvn clean compile
```

## 4. Start the web database API

```powershell
mvn exec:java "-Dexec.mainClass=com.greencampus.ApiServer"
```

Keep that terminal open. The API runs at:

`http://localhost:8080`

Test it in the browser:

`http://localhost:8080/api/health`

You should receive a JSON success message.

## 5. Start the website

Use the same VS Code Live Server workflow used in the previous stages and open `index.html`.

The existing UI, Bootstrap design, CSS3/Flexbox/Grid work, JavaScript validation, ES6 weather module, Chart.js graphs, and XML/DTD/XSD files remain in the project.

Registration and Login now use the Java API and store account data in MySQL. The report form also saves submissions to MySQL. Only a safe user profile is kept in browser `localStorage`; the password is not stored there.

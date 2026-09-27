# Green Campus Sustainability Analytics Portal — ES6 Global Weather Edition

This version is based on the client-side-validation project and adds the missing ES6 weather/graph requirement.

## New features
- Global location search using Open-Meteo Geocoding API.
- Search by city/place/postal location or latitude,longitude.
- Live current temperature, humidity, wind speed and condition.
- 24-hour environmental graph using Chart.js.
- Weather graph shown next to campus sustainability metrics.
- ES6 module `js/weather.js` using `const`, `let`, arrow functions, async/await, template literals, destructuring, Map, URLSearchParams, spread and array methods.

## Run
Use VS Code Live Server or host the folder on GitHub Pages. Do not rely on opening `report.html` directly with `file://`, because browser security can block module/API requests.

## API
Open-Meteo Geocoding and Forecast APIs are used. No API key is required for the public/free use described by the provider.

## MySQL Database Integration (Added Without Removing Previous Stages)

The project now includes a `database/green_campus.sql` recreation script and a small Java HTTP API.

### Database tables

- `users` — web registration/login accounts
- `resources` — existing Java CRUD resources
- `consumption_records` — existing Java CRUD consumption records
- `sustainability_metrics` — existing Java CRUD sustainability metrics
- `sustainability_reports` — web sustainability report submissions linked to registered users

### Run the API

From `Kothii/java` in PowerShell:

```powershell
$env:GREEN_CAMPUS_DB_PASSWORD="YOUR_MYSQL_PASSWORD"
mvn clean compile
mvn exec:java "-Dexec.mainClass=com.greencampus.ApiServer"
```

Keep the API terminal open and run the website with Live Server as before.

### Data flow

`Register page → Java API → MySQL users table`

`Login page → Java API → MySQL users table`

`Report page → Java API → MySQL sustainability_reports table`

The previous Java console CRUD stage continues to use `resources`, `consumption_records`, and `sustainability_metrics` unchanged.

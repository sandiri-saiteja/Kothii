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

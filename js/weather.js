// ==========================================================
// Global Weather + Environmental Graph
// ES6 features: const/let, arrow functions, async/await,
// template literals, destructuring, spread, map and modules.
// ==========================================================

const GEOCODING_URL = "https://geocoding-api.open-meteo.com/v1/search";
const WEATHER_URL = "https://api.open-meteo.com/v1/forecast";

let environmentChart = null;
let suggestionResults = [];

const $ = (id) => document.getElementById(id);

const weatherCodeMap = new Map([
    [0, "Clear sky"],
    [1, "Mainly clear"],
    [2, "Partly cloudy"],
    [3, "Overcast"],
    [45, "Fog"],
    [48, "Rime fog"],
    [51, "Light drizzle"],
    [53, "Moderate drizzle"],
    [55, "Dense drizzle"],
    [56, "Light freezing drizzle"],
    [57, "Dense freezing drizzle"],
    [61, "Slight rain"],
    [63, "Moderate rain"],
    [65, "Heavy rain"],
    [66, "Light freezing rain"],
    [67, "Heavy freezing rain"],
    [71, "Slight snow"],
    [73, "Moderate snow"],
    [75, "Heavy snow"],
    [77, "Snow grains"],
    [80, "Slight rain showers"],
    [81, "Moderate rain showers"],
    [82, "Violent rain showers"],
    [85, "Slight snow showers"],
    [86, "Heavy snow showers"],
    [95, "Thunderstorm"],
    [96, "Thunderstorm with slight hail"],
    [99, "Thunderstorm with heavy hail"]
]);

const setStatus = (message, type = "secondary") => {
    const status = $("weatherStatus");
    status.className = `alert alert-${type} mt-4 mb-0`;
    status.textContent = message;
};

const isCoordinateQuery = (value) => {
    const parts = value.split(",").map((part) => Number(part.trim()));
    return parts.length === 2 && parts.every(Number.isFinite) && parts[0] >= -90 && parts[0] <= 90 && parts[1] >= -180 && parts[1] <= 180;
};

const parseCoordinates = (value) => {
    const [latitude, longitude] = value.split(",").map((part) => Number(part.trim()));
    return { latitude, longitude };
};

const buildLocationLabel = ({ name, admin1, country }) =>
    [name, admin1, country].filter(Boolean).join(", ");

const fetchJson = async (url) => {
    const response = await fetch(url);
    if (!response.ok) throw new Error(`Request failed (${response.status})`);
    return response.json();
};

const geocodeLocation = async (query) => {
    const params = new URLSearchParams({
        name: query,
        count: "8",
        language: "en",
        format: "json"
    });

    const data = await fetchJson(`${GEOCODING_URL}?${params}`);
    return data.results ?? [];
};

const fetchWeather = async ({ latitude, longitude }) => {
    const params = new URLSearchParams({
        latitude: String(latitude),
        longitude: String(longitude),
        current: [
            "temperature_2m",
            "relative_humidity_2m",
            "apparent_temperature",
            "wind_speed_10m",
            "weather_code"
        ].join(","),
        hourly: [
            "temperature_2m",
            "relative_humidity_2m",
            "wind_speed_10m"
        ].join(","),
        forecast_days: "2",
        timezone: "auto"
    });

    return fetchJson(`${WEATHER_URL}?${params}`);
};

const getCondition = (code) => weatherCodeMap.get(code) ?? "Unknown";

const formatHour = (isoTime) => {
    const date = new Date(isoTime);
    return date.toLocaleTimeString([], { hour: "numeric", minute: "2-digit" });
};

const renderCurrentWeather = ({ current, locationLabel, timezone }) => {
    const {
        temperature_2m: temperature,
        relative_humidity_2m: humidity,
        wind_speed_10m: wind,
        weather_code: weatherCode
    } = current;

    $("temperatureValue").textContent = `${temperature} °C`;
    $("humidityValue").textContent = `${humidity} %`;
    $("windValue").textContent = `${wind} km/h`;
    $("conditionValue").textContent = getCondition(weatherCode);
    $("selectedLocation").textContent = locationLabel;
    $("updatedTime").textContent = `Timezone: ${timezone}`;
};

const renderChart = ({ hourly }) => {
    const hoursToShow = 24;
    const times = hourly.time.slice(0, hoursToShow);
    const temperatures = hourly.temperature_2m.slice(0, hoursToShow);
    const humidity = hourly.relative_humidity_2m.slice(0, hoursToShow);
    const wind = hourly.wind_speed_10m.slice(0, hoursToShow);

    const datasets = [
        {
            label: "Temperature (°C)",
            data: [...temperatures],
            borderWidth: 3,
            tension: 0.35,
            yAxisID: "y"
        },
        {
            label: "Humidity (%)",
            data: [...humidity],
            borderWidth: 3,
            tension: 0.35,
            yAxisID: "y1"
        },
        {
            label: "Wind (km/h)",
            data: [...wind],
            borderWidth: 2,
            borderDash: [6, 4],
            tension: 0.35,
            yAxisID: "y"
        }
    ];

    environmentChart?.destroy();

    environmentChart = new Chart($("environmentChart"), {
        type: "line",
        data: {
            labels: times.map(formatHour),
            datasets
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            interaction: { mode: "index", intersect: false },
            plugins: {
                legend: { position: "top" },
                tooltip: { usePointStyle: true }
            },
            scales: {
                y: {
                    title: { display: true, text: "Temperature / Wind" },
                    beginAtZero: false
                },
                y1: {
                    position: "right",
                    min: 0,
                    max: 100,
                    title: { display: true, text: "Humidity (%)" },
                    grid: { drawOnChartArea: false }
                }
            }
        }
    });
};

const displaySuggestions = (results) => {
    const container = $("locationSuggestions");
    suggestionResults = results;
    container.innerHTML = "";

    if (!results.length) {
        container.classList.add("d-none");
        return;
    }

    results.forEach((result, index) => {
        const button = document.createElement("button");
        button.type = "button";
        button.className = "list-group-item list-group-item-action";
        button.dataset.index = String(index);
        button.innerHTML = `<strong>${result.name}</strong><br><small>${buildLocationLabel(result)}</small>`;
        container.appendChild(button);
    });

    container.classList.remove("d-none");
};

const loadWeather = async (location) => {
    try {
        setStatus("Loading live weather data...", "info");

        const weather = await fetchWeather(location);
        const locationLabel = location.label ?? `${location.latitude.toFixed(4)}, ${location.longitude.toFixed(4)}`;

        renderCurrentWeather({
            current: weather.current,
            locationLabel,
            timezone: weather.timezone
        });

        renderChart(weather);
        setStatus(`Weather loaded successfully for ${locationLabel}.`, "success");
    } catch (error) {
        console.error(error);
        setStatus("Unable to load weather. Check the location and your internet connection.", "danger");
    }
};

const handleSearch = async (event) => {
    event.preventDefault();

    const input = $("locationInput");
    const query = input.value.trim();
    $("locationSuggestions").classList.add("d-none");

    if (query.length < 2) {
        input.classList.add("is-invalid");
        $("locationFeedback").textContent = "Enter at least 2 characters or valid latitude,longitude coordinates.";
        return;
    }

    input.classList.remove("is-invalid");

    if (isCoordinateQuery(query)) {
        const coordinates = parseCoordinates(query);
        await loadWeather({ ...coordinates, label: `${coordinates.latitude}, ${coordinates.longitude}` });
        return;
    }

    try {
        setStatus("Finding the location worldwide...", "info");
        const results = await geocodeLocation(query);

        if (!results.length) {
            setStatus("No matching location found. Try a city, country, postal code, or coordinates.", "warning");
            return;
        }

        const [first] = results;
        displaySuggestions(results);
        await loadWeather({
            latitude: first.latitude,
            longitude: first.longitude,
            label: buildLocationLabel(first)
        });
    } catch (error) {
        console.error(error);
        setStatus("Location search failed. Please check your internet connection.", "danger");
    }
};

$("weatherSearchForm")?.addEventListener("submit", handleSearch);

$("locationInput")?.addEventListener("input", () => {
    $("locationInput").classList.remove("is-invalid");
    $("locationSuggestions").classList.add("d-none");
});

$("locationSuggestions")?.addEventListener("click", async (event) => {
    const button = event.target.closest("button[data-index]");
    if (!button) return;

    const result = suggestionResults[Number(button.dataset.index)];
    if (!result) return;

    $("locationInput").value = buildLocationLabel(result);
    $("locationSuggestions").classList.add("d-none");

    await loadWeather({
        latitude: result.latitude,
        longitude: result.longitude,
        label: buildLocationLabel(result)
    });
});

// Load a useful default location without limiting the user to it.
loadWeather({ latitude: 17.3850, longitude: 78.4867, label: "Hyderabad, India (default)" });

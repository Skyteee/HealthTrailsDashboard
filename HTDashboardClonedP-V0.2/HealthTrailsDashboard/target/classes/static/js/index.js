function setCookie(name, value, days) {
    const expires = new Date(Date.now() + days * 864e5).toUTCString();
    document.cookie = name + '=' + encodeURIComponent(value) + '; expires=' + expires + '; path=/';
}

function getCookie(name) {
    return document.cookie.split('; ').reduce((r, v) => {
        const parts = v.split('=');
        return parts[0] === name ? decodeURIComponent(parts[1]) : r;
    }, '');
}

function initMap() {
    const defaultLat = 49.446; // Default latitude
    const defaultLon = 8.6116; // Default longitude
    const defaultZoom = 13;    // Default zoom level

    // Retrieve coordinates and zoom level from cookies
    const latCookie = getCookie('lat');
    const lonCookie = getCookie('lon');
    const zoomCookie = getCookie('zoom');
    const mapCenter = latCookie && lonCookie ? [parseFloat(latCookie), parseFloat(lonCookie)] : [defaultLat, defaultLon];
    const mapZoom = zoomCookie ? parseInt(zoomCookie, 10) : defaultZoom;

    // Create a map instance with initial coordinates and zoom level
    const map = L.map('map').setView(mapCenter, mapZoom);

    // Add OpenStreetMap tile layer
    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
        maxZoom: 19,
        attribution: '&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
    }).addTo(map);

    // Initialize marker at the map center
    let marker = L.marker(mapCenter).addTo(map).bindPopup('You clicked here!')

    // Save zoom level whenever the user zooms
    map.on('zoomend', function() {
        setCookie('zoom', map.getZoom(), 7); // Store zoom level for 7 days
    });

    // Add a click event listener to the map
    map.on('click', function(e) {
        const lat = e.latlng.lat;
        const lon = e.latlng.lng;

        // Store coordinates and zoom level in cookies
        setCookie('lat', lat, 7); // Store for 7 days
        setCookie('lon', lon, 7); // Store for 7 days

        // Move the existing marker to the new position
        marker.setLatLng([lat, lon]).update();

        fetch(`/air-data/air-quality?lat=${lat}&lon=${lon}`)
            .then(response => {
                if (!response.ok) {
                    throw new Error('Network response was not ok ' + response.statusText);
                }
                return response.json();
            })
            .then(data => {
                // Update placeholders with air quality data
                document.getElementById("latitude").textContent = data.lat;
                document.getElementById("longitude").textContent = data.lon;
                document.getElementById("aqi").textContent = data.aqi;

                // Create an object to hold air quality values
                const airQualityValues = {
                    co: parseFloat(data.co),
                    no: parseFloat(data.no),
                    no2: parseFloat(data.no2),
                    o3: parseFloat(data.o3),
                    so2: parseFloat(data.so2),
                    pm25: parseFloat(data.pm25),
                    pm10: parseFloat(data.pm10),
                    nh3: parseFloat(data.nh3),
                    aqi: parseFloat(data.aqi),
                };

                // If marker does not exist, create it
                if (!marker) {
                    marker = L.marker([lat, lon]).addTo(map).bindPopup('You clicked here!').openPopup();
                } else {
                    // Move the existing marker to the new position
                    marker.setLatLng([lat, lon]).update();
                }

                const updateDataItem = (key, value) => {
                    const element = document.getElementById(key);
                    element.textContent = value.toFixed(2); // Display the value

                    // Remove old classes for all metrics
                    element.parentElement.classList.remove('good', 'fair', 'moderate', 'poor', 'veryPoor');

                    if (key === 'aqi') {
                        const aqiContainer = document.getElementById('aqiContainer');
                        // Remove all previous AQI classes
                        aqiContainer.classList.remove('good', 'fair', 'moderate', 'poor', 'veryPoor');

                        // Add the new AQI class based on the AQI value
                        if (value === 1) {
                            aqiContainer.classList.add('good');
                        } else if (value === 2) {
                            aqiContainer.classList.add('fair');
                        } else if (value === 3) {
                            aqiContainer.classList.add('moderate');
                        } else if (value === 4) {
                            aqiContainer.classList.add('poor');
                        } else if (value === 5 || value === 6) {
                            aqiContainer.classList.add('veryPoor');
                        }
                    }

                    // For other pollutants
                    if (key === 'so2') {
                        if (value < 20) {
                            element.parentElement.classList.add('good');
                        } else if (value < 80) {
                            element.parentElement.classList.add('fair');
                        } else if (value < 250) {
                            element.parentElement.classList.add('moderate');
                        } else if (value < 350) {
                            element.parentElement.classList.add('poor');
                        } else {
                            element.parentElement.classList.add('veryPoor');
                        }
                    } else if (key === 'no2') {
                        if (value < 40) {
                            element.parentElement.classList.add('good');
                        } else if (value < 70) {
                            element.parentElement.classList.add('fair');
                        } else if (value < 150) {
                            element.parentElement.classList.add('moderate');
                        } else if (value < 200) {
                            element.parentElement.classList.add('poor');
                        } else {
                            element.parentElement.classList.add('veryPoor');
                        }
                    } else if (key === 'pm10') {
                        if (value < 20) {
                            element.parentElement.classList.add('good');
                        } else if (value < 50) {
                            element.parentElement.classList.add('fair');
                        } else if (value < 100) {
                            element.parentElement.classList.add('moderate');
                        } else if (value < 200) {
                            element.parentElement.classList.add('poor');
                        } else {
                            element.parentElement.classList.add('veryPoor');
                        }
                    } else if (key === 'pm25') {
                        if (value < 10) {
                            element.parentElement.classList.add('good');
                        } else if (value < 25) {
                            element.parentElement.classList.add('fair');
                        } else if (value < 50) {
                            element.parentElement.classList.add('moderate');
                        } else if (value < 75) {
                            element.parentElement.classList.add('poor');
                        } else {
                            element.parentElement.classList.add('veryPoor');
                        }
                    } else if (key === 'o3') {
                        if (value < 60) {
                            element.parentElement.classList.add('good');
                        } else if (value < 100) {
                            element.parentElement.classList.add('fair');
                        } else if (value < 140) {
                            element.parentElement.classList.add('moderate');
                        } else if (value < 180) {
                            element.parentElement.classList.add('poor');
                        } else {
                            element.parentElement.classList.add('veryPoor');
                        }
                    } else if (key === 'co') {
                        if (value < 4400) {
                            element.parentElement.classList.add('good');
                        } else if (value < 9400) {
                            element.parentElement.classList.add('fair');
                        } else if (value < 12400) {
                            element.parentElement.classList.add('moderate');
                        } else if (value < 15400) {
                            element.parentElement.classList.add('poor');
                        } else {
                            element.parentElement.classList.add('veryPoor');
                        }
                    } else if (key === 'nh3') {
                        if (value < 10) {
                            element.parentElement.classList.add('good');
                        } else if (value < 25) {
                            element.parentElement.classList.add('fair');
                        } else if (value < 50) {
                            element.parentElement.classList.add('moderate');
                        } else if (value < 100) {
                            element.parentElement.classList.add('poor');
                        } else {
                            element.parentElement.classList.add('veryPoor');
                        }
                    } else if (key === 'no') {
                        if (value < 40) {
                            element.parentElement.classList.add('good');
                        } else if (value < 70) {
                            element.parentElement.classList.add('fair');
                        } else if (value < 150) {
                            element.parentElement.classList.add('moderate');
                        } else if (value < 200) {
                            element.parentElement.classList.add('poor');
                        } else {
                            element.parentElement.classList.add('veryPoor');
                        }
                    }
                };

// Update each air quality data item
                for (const [key, value] of Object.entries(airQualityValues)) {
                    updateDataItem(key, value);
                }

            })
            .catch(error => console.error('Error fetching air quality data:', error));
    });
}

document.addEventListener("DOMContentLoaded", initMap);

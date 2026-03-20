Act as an expert Database Architect and Full-Stack Developer. I am building a running community application using Spring Boot, Angular, and PostgreSQL. I want to build a "Geospatial Route Discovery" feature.

I need you to write the code and provide the technical architecture. Currently, users just run and log distances. I want them to discover popular running routes nearby, see elevation profiles, and view a heat map of where the community runs the most.

Please review the requirements below and generate the necessary architecture, queries, and code.

1. Feature Overview & Concepts
Route Discovery: Users open a map and see the top 5 running routes within a 5km radius of their current location.
Community Heatmap: A visual overlay on the map showing the most heavily trafficked running paths in the city.
Segment Leaderboards: Specific stretches of a route (e.g., a steep 1km hill) where users automatically compete for the fastest time.

2. Technical & Architecture Constraints
PostGIS Integration: You must migrate the current `VARCHAR(300)` location data to PostGIS `geometry`/`geography` types.
Spatial Queries: Write the exact PostgreSQL/PostGIS queries required to find overlapping routes, calculate the distance between routes, and find activities that cross a specific "Segment" polygon.
Performance: Spatial queries can be slow. Outline indexing strategies (e.g., GiST indexes) and caching mechanisms to ensure the map loads quickly.

3. UI/UX Requirements
Interactive Map: An Angular component integrating Mapbox GL JS or Leaflet. It must beautifully render GeoJSON data returned by the backend.
Elevation Profile: A sleek chart (using Chart.js or D3) showing the elevation gain over the distance of the selected route.

4. Desired Output
Database Migration: The SQL scripts to enable PostGIS, add the geometry columns, and create GiST indexes.
Backend Queries: The native Spring Data JPA queries or JOOQ implementation to execute the spatial searches (e.g., finding routes within X radius).
Frontend Map Implementation: The Angular component code that takes the GeoJSON from the API and renders the interactive heatmap and route lines.

Act as an expert Full-Stack Developer and Systems Architect. I am building a running community application using Spring Boot and Angular, and I want to introduce a new feature: "Real-Time Virtual Race Tracking".

I need you to write the code and provide the technical architecture for this feature. Currently, runs are logged after the fact. I want users participating in virtual events to share their live location, pace, and ranking on a dynamic leaderboard.

Please review the requirements below and generate the necessary backend architecture, frontend code, and logic structure.

1. Feature Overview & Concepts
Live Event Map: A real-time map showing the current positions of all participants in a specific virtual race.
Dynamic Leaderboard: A leaderboard that updates instantly based on the distance covered and current pace of the runners.
Spectator Mode: Allow friends and community members to watch the race live, send emoji reactions, and comment on a live feed.

2. Technical & Architecture Constraints
WebSocket Integration: Use Spring WebSocket and STOMP for real-time bidirectional communication.
Frontend State: Define how the Angular frontend will manage high-frequency location updates without freezing the browser (e.g., using NgRx or Signals).
Backend Throttling: Implement rate limiting or debouncing on the backend so the database isn't overwhelmed by constant GPS coordinate inserts. Consider using Redis for transient live data.
Geospatial Data: Outline how to calculate the distance between coordinates efficiently to update the leaderboard.

3. UI/UX Requirements
Race Dashboard: A clean, distraction-free Angular component showing the map (using Leaflet or Google Maps), live rank, and remaining distance.
Reaction Overlay: Floating animations when spectators send emoji reactions.

4. Desired Output
Architecture Diagram/Explanation: How the mobile app/frontend sends location updates, how Redis handles it, and how WebSockets broadcast it.
Backend Code: The Spring WebSocket configuration, the controller for handling location pings, and the Redis service snippet.
Frontend Logic: The Angular service connecting to the WebSocket and the component logic handling the live map markers.

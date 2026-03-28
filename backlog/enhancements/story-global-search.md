# Story: Global Semantic Search

### Status: DONE ✅ (2026-03-28)

## 🎯 Goal
Provide users with a powerful, fast, and unified way to find anything in the RunHub ecosystem—from friends to local running clubs.

## 👤 User Story
`As a Runner, I want to search for 'Casablanca Night Run' so that I can quickly find the community, its upcoming events, and related posts.`

## 🛠️ Acceptance Criteria
- [x] Search Engine: Implement **PostgreSQL Full-Text Search (tsvector)** over `users`, `communities`, `events`, and `posts`.
- [x] UI: A global search bar in the top navigation with "Autocomplete" suggestions.
- [x] Filtering: Allow filters by category (Users, Clubs, Activities, Events).
- [x] Performance: Search results returned in < 150ms for a dataset of 100k records.

## 🚀 Powerful Addition: "Semantic AI Search"
Integrate with the AI Coach backend. If a user searches for "Where can I run near the beach?", the search uses **Vector Embeddings** to find communities near the Corniche even if the word "beach" isn't in their description.

## 💡 Technical Strategy
1. Create a `search` view in PostgreSQL that aggregates searchable fields.
2. Implement a `/api/search` endpoint in Spring Boot with `Specification` based filtering.
3. On the frontend, use an Angular `DebounceTime` operator to prevent excessive API calls during typing.

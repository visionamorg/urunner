# Epic: Infrastructure & Developer Experience (DX)
## Story: GraphQL Migration for Social Feed

**As a** frontend developer,
**I want to** fetch the social feed using a GraphQL endpoint,
**So that** I can request exactly the fields I need (e.g., just author name and first 2 comments) and avoid over-fetching large payload blocks on mobile devices.

### Acceptance Criteria:
- *Given* the Angular feed component mounts, *when* it makes a GraphQL query, *then* it receives nested data (Post -> Author -> Comments -> Comment Author) in a single optimized payload.

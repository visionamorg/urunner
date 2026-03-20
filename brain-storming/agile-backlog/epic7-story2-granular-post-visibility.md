# Epic: Privacy & Trust
## Story: Granular Post Visibility Controls

**As a** user who logs daily mundane runs,
**I want to** restrict visibility so only specific communities or selected friends can see my casual runs,
**So that** I don't spam the global or public feeds with every 3km recovery jog I do.

### Acceptance Criteria:
- *Given* I am saving a run, *when* I click Privacy Settings, *then* I can select "Public", "Followers Only", "Only Me", or "Specific Communities".
- *Given* I select a "Specific Community", *then* the post is only injected into the feeds of users who share a `community_members` association with me in that group.

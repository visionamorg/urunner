# Epic: Activity Canvas & Export Studio
## Story: "Publish Template to Community" Feature

**As a** creative user who has just designed a beautiful custom layout using the drag-and-drop builder,
**I want to** publish and share my template directly to the Explore Community feed,
**So that** other runners around the world can discover, use, and save my design for their own runs.

**Status: Done**

### Acceptance Criteria:

1. **The "Publish Design" Button:**
   - *Given* I am in the Export Builder and have modified a template using custom fonts, stickers, or repositioned elements.
   - *When* I open the template options menu, *then* there is a prominent "Publish to Community" button (only visible if the template has been altered from its default state).

2. **Publishing Form & Metadata:**
   - *Given* I click "Publish to Community," *then* a modal appears asking me to give my template a visually appealing Name (e.g., "Midnight Neon Run").
   - *Then* I am required to select 1-3 category tags (e.g., "Minimalist", "Dark Mode", "Club Template") to help the ranking algorithm sort it in the Explore feed.

3. **Database & Visibility Push:**
   - *Given* I submit the publishing form, *then* a JSON representation of my canvas layout (X/Y coordinates, selected fonts, backgrounds, active stickers) is saved to the `community_templates` database table under my User ID.
   - *Then* my template immediately becomes visible and searchable in the public "Explore Community" grid.

4. **Creator Analytics:**
   - *Given* my template is public, *when* I view my own profile, *then* I can see a "Creator Stats" section showing exactly how many times my template was used to export a run and how many users bookmarked it.
   - *Then* users who generate more than 1,000 saves on their designs unlock an exclusive "Verified Creator" badge next to their name.

# Epic: Activity Canvas & Export Studio
## Story: Community Template Marketplace

**As a** designer who uses the RunHub app,
**I want to** submit my own custom CSS layouts into a Template Marketplace,
**So that** other users can vote on and use my unique designs for their run exports.

### Acceptance Criteria:
- *Given* a user navigates to the template selection carousel, *then* they see an "Explore Community Templates" button.
- *When* they click it, *then* they can browse custom user-generated templates ranked by popularity and download them for their own use.

### Status: ✅ Done
**Implemented:** Full-stack Community Template Marketplace. Backend: ExportTemplate + TemplateVote entities, JPA repositories, service with CRUD + vote toggling + download counting, REST controller at /api/export-templates. Frontend: "Explore Community Templates" button in template carousel, marketplace card list with name/author/description/votes/downloads, vote toggle with heart icon. Database migration SQL for export_templates and template_votes tables.

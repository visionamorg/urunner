# Epic: Community Store
## Story: Feature - Product Management & Inventory

**As a** Community Admin
**I want to** add, edit, and manage products belonging to my community
**So that** members can browse and purchase accurate inventory (e.g., shirts, nutrition, digital training plans).

### Description
Admins need a dedicated interface to manage their catalog. A product can be physical (requires shipping) or digital (e.g., PDF download or access code). Physical products may have variants (e.g., Size, Color) and finite inventory.

### Acceptance Criteria
- [ ] The Store Dashboard has a "Products" section with a list of current items and "Add Product" button.
- [ ] Adding a product allows specifying: Title, Description, Price, Image URLs (multiple), Product Type (Physical/Digital), and Inventory Tracking (Stock quantity).
- [ ] Admins can create variants for a product (e.g., Size M, Size L) where each variant has its own stock amount.
- [ ] Products can be toggled as "Draft" or "Published" (visible to members).
- [ ] When stock reaches 0, the item or variant is marked "Out of Stock" automatically.

### Technical Notes for Claude
- Create new entities: `Product` and `ProductVariant`. They should belong to a `Community`.
- `Product`: id, communityId, title, description, basePrice, images (`List<String>`), isDigital, isPublished.
- `ProductVariant`: id, productId, name (e.g., "Large / Red"), sku, additionalPrice, stockQuantity.
- Expose CRUD REST endpoints for `/api/communities/{id}/products`.
- Ensure images can be uploaded to the existing cloud storage bucket before saving the product.

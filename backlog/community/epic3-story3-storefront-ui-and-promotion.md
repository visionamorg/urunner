# Epic: Community Store
## Story: Feature - Community Storefront UI & Promotion

**As a** Community Member
**I want to** click on a "Store" tab within the community page to browse available products
**So that** I can discover and buy community merch, team kits, and sponsor products.

### Description
The user-facing store is a prominent tab on the Community Detail page. It showcases published products in an attractive grid layout. Admins should also be able to promote products directly in the community feed to drive sales.

### Acceptance Criteria
- [ ] Add a new "Store" tab next to the existing "Feed", "Events", "Members" tabs in the Community UI.
- [ ] The Store tab displays a beautiful grid of all `Published` products belonging to the community.
- [ ] Clicking a product opens a Product Detail modal/page showing all images in a carousel, description, price, and a dropdown for variant selection (if applicable).
- [ ] Out of stock items specify "Sold Out" and disable the "Add to Cart" button.
- [ ] (Admin Feature) Admins can create a Feed Post and attach a Product card to it. When members see it in the feed, they can click "Buy Now" to go directly to the product.

### Technical Notes for Claude
- Update the Community Detail component to query `/api/communities/{id}/products?published=true`.
- The UI should indicate the physical vs digital nature of the item.
- For the feed promotion, update the `FeedPost` entity to optionally include a `linkedProductId`. Update the feed UI to render a product preview card if `linkedProductId` is present.

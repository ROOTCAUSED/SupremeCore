# Towny Real Estate Hook (Design)

## Goal
Expose Towny plots for sale via commands and provide safe teleport for plot viewing.

## Command UX

- `/realestate list [town] [page]`
  - Lists for-sale plots from Towny.
  - Output includes listingId, town, world, plot coords, and price.

- `/realestate view <listingId>`
  - Teleports player to a safe viewing location for a selected listing.

## Data Source

Use Towny API as source of truth:

- Town list from `TownyUniverse`
- Plot sale state from `TownBlock` (`isForSale` / sale metadata)
- Price from TownBlock sale value

## Behavior

- Cache listing snapshots for 10–30 seconds to reduce heavy scans.
- Stable listing ids per refresh window so `view <id>` works predictably.
- Restrict teleports by world denylist + cooldown + optional warmup.
- Never bypass server protections (combat tag, region policy, etc.).

## Config Additions (planned)

```yaml
realestate:
  enabled: true
  list-page-size: 8
  cache-seconds: 20
  view-cooldown-seconds: 15
  view-warmup-seconds: 0
  allowed-worlds: []
```

## Follow-up Tasks

1. Add `RealEstateManager` to gather and cache Towny listings.
2. Wire command responses to real listing data.
3. Add teleport safety resolver (surface-safe location).
4. Add permissions and polish formatting.

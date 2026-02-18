# SupremeCore

Core gameplay plugin for the Supreme network.

SupremeCore currently includes modular systems for:
- Announcements
- Tomes
- Artefacts
- Landmarks (discovery + journal + nearest)
- Sanguine (vampirism state management)
- RealEstate (Towny plot listings/view flow)

---

## Requirements

- Java **17**
- Paper/Spigot-compatible server (API version 1.16+)
- Maven 3.8+
- Runtime dependencies:
  - **WorldGuard** (required)
  - **PlaceholderAPI** (soft dependency)

---

## Installation

1. Download the latest `SupremeCore-*.jar` from GitHub Releases.
2. Drop it into your server `plugins/` directory.
3. Ensure required dependencies are installed.
4. Start/restart the server.

---

## Commands

- `/announcer` (`/aa`) — interval announcer management
- `/tomes` — manage custom tomes
- `/artefacts` — manage custom artefacts
- `/landmarks` — list discovered landmarks / journal-related actions
- `/vampire <curse|cure|status> <player>` — manage vampirism state
- `/realestate <list|view>` (`/re`) — list for-sale Towny plots and view one
- `/morality <status|top|add|set|reload>` (`/moral`) — check alignment and manage morality

---

## Permissions

- `announcer.send`
- `tomes.retrieve`
- `tomes.add`
- `tomes.list`
- `tomes.remove`
- `sanguine.manage`
- `artefacts.retrieve`
- `artefacts.add`
- `landmarks.list`
- `landmarks.nearest`
- `realestate.list`
- `realestate.view`
- `morality.admin`

---

## Build (local)

```bash
mvn -B clean package
```

Build output:
- `target/*.jar`

---

## CI / Release Automation

GitHub Actions workflow is configured to:
- Build on pushes to `main` and `feat/**`
- Build on pull requests
- Build on tags matching `v*`

When pushing a version tag (for example `v0.0.1-beta`), Actions will:
1. Build the plugin jar
2. Upload build artifacts
3. Create/publish a GitHub Release
4. Attach `target/*.jar` to that release

Create a release tag:

```bash
git tag v0.0.2-beta
git push origin v0.0.2-beta
```

---

## Development Notes

- Keep feature work in PRs whenever possible.
- Keep commits signed/verified.
- RealEstate integration is designed around Towny market listing/view behavior.

---

## Maintainers

- `xMachiavellix`
- `Jarvis` (automation/dev support)

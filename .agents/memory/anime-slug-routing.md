---
name: Anime slug routing
description: Anime content uses the shared ultrago API but has a slug-based contract.
---

Anime endpoints share the regular API host, but their identifiers are slugs rather than TMDB IDs. Keep anime API requests in a dedicated client and only resolve a slug by title when an anime originated from TMDB.

**Why:** Sending an anime slug through TMDB or the regular numeric stream endpoint returns incorrect content or fails silently.

**How to apply:** Preserve the anime slug on the content model and branch server, season, and episode loading on anime content type before any TMDB-ID request.
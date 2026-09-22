---
name: jpa-n1-fix-lesson-todos
description: "Open to-study checklist from Notion page \"Fixing N+1 | @EntityGraph, JOIN FETCH & Batch Fetching\" (M8 JPA curriculum, Lecture #44)"
metadata: 
  node_type: memory
  type: project
  originSessionId: 5e482d65-bc31-4a08-850f-e76f2aac2d50
  modified: 2026-09-12T10:28:39.238Z
---

Notion page "Fixing N+1 | @EntityGraph, JOIN FETCH & Batch Fetching" (Lecture #44, M8 — Spring Data JPA, Difficulty: Hard) has Status "Not Started" as of 2026-09-12, with an unstarted To Study checklist (no revision notes yet, no separate open-questions section on the page):

- [ ] `@EntityGraph` (named and ad hoc)
- [ ] `JOIN FETCH` in JPQL
- [ ] Hibernate batch fetching (`hibernate.default_batch_fetch_size`)
- [ ] DTO projections to sidestep N+1 entirely
- [ ] Measuring the fix — before/after query count on the same endpoint

**Why:** This is the direct follow-up lesson to [[jpa-n1-problem-lazy-eager]] (Lecture #43, already Done) — that page covers *diagnosing* N+1 (LAZY vs EAGER, spotting via SQL logs, Statistics API); this page covers *fixing* it with the four standard techniques plus how to verify the fix worked.

**How to apply:** When teaching this page's session, follow it topic-by-topic in the order above (matches the checklist order), Hinglish, situation-first/first-principles style per repo teaching conventions in CLAUDE.md. Time-constrained session (2026-09-12) — keep hands-on limited to the highest-value items (likely JOIN FETCH + @EntityGraph + measuring before/after via Statistics API, since relay project already has that harness from Lecture #43's hands-on `@BatchSize` test). Update the Notion page (Status → Done, ticks, revision notes) at the end per CLAUDE.md's teaching-style convention.

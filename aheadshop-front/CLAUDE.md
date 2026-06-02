# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build & Dev Commands

```bash
npm run dev        # Vite dev server at localhost:5173, proxies /api → localhost:8080
npm run build      # vue-tsc -b && vite build
npm run preview    # Preview production build
npx vue-tsc --noEmit  # Type-check only (no output)
```

No test runner or linter is configured.

## Architecture

This is the Vue 3 frontend for **AheadShop-Plus**, a Spring Cloud microservice e-commerce platform. The backend runs behind a gateway at `localhost:8080`; all API calls go through `/api/*` which Vite proxies to the gateway.

### Tech Stack

Vue 3 (Composition API + `<script setup lang="ts">`) · Vite 8 · TypeScript 6 · Element Plus (zh-CN locale) · Pinia · Vue Router 5 · Axios · ECharts

### API Layer

- [src/api/request.ts](src/api/request.ts) — Shared Axios instance with `baseURL: '/api'`. Auto-injects `Bearer` token from `localStorage`. Response interceptor unwraps `Result<T>` (`{code, msg, data}`) — callers receive `data` directly. 401 → redirect to `/login`.
- Domain API files (`user.ts`, `product.ts`, `cart.ts`, `order.ts`, `search.ts`, `admin.ts`, `pay.ts`) import the shared instance and export typed functions.
- Backend response contract: `{ code: 200, msg: "success", data: T }`. Non-200 codes are rejected by the interceptor.

### State Management

Pinia stores in `src/store/modules/`:
- **user** — `token` / `refreshToken` (persisted in localStorage) / `userInfo` (fetched after login). `fetchUserInfo()` must be called to populate roles.
- **cart** — Cart badge count, synced via API.
- **app** — Global UI state.

### Routing & Guards

[src/router/index.ts](src/router/index.ts) — Two layouts:
- **DefaultLayout** — Public storefront (header + footer). Children: home, product detail, category, search, cart, orders, user center.
- **AdminLayout** — Admin panel (collapsible sidebar + breadcrumb). Children: dashboard, products, orders, users.

Route meta controls access:
- `requiresAuth` — Must have token in localStorage.
- `guestOnly` — Redirects to `/` if already logged in (login/register pages).
- `requiresAdmin` — Fetches user info and checks `roles` includes `ADMIN`; redirects to `/403` if not.

### Design System

CSS variables defined in [src/styles/reset.css](src/styles/reset.css):
- Colors: `--color-cream` (#fdfbf7), `--color-charcoal` (#2d2926), `--color-amber` (#d4a574), `--color-text`, `--color-border`, `--color-surface`
- Fonts: `--font-display` (Playfair Display), `--font-body` (DM Sans) — loaded via Google Fonts in index.html
- Shadows: `--shadow-sm`, `--shadow-md`, `--shadow-sm`
- Radii: `--radius-sm` (6px), `--radius-md` (8px), `--radius-lg` (12px)

Element Plus primary button overridden to use charcoal color.

### Path Aliases

`@/` → `src/` (configured in both vite.config.ts and tsconfig.app.json).

### Backend Service Map

All requests go through the gateway (`/api`). The gateway strips `/api` and routes to downstream services:

| Prefix | Service | Port |
|--------|---------|------|
| `/api/user/**` | user | 8081 |
| `/api/product/**` | product | 8082 |
| `/api/cart/**` | cart | 8083 |
| `/api/order/**` | order | 8084 |
| `/api/pay/**` | pay | 8085 |
| `/api/search/**` | search | 8086 |
| `/api/admin/**` | admin | 8087 |

Product service controllers use `/product/` prefix (e.g., `@RequestMapping("/product/spu")`). Gateway applies `StripPrefix=1` to `/api/product/**` so `/api/product/spu/list` → `/product/spu/list`.

### TypeScript Config

`tsconfig.app.json` extends `@vue/tsconfig/tsconfig.dom.json` with `ignoreDeprecations: "6.0"` (required for TS 6.x `baseUrl` support). Strict unused-locals/parameters checking is enabled.

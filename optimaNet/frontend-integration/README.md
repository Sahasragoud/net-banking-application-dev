# Frontend Integration Pack

This folder contains drop-in assets to connect frontend pages to OptimaNet backend v2 APIs.

## Files
- `api-client.ts`: typed client for all v2 endpoints.
- `pages-api-mapping.md`: page-by-page endpoint mapping and payloads.

## Quick setup
1. Copy `api-client.ts` into your frontend `src/api/` folder.
2. Set `VITE_API_BASE_URL=http://localhost:8080` in frontend `.env`.
3. Replace existing page API calls with methods from `api` export.

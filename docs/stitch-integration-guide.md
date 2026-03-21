# Stitch Integration Guide for RunHub

## What is Stitch?

Stitch is Google's AI design generation platform. You create UI designs using text prompts at [stitch.withgoogle.com](https://stitch.withgoogle.com), and those designs are stored as HTML/CSS in Stitch projects. The **stitch-mcp** tool bridges Stitch with your local development environment and coding agents (like Claude Code).

## How It Works with RunHub

```
Stitch (design in browser)
    |
    v
stitch-mcp (fetches designs via API)
    |
    ├── Preview locally (Vite dev server)
    ├── Generate Astro site from screens
    └── Feed designs to Claude Code via MCP
            |
            v
        Modify HTML/CSS → Integrate into RunHub Angular frontend
```

---

## Setup (Already Done)

### 1. API Key
Your Stitch API key is stored in `.env`:
```
STITCH_API_KEY=your_stitch_api_key
```

### 2. MCP Configuration
The `.mcp.json` at the project root configures Claude Code to connect to Stitch:
```json
{
  "mcpServers": {
    "stitch": {
      "command": "npx",
      "args": ["@_davideast/stitch-mcp", "proxy"],
      "env": {
        "STITCH_API_KEY": "${STITCH_API_KEY}"
      }
    }
  }
}
```

### 3. Install the CLI (optional, for direct terminal use)
```bash
npm install -g @_davideast/stitch-mcp
```

---

## Daily Workflow

### Step 1: Design in Stitch
1. Go to [stitch.withgoogle.com](https://stitch.withgoogle.com)
2. Create a new project (or open existing one)
3. Use prompts to generate screens, e.g.:
   - "A dark-themed running dashboard with weekly mileage chart"
   - "A community feed page with posts, likes, and comments"
   - "A runner profile page with stats and badges"
4. Note your **Project ID** (visible in the Stitch URL)

### Step 2: Preview Designs Locally
```bash
# Preview all screens on a local dev server
npx @_davideast/stitch-mcp serve -p <PROJECT_ID>

# Browse screens interactively in terminal
npx @_davideast/stitch-mcp screens -p <PROJECT_ID>

# Interactive resource browser
npx @_davideast/stitch-mcp view
```

### Step 3: Use with Claude Code (MCP)
When you start a new Claude Code session in the RunHub directory, the Stitch MCP server starts automatically. You can then ask Claude to:

- **Fetch a screen's code:**
  > "Get the HTML from my Stitch screen [screen-id] and convert it to an Angular component"

- **Build a full site from screens:**
  > "Use Stitch to build a site from project [project-id], mapping the dashboard screen to / and the profile screen to /profile"

- **Get a screen screenshot:**
  > "Show me the screenshot of screen [screen-id] from my Stitch project"

### Step 4: Integrate Designs into RunHub
Once Claude fetches the Stitch HTML/CSS, you can ask it to:

1. **Convert to Angular components** — Stitch outputs HTML/CSS; Claude can transform it into Angular 17 standalone components with Tailwind classes matching RunHub's dark theme (`bg-[#050a18]`)

2. **Extract styles** — Pull useful CSS patterns into Tailwind utilities or component styles

3. **Map to existing routes** — Integrate new designs into `app.routes.ts`

Example conversation:
```
You: Get the screen code from Stitch project abc123, screen xyz789.
     Convert it into a new Angular standalone component at
     frontend/src/app/features/landing/landing.component.ts
     Use RunHub's existing Tailwind theme and dark background.
```

---

## CLI Commands Reference

| Command | What it does |
|---------|-------------|
| `npx @_davideast/stitch-mcp init` | Setup wizard (auth, gcloud, MCP config) |
| `npx @_davideast/stitch-mcp doctor` | Check if everything is configured correctly |
| `npx @_davideast/stitch-mcp serve -p <ID>` | Preview project screens on local Vite server |
| `npx @_davideast/stitch-mcp screens -p <ID>` | Browse screens in terminal |
| `npx @_davideast/stitch-mcp view` | Interactive project/screen browser |
| `npx @_davideast/stitch-mcp site -p <ID>` | Generate an Astro site from screens |
| `npx @_davideast/stitch-mcp snapshot` | Save screen state to a file |
| `npx @_davideast/stitch-mcp proxy` | Run MCP server (used by Claude Code automatically) |
| `npx @_davideast/stitch-mcp logout` | Revoke credentials |

---

## MCP Tools Available to Claude Code

When the Stitch MCP server is running, Claude Code gets three tools:

### 1. `build_site`
Builds a full site from Stitch screens mapped to routes.
```json
{
  "projectId": "your-project-id",
  "routes": [
    { "screenId": "screen-1", "route": "/" },
    { "screenId": "screen-2", "route": "/profile" },
    { "screenId": "screen-3", "route": "/dashboard" }
  ]
}
```
Returns HTML for each page.

### 2. `get_screen_code`
Retrieves a single screen's HTML/CSS code. Use when you want to convert one screen into an Angular component.

### 3. `get_screen_image`
Downloads a screenshot of a screen as base64-encoded image. Useful for visual reference.

---

## Environment Variables

| Variable | Purpose | Where to set |
|----------|---------|-------------|
| `STITCH_API_KEY` | API key for Stitch access | `.env` (already configured) |
| `STITCH_ACCESS_TOKEN` | Alternative: pre-existing OAuth token | `.env` if using OAuth |
| `STITCH_PROJECT_ID` | Default project ID (optional) | `.env` to avoid passing -p every time |
| `STITCH_USE_SYSTEM_GCLOUD` | Use system gcloud instead of bundled | `.env` if needed |

---

## Recommended Workflow for RunHub

### For new pages/features:
1. Design in Stitch using prompts that describe RunHub's aesthetic
2. Preview locally with `serve -p`
3. Open Claude Code in RunHub directory
4. Ask Claude to fetch the design and convert to Angular component
5. Claude adapts HTML/CSS to Tailwind + RunHub theme
6. Review and push

### For redesigning existing pages:
1. Screenshot the current page for reference
2. Create a Stitch design with improvements
3. Ask Claude to fetch the new design and update the existing component
4. Review diff carefully before committing

### Design prompt tips for RunHub consistency:
- Always mention "dark theme with background #050a18"
- Reference "Tailwind CSS styling"
- Describe the running/sports context
- Mention "mobile responsive with bottom navigation"

---

## Troubleshooting

| Issue | Solution |
|-------|---------|
| MCP server not starting | Run `npx @_davideast/stitch-mcp doctor` to diagnose |
| Auth errors | Check `STITCH_API_KEY` in `.env`, or re-run `init` |
| Permission errors | Ensure your Google Cloud project has Stitch API enabled |
| Screens not loading | Verify project ID is correct |
| Claude can't access Stitch | Restart Claude Code session to reload `.mcp.json` |

---

## Security Notes

- **Never commit `.env`** — it's git-ignored and contains your API key
- **Rotate your API key** if it's ever exposed (regenerate in Google Cloud Console)
- **`.mcp.json` is safe to commit** — it only references env vars, not actual secrets

# EVO-120

## Overview

This project is the base for a custom **PES 6 online ecosystem** inspired by **Master League Online (MLO)**.

The goal is not only to run a website or an online server, but to build a complete platform around PES 6 with:

- player accounts
- team ownership
- squad management
- transfer market
- Option File generation
- integration with an updated `fiveserver`
- future launcher/update support

This repository starts from the old evo-league structure, but it is being repurposed into a cleaner platform for a modern PES 6 online experience.

---

## Main Goals

The long-term objective of this project is to support:

1. **Web administration**
   - assign a user to a specific PES team
   - manage rosters
   - review users, teams and activity
   - trigger Option File builds

2. **MLO-style team ownership**
   - each user controls one team
   - users can buy and sell players
   - squad changes are stored centrally

3. **Global Option File generation**
   - all team edits are merged into one single global PES 6 Option File
   - only the administrator downloads and distributes the final build
   - the generated data can later be packaged for a launcher or update system

4. **Integration with `fiveserver`**
   - `fiveserver` will be used as the updated online game server
   - this repository will provide the web/admin layer around it
   - old Sixserver-era assumptions will gradually be replaced

---

## Repository Structure

### `/http`
PHP website and administration panel.

This is where the following modules live:

- user-facing site pages
- admin pages
- roster/team assignment tools
- market tools
- Option File build tools

### `/cron`
Scheduled scripts.

This folder is intended for:

- maintenance tasks
- syncing data
- future Option File build automation
- future integration jobs with `fiveserver`

### `/Sixserver`
Legacy code from the original evo-league project.

This folder is kept for historical and compatibility reasons, but it is **not the long-term online server target** for this project.

The updated target server is `fiveserver`, maintained separately.

### `/setup`
Database setup scripts.

This contains the SQL schema used by the website and supporting tools.

---

## Current Direction

This project is being adapted to support the following architecture:

```text
[fiveserver]
    online server / lobbies / matches

[this repository]
    website / admin / MLO logic / market / OF build management

[Option File Builder]
    generates the final global PES 6 Option File from website data

# EVO-120

## Overview

This project is the base for a custom **PES 6 online ecosystem** inspired by **Master League Online (MLO)**.

The goal is to build a complete platform around PES 6 with:

- player accounts
- team ownership
- squad management
- transfer market
- Option File generation
- integration with an updated `fiveserver`
- future launcher/update support

---

## Main Goals

### 1. Web administration
- assign a user to a specific PES team
- manage rosters
- review users and teams
- trigger Option File builds

### 2. MLO-style team ownership
- one user = one team
- centralized squads
- persistent teams

### 3. Global Option File generation
- one shared `.opt`
- generated from database
- downloaded only by admin

### 4. Integration with fiveserver
- `fiveserver` = online server
- this repo = platform layer

---

## Repository Structure

- `/http` → PHP website and admin panel  
- `/cron` → scheduled jobs  
- `/Sixserver` → legacy (do not extend)  
- `/setup` → database setup  

---

## Architecture (Target)

fiveserver → handles matches  
this repo → handles users, teams, market  
builder → generates Option File  

---

## Requirements

- PHP 5.5+
- MySQL
- Linux VPS recommended

---

## Installation

### Database
Import:
`/setup/dbinit.sql`

### Website
Copy:
`/http` → web root

### Config
Edit:
`http/config.php`

---

## Development Roadmap

### Stage 1
- DB structure
- admin panel
- team assignment

### Stage 2
- squad management
- market

### Stage 3
- Option File builder

### Stage 4
- fiveserver integration

### Stage 5
- launcher

---

## Important Notes

### Sixserver
Legacy only. Do not extend.

### Option File
- global
- generated centrally
- ensures sync

---

## Security Warning

Legacy codebase. Review before production.

---

## Project Goal

Recreate a persistent PES 6 online experience inspired by Master League Online.

---

## Credits

Original evo-league credits remain valid.

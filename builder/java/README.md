# Java Option File Builder

This folder will contain the real Java-based PES 6 Option File builder.

## Goal

Load a base `KONAMI-WIN32PES6OPT`, apply team roster changes from `snapshot.json`, and write the final global Option File.

## Planned responsibilities

- read input snapshot JSON
- load base Option File
- map each `sixTeamId` to the correct squad block
- replace squad players in the correct order
- preserve internal file structure
- recalculate checksums
- write final `KONAMI-WIN32PES6OPT`

## Notes

The future implementation is expected to reuse or replicate logic from existing PES 6 Option File editor classes such as:

- `OptionFile`
- `Squads`
- `SquadList`
- `SquadNumList`

## Current status

At this stage, the PHP/cron side already exports:

- `snapshot.json`
- `manifest.json`
- build folders

The current shell runner only copies the base Option File as a placeholder.

The real Java implementation will replace that placeholder.

## Expected CLI usage

A future command should look like:

```bash
java -jar builder.jar --base /path/to/KONAMI-WIN32PES6OPT --snapshot /path/to/snapshot.json --output /path/to/output/KONAMI-WIN32PES6OPT

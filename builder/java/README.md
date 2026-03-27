# Java Option File Builder

This folder contains the Java-based PES 6 Option File builder.

## Goal

Load a base `KONAMI-WIN32PES6OPT`, apply team roster changes from `snapshot.json`, and write the final global Option File.

## Current responsibilities

At the current stage, the Java builder can:

- read the input snapshot JSON
- parse `teams -> ordered player IDs`
- load the base Option File as binary data
- resolve team offsets from external config
- write player IDs experimentally into primary and mirror offsets
- run a placeholder checksum/update step
- save the output Option File

## Important limitation

The current build pipeline is still **experimental**.

It does **not yet guarantee**:
- correct real squad offsets for all teams
- correct mirrored/internal block logic
- correct PES 6 checksum recalculation
- full compatibility with the final in-game Option File format

## Current Java structure

- `Main.java` → entry point
- `JsonParser.java` → parses `snapshot.json`
- `OptionFile.java` → binary read/write wrapper
- `OptionFileConstants.java` → known OF areas and constants
- `TeamOffsetResolver.java` → teamId -> offsets
- `SquadEditor.java` → experimental player writing
- `ChecksumUpdater.java` → checksum placeholder
- `OptionFileBuilder.java` → orchestrates the build

## External config

Team offsets are currently loaded from:

```text
builder/config/team-offsets.properties

# Java Option File Builder

This folder contains the Java-based PES 6 Option File builder.

## Goal

Load a base `KONAMI-WIN32PES6OPT`, apply team roster changes from `snapshot.json`, and write the final global Option File.

## Current responsibilities

At the current stage, the Java builder can:

- read the input snapshot JSON
- parse teams and ordered player IDs
- load the base Option File as binary data
- resolve team offsets from external config
- write player IDs experimentally into primary and mirror offsets
- run a placeholder checksum/update step
- save the output Option File

## Important limitation

The current build pipeline is still **experimental**.

It does NOT yet guarantee:

- correct squad offsets for all teams
- correct mirrored/internal block logic
- correct PES 6 checksum recalculation
- full compatibility with the in-game Option File format

## Java structure

- Main.java → entry point  
- JsonParser.java → parses snapshot  
- OptionFile.java → binary reader/writer  
- OptionFileConstants.java → known OF offsets  
- TeamOffsetResolver.java → teamId → offsets  
- SquadEditor.java → squad writing logic  
- ChecksumUpdater.java → checksum placeholder  
- OptionFileBuilder.java → orchestrator  

## External config

Team offsets are loaded from:

builder/config/team-offsets.properties

Example:

teamId=primaryOffset,mirrorOffset  
1=0x0A1080,0x0A3080  

## CLI usage

java Main --base /path/to/KONAMI-WIN32PES6OPT --snapshot /path/to/snapshot.json --output /path/to/output/KONAMI-WIN32PES6OPT

## Next milestone

- replace placeholder offsets with real ones  
- validate real squad write locations  
- implement correct checksum logic  

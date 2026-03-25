# Option File Builder

This folder contains the components used to generate the global PES 6 Option File.

## Goal

Take a base PES 6 Option File, apply squad changes from the website database, and generate a final global build.

## Expected Inputs

- `base/KONAMI-WIN32PES6OPT` → base Option File
- `snapshot.json` → exported from website database
- future builder arguments:
  - input snapshot path
  - output folder path
  - base option file path

## Expected Outputs

- `KONAMI-WIN32PES6OPT`
- optional DB package / patch files
- `manifest.json`

## Current Status

At this stage, the project only prepares build folders and snapshot JSON from PHP.

The actual Java builder integration will be added next.

## Planned Structure

- `base/` → base Option File
- `output/` → optional local test output
- `scripts/` → shell helpers to invoke the builder
- `java/` → future Java source or wrapped builder files

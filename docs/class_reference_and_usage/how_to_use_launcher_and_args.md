# How to Launch egps-shell and Use `@eGPS.args`

This document describes the current startup entry points for `egps-shell`.

**Implementation basis:** `src/egps2/Launcher.java`, `src/egps2/Launcher4Dev.java`, `eGPS.args`

## Current entry points

### Production launcher

```bash
java -cp "./out/production/egps-main.gui:dependency-egps/*" -Xmx12g @eGPS.args egps2.Launcher
```

Use this as the normal shell startup path.

### Development launcher

```bash
java -cp "./out/production/egps-main.gui:dependency-egps/*" -Xmx12g @eGPS.args egps2.Launcher4Dev
```

`Launcher4Dev` only sets `Launcher.isDev = true` and then delegates to `Launcher.main(args)`.

### Launch one module directly

```bash
java -cp "./out/production/egps-main.gui:dependency-egps/*" -Xmx12g @eGPS.args egps2.Launcher egps2.builtin.modules.voice.IndependentModuleLoader
```

If you pass one or more module-loader FQCNs after `egps2.Launcher`, the shell registers them and opens them after the main frame becomes available.

## What `@eGPS.args` does

`@eGPS.args` supplies the current `--add-exports`, `--add-opens`, and related JVM flags required by this Swing/JIDE-based application on modern JDKs.

Use it for normal startup unless you are deliberately reproducing a JVM-argument problem.

## What happens during startup

`Launcher.main(...)` currently does these things:

1. sets UTF-8 related system properties
2. checks that a usable display environment exists
3. forces `Locale.ENGLISH`
4. prepares the Swing/JIDE look and feel
5. initializes the first-run configuration area under `~/.egps2/config`
6. registers predefined module loaders from command-line arguments
7. starts the GUI and shows the singleton `MyFrame`

## Config location

The current user config root is `~/.egps2/config`.

In code this comes from:

- `EGPSProperties.PROPERTIES_DIR_NAME = "config"`
- `EGPSProperties.PROPERTIES_DIR = <user.home>/.egps2/config`

## Practical rules

- Prefer `Launcher` for normal runtime behavior.
- Prefer `Launcher4Dev` only when you want development-mode behavior from the same code path.
- Keep `@eGPS.args` in the startup command for routine use.
- If startup logic needs a module to open automatically, pass the module loader class name after the launcher class.

## Current boundaries

- This is a GUI launcher and requires a usable display environment.
- First-run startup may create config content automatically.
- Module arguments are expected to be `IModuleLoader` implementation class names, not arbitrary classes.

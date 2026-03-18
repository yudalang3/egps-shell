# eGPS Plugin Development Tutorial (Plugin Mode)

## What is Plugin mode?

Plugin mode packages a module as a JAR and places it under `~/.egps2/config/plugin/` as an external extension.

Typical use cases:

- third-party tools
- features distributed to other users
- experimental functionality
- custom extensions outside the built-in shell bundle

## Two development styles

### Style 1: extend `FastBaseTemplate`

Recommended for:

- simple tool plugins
- quick prototypes
- single-purpose modules
- plugins where the UI shell can stay lightweight

### Style 2: implement `IModuleLoader`

Recommended for:

- more complex business logic
- stronger structural control
- team-maintained plugins
- larger plugin projects

## Style 1 walkthrough: `FastBaseTemplate`

Typical steps:

1. create the project structure
2. write the plugin class
3. compile the plugin
4. create `eGPS2.plugin.properties`
5. package the JAR
6. install it into `~/.egps2/config/plugin/`
7. test it through eGPS

### Important note about `eGPS2.plugin.properties`

For the current loader implementation, the truly effective fields are:

- `launchClass` (required)
- `dependentJars` (optional)

Other metadata may still be useful to humans, but they are not part of the current core loading contract.

## Style 2 walkthrough: direct `IModuleLoader`

This route typically includes:

1. create the project structure
2. write the loader class
3. write the panel / face class
4. compile, package, and install

Use this style when loader and panel responsibilities should be clearly separated.

## Advanced topics

### Custom icons

Plugins can supply icons so they integrate more naturally with the shell UI.

### Third-party dependencies

Two common strategies are:

- package dependencies into a fat JAR
- keep them as sibling JARs and list them in `dependentJars`

### Internationalization

If the plugin needs bilingual text, keep the user-facing strings organized from the beginning.

### Persisting user configuration

If the plugin stores user configuration, avoid scattering ad hoc files. Keep persistence rules clear and predictable.

## Build-script example

A practical plugin build script usually performs:

- cleanup
- compilation
- config-file generation
- JAR packaging
- move to `dist`
- optional automatic installation

## Debugging tips

### Inspect console output

The startup console and error output are the first place to check.

### Use logging

Structured logging is much better than scattered `print` statements once the plugin becomes nontrivial.

### Handle exceptions clearly

A plugin should fail transparently enough that the user and developer can understand why it did not load.

## Checklist

Before testing:

- the JAR is built correctly
- `eGPS2.plugin.properties` exists at the JAR root
- `launchClass` points to a valid class
- dependencies are packaged or listed correctly

## Next step

Continue with:

- `03_BUILTIN_DEVELOPMENT.md` if you want built-in deployment
- `04_ARCHITECTURE.md` if you want the deeper runtime model
- `05_eGPS2.plugin.properties.md` for the config-file contract

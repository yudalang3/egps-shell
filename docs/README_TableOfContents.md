# egps-shell Docs Index

This `docs/` directory is the reference documentation set for `egps-shell`.

`egps-shell` is the GUI shell and baseline runtime described to external readers. Its current closed-source implementation lives in the `egps-main` repository.

## Writing Scope

The default subject in `docs/` is `egps-shell`.

Use this writing pattern throughout the directory:

- When explaining behavior to external readers, speak in terms of `egps-shell`.
- When citing implementation evidence, say that the behavior is implemented in `egps-main` by specific classes or modules.

## Boundary: README vs docs vs manuals

| Location | Main subject | Purpose | Typical questions |
| --- | --- | --- | --- |
| `README.md`, `README_zh.md` | `egps-main` | Repository entry | What is this repo? How do I build and run the current source tree? |
| `docs/` | `egps-shell` | Reference documentation | How does the shell start? How are modules discovered? How does VOICE work? |
| `manuals/` | `egps-shell` | Tutorials and practical guides | How do I learn the framework? How do I develop a module or plugin step by step? |

## Document Types In This Directory

### Runtime and architecture reference

- `docs/launchSystem/understanding_how_main.gui_launched.md`: explains the current launch flow, startup entry classes, and runtime configuration paths.
- `docs/module&pluginSystem/plugin_integration.md`: explains the current plugin loading and discovery model.
- `docs/module&pluginSystem/plugin_gallery_integration_statement.md`: explains the current relationship between plugins, config-backed loading, and Module Gallery presentation.
- `docs/module&pluginSystem/plugin_types_statement.md`: explains the current supported plugin implementation styles and their boundaries.
- `docs/module&pluginSystem/module_discovery_exclusion_rules.md`: explains the current module-discovery filter rules.
- `docs/module&pluginSystem/module_discovery_subtab_exclusion.md`: explains why VOICE SubTabs are excluded from ordinary module discovery.
- `docs/module&pluginSystem/module_versioning_system.md`: explains the current module version contract and `ModuleVersion` usage.
- `docs/itoolsManager/itoolmanager_module_discovery_statement.md`: explains the current ITools Manager discovery and status-tracking mechanism.

### Subsystem and topic guides

- `docs/global_preference/README.md`: entry page for the global preference and font documentation cluster.
- `docs/global_preference/global_font_system_statement.md`: explains the current runtime behavior of the global font system.
- `docs/global_preference/global_font_system_design.md`: explains the current design goals and boundaries of the global font system.
- `docs/global_preference/global_font_system_code_map.md`: lists the current related code locations, integration points, and maintenance pitfalls for the font system.
- `docs/global_preference/how_to_use_global_preference.md`: shows modules and plugins how to use the current global preference and font system correctly.
### Class references and usage guides

- `docs/class_reference_and_usage/how_to_use_MSG_dialog.md`: shows how to use the MSG dialog helper API.
- `docs/class_reference_and_usage/how_to_use_MSG_dialog_zh.md`: Chinese guide to using the MSG dialog helper API.
- `docs/class_reference_and_usage/how_to_use_UnifiedAccessPoint.md`: explains when and how to use the shell-wide `UnifiedAccessPoint`.
- `docs/class_reference_and_usage/how_to_use_UnifiedAccessPoint_zh.md`: Chinese guide to using the shell-wide `UnifiedAccessPoint`.
- `docs/class_reference_and_usage/how_to_use_launcher_and_args.md`: explains how to start `egps-shell` and when to keep `@eGPS.args`.
- `docs/class_reference_and_usage/how_to_use_launcher_and_args_zh.md`: Chinese guide to starting `egps-shell` and using `@eGPS.args`.
- `docs/class_reference_and_usage/understanding_classes_overview.md`: English high-level inventory of the current source tree's major classes.
- `docs/class_reference_and_usage/understanding_classes_overview_zh.md`: Chinese high-level inventory of the current source tree's major classes.
- `docs/class_reference_and_usage/understanding_frame_core.md`: English guide to current frame-core classes.
- `docs/class_reference_and_usage/understanding_frame_core_zh.md`: Chinese guide to current frame-core classes.
- `docs/class_reference_and_usage/understanding_module_interfaces.md`: English guide to current module interfaces and contracts.
- `docs/class_reference_and_usage/understanding_module_interfaces_zh.md`: Chinese guide to current module interfaces and contracts.
- `docs/class_reference_and_usage/understanding_panels_dialogs.md`: English guide to reusable panels and dialogs.
- `docs/class_reference_and_usage/understanding_panels_dialogs_zh.md`: Chinese guide to reusable panels and dialogs.
- `docs/class_reference_and_usage/understanding_utilities_templates.md`: English guide to shared utilities and template-side helper classes.
- `docs/class_reference_and_usage/understanding_utilities_templates_zh.md`: Chinese guide to shared utilities and template-side helper classes.
- `docs/files_with_main_methods.md`: English inventory of source files that currently define `main(...)` entry methods.
- `docs/files_with_main_methods_zh.md`: Chinese inventory of source files that currently define `main(...)` entry methods.

### Indexes and maintenance navigation

- `docs/README_TableOfContents.md`: this English index page for the `docs/` directory.
- `docs/README_TableOfContents_zh.md`: the Chinese index page for the `docs/` directory.
- `docs/readme_docs_manuals_update_plan.md`: explains the current division of responsibility between `README`, `docs/`, and `manuals`.

## Recommended Reading Order

If you are new to the reference docs, a practical order is:

1. `docs/launchSystem/understanding_how_main.gui_launched.md`
2. `docs/module&pluginSystem/plugin_integration.md`
3. `docs/module&pluginSystem/module_discovery_exclusion_rules.md`
4. `manuals/01_VOICE_architecture.md`
5. `manuals/02_VOICE_GUI_design.md`
6. `manuals/00Readme.md`
7. `manuals/module_plugin_course/README.md`

## Runtime Conventions Referenced Across Docs

- User configuration directory: `~/.egps2/config`
- Module loading configuration: `~/.egps2/config/egps2.loading.module.config.txt`
- Plugin directory: `~/.egps2/config/plugin/`
- Recommended runtime argument file: `@eGPS.args`

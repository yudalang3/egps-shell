# How to Use UnifiedAccessPoint

`UnifiedAccessPoint` is the shell-wide entry point for shared runtime objects in `egps-main`.

**Implementation basis:** `src/egps2/UnifiedAccessPoint.java`

## What it is for

Use `UnifiedAccessPoint` when a module or dialog needs one of these current shell-level services:

- access to the singleton main window
- access to `LaunchProperty` and the current global fonts
- access to i18n resource strings
- access to bundled image resources
- registration of actions that should run after the main frame is visible
- loading an `IModuleLoader` into the current shell window

## Common usage patterns

### Read global fonts and runtime preferences

```java
LaunchProperty launchProperty = UnifiedAccessPoint.getLaunchProperty();
Font defaultFont = launchProperty.getDefaultFont();
Font titleFont = launchProperty.getDefaultTitleFont();
```

Use this path when a panel, dialog, or module should follow the shell's current font configuration.

### Read i18n strings

```java
String title = UnifiedAccessPoint.getResourceString("dialog.info");
String message = UnifiedAccessPoint.getResourceString("dialog.msg.finish");
```

Use this instead of hard-coding user-facing text when the text already exists in the resource bundle.

### Read bundled images

```java
URL iconUrl = UnifiedAccessPoint.getImageResource("module/24gf-lock2.png");
```

Use this for resources under the packaged `/images/` tree.

### Access the main frame

```java
MyFrame frame = UnifiedAccessPoint.getInstanceFrame();
```

Use this only when the code really needs the shell window, for example as a dialog owner or when integrating with the main tabbed UI.

### Register work after the main frame exists

```java
UnifiedAccessPoint.registerActionAfterMainFrame(() -> {
    SwingUtilities.invokeLater(() -> {
        UnifiedAccessPoint.loadTheModuleFromIModuleLoader(new IndependentModuleLoader());
    });
});
```

Use this when startup logic depends on the GUI already being created.

### Load a module into the shell

```java
IModuleLoader loader = new IndependentModuleLoader();
ModuleFace moduleFace = UnifiedAccessPoint.loadTheModuleFromIModuleLoader(loader);
```

This is the standard shell-side path for opening a module tab from an `IModuleLoader`.

## Practical rules

- Call `getLaunchProperty()` freely for fonts and preference-backed UI decisions.
- Check `isGULaunched()` before assuming the shell window already exists.
- If your registered action touches Swing components, dispatch that GUI work back to the EDT with `SwingUtilities.invokeLater(...)`.
- Use `loadTheModuleFromIModuleLoader(...)` instead of duplicating tab-creation logic.

## What not to do

- Do not treat `UnifiedAccessPoint` as a generic storage place for unrelated business state.
- Do not perform long-running work directly inside GUI callbacks just because you obtained the frame here.
- Do not force creation of the main frame from code that should still be CLI-safe unless the GUI is actually required.

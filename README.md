# Lit DevTools Plugin for WebStorm/IntelliJ

A local WebStorm/IntelliJ plugin (Kotlin) for enhanced Lit 2.x–3.x web components development experience.

## Quick Start: Is the Plugin Working?

After installation, you should immediately see these signs that the plugin is active:

1. **📢 Startup Notification**: A balloon notification saying "Lit DevTools Plugin Active"
2. **🔧 Lit Inspector Tool Window**: A new "Lit Inspector" tab on the right side of your IDE
3. **📝 Log Messages**: Search for "Lit DevTools" in Help → Show Log to see plugin activity

**Not seeing these?** Check the [Troubleshooting](#troubleshooting) section or see [TESTING.md](TESTING.md) for detailed verification steps.

## Features

* **Autocompletion** of Lit tags and attributes in HTML/XML files
* **Ctrl/Cmd-Click navigation** from `<my-tag>` to the TypeScript class in HTML/XML files
* **Structured view** of Web Components organized by:
  - Properties
  - State
  - Private fields
  - Methods
  - Events
  - CSS

**Note**: Version 0.1.1 focuses on HTML/XML file support. Template literal support (for `html\`...\`` in TypeScript/JavaScript) will be added in a future release.

Tested with IntelliJ Platform 2024.2+ and the JavaScript plugin.

## Project Structure

```
lit-devtools-plugin/
├─ build.gradle.kts
├─ settings.gradle.kts
├─ gradle.properties
├─ src/main/resources/
│  └─ META-INF/plugin.xml
└─ src/main/kotlin/com/david/litdevtools/
   ├─ LitSettings.kt
   ├─ index/LitTagIndex.kt
   ├─ psi/LitPsiUtil.kt
   ├─ nav/LitTagReferenceContributor.kt
   ├─ completion/LitHtmlCompletionContributor.kt
   ├─ structure/LitStructureViewBuilder.kt
   ├─ structure/LitStructureElements.kt
   └─ ui/LitToolWindowFactory.kt (optional)
```

## Building the Plugin

1. **Build the plugin:**
   ```bash
   gradle wrapper
   ./gradlew buildPlugin
   ```

   The ZIP file will be created in `build/distributions/lit-devtools-plugin-0.1.1.zip`.

2. **Install in WebStorm/IntelliJ:**
   - Open WebStorm/IntelliJ
   - Go to *Settings → Plugins → ⚙️ Install from Disk…*
   - Select the generated ZIP file
   - Restart the IDE

## Usage

Once installed, the plugin automatically enhances your Lit development experience:

### Verifying the Plugin is Active

After installing and restarting WebStorm/IntelliJ, you should see confirmation that the plugin is active:

1. **Startup Notification**: A notification balloon will appear stating "Lit DevTools Plugin Active"
2. **Lit Inspector Tool Window**: Look for the "Lit Inspector" tab on the right side of your IDE
   - Click it to open the status panel
   - It will show detected Lit components in the currently open file
   - Use the "Refresh" button to update the display
3. **IDE Logs**: Check Help → Show Log in Explorer/Finder and search for "Lit DevTools" to see detailed activity logs

### Navigation
- **Ctrl/Cmd-Click** on any custom element tag (e.g., `<my-component>`) in HTML/XML files to navigate to its TypeScript class definition

**Note**: Navigation inside TypeScript/JavaScript `html` template literals is planned for a future release.

### Code Completion
- When editing HTML/XML files, start typing attributes on Lit components to see autocomplete suggestions based on `@property()` decorators
- Events are suggested with the `@` prefix (e.g., `@my-event`)

**Note**: Completion inside TypeScript/JavaScript `html` template literals is planned for a future release.

### Structure View
- Open any TypeScript file containing a Lit component
- Open the Structure View (Alt+7 / CMD+7)
- See your component members organized in clean sections:
  - **Properties**: Fields decorated with `@property()`
  - **State**: Fields decorated with `@state()`
  - **Private**: Private fields and those starting with `_`
  - **Methods**: Class methods
  - **Events**: Detected CustomEvent dispatches
  - **CSS**: Whether the component has styles

## Troubleshooting

### Plugin Not Working?

If the plugin appears to not be working:

1. **Check if plugin is enabled**:
   - Go to Settings → Plugins
   - Search for "Lit DevTools"
   - Ensure it's checked/enabled
   - Restart the IDE if you just enabled it

2. **Verify plugin is loaded**:
   - Look for the "Lit Inspector" tool window on the right side
   - Check for the startup notification when opening a project
   - Open Help → Show Log in Explorer/Finder
   - Search the log for "Lit DevTools" - you should see messages like:
     - "Lit DevTools: Plugin starting up"
     - "Lit DevTools: LitHtmlCompletionContributor initialized"
     - "Lit DevTools: LitTagReferenceContributor initialized"

3. **Check your project setup**:
   - Ensure you have TypeScript/JavaScript files with Lit components
   - Components should use `@customElement('tag-name')` decorator
   - Properties should use `@property()` or `@state()` decorators
   - Open the Lit Inspector tool window and click "Refresh" while viewing a Lit component file

4. **Test with example files**:
   - Try opening `examples/example-component.ts` and `examples/index.html` from this repository
   - Open the Structure View (Alt+7 / CMD+7) on the TypeScript file
   - Try Ctrl/Cmd-Click on `<example-component>` in the HTML file

5. **Enable detailed logging**:
   - The plugin logs all major operations at INFO level
   - Look for messages about component detection, completion requests, and navigation lookups
   - If you see no "Lit DevTools" messages in the logs, the plugin may not be loading properly

### Common Issues

- **No completions showing**: The plugin only provides completions for tags defined in your project with `@customElement()`. Standard HTML elements are not affected.
- **Navigation not working**: Ensure the tag name in HTML exactly matches the `@customElement('tag-name')` decorator value
- **Structure view empty**: Structure view enhancements only apply to TypeScript/JavaScript files containing classes that extend `LitElement`

## Requirements

- IntelliJ Platform 2024.2 or later (build 242.*)
- JavaScript plugin (bundled with WebStorm/IntelliJ IDEA Ultimate)
- JDK 17 or later for building

## Technical Notes

- The plugin uses **local resolution** for navigation and completion (searches within the current file and project scope)
- Detection of `@property` and `@state` decorators is heuristic-based and works well with standard Lit 3 patterns
- No modifications are made to your repository - the plugin runs entirely within the IDE

## Future Extensions

Potential enhancements that can be added:

- **DocumentationProvider**: Display JSDoc/types on hover
- **Enum/union value completion**: Suggest valid values for properties with union types
- **Global index** (StubIndex): Improved performance for large monorepos
- **Events via JSDoc @fires**: Enhanced event detection
- **CSS parts/vars**: Parser for `::part()` and CSS custom properties

## License

See [LICENSE](LICENSE) file.

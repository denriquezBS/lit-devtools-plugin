# Lit DevTools Plugin for WebStorm/IntelliJ

A local WebStorm/IntelliJ plugin (Kotlin) for enhanced Lit 2.x–3.x web components development experience.

## Features

* **Autocompletion** of Lit tags and attributes in HTML
* **Ctrl/Cmd-Click navigation** from `<my-tag>` to the TypeScript class
* **Structured view** of Web Components organized by:
  - Properties
  - State
  - Private fields
  - Methods
  - Events
  - CSS

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
   ./gradlew buildPlugin
   ```

   The ZIP file will be created in `build/distributions/lit-devtools-plugin-0.1.0.zip`.

2. **Install in WebStorm/IntelliJ:**
   - Open WebStorm/IntelliJ
   - Go to *Settings → Plugins → ⚙️ Install from Disk…*
   - Select the generated ZIP file
   - Restart the IDE

## Usage

Once installed, the plugin automatically enhances your Lit development experience:

### Navigation
- **Ctrl/Cmd-Click** on any custom element tag (e.g., `<my-component>`) in HTML to navigate to its TypeScript class definition

### Code Completion
- When editing HTML files, start typing attributes on Lit components to see autocomplete suggestions based on `@property()` decorators
- Events are suggested with the `@` prefix (e.g., `@my-event`)

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

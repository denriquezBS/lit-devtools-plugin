# Testing the Lit DevTools Plugin

This directory contains example files to test the plugin's navigation and autocomplete features.

## Files

- **alerts-icons.ts**: Defines a `BseOasesAlertsIcons` Lit component with properties
- **site-meters.html**: HTML file that uses the `<bse-oases-alerts-icons>` custom element (around line 88)
- **dashboard-view.ts**: TypeScript component that uses other components in its template (demonstrates TS-to-TS navigation)
- **example-component.ts**: Another example Lit component
- **index.html**: Original example file

## How to Test

### 1. Build and Install the Plugin

```bash
./gradlew buildPlugin
```

Then install the generated ZIP file from `build/distributions/` in IntelliJ/WebStorm:
- Settings → Plugins → ⚙️ Install from Disk...
- Select the ZIP file
- Restart the IDE

### 2. Open the Project

Open this `lit-devtools-plugin` directory as a project in IntelliJ/WebStorm.

### 3. Test Navigation from HTML to TypeScript

1. Open `examples/site-meters.html`
2. Find line 83 (or around line 88) where `<bse-oases-alerts-icons` is used
3. **Ctrl/Cmd-Click** on `bse-oases-alerts-icons` in the tag
4. ✅ **Expected**: The IDE should navigate to `alerts-icons.ts` and highlight the `BseOasesAlertsIcons` class

### 4. Test Navigation from TypeScript to TypeScript

1. Open `examples/dashboard-view.ts`
2. Find the `render()` method where `<bse-oases-alerts-icons>` is used in the `html` template
3. **Ctrl/Cmd-Click** on `bse-oases-alerts-icons` in the template literal
4. ✅ **Expected**: The IDE should navigate to `alerts-icons.ts` and highlight the `BseOasesAlertsIcons` class
5. Similarly, Ctrl/Cmd-Click on `<example-component>` should navigate to `example-component.ts`

### 5. Test Autocomplete in HTML Files

1. In `examples/site-meters.html`, edit the `<bse-oases-alerts-icons` tag
2. Start typing a new attribute after `<bse-oases-alerts-icons `
3. Press **Ctrl+Space** to trigger autocomplete
4. ✅ **Expected**: You should see suggestions for:
   - `icon` (type: String, = 'warning')
   - `color` (type: String, = 'red')
   - `size` (type: Number, = 24)

### 6. Test Autocomplete in TypeScript Files

1. In `examples/dashboard-view.ts`, edit the `<bse-oases-alerts-icons` tag in the template
2. Start typing a new attribute after `<bse-oases-alerts-icons `
3. Press **Ctrl+Space** to trigger autocomplete
4. ✅ **Expected**: You should see the same suggestions for `icon`, `color`, and `size`

### 7. Verify the Fix

The plugin now works because:
- `plugin.xml` uses `language="XML"` for HTML files (not `language="HTML"`)
- `plugin.xml` also registers for `language="JavaScript"` and `language="TypeScript"` for TS-to-TS navigation
- The completion pattern correctly matches XML attribute names in both HTML and template literals
- The reference contributor properly resolves custom element tags to their TypeScript classes

## Troubleshooting

If the features don't work:

1. **Check plugin is enabled**: Settings → Plugins → Look for "Lit DevTools"
2. **Verify JavaScript plugin is active**: Settings → Plugins → Search for "JavaScript"
3. **Check file types**: Make sure `.html` files are recognized as HTML/XML
4. **Restart IDE**: Sometimes a restart is needed after installation
5. **Check project scope**: The plugin searches within `GlobalSearchScope.projectScope()`

## Component Naming Pattern

The example uses `bse-oases-alerts-icons` to match the user's naming pattern:
- Custom element names must contain a hyphen (Web Components standard)
- The plugin works with any valid custom element name
- The `@customElement('bse-oases-alerts-icons')` decorator defines the tag name
- The tag name in HTML must exactly match the decorator argument

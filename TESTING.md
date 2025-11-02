# Testing Guide for Lit DevTools Plugin

This guide helps you verify that the Lit DevTools plugin is properly installed and working in WebStorm/IntelliJ IDEA.

## Installation Verification

### Step 1: Confirm Plugin is Installed

1. Open WebStorm/IntelliJ IDEA
2. Go to **Settings → Plugins** (or **Preferences → Plugins** on macOS)
3. Search for "Lit DevTools"
4. Verify it shows as installed and enabled (checkbox is checked)
5. If you just installed it, restart the IDE

### Step 2: Look for Startup Indicators

After restarting, you should see these indicators that the plugin is active:

#### A. Startup Notification
- A notification balloon should appear in the bottom-right corner
- Title: "Lit DevTools Plugin Active"
- Message: "Lit DevTools plugin v0.1.0 is now active..."
- If you missed it, check the Event Log: **View → Tool Windows → Event Log**

#### B. Lit Inspector Tool Window
- Look at the right side of your IDE window
- You should see a tab labeled **"Lit Inspector"**
- Click it to open the diagnostic panel
- The panel shows:
  - Plugin version and status
  - Currently open file information
  - Detected Lit components with details
  - A "Refresh" button to update the display

#### C. IDE Logs
- Go to **Help → Show Log in Explorer** (Windows/Linux) or **Help → Show Log in Finder** (macOS)
- Open the `idea.log` file in a text editor
- Search for "Lit DevTools"
- You should see messages like:
  ```
  INFO - Lit DevTools: Plugin starting up for project <your-project-name>
  INFO - Lit DevTools: LitHtmlCompletionContributor initialized
  INFO - Lit DevTools: LitTagReferenceContributor initialized
  INFO - Lit DevTools: LitStructureViewBuilder initialized
  ```

## Feature Testing

Once you've confirmed the plugin is loaded, test each feature:

### Test 1: Component Detection

1. Open the `examples/example-component.ts` file from this repository
2. Open the **Lit Inspector** tool window (right side)
3. Click the **Refresh** button
4. You should see:
   ```
   Found 1 Lit component(s):
   
   Component: <example-component>
     Class: ExampleComponent
     Properties: 3
       - name: string
       - count: number
       - disabled: boolean
     State: 2
       - _internalValue: string
       - _isLoading: boolean
     Methods: 4
     Events: 2
       - count-changed
       - data-loaded
     Has Styles: true
   ```

**✓ Pass**: Component details are displayed  
**✗ Fail**: Shows "No Lit components found" → Check that the file has `@customElement()` decorator

### Test 2: Structure View

1. Keep `examples/example-component.ts` open
2. Open the Structure View: **Alt+7** (Windows/Linux) or **Cmd+7** (macOS)
3. Expand the `ExampleComponent` class node
4. You should see organized sections:
   - **Properties** (name, count, disabled)
   - **State** (_internalValue, _isLoading)
   - **Private** (_privateCounter)
   - **Methods** (connectedCallback, _handleClick, etc.)
   - **Events** (count-changed, data-loaded)
   - **CSS** (styles)

**✓ Pass**: Sections are displayed with correct items  
**✗ Fail**: Standard structure view without sections → Check IDE logs for errors

### Test 3: Ctrl/Cmd-Click Navigation

1. Open `examples/index.html`
2. Find the line `<example-component name="Test"></example-component>`
3. Hold **Ctrl** (Windows/Linux) or **Cmd** (macOS) and hover over `example-component`
4. The tag should be underlined, indicating a link
5. Click while holding Ctrl/Cmd
6. You should navigate to `ExampleComponent` class definition in `example-component.ts`

**Check the logs** when you perform this action:
```
INFO - Lit DevTools: Looking up navigation reference for tag <example-component>
INFO - Lit DevTools: Found 1 navigation target(s) for <example-component>
```

**✓ Pass**: Navigates to TypeScript class  
**✗ Fail**: No navigation or underline → Check logs for "No navigation target found"

### Test 4: Attribute Autocompletion

1. Open `examples/index.html`
2. On a new line, start typing: `<example-component `
3. After typing the space, trigger autocomplete: **Ctrl+Space**
4. You should see completion suggestions:
   - `name` (with type "string")
   - `count` (with type "number")
   - `disabled` (with type "boolean")
   - `@count-changed` (event)
   - `@data-loaded` (event)

**Check the logs** when you trigger completion:
```
INFO - Lit DevTools: Attempting completion for tag <example-component>
INFO - Lit DevTools: Providing 3 property completions and 2 event completions for <example-component>
```

**✓ Pass**: Autocomplete shows properties and events  
**✗ Fail**: No custom completions → Check logs for "No Lit component found for tag"

## Troubleshooting

### No Startup Notification

**Possible causes:**
1. Plugin didn't load → Check Settings → Plugins
2. Notification was dismissed → Check View → Tool Windows → Event Log
3. Project didn't open yet → Close and reopen project

### Lit Inspector Window Missing

**Solutions:**
1. Go to **View → Tool Windows**
2. Look for "Lit Inspector" in the menu
3. Click to open it
4. If not in the menu, plugin may not be loaded → Check Settings → Plugins

### No Log Messages

**If you see no "Lit DevTools" messages in idea.log:**

1. Verify plugin is enabled in Settings → Plugins
2. Restart the IDE
3. Check if the plugin ZIP was installed correctly
4. Try reinstalling: Settings → Plugins → ⚙️ → Install Plugin from Disk

### Features Not Working

**If structure view, navigation, or completion don't work:**

1. **Check your Lit component structure**:
   - Must have `@customElement('tag-name')` decorator
   - Must extend `LitElement`
   - Properties need `@property()` decorator
   
2. **Verify JavaScript plugin is enabled**:
   - Settings → Plugins
   - Search for "JavaScript"
   - Ensure it's enabled

3. **Check file types are recognized**:
   - .ts files should be recognized as TypeScript
   - .html files should be recognized as HTML
   - Settings → Editor → File Types

4. **Review the logs** for specific error messages:
   - Each feature logs when it's triggered
   - Look for exceptions or error messages
   - Share relevant log sections when reporting issues

## Expected Log Messages

When the plugin is working correctly, you should see these types of messages:

### On Startup
```
INFO - Lit DevTools: Plugin starting up for project MyProject
INFO - Lit DevTools: LitHtmlCompletionContributor initialized
INFO - Lit DevTools: LitTagReferenceContributor initialized
INFO - Lit DevTools: Registering reference providers
INFO - Lit DevTools: LitStructureViewBuilder initialized
INFO - Lit DevTools: Startup notification sent
INFO - Lit DevTools: Creating tool window content
```

### When Opening a Lit Component File
```
INFO - Lit DevTools: Creating structure view for example-component.ts
INFO - Lit DevTools: Found component <example-component> with 3 properties, 2 state fields, 2 events
```

### When Using Navigation
```
INFO - Lit DevTools: Looking up navigation reference for tag <example-component>
INFO - Lit DevTools: Found 1 navigation target(s) for <example-component>
```

### When Using Completion
```
INFO - Lit DevTools: Attempting completion for tag <example-component>
INFO - Lit DevTools: Providing 3 property completions and 2 event completions for <example-component>
```

### When Refreshing Lit Inspector
```
INFO - Lit DevTools: Refreshing inspector panel
INFO - Lit DevTools: Inspector panel refreshed
```

## Need Help?

If you've followed this guide and the plugin still isn't working:

1. **Collect diagnostic information**:
   - IDE version (Help → About)
   - Plugin version (Settings → Plugins)
   - Relevant section of idea.log with "Lit DevTools" messages
   - Screenshot of Lit Inspector panel

2. **Report an issue** with:
   - What you expected to happen
   - What actually happened
   - Steps to reproduce
   - Diagnostic information from step 1

## Summary Checklist

Use this checklist to verify the plugin is fully working:

- [ ] Plugin appears in Settings → Plugins as installed and enabled
- [ ] Startup notification appears when opening a project
- [ ] "Lit Inspector" tool window is visible on the right side
- [ ] IDE logs contain "Lit DevTools" messages
- [ ] Lit Inspector shows component details when viewing example-component.ts
- [ ] Structure View (Alt/Cmd+7) shows organized sections
- [ ] Ctrl/Cmd-Click on HTML tags navigates to TypeScript class
- [ ] Autocomplete suggests properties and events for Lit components

If all items are checked, the plugin is working correctly! 🎉

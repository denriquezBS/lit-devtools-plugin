import { LitElement, html, css } from 'lit';
import { customElement, property, state } from 'lit/decorators.js';

/**
 * An example component to demonstrate the Lit DevTools plugin features.
 * This shows:
 * - @property decorators for reactive properties
 * - @state decorators for internal state
 * - Private fields
 * - Methods
 * - Event dispatching
 * - CSS styles
 */
@customElement('example-component')
export class ExampleComponent extends LitElement {
  // Properties - will appear in the "Properties" section
  @property({ type: String })
  name: string = 'World';

  @property({ type: Number })
  count: number = 0;

  @property({ type: Boolean })
  disabled: boolean = false;

  // State - will appear in the "State" section
  @state()
  private _internalValue: string = '';

  @state()
  private _isLoading: boolean = false;

  // Private fields - will appear in the "Private" section
  private _privateCounter: number = 0;

  // CSS - will be detected in the "CSS" section
  static styles = css`
    :host {
      display: block;
      padding: 16px;
      border: 1px solid #ccc;
    }

    button {
      padding: 8px 16px;
      background: #007acc;
      color: white;
      border: none;
      cursor: pointer;
    }

    button:disabled {
      opacity: 0.5;
      cursor: not-allowed;
    }
  `;

  // Methods - will appear in the "Methods" section
  connectedCallback() {
    super.connectedCallback();
    console.log('Component connected');
  }

  private _handleClick() {
    this.count++;
    this._privateCounter++;
    
    // Events - "count-changed" will be detected in the "Events" section
    this.dispatchEvent(new CustomEvent('count-changed', {
      detail: { count: this.count },
      bubbles: true,
      composed: true
    }));
  }

  private async _loadData() {
    this._isLoading = true;
    // Simulate async operation
    await new Promise(resolve => setTimeout(resolve, 1000));
    this._internalValue = 'Loaded!';
    this._isLoading = false;
    
    // Another event - "data-loaded" will also be detected
    this.dispatchEvent(new CustomEvent('data-loaded', {
      detail: { value: this._internalValue }
    }));
  }

  render() {
    return html`
      <div>
        <h2>Hello, ${this.name}!</h2>
        <p>Count: ${this.count}</p>
        <button 
          @click=${this._handleClick} 
          ?disabled=${this.disabled}
        >
          Increment
        </button>
        <button @click=${this._loadData}>
          Load Data
        </button>
        ${this._isLoading ? html`<p>Loading...</p>` : ''}
        ${this._internalValue ? html`<p>${this._internalValue}</p>` : ''}
      </div>
    `;
  }
}

import { LitElement, html, css } from 'lit';
import { customElement, property } from 'lit/decorators.js';

/**
 * A dashboard component that uses other Lit components in its template.
 * This demonstrates navigation from .ts to .ts when using components inside templates.
 */
@customElement('dashboard-view')
export class DashboardView extends LitElement {
  @property({ type: Number })
  alertCount: number = 0;

  static styles = css`
    :host {
      display: block;
      padding: 20px;
    }
    .alerts-section {
      margin: 20px 0;
    }
  `;

  render() {
    return html`
      <div class="dashboard">
        <h1>Dashboard</h1>
        
        <div class="alerts-section">
          <h2>Alerts (${this.alertCount})</h2>
          
          <!-- Should be able to Ctrl/Cmd-Click on "bse-oases-alerts-icons" to navigate to alerts-icons.ts -->
          <bse-oases-alerts-icons 
            icon="error"
            color="red"
            size="32"
          ></bse-oases-alerts-icons>
          
          <bse-oases-alerts-icons 
            icon="warning"
            color="orange"
            size="24"
          ></bse-oases-alerts-icons>
        </div>
        
        <div class="example-section">
          <!-- Should also navigate to example-component.ts -->
          <example-component 
            name="Dashboard User"
            count="5"
          ></example-component>
        </div>
      </div>
    `;
  }
}

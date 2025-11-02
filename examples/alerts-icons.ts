import { LitElement, html, css } from 'lit';
import { customElement, property } from 'lit/decorators.js';

/**
 * Example component matching the user's naming pattern.
 * This demonstrates navigation from <bse-oases-alerts-icons> tag to this file.
 */
@customElement('bse-oases-alerts-icons')
export class BseOasesAlertsIcons extends LitElement {
  @property({ type: String })
  icon: string = 'warning';

  @property({ type: String })
  color: string = 'red';

  @property({ type: Number })
  size: number = 24;

  static styles = css`
    :host {
      display: inline-block;
    }
  `;

  render() {
    return html`
      <svg width="${this.size}" height="${this.size}" fill="${this.color}">
        <circle cx="12" cy="12" r="10" />
      </svg>
    `;
  }
}

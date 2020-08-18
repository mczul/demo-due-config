import { Component } from '@angular/core';

@Component({
  selector: 'scnf-root',
  template: `
    <h1>Scheduled Config</h1>

    <scnf-health-indicator></scnf-health-indicator>
    
    <router-outlet></router-outlet>
  `,
  styles: []
})
export class AppComponent {
  title = 'scheduled-config-frontend';
}

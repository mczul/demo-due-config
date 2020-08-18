import { Component, OnInit, OnDestroy } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Subject } from 'rxjs/internal/Subject';
import { timer } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
@Component({
  selector: 'scnf-health-indicator',
  template: `
    <div>
      <div>Backend</div>
      <div>{{ state }}</div>
    </div>
  `,
  styles: [
  ]
})
export class HealthIndicatorComponent implements OnInit, OnDestroy {
  private unsubscribeSubject = new Subject<void>();
  state = 'probing';

  // TODO: Add Actuator to backend and utilize data

  constructor(private httpClient: HttpClient) { }

  ngOnInit(): void {
    timer(0, 5000)
      .pipe(
        takeUntil(this.unsubscribeSubject)
      )
      .subscribe((counter: number) => {
        console.log(`Polling backend: ${counter}`);
      });
  }

  ngOnDestroy(): void {
    this.unsubscribeSubject.next();
    this.unsubscribeSubject.complete();
  }

}

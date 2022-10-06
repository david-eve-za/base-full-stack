import {Component, Input, OnInit} from '@angular/core';

@Component({
  selector: 'app-title-h1',
  templateUrl: './title-h1.component.html',
  styleUrls: ['./title-h1.component.css']
})
export class TitleH1Component implements OnInit {

  @Input() title: string = '';
  @Input() type: 'primary' | 'secondary' | 'success' | 'danger' | 'warning' | 'info' | 'light' | 'dark' = 'primary';

  constructor() { }

  ngOnInit(): void {
  }

}

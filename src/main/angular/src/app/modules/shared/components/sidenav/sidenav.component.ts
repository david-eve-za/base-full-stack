import { Component, OnInit } from '@angular/core';
import {MediaMatcher} from "@angular/cdk/layout";

@Component({
  selector: 'app-sidenav',
  templateUrl: './sidenav.component.html',
  styleUrls: ['./sidenav.component.css']
})
export class SidenavComponent implements OnInit {

  mobileQuery: MediaQueryList;

  menuNav = [
    {name: 'Home', icon: 'home', route: 'home'},
    {name: 'Categories', icon: 'category', route: 'category'},
    {name: 'Products', icon: 'production_quantity_limits', route: 'home'},
  ];

  constructor(media:MediaMatcher) {
    this.mobileQuery = media.matchMedia('(max-width: 600px)');
  }

  ngOnInit(): void {
  }

}

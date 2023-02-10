import {Component, OnInit} from '@angular/core';
import {MediaMatcher} from "@angular/cdk/layout";
import {KeycloakService} from "keycloak-angular";
import {KeycloakProfile} from "keycloak-js";

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
    {name: 'Products', icon: 'production_quantity_limits', route: 'product'},
    {name: 'Books', icon: 'book', route: 'books'},
  ];

  userProfile!: KeycloakProfile;

  constructor(media: MediaMatcher, private keycloakService: KeycloakService) {
    this.mobileQuery = media.matchMedia('(max-width: 600px)');
  }

  async ngOnInit(): Promise<void> {
    this.userProfile = await this.keycloakService.loadUserProfile();
  }

  logout() {
    this.keycloakService.logout();
  }
}

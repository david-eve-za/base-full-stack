import { Injectable } from '@angular/core';
import {KeycloakService} from "keycloak-angular";

@Injectable({
  providedIn: 'root'
})
export class UtilService {

  constructor(private keycloakService:KeycloakService) { }

  getUserRoles():string[] {
    return this.keycloakService.getUserRoles();
  }

  isAdmin():boolean {
    return this.keycloakService.getUserRoles().includes('admin');
  }
}

import {Injectable} from '@angular/core';
import {ActivatedRouteSnapshot, CanActivate, Router, RouterStateSnapshot, UrlTree} from '@angular/router';
import {Observable} from 'rxjs';
import {KeycloakAuthGuard, KeycloakService} from "keycloak-angular";

@Injectable({
  providedIn: 'root'
})
export class AuthGuard extends KeycloakAuthGuard{

  constructor(router: Router,keycloakAngular: KeycloakService) {
    super(router, keycloakAngular);
    this.router = router;
  }

  isAccessAllowed(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Promise<boolean | UrlTree> {
    return new Promise(async (resolve, reject) => {
      let permission;
      if (!this.authenticated) {
        this.keycloakAngular.login().catch((e) => console.log(e));
        return reject(false);
      }
      const requiredRoles = route.data['roles'];
      if (!requiredRoles || requiredRoles.length === 0) {
        resolve(true);
      } else if (!this.roles || this.roles.length === 0) {
        resolve(false);
      } else {
        for (const requiredRole of requiredRoles) {
          if (this.roles.indexOf(requiredRole) > -1) {
            resolve(true);
          }
        }
        resolve(false);
      }
    } );
  }

}

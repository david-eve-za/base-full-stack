import {KeycloakService} from "keycloak-angular";

export class KeycloakInit {

}
export function initializeKeycloak(keycloak: KeycloakService) {
  return () =>
    keycloak.init({
      config: {
        url: 'http://192.168.1.15:8082/',
        realm: 'MiscRealm',
        clientId: 'angular-client'
      },
      initOptions: {
        onLoad: 'login-required',
        flow: "standard",
        silentCheckSsoRedirectUri:
          window.location.origin + '/assets/silent-check-sso.html'
      },
      loadUserProfileAtStartUp: true,
      bearerExcludedUrls: ['/assets'],

    });
}

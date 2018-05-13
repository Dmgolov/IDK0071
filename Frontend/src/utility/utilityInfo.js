import {inject} from 'aurelia-framework';
import {AuthService} from 'aurelia-authentication';

@inject(AuthService)
export class UtilityInfo {
  constructor(authService) {
    this.authService = authService;

    this.username = "";
    this.lobbyId = null;
    this.gameMode = "";
  }

  requestUsernameUpdate() {
    if (this.authService.isAuthenticated()) {

      this.authService.getMe()
      .then(data => {
        if (data.username !== "null") {
          this.username = data.username;
        } else {
          this.authService.logout();
        }
      })
      .catch(console.error);
    }
  }

}

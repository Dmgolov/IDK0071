import {inject} from "aurelia-framework";
import {Router} from 'aurelia-router';
import {UtilityInfo} from "../utility/utilityInfo";
import {AuthService} from 'aurelia-authentication';
import {Endpoint} from 'aurelia-api';

@inject(UtilityInfo, Router, AuthService, Endpoint.of('auth'))
export class SignUp {
  constructor(utilityInfo, router, authService, authEndpoint) {
    this.utilityInfo = utilityInfo;
    this.router = router;
    this.authService = authService;
    this.authEndpoint = authEndpoint;

    this.username = "";
    this.email = "";
    this.password = "";

    this.resultFailed = false;
    this.message = "";
  }

  signUp() {
    let username = this.utilityInfo.htmlEscape(this.username);
    let email = this.utilityInfo.htmlEscape(this.email);
    let password = this.utilityInfo.htmlEscape(this.password);

    this.authEndpoint.post('signup', {
      "displayName": username,
      "email": email,
      "password": password
    })
    .then(data => {
      if (data.result === "failed") {
        this.resultFailed = true;
        this.message = data.message;
      } else {
        this.resultFailed = false;
        this.message = "";
        this.router.navigate("home");
      }
    })
    .catch(console.error);
  }

  goBack() {
    this.router.navigate("home");
  }

}

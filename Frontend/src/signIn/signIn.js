import {inject, computedFrom} from "aurelia-framework";
import {Router} from 'aurelia-router';
import {UtilityInfo} from "../utility/utilityInfo";
import {AuthService} from 'aurelia-authentication';
import {Endpoint} from 'aurelia-api';

@inject(UtilityInfo, Router, AuthService, Endpoint.of('auth'))
export class SignIn {
  constructor(utilityInfo, router, authService, authEndpoint) {
    this.utilityInfo = utilityInfo;
    this.router = router;
    this.authService = authService;
    this.authEndpoint = authEndpoint;

    this.email = "";
    this.password = "";

    this.resultFailed = false;
    this.message = "";
  }

  signIn() {
    let email = this.utilityInfo.htmlEscape(this.email);
    let password = this.utilityInfo.htmlEscape(this.password);

    return this.authService.login(email, password)
      .then(data => {
      })
      .catch(err => {
        this.authEndpoint.post('signin', {
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
          }
        })
        .catch(console.error);
      });
  }

  goBack() {
    this.router.navigate("home");
  }

}

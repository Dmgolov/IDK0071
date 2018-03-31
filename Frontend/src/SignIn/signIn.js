import {inject, computedFrom} from "aurelia-framework";
import {HttpClient, json} from "aurelia-fetch-client";
import {Router} from 'aurelia-router';
import {UtilityInfo} from "../utility/utilityInfo";
import {AuthService} from 'aurelia-authentication';

@inject(UtilityInfo, Router, AuthService)
export class SignIn {
  constructor(utilityInfo, router, authService) {
    this.utilityInfo = utilityInfo;
    this.router = router;
    this.authService = authService;

    this.email;
    this.password;
  }

  // make a getter to get the authentication status.
  // use computedFrom to avoid dirty checking
  // @computedFrom('authService.authenticated')
  // get authenticated() {
  //   return this.authService.authenticated;
  // }

  login() {
      return this.authService.login(this.email, this.password)
      .then(response => {
          console.log("success logged " + response);
      })
      .catch(err => {
          console.log("login failure");
      });
  };

  // authenticate(name) {
  //     return this.authService.authenticate(name)
  //     .then(response => {
  //         console.log("auth response " + response);
  //     });
  // }
}

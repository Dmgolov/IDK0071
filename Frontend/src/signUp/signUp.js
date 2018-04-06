import {inject} from "aurelia-framework";
import {Router} from 'aurelia-router';
import {UtilityInfo} from "../utility/utilityInfo";
import {AuthService} from 'aurelia-authentication';

@inject(UtilityInfo, Router, AuthService)
export class SignUp {
  constructor(utilityInfo, router, authService) {
    this.utilityInfo = utilityInfo;
    this.router = router;
    this.authService = authService;

    this.username;
    this.email;
    this.password;
  }

  signUp() {
    this.authService.signup(this.username, this.email, this.password)
      .then(data => {
        // console.log(data);
      })
      .catch(err => {
        console.log("registration failure");
        // console.log(err);
      });
  }

  goBack() {
    this.router.navigate("home");
  }

}

export default {
  endpoint: 'auth',
  configureEndpoints: ['auth', 'lobby', 'game'],
  // baseUrl: 'http://localhost:8080',
  loginRoute: '/signIn',
  loginOnSignup: false,
  signupRedirect: '#/home',
  logoutRedirect: '#/home',
  loginUrl: 'signin',
  signupUrl: 'signup',
  logoutUrl: 'signout',
  logoutMethod: 'get',
  profileUrl: 'user',
  unlinkUrl: 'unlink',
  storageChangedReload: true,    // ensure secondary tab reloading after auth status changes
  expiredRedirect: 1,            // redirect to logoutRedirect after token expiration
  providers: {
    google: {
      url: 'google',
      clientId: '239531536023-ibk10mb9p7ullsw4j55a61og5lvnjrff6.apps.googleusercontent.com'
    },
    facebook:{
      url: 'facebook',
      clientId: '1465278217541708498'
    }
  }
};

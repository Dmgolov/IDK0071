import environment from './environment';
import authConfig from './authConfig';

export function configure(aurelia) {
  aurelia.use
    .standardConfiguration()
    /* setup the api endpoints first (if desired) */
    .plugin('aurelia-api', configure => {
      configure
        .registerEndpoint('auth', 'http://localhost:8080/auth/')
        .registerEndpoint('protected-api', 'http://localhost:8080/protected-api/')
        .registerEndpoint('public-api', 'http://localhost:8080/public-api/');
    })
    /* configure aurelia-authentication */
    .plugin('aurelia-authentication', baseConfig => {
        baseConfig.configure(authConfig);
    })
    .feature('resources');

  if (environment.debug) {
    aurelia.use.developmentLogging();
  }

  if (environment.testing) {
    aurelia.use.plugin('aurelia-testing');
  }

  aurelia.start().then(() => aurelia.setRoot());
}

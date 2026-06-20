module.exports = {
  '/api/v1/auth/*': {
    target: 'http://localhost:8080',
    secure: false,
    pathRewrite: { '^/api/v1/auth': '/auth' },
    logLevel: 'debug',
  },
};

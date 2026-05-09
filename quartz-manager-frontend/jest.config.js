const {createEsmPreset} = require('jest-preset-angular/presets');

module.exports = {
  ...createEsmPreset({
    tsconfig: '<rootDir>/tsconfig.spec.json',
    stringifyContentPathRegex: '\\.(html|svg)$'
  }),
  setupFilesAfterEnv: ['<rootDir>/jest.setup.ts'],
  transformIgnorePatterns: [
    'node_modules/(?!(@angular|@stomp/rx-stomp|@stomp/stompjs|.*\\.mjs$)/)'
  ]
};

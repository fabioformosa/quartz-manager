const {createEsmPreset} = require('jest-preset-angular/presets');

module.exports = {
  ...createEsmPreset({
    tsconfig: '<rootDir>/tsconfig.spec.json',
    stringifyContentPathRegex: '\\.(html|svg)$'
  }),
  moduleNameMapper: {
    '^tslib$': '<rootDir>/node_modules/tslib/tslib.es6.mjs',
    '^rxjs$': '<rootDir>/node_modules/rxjs/dist/cjs/index.js',
    '^rxjs/operators$': '<rootDir>/node_modules/rxjs/dist/cjs/operators/index.js',
    '^rxjs/(.*)$': '<rootDir>/node_modules/rxjs/dist/cjs/$1',
    '^@fortawesome/fontawesome$': '<rootDir>/node_modules/@fortawesome/fontawesome/index.js',
    '^@fortawesome/fontawesome-free-solid$': '<rootDir>/node_modules/@fortawesome/fontawesome-free-solid/index.js'
  },
  setupFilesAfterEnv: ['<rootDir>/jest.setup.ts'],
  transformIgnorePatterns: [
    'node_modules/(?!(@angular|@fortawesome|@stomp/rx-stomp|@stomp/stompjs|.*\\.mjs$)/)'
  ]
};

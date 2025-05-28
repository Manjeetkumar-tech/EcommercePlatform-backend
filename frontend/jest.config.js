module.exports = {
  testEnvironment: 'jest-environment-jsdom',
  moduleFileExtensions: ['js', 'json', 'vue'],
  transform: {
    '^.+\\.js$': 'babel-jest',
    '^.+\\.vue$': '@vue/vue3-jest',
  },
  moduleNameMapper: {
    '^@/(.*)$': '<rootDir>/src/$1',
  },
  // Mock localStorage
  setupFilesAfterEnv: ['jest-localstorage-mock'],
  // Fix for "ReferenceError: regeneratorRuntime is not defined" with async/await
  // This can happen if babel-polyfill or similar is not included,
  // @babel/preset-env should handle this but sometimes needs explicit setup for Jest
  // For Jest 27 and Babel 7, @babel/preset-env with correct targets should be enough.
  // If issues arise, might need to add 'transform-runtime' to babel config or polyfills here.
  // For now, assume @vue/cli-plugin-babel/preset has it covered or it's in babel.config.js
  // If not, would add: transform: { '^.+\\.js$': 'babel-jest' } and ensure babel.config.js is set up.

  // If vue-router is used in components, it's good to mock it globally or per test suite.
  // For example:
  // setupFiles: ['./tests/setup.js'], // if you have a global setup file
};

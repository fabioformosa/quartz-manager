import sonarjs from 'eslint-plugin-sonarjs';
import tsParser from '@typescript-eslint/parser';

export default [
  {
    files: ['src/**/*.ts'],
    languageOptions: {
      parser: tsParser,
      parserOptions: {
        project: 'tsconfig.json',
        sourceType: 'module'
      }
    },
    plugins: {
      sonarjs
    },
    rules: {
      ...sonarjs.configs.recommended.rules,
      'sonarjs/deprecation': 'off',
      'sonarjs/no-commented-code': 'off',
      'sonarjs/no-dead-store': 'off',
      'sonarjs/no-incomplete-assertions': 'off',
      'sonarjs/no-primitive-wrappers': 'off',
      'sonarjs/no-unused-vars': 'off',
      'sonarjs/prefer-promise-shorthand': 'off',
      'sonarjs/todo-tag': 'off',
      'sonarjs/unused-import': 'off'
    }
  }
];

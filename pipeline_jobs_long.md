# Pipeline Jobs – Full Descriptions

## 1. deploy-local
Deploys the OrderFlow API WAR to your local Tomcat.

## 2. deploy-prod
Deploys the OrderFlow API WAR to the Hostpoint Tomcat (production).

## 3. deploy-local-then-prod
Runs deploy-local first; deploys to prod only if local is successful.

## 4. orderflow-ci
Runs full CI: mvn clean verify, tests, integration tests, JaCoCo.

## 5. orderflow-smoke-prod
Production smoke checks: /api/ping, /v3/api-docs, etc.

## 6. orderflow-flyway-validate
Validates Flyway migrations before deployment.

## 7. orderflow-static-analysis
Runs SpotBugs, Checkstyle, and PMD for code quality.

## 8. orderflow-dependency-audit
Runs OWASP Dependency Check or Snyk for vulnerabilities.

## 9. orderflow-e2e-local
Runs Selenium/Selenide E2E tests on local environment.

## 10. orderflow-release-tag
Creates a Git version tag and GitHub Release.

## 11. orderflow-rollback-prod
Rolls back to previous WAR if production fails.

## 12. orderflow-api-contract-check
Validates OpenAPI contract compatibility.

## 13. orderflow-log-scrape-prod
Scrapes production logs for errors, anomalies.

## 14. orderflow-db-backup-prod
Creates a production database backup before deployment.

## 15. orderflow-precommit-helper
Fast pre-commit checks: compile, minimal tests, linting.

# Pipeline Jobs – Short List

1. deploy-local — Deploy OrderFlow API to local Tomcat.
2. deploy-prod — Deploy OrderFlow API to production Hostpoint Tomcat.
3. deploy-local-then-prod — Deploy to local first; only deploy to production if local succeeds.
4. orderflow-ci — Build, unit tests, integration tests, JaCoCo.
5. orderflow-smoke-prod — Ping + OpenAPI smoke tests on prod.
6. orderflow-flyway-validate — Validate Flyway migrations.
7. orderflow-static-analysis — SpotBugs, Checkstyle, PMD.
8. orderflow-dependency-audit — OWASP/Snyk dependency scan.
9. orderflow-e2e-local — Selenium/Selenide E2E tests on localhost.
10. orderflow-release-tag — Version tag + GitHub release.
11. orderflow-rollback-prod — Roll back previous WAR.
12. orderflow-api-contract-check — Validate OpenAPI schema.
13. orderflow-log-scrape-prod — Error scan on prod logs.
14. orderflow-db-backup-prod — Backup production DB.
15. orderflow-precommit-helper — Fast checks before commits.

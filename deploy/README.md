# Production deployment setup

The GitHub Actions workflow builds the JAR on a GitHub-hosted runner and
deploys it to the existing Ubuntu server over SSH.

## GitHub Environment

Create the `exchangeCheckerEnv` environment and add these secrets:

- `DEPLOY_HOST`: server hostname or IP address
- `DEPLOY_USER`: SSH user
- `DEPLOY_SSH_PRIVATE_KEY`: private key for the SSH user
- `DEPLOY_KNOWN_HOSTS`: matching host key from `known_hosts`

Do not commit `private.properties`, application credentials, or SSH keys.

## Server preflight

Run once on the server:

```bash
sudo mkdir -p /var/exchangeChecker/releases
sudo apt-get update
sudo apt-get install -y curl
sudo -n true
systemctl is-enabled exchangeRate.service
systemctl is-active exchangeRate.service
```

The service must keep `/var/exchangeChecker` as its `WorkingDirectory`.
The deployment script maintains `/var/exchangeChecker/exchangeRate.jar` as
the active JAR and keeps previous releases in the `releases` directory.

The application configuration remains on the server in
`/var/exchangeChecker/private.properties`.

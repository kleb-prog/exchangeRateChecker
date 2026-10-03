#!/usr/bin/env bash
set -Eeuo pipefail

commit_sha="${1:?commit SHA is required}"
uploaded_jar="${2:?uploaded JAR path is required}"

app_dir="/var/exchangeChecker"
releases_dir="$app_dir/releases"
active_jar="$app_dir/exchangeRate.jar"
service_name="exchangeRate.service"
health_url="http://127.0.0.1:8080/actuator/health"
release_jar="$releases_dir/exchangeRate-${commit_sha}.jar"
previous_jar=""

rollback() {
  if [[ -n "$previous_jar" && -e "$previous_jar" ]]; then
    ln -sfn "$previous_jar" "$active_jar"
    systemctl restart "$service_name" || true
  fi
}

trap 'rollback' ERR

install -d -m 0755 "$releases_dir"
test -f "$uploaded_jar"
install -m 0644 "$uploaded_jar" "$release_jar"

if [[ -e "$active_jar" || -L "$active_jar" ]]; then
  previous_jar="$releases_dir/exchangeRate-backup-$(date +%Y%m%d%H%M%S).jar"
  cp -L "$active_jar" "$previous_jar"
fi

ln -sfn "$release_jar" "$active_jar"
systemctl restart "$service_name"

for attempt in {1..30}; do
  if systemctl is-active --quiet "$service_name" && curl --fail --silent --show-error "$health_url" >/dev/null; then
    trap - ERR
    find "$releases_dir" -maxdepth 1 -type f -name 'exchangeRate-*.jar' -printf '%T@ %p\n' \
      | sort -nr | tail -n +6 | cut -d' ' -f2- | xargs -r rm -f
    rm -f "$uploaded_jar" /tmp/exchangeRate-deploy.sh
    exit 0
  fi
  sleep 2
done

echo "Deployment failed after $attempt health checks; service status:" >&2
systemctl --no-pager --full status "$service_name" >&2 || true
echo "Recent service logs:" >&2
journalctl --no-pager -u "$service_name" -n 80 >&2 || true
rollback
exit 1

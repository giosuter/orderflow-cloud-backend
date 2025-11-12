cd /Users/giovannisuter/dev/projects/orderflow-cloud/back-end/orderflow-cloud-backend

mkdir -p scripts
# create/edit the file with your editor (vim as you prefer)
vim scripts/deploy_prod.sh

# make it executable and commit (important so Jenkins can run it)
chmod +x scripts/deploy_prod.sh
git add scripts/deploy_prod.sh
git commit -m "chore(ci): add production deploy script"
git push
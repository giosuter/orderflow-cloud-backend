#!/usr/bin/env bash
set -euo pipefail

# 1) Ensure target folders exist
mkdir -p src/main/java/ch/devprojects/orderflow/web/{dto,mapper}
mkdir -p src/test/java/ch/devprojects/orderflow/web/{dto,mapper}

# 2) Move *main* DTOs and Mappers under web/
if [ -d src/main/java/ch/devprojects/orderflow/dto ]; then
  git mv -k src/main/java/ch/devprojects/orderflow/dto/* src/main/java/ch/devprojects/orderflow/web/dto/ 2>/dev/null || true
fi
if [ -d src/main/java/ch/devprojects/orderflow/mapper ]; then
  git mv -k src/main/java/ch/devprojects/orderflow/mapper/* src/main/java/ch/devprojects/orderflow/web/mapper/ 2>/dev/null || true
fi

# 3) Move *test* DTOs and Mappers under web/
if [ -d src/test/java/ch/devprojects/orderflow/dto ]; then
  git mv -k src/test/java/ch/devprojects/orderflow/dto/* src/test/java/ch/devprojects/orderflow/web/dto/ 2>/dev/null || true
fi
if [ -d src/test/java/ch/devprojects/orderflow/mapper ]; then
  git mv -k src/test/java/ch/devprojects/orderflow/mapper/* src/test/java/ch/devprojects/orderflow/web/mapper/ 2>/dev/null || true
fi

# 4) Update package declarations in moved files
fix_pkg() {
  local from="$1" ; local to="$2"
  rg -l "^package ${from};" src/main/java src/test/java 2>/dev/null | while read -r f; do
    gsed -i "s/^package ${from};/package ${to};/" "$f" 2>/dev/null || \
    sed -i '' "s/^package ${from};/package ${to};/" "$f"
  done
}
# From core to web.* packages
fix_pkg "ch.devprojects.orderflow.dto"    "ch.devprojects.orderflow.web.dto"
fix_pkg "ch.devprojects.orderflow.mapper" "ch.devprojects.orderflow.web.mapper"

# 5) Fix imports throughout the codebase
replace_import() {
  local from="$1" ; local to="$2"
  rg -l "import ${from}\." src/main/java src/test/java 2>/dev/null | while read -r f; do
    gsed -i "s#import ${from}\.#import ${to}.#g" "$f" 2>/dev/null || \
    sed -i '' "s#import ${from}\.#import ${to}.#g" "$f"
  done
}
replace_import "ch.devprojects.orderflow.dto"    "ch.devprojects.orderflow.web.dto"
replace_import "ch.devprojects.orderflow.mapper" "ch.devprojects.orderflow.web.mapper"

# 6) If any empty old dirs remain, remove them
rmdir -p src/main/java/ch/devprojects/orderflow/mapper 2>/dev/null || true
rmdir -p src/main/java/ch/devprojects/orderflow/dto 2>/dev/null || true
rmdir -p src/test/java/ch/devprojects/orderflow/mapper 2>/dev/null || true
rmdir -p src/test/java/ch/devprojects/orderflow/dto 2>/dev/null || true

echo "Refactor complete. Next steps:
1) mvn -q -DskipTests=false clean verify
2) Review and commit: git add -A && git commit -m 'Normalize DTO/mapper packages under web/* and fix imports' "

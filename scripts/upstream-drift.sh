#!/usr/bin/env bash
# 对照上游 JMNeXt，报告 MultiComiX 的界面层漂移。
#
# 为什么需要它：MultiComiX 的界面是「从 JMNeXt 搬过来再改成多源」，上游每个版本都会改这些文件。
# 人肉记不住谁落后了多少，所以把判断变成可执行的对照。
#
# 注意：两边的目录结构不同（上游 ui/screens/<模块>/X.kt，这边先是扁平的 ui/screens/X.kt），
# 所以按**文件名**配对，而不是按路径。
#
# 输出：缺失 / 分叉（两边都有但内容不同）/ 同步，并在末尾给出总数。
#
# 用法： scripts/upstream-drift.sh [上游路径]
set -u
UPSTREAM=${1:-../JMNeXt/app/src/main/kotlin/com/jmnext}
HERE=$(cd "$(dirname "$0")/.." && pwd)
LOCAL="$HERE/app-android/src/main/kotlin/com/multicomix/android"

[ -d "$UPSTREAM" ] || { echo "找不到上游：$UPSTREAM"; exit 1; }

miss=0; fork=0; same=0
printf '  %-38s %-6s %s\n' "文件" "状态" "上游/本地行数"
while IFS= read -r u; do
    base=$(basename "$u")
    rel=${u#"$UPSTREAM"/}
    l=$(find "$LOCAL" -name "$base" -type f 2>/dev/null | head -1)
    ur=$(wc -l < "$u")
    if [ -z "$l" ]; then
        miss=$((miss+1)); printf '  %-38s %-6s %s\n' "$rel" "缺失" "$ur/-"
    elif cmp -s "$u" "$l"; then
        same=$((same+1)); printf '  %-38s %-6s %s\n' "$rel" "同步" "$ur"
    else
        fork=$((fork+1)); printf '  %-38s %-6s %s\n' "$rel" "分叉" "$ur/$(wc -l < "$l")"
    fi
done < <(find "$UPSTREAM/ui" -name '*.kt' | sort)

printf '\n  合计：同步 %s，分叉 %s，缺失 %s（上游共 %s 个界面文件）\n' \
    "$same" "$fork" "$miss" "$((same+fork+miss))"

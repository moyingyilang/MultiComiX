# 跟上游 JMNeXt 的同步办法

TriComiX 的界面是「从 JMNeXt 搬过来再改成多源」，而上游还在改。
靠记忆记不住谁落后多少，所以把判断做成可执行的对照。

## 一、先量：`scripts/upstream-drift.sh`

```bash
scripts/upstream-drift.sh              # 默认对照 ../JMNeXt
scripts/upstream-drift.sh /path/to/JMNeXt
```

按**文件名**配对（两边的目录结构不同：上游 `ui/screens/<模块>/X.kt`，这里先是扁平的 `ui/screens/X.kt`），
输出三类：缺失、分叉、同步，末尾给总数。

2026-11 首次实测：上游 58 个界面文件 → **同步 0、分叉 18、缺失 40**。

## 二、三类文件，三种处理

| 类别 | 例子 | 处理 |
| --- | --- | --- |
| 与图源无关、可直接搬 | `ui/theme/*`、`ui/components/{SectionTitle,StateBox,LoadMoreFooter,Glass…}` | 改 package 名后整文件复制，**不要手改**（改了下次对照就对不上） |
| 需要改写的 | 各屏幕里对 JM 专有类型的调用（`AlbumDetail`、`JmRepository` 等） | 按 `ui-port-plan.md` 的映射改成 `ComicSource` + `core` 模型 |
| 本期不搬 | 评论、画师、签到、通知 | 统一接口还没表达这些能力，强搬会让「图源无关」破产 |

## 三、踩过的坑

1. **package 名与 import 要一起改**。只改 package 会留一条 `import com.jmnext.ui.theme.JmTheme`，
   编译时报的是"找不到符号"，很容易误判成上游缺东西。
2. **上游的 `JmEasing` 在 `Theme.kt` 里，不是单独文件**。搬 `JmMotion.kt` 时会缺 `emphasized`
   等成员；正确做法是把缺的成员补进本地的 `JmEasing`，而不是新建一个同名 object（会 Redeclaration）。
3. **`SectionTitle` 依赖 `JmTheme`**，属于"可直接搬"的前提是本项目已有同名主题入口；
   TriComiX 有，所以只改 import 就行。
4. 容器里构建要先修 `/dev`（见 `android-termux-builders4JMNeXt/10-enter.sh`），
   否则任何 `>/dev/null` 都报 Permission denied，看起来像项目丢了。

## 四、本轮已同步

| 文件 | 状态 |
| --- | --- |
| `ui/theme/JmMotion.kt` | 已搬（改 package；补 `JmEasing.emphasized`） |
| `ui/theme/ReduceMotion.kt` | 已搬（只依赖 Android 框架） |
| `ui/components/SectionTitle.kt` | 已搬（改 package 与 `JmTheme` import） |

验证：容器内 `:app-android:compileDebugKotlin` 通过。

## 五、下一批

按 drift 报告里「缺失」的数量排序，优先搬与图源无关的组件：
`ExpandableOptionRow`、`RealGlass`、`RubberBand`、`ThemeModeIcon`、`ItemMotion`（已有则对齐）。
再往下才是屏幕：`Home`、`Favorites`、`Search`、`Detail`、`Reader` 的接口改写。

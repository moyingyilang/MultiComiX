package com.multicomix.core.model

/**
 * 统一模型 —— **字段以上游 JMNeXt 的 DTO 为基准**。
 *
 * 为什么以上游为基准而不是另设计一套：MultiComiX 的界面是从 JMNeXt 搬来的，
 * 屏幕里读的是 `album.author`、`item.liked`、`detail.series` 这些字段。
 * 如果统一模型自创一套字段名，每搬一屏都要写一层翻译，越搬越偏。
 *
 * 三个源（JM / Pica / EH）表达不了的字段一律给默认值，不删字段：
 * 界面按「有没有值」显隐，而不是按「哪个源」分支。
 *
 * 源特有的、暂时无法映射的信息仍进 [extra]，但**不鼓励**把本该是字段的东西塞进去。
 */

/** 作品：各源的 album / comic / gallery 统一到这里。字段取自上游 ListItem。 */
data class Comic(
    val sourceId: String,
    val id: String,
    val title: String,
    val author: String? = null,
    val coverUrl: String? = null,
    val description: String? = null,
    val tags: List<String> = emptyList(),
    val category: String? = null,
    val categorySub: String? = null,
    val updateAt: String? = null,
    val addDate: String? = null,
    val isFavorite: Boolean = false,
    val liked: Boolean = false,
    val extra: Map<String, String> = emptyMap(),
)

/**
 * 作品详情：字段取自上游 AlbumDetail。
 *
 * [author] 是**数组**（上游如此：每个作者可点击跳标签搜索）；
 * [actors] / [works] 同理。能不给就不要给单字符串版本，否则界面又要为一处特例分支。
 */
data class ComicDetail(
    val comic: Comic,
    val name: String? = null,
    val authors: List<String> = emptyList(),
    val actors: List<String> = emptyList(),
    val works: List<String> = emptyList(),
    val tags: List<String> = emptyList(),
    val description: String? = null,
    val addTime: String? = null,
    val commentTotal: Int = 0,
    val likes: Int = 0,
    val liked: Boolean = false,
    val isFavorite: Boolean = false,
    val totalPhotos: Int = 0,
    val totalViews: String? = null,
    val series: List<Chapter> = emptyList(),
    val related: List<Comic> = emptyList(),
    val realLink: String? = null,
    val extra: Map<String, String> = emptyMap(),
)

/** 章节，字段取自上游 SeriesItem：`sort` 是章节序号（字符串，历史原因），`name` 可空。 */
data class Chapter(
    val id: String,
    val sort: String? = null,
    val name: String? = null,
    /** 展示排序用：`sort` 可解析成数字时用数字，否则按出现顺序。 */
    val order: Int = 0,
    val extra: Map<String, String> = emptyMap(),
)

/**
 * 阅读单元。
 *
 * 各源语义不同 —— jm/pica 是"章节"，eh 是"分页"。
 * 界面只依赖"页序列"，不要依赖"章节"这个概念。
 */
data class PageRef(
    val chapterId: String,
    val index: Int,
    val extra: Map<String, String> = emptyMap(),
)

/** 阅读数据，字段取自上游 ReadPayload。 */
data class ReaderPayload(
    val id: String,
    val name: String? = null,
    val images: List<PageRef> = emptyList(),
    val totalPage: Int = 0,
    /** 低于该值的作品不做切片还原（上游 `scramble_id`）。 */
    val scrambleId: Int = 0,
    val extra: Map<String, String> = emptyMap(),
)

/** 取图请求：URL + 需要的请求头 + 可选的本地变换。 */
data class ImageRequest(
    val url: String,
    val headers: Map<String, String> = emptyMap(),
    val unscramble: UnscrambleSpec? = null,
)

/** 打乱图还原参数（源私有逻辑，放在这里是为了让界面层不必知道细节）。 */
data class UnscrambleSpec(val seed: String, val index: Int)

enum class ImageQuality { LOW, MEDIUM, HIGH, ORIGINAL }

data class Section(val title: String, val items: List<Comic>)

data class Paged<T>(val items: List<T>, val page: Int, val hasMore: Boolean)

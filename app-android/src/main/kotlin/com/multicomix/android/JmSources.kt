package com.multicomix.android

import android.content.Context
import com.multicomix.android.data.auth.AndroidKeystoreKeyProvider
import com.multicomix.android.data.prefs.SharedPrefsKeyValueStore
import com.multicomix.jm.data.JmRepository
import com.multicomix.jm.data.auth.AuthStore
import com.multicomix.jm.data.auth.SecureStore
import com.multicomix.jm.data.prefs.BlockStore
import com.multicomix.jm.source.JmSource

/**
 * 把 JM 源装起来：它需要「本地存储 + 密钥材料」这两样平台相关的东西，
 * 因此由应用侧提供（Android 用 SharedPreferences + Android Keystore）。
 *
 * 组装顺序照主项目 JMNeXt 桌面端的做法：SecureStore(prefs, keys) -> AuthStore(secure)，
 * BlockStore 单独一份存储，然后 JmRepository.create(...) 内部自建 remote/session。
 */
object JmSources {

    fun create(context: Context): JmSource {
        val app = context.applicationContext
        val secure = SecureStore(
            prefs = SharedPrefsKeyValueStore(app, "multicomix_jm_secure"),
            keys = AndroidKeystoreKeyProvider(),
        )
        val repository = JmRepository.create(
            authStore = AuthStore(secure),
            blockStore = BlockStore(SharedPrefsKeyValueStore(app, "multicomix_jm_block")),
            debug = false,
        )
        return JmSource(repository)
    }
}

package io.yefangwong.guard.api;

import io.yefangwong.guard.dialect.SqlDialect;
import java.util.Map;

/**
 * 插件執行上下文，提供 Metadata 與當前環境資訊。
 */
public interface GuardContext {
    SqlDialect getDialect();
    Object getMetadata(); // 核心 Metadata 模型
    Map<String, Object> getAttributes();
}

package io.yefangwong.guard.api;

import java.util.List;

/**
 * 客製化指令介面：允許將第三方功能註冊為 CLI 子命令。
 */
public interface CustomCommand {
    String getName();
    String getDescription();
    
    /**
     * 執行指令邏輯。
     */
    int execute(List<String> args, GuardContext context);
}

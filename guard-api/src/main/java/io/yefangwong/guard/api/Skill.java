package io.yefangwong.guard.api;

/**
 * Agent SKILL 介面：允許對 SQL 進行自定義驗證或處理。
 */
public interface Skill {
    String getName();
    String getDescription();

    /**
     * 在 SQL 校驗階段執行。
     * @return true 若驗證通過，false 則攔截執行。
     */
    boolean validate(String sql, Object ast, GuardContext context);
}

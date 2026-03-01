# ADR 0003: 使用者介面語言在地化 (簡繁轉換)

*   **日期:** 2026-03-01
*   **狀態:** 已接受 (Accepted)

## 1. 背景 (Context)
目前的 Intelligence 層使用本地 LLM (如 Qwen-Coder)，其訓練語料以簡體中文為主。儘管 Prompt 要求繁體輸出，模型仍可能回覆簡體，這會破壞企業級專業軟體的在地化體驗。

## 2. 決策 (Decision)
我們決定在 `guard-ai-client` 層級引入 **OpenCC4J (Open Chinese Convert for Java)**。
*   **攔截機制**: 在 `LocalAiClient` 收到 AI 回覆後，立即執行 S2T (Simplified to Traditional) 轉換。
*   **對齊習慣**: 確保術語符合台灣/香港使用習慣（如：欄位 vs 列）。

## 3. 結果 (Consequences)
*   **優點**: 提供一致的繁體中文 TUI 體驗，不依賴 LLM 的輸出穩定性。
*   **限制**: 增加了一小部分運行時記憶體開銷，但對於 CLI 應用影響微乎其微。

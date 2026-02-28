# ADR 0001: 核心架構決策 - 5 層解耦與 TUI (REPL) 模式

*   **日期:** 2026-02-27
*   **決策者:** Gemini CLI (Senior Architect)
*   **狀態:** 已接受 (Accepted)

## 1. 背景 (Context)
為了解決企業級 ERD (Entity Relationship Diagram) 診斷與安全防護的需求，我們需要一個與資料庫無關 (DB-Agnostic) 的 SQL 防護核心。

## 2. 決策 (Decision)
我們決定採用以下設計：
*   **Java 8+:** 確保在企業內網 (Enterprise On-Premise) 具有最高的相容性與穩定性。
*   **五層架構 (5-Tier Architecture):** 將互動層 (CLI)、智慧層 (AI)、核心層 (Core)、執行層 (Executor) 與擴充層 (API) 完全解耦，支持插件化。
*   **TUI (REPL) 模式:** CLI 作為首選接口，以提高診斷工具的即時感與專業性，降低開發者使用的門檻。
*   **PlantUML 為文件核心:** 所有設計文件以程式碼方式 (Diagram as Code) 儲存於 `docs/`。

## 3. 結果 (Consequences)
*   **優點:** 易於單元測試、支持 CI/CD 自動化、架構變更易於追踪。
*   **挑戰:** 需要開發自定義的 ANSI 渲染引擎以達成高性能 TUI。

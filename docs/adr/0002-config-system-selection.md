# ADR 0002: 動態配置系統與 AI 通訊選型

*   **日期:** 2026-02-27
*   **狀態:** 已接受 (Accepted)

## 1. 背景 (Context)
為了解決不同環境下的 AI Endpoint (如本地 192.168.137.1 與雲端) 以及資料庫路徑的切換問題，我們需要一個靈活的配置引擎。

## 2. 決策 (Decision)
*   **格式選型:** 採用 **YAML (guard.yaml)** 作為配置格式，因其具有優於 JSON 的可讀性，並符合主流 DevOps 配置習慣。
*   **解析工具:** 使用 **Jackson (jackson-dataformat-yaml)**。這能與我們現有的 `jackson-databind` (Metadata 使用) 無縫整合，且支援 JDK 8。
*   **整合位置:** 將 `ConfigLoader` 置於 `guard-core`，讓 CLI 與未來可能出現的 API Service 都能共享配置邏輯。

## 3. 結果 (Consequences)
*   **優點:** 降低系統耦合度，開發者可在不重編譯的情況下切換 AI 模型或資料庫路徑。
*   **限制:** 需確保 `guard.yaml` 檔案存在於運行目錄中。

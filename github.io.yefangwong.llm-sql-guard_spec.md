# llm-sql-guard 技術規格說明書 (Strategic Spec)

## 1. 專案願景
`llm-sql-guard` 是一個專為企業環境設計的 **SQL 安全審核中樞**。它旨在解決 LLM 生成 SQL 時的幻覺問題，確保所有 SQL 指令在執行前皆符合企業的 ERD 規範。

## 2. 核心架構原則
- **相容性**：全程支援 JDK 8。
- **來源中立 (Source Agnostic)**：支援 Teams Copilot、SpringBoot 服務或人工輸入。
- **穩定性優先 (UX Stability)**：一旦操作流程與 UI 風格定型，嚴禁頻繁調整，以維護用戶習慣與自動化腳本的穩定。
- **智慧診斷**：整合本地 AI 提供語義級別的錯誤解釋。

## 3. UI/UX 規範 (Design System)
- **色彩語義化**：
  - `Green`: [✅ VALIDATED / OK]
  - `Red`: [❌ VIOLATION / ERROR]
  - `Yellow`: [🤔 SUGGESTION / WARN]
  - `Cyan`: [關鍵字 / 命令]
  - `Gray`: [輔助資訊 / 裝飾線]
- **結構穩定性**：所有的錯誤診斷必須以「紅框卡片」呈現，所有的建議必須以「黃色列表」呈現。
- **操作一致性**：CLI 參數與交互指令（如 /exit, /help）在版本更新中需保持向下相容。

## 4. 模組定義
(略，同前次更新)

## 5. 動態配置規範 (guard.yaml)
(略，同前次更新)

# 🛡️ llm-sql-guard 專案開發藍圖 (TODO List - 終極整合版)

> **核心目標**：打造具備「AI 智慧診斷」能力的安全 SQL 工作站。
> **戰略原則**：
> 1. **沙盒先行 (Spider First)**：在 CLI 穩定後才對外提供服務。
> 2. **來源中立 (Source Agnostic)**：支援 Teams Copilot、SpringBoot 或手動貼入。
> 3. **穩定性優先 (UX Stability)**：一旦介面定型，嚴禁頻繁變動。

---

## 🚦 優先級說明
- 🔴 **高 (High)**: 核心架構、SQL 校驗、AI 診斷、Spider 驗證、UI 標準化。
- 🟡 **中 (Medium)**: TUI 體驗優化、Agent SKILL、表格渲染、配置彈性。
- 🔵 **低 (Low)**: 效能微調、Oracle 進階適配、封裝美化。

---

## 🟢 第一階段：專案骨架與 UI 標準化 (Foundations & Design System)
- [x] 🔴 **[高] Gradle 多模組架構搭建**：已完成 core, cli, api, ui, executor, dialect, ai-client, service。
- [x] 🔴 **[高] 4-Tier Metadata 模型定義**：DB -> Schema -> Table -> Column -> Relation。
- [x] 🔴 **[高] 抽象方言介面 (Dialect SPI)**：實作 SQLite 支援。
- [ ] 🔴 **[高] 標準化 UI 組件庫封裝**：統一 ANSI 色彩與邊框規範，確保介面風格不漂移。
- [ ] 🔴 **[高] 動態配置系統 (Config Engine)**：支援從 `guard.yaml` 讀取本地 AI (IP, Port, Model) 連線資訊。
- [x] 🔴 **[高] Windows 兼容性基礎**：整合 JAnsi 色彩控制。

## 🟠 第二階段：核心校驗引擎與智慧建議 (Core & Intelligence)
- [x] 🔴 **[高] SQL AST Visitor 實作**：深度語義校驗與 **別名追蹤 (Alias Tracking)**。
- [x] 🟡 **[中] 智慧建議引擎 (Fuzzy Match)**：實作 Levenshtein 算法提供「Did you mean?」建議。
- [ ] 🟡 **[中] 作用域敏感校驗優化**：處理 Spider 資料集中的 CTE 與多層嵌套子查詢。
- [ ] 🔵 **[低] Oracle 方言預研**：定義 Oracle 專屬關鍵字與常用函數白名單。

## 🎨 第三階段：類 Gemini-CLI 視覺體驗 (Modern TUI & UX)
- [x] 🔴 **[高] 交互式 REPL 外殼 (JLine 3)**：基礎框架與歷史紀錄。
- [x] 🔴 **[高] 資料表格渲染引擎 (guard-ui)**：ANSI 邊框與彩色表格展示。
- [ ] 🟡 **[中] 視覺化資訊卡片 (Standardized Layout)**：仿 `claude-code` 繪製「診斷卡片」與「建議區塊」，佈局一旦定型不輕易更換。
- [ ] 🟡 **[中] 來源中立型輸入處理**：優化多行貼上邏輯，適配 Teams Copilot 與 SpringBoot 日誌格式。
- [ ] 🟡 **[中] 自動補全組件 (Autocomplete)**：根據 Metadata 實作 Table/Column 的 Tab 補全。
- [ ] 🟡 **[中] 動態狀態指示器 (Spinners)**：在校驗與診斷時顯示 `⠋ Analyzing...`。

## ⚡ 第四階段：安全執行與 AI 輔助診斷 (Execution & AI Diagnosis)
- [x] 🔴 **[高] JDBC 安全執行器 (guard-executor)**：實作唯讀保護與結果集轉換。
- [ ] 🔴 **[高] guard-ai-client 實作**：串接本地 AI (192.168.137.1) 在 CLI 中解釋校驗失敗原因。
- [ ] 🔴 **[高] 診斷報告結構化 (Diagnostic JSON)**：實作機器可讀格式，支援後端服務 (SpringBoot) 進行自動化連動。
- [ ] 🟡 **[中] 模擬查詢模式 (Simulation)**：在無實體 DB 權限時模擬回傳結果結構。

## 🧪 第五階段：Spider 資料集與流程穩定化 (Benchmark & Sandbox)
- [ ] 🔴 **[高] Spider 資料集適配器**：將 Spider 的 `tables.json` 轉換為本專案標準 JSON 模型。
- [ ] 🔴 **[高] Metadata 轉 Prompt 工具**：驗證「提供 ERD 描述」能顯著提昇 AI SQL 正確性。
- [ ] 🟡 **[中] 批量校驗指令 (`test-batch`)**：自動跑完 Spider 案例並產出視覺化統計報告。
- [ ] 🟡 **[中] 流程定型 (Workflow Finalization)**：確保 `Check -> Diagnose -> Execute -> Render` 流程在不同資料庫下皆穩定。

## 🧩 第六階段：Agent SKILL 與指令擴充 (Plugin System)
- [ ] 🔴 **[高] guard-api 核心契約完善**：完成 Skill, CustomCommand 介面定義。
- [ ] 🔴 **[高] 插件加載器 (ExtensionLoader)**：支援從外部路徑動態載入技能與指令 JAR 檔。
- [ ] 🟡 **[中] 內建 Agent SKILL 開發**：如 PII 敏感數據檢測技能、查詢複雜度限制技能。
- [ ] 🟡 **[中] 客製化指令整合**：允許第三方指令註冊至 CLI 命令列。

## 🌐 第七階段：企業級整合與分發 (Enterprise & Distribution)
- [ ] 🔴 **[高] guard-service (REST API)**：實作診斷接口，讓 SpringBoot 後端服務能直接獲取修正建議。
- [ ] 🔴 **[高] Windows 封裝與交付**：裁剪微型 JRE 並產出免安裝 `guard.exe`，包含數位簽章。
- [ ] 🟡 **[中] Teams Copilot API 插件規範**：產出 OpenAPI 3.0 定義與自定義指令集。
- [ ] 🔵 **[低] 效能基準測試**：確保大規模 Metadata 下解析延遲 < 30ms。

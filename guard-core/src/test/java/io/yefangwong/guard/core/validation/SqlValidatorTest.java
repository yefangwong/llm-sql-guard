package io.yefangwong.guard.core.validation;

import io.yefangwong.guard.core.model.DatabaseMetadata;
import io.yefangwong.guard.core.model.MetadataLoader;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class SqlValidatorTest {
    private SqlValidator validator;

    @Before
    public void setUp() throws Exception {
        // 使用真實的 test-metadata.json 進行測試
        DatabaseMetadata metadata = MetadataLoader.load("test-metadata.json");
        validator = new SqlValidator(metadata);
    }

    @Test
    public void testValidSelect() {
        ValidationResult result = validator.validate("SELECT id, name FROM users");
        assertTrue("基本 SELECT 應通過: " + result.getErrors(), result.isSuccess());
    }

    @Test
    public void testAliasTracking() {
        ValidationResult result = validator.validate("SELECT u.name FROM users u WHERE u.id = 1");
        assertTrue("別名追蹤應通過: " + result.getErrors(), result.isSuccess());
    }

    @Test
    public void testTableNotFound() {
        ValidationResult result = validator.validate("SELECT * FROM employee");
        assertFalse("不存在的表應報錯", result.isSuccess());
        assertTrue(result.getErrors().get(0).contains("Table not found"));
        // 驗證 Fuzzy Suggestion (users 雖然不像 employee，但測試架構是否運作)
        System.out.println("❌ 預期錯誤: " + result.getErrors());
    }

    @Test
    public void testColumnNotFound() {
        ValidationResult result = validator.validate("SELECT salary FROM users");
        assertFalse("不存在的欄位應報錯", result.isSuccess());
        assertTrue(result.getErrors().get(0).contains("Column 'salary' not found"));
    }

    @Test
    public void testSecurityGuard() {
        ValidationResult result = validator.validate("DROP TABLE users");
        assertFalse("DDL 應被安全性阻斷", result.isSuccess());
        assertTrue(result.getErrors().get(0).contains("Security Violation"));
    }

    @Test
    public void testCaseInsensitive() {
        ValidationResult result = validator.validate("select ID, NAME from USERS");
        assertTrue("大小寫不敏感應通過", result.isSuccess());
    }
}

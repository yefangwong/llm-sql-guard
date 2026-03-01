package io.yefangwong.guard.core.model;

import org.junit.Test;
import java.io.IOException;
import static org.junit.Assert.*;

public class MetadataLoaderTest {
    @Test
    public void testLoadMetadata() throws IOException {
        DatabaseMetadata metadata = MetadataLoader.load("test-metadata.json");
        
        // 驗證 Database 名稱
        assertEquals("SALES_PROD", metadata.getName());
        
        // 驗證 Schema 與 Table 存在
        SchemaMetadata publicSchema = metadata.getSchema("PUBLIC");
        assertNotNull("PUBLIC schema should exist", publicSchema);
        
        TableMetadata usersTable = publicSchema.getTable("users");
        assertNotNull("users table should exist", usersTable);
        
        // 驗證 Column 欄位
        assertTrue("users table should contain 'id' column", 
                   usersTable.getColumns().containsKey("id"));
        
        // 驗證 Relation 關聯資訊
        TableMetadata ordersTable = publicSchema.getTable("orders");
        assertNotNull("orders table should exist", ordersTable);
        assertFalse("orders table should have relations", 
                    ordersTable.getRelations().isEmpty());
        
        RelationMetadata rel = ordersTable.getRelations().get(0);
        assertEquals("user_id", rel.getSourceColumn());
        assertEquals("users", rel.getTargetTable());
        
        System.out.println("✅ MetadataLoader 測試通過，成功載入 4-Tier Metadata!");
    }
}

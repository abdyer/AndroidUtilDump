package couk.doridori.android.lib.db.utils;

import junit.framework.TestCase;

/**
 * User: doriancussen
 * Date: 20/02/2013
 */
public class SqlCreateStmtBuilderTest extends TestCase
{
    public void testBuild_noCols_throws()
    {
        boolean exceptionThrown = false;

        try
        {
            SqlCreateStmtBuilder builder = new SqlCreateStmtBuilder("FakeTableName");
            builder.build();
        }
        catch (SqlCreateStmtBuilder.SqlCreateBuilderException e)
        {
            exceptionThrown = true;
        }

        assertTrue(exceptionThrown);
    }

    public void testBuild_oneColNoConstraint_correctOutput()
    {
        SqlCreateStmtBuilder builder = new SqlCreateStmtBuilder("FakeTable");
        builder.addCol("FakeCol", SQLiteDataTypes.TEXT);
        String sql = builder.build();
        assertEquals(sql, "CREATE TABLE FakeTable (FakeCol TEXT);");
    }

    public void testBuild_oneColNotNullConstraint_correctOutput()
    {
        SqlCreateStmtBuilder builder = new SqlCreateStmtBuilder("FakeTable");
        builder.addCol("FakeCol", SQLiteDataTypes.TEXT, new SqlCreateStmtBuilder.NotNull());
        String sql = builder.build();
        assertEquals(sql, "CREATE TABLE FakeTable (FakeCol TEXT NOT NULL);");
    }

    public void testBuild_twoColNoConstraint_correctOutput()
    {
        SqlCreateStmtBuilder builder = new SqlCreateStmtBuilder("FakeTable");
        builder.addCol("FakeCol", SQLiteDataTypes.TEXT);
        builder.addCol("FakeCol2", SQLiteDataTypes.INTEGER);
        String sql = builder.build();
        assertEquals(sql, "CREATE TABLE FakeTable (FakeCol TEXT, FakeCol2 INTEGER);");
    }

    public void testBuild_twoColNotNullConstraint_correctOutput()
    {
        SqlCreateStmtBuilder builder = new SqlCreateStmtBuilder("FakeTable");
        builder.addCol("FakeCol", SQLiteDataTypes.TEXT, new SqlCreateStmtBuilder.NotNull());
        builder.addCol("FakeCol2", SQLiteDataTypes.INTEGER, new SqlCreateStmtBuilder.NotNull());
        String sql = builder.build();
        assertEquals(sql, "CREATE TABLE FakeTable (FakeCol TEXT NOT NULL, FakeCol2 INTEGER NOT NULL);");
    }

    public void testBuild_oneColPrimaryKeyAutoInc_correctOutput()
    {
        SqlCreateStmtBuilder builder = new SqlCreateStmtBuilder("FakeTable");
        builder.addCol("FakeCol", SQLiteDataTypes.INTEGER, new SqlCreateStmtBuilder.PrimaryKey(true));
        String sql = builder.build();
        assertEquals(sql, "CREATE TABLE FakeTable (FakeCol INTEGER PRIMARY KEY AUTO INCREMENT);");
    }
}

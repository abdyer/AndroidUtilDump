package couk.doridori.android.lib.db.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Class to make it easier to construct CREATE statements. Has a fluid interface. See <a href="http://www.sqlite.org/lang_createtable.html">http://www.sqlite.org/lang_createtable.html</a>
 *
 * User: doriancussen
 */
public class SqlCreateStmtBuilder
{
    public static final String CREATE_TABLE = "CREATE TABLE ";

    private final String mTableName;
    private List<Col> mCols = new ArrayList<Col>();

    public SqlCreateStmtBuilder(String tableName)
    {
        if(null == tableName)
            throw new SqlCreateBuilderException("Table name must not be null");

        mTableName = tableName;
    }

    public SqlCreateStmtBuilder addCol(String name, SQLiteDataTypes type, CreateConstraint... createConstraint)
    {
        mCols.add(new Col(name, type, createConstraint));

        return this;
    }

    public String build()
    {
        if(mCols.size() == 0)
            throw new SqlCreateBuilderException("You need to supply some columns!");

        StringBuilder builder = new StringBuilder();
        builder.append(CREATE_TABLE);
        builder.append(mTableName);
        builder.append(" (");

        for(int i = 0; i < mCols.size(); i++)
        {
            if(i > 0)
                builder.append(", ");

            builder.append(mCols.get(i).toSQL());
        }

        builder.append(");");
        return builder.toString();
    }

    private class Col
    {
        final String name;
        final SQLiteDataTypes type;
        final CreateConstraint[] createConstraints;

        private Col(String name, SQLiteDataTypes type, CreateConstraint[] createConstraints)
        {
            this.name = name;
            this.type = type;
            this.createConstraints = createConstraints;
        }

        private String toSQL()
        {
            StringBuilder builder = new StringBuilder();
            builder.append(name);
            builder.append(" ");
            builder.append(type.toString());

            for(int i = 0; i < createConstraints.length; i++)
            {
                builder.append(" ");
                builder.append(createConstraints[i].toSql());
            }

            return builder.toString();
        }
    }

    /**
     * <a href="http://www.sqlite.org/syntaxdiagrams.html#column-constraint">http://www.sqlite.org/syntaxdiagrams.html#column-constraint</a>
     */
    public interface CreateConstraint
    {
        public String toSql();
    }

    /**
     * implemented as a seperate class as each constraint is build differently. <a href="http://www.sqlite.org/syntaxdiagrams.html#column-constraint">http://www.sqlite.org/syntaxdiagrams.html#column-constraint</a>
     */
    public static class NotNull implements CreateConstraint
    {
        @Override
        public String toSql()
        {
            return "NOT NULL";
        }
        //may want to add a conflict clause
    }

    public static class PrimaryKey implements CreateConstraint
    {
        private final boolean mAutoIncrement;

        public PrimaryKey(boolean autoIncrement)
        {
            mAutoIncrement = autoIncrement;
        }

        @Override
        public String toSql()
        {
            StringBuilder builder = new StringBuilder();
            builder.append("PRIMARY KEY");
            if(mAutoIncrement)
                builder.append(" AUTO INCREMENT");

            return builder.toString();
        }
    }

    public class SqlCreateBuilderException extends RuntimeException
    {
        public SqlCreateBuilderException(String detailMessage)
        {
            super(detailMessage);
        }
    };
}

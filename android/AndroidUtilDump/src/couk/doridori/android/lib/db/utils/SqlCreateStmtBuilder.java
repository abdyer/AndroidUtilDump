package couk.doridori.android.lib.db.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Class to make it easier to construct CREATE statements. See corresponding test class to see usages. Has a fluid interface. See <a href="http://www.sqlite.org/lang_createtable.html">http://www.sqlite.org/lang_createtable.html</a>
 *
 * User: doriancussen
 */
public class SqlCreateStmtBuilder
{
    public static enum ConflictClause
    {
        ROLLBACK, ABORT, FAIL, IGNORE, REPLACE;

        @Override
        public String toString()
        {
            return " ON CONFLICT "+this.name();
        }
    }

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

        //add unique contraints at the end
        Unique.appendUniqueConstraints(builder, mCols);

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
                if(createConstraints[i] instanceof Unique)
                {
                    //do nothing, will be added at end
                }
                else
                {
                    builder.append(" ");
                    builder.append(createConstraints[i].toSql());
                }
            }

            return builder.toString();
        }

        /**
         * @return the contraint if it exists, else null
         */
        private Unique hasUniqueConstraint()
        {
            for(CreateConstraint constraint : createConstraints)
            {
                if(constraint instanceof Unique)
                    return (Unique)constraint;
            }

            return null;
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
                builder.append(" AUTOINCREMENT");

            return builder.toString();
        }
    }

    public static class Unique implements CreateConstraint
    {
        private final ConflictClause mClause;

        /**
         * @param clause can be null
         */
        public Unique(ConflictClause clause)
        {
            mClause = clause;
        }

        @Override
        public String toSql()
        {
            return ""; //return nothing for the individual column def - this is just a flag and we will add the unique constraint at the end of the create statement else sqlite will get very unhappy
        }

        /**
         * We have to do this at the end like CREATE TABLE a (i INT, j INT, UNIQUE(i, j) ON CONFLICT REPLACE); instead of
         * doing the way specified in the SQLite docs else SQLite will complain
         */
        public static void appendUniqueConstraints(StringBuilder builder, List<Col> cols)
        {
            int count = 0;
            ConflictClause conflictClause = null;

            for(Col col : cols)
            {
                Unique unique = col.hasUniqueConstraint();

                if(null != unique)
                {
                    if(0 == count)
                    {
                        builder.append(", UNIQUE(");
                        builder.append(col.name);
                        conflictClause = unique.mClause;
                    }
                    else
                    {
                        if(conflictClause != unique.mClause)
                            throw new RuntimeException("only support multiple conflict clauses for unique if they are of the same type - need to impl");

                        builder.append(", ");
                        builder.append(col.name);
                    }

                    count = count + 1;
                }
            }

            if(count > 0)
            {
                builder.append(")"+conflictClause.toString());
            }
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


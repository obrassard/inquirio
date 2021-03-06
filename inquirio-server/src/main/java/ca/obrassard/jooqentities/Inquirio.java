/*
 * This file is generated by jOOQ.
 */
package ca.obrassard.jooqentities;


import ca.obrassard.jooqentities.tables.Lostitems;
import ca.obrassard.jooqentities.tables.Notification;
import ca.obrassard.jooqentities.tables.Tokens;
import ca.obrassard.jooqentities.tables.Users;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Generated;

import org.jooq.Catalog;
import org.jooq.Table;
import org.jooq.impl.SchemaImpl;


/**
 * This class is generated by jOOQ.
 */
@Generated(
    value = {
        "http://www.jooq.org",
        "jOOQ version:3.11.4"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Inquirio extends SchemaImpl {

    private static final long serialVersionUID = -1535462513;

    /**
     * The reference instance of <code>inquirio</code>
     */
    public static final Inquirio INQUIRIO = new Inquirio();

    /**
     * The table <code>inquirio.LostItems</code>.
     */
    public final Lostitems LOSTITEMS = ca.obrassard.jooqentities.tables.Lostitems.LOSTITEMS;

    /**
     * The table <code>inquirio.Notification</code>.
     */
    public final Notification NOTIFICATION = ca.obrassard.jooqentities.tables.Notification.NOTIFICATION;

    /**
     * The table <code>inquirio.Tokens</code>.
     */
    public final Tokens TOKENS = ca.obrassard.jooqentities.tables.Tokens.TOKENS;

    /**
     * The table <code>inquirio.Users</code>.
     */
    public final Users USERS = ca.obrassard.jooqentities.tables.Users.USERS;

    /**
     * No further instances allowed
     */
    private Inquirio() {
        super("inquirio", null);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public Catalog getCatalog() {
        return DefaultCatalog.DEFAULT_CATALOG;
    }

    @Override
    public final List<Table<?>> getTables() {
        List result = new ArrayList();
        result.addAll(getTables0());
        return result;
    }

    private final List<Table<?>> getTables0() {
        return Arrays.<Table<?>>asList(
            Lostitems.LOSTITEMS,
            Notification.NOTIFICATION,
            Tokens.TOKENS,
            Users.USERS);
    }
}

/*
 * This file is generated by jOOQ.
 */
package ca.obrassard.jooqentities.tables;


import ca.obrassard.jooqentities.Indexes;
import ca.obrassard.jooqentities.Inquirio;
import ca.obrassard.jooqentities.Keys;
import ca.obrassard.jooqentities.tables.records.NotificationRecord;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Generated;

import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Identity;
import org.jooq.Index;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.Schema;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.UniqueKey;
import org.jooq.impl.DSL;
import org.jooq.impl.TableImpl;


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
public class Notification extends TableImpl<NotificationRecord> {

    private static final long serialVersionUID = 498990574;

    /**
     * The reference instance of <code>inquirio.notification</code>
     */
    public static final Notification NOTIFICATION = new Notification();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<NotificationRecord> getRecordType() {
        return NotificationRecord.class;
    }

    /**
     * The column <code>inquirio.notification.Id</code>.
     */
    public final TableField<NotificationRecord, Integer> ID = createField("Id", org.jooq.impl.SQLDataType.INTEGER.nullable(false).identity(true), this, "");

    /**
     * The column <code>inquirio.notification.SenderId</code>.
     */
    public final TableField<NotificationRecord, Integer> SENDERID = createField("SenderId", org.jooq.impl.SQLDataType.INTEGER.nullable(false), this, "");

    /**
     * The column <code>inquirio.notification.ItemId</code>.
     */
    public final TableField<NotificationRecord, Integer> ITEMID = createField("ItemId", org.jooq.impl.SQLDataType.INTEGER.nullable(false), this, "");

    /**
     * The column <code>inquirio.notification.Photo</code>.
     */
    public final TableField<NotificationRecord, byte[]> PHOTO = createField("Photo", org.jooq.impl.SQLDataType.BLOB, this, "");

    /**
     * The column <code>inquirio.notification.Message</code>.
     */
    public final TableField<NotificationRecord, String> MESSAGE = createField("Message", org.jooq.impl.SQLDataType.VARCHAR(200).nullable(false), this, "");

    /**
     * The column <code>inquirio.notification.Date</code>.
     */
    public final TableField<NotificationRecord, Timestamp> DATE = createField("Date", org.jooq.impl.SQLDataType.TIMESTAMP.defaultValue(org.jooq.impl.DSL.field("CURRENT_TIMESTAMP", org.jooq.impl.SQLDataType.TIMESTAMP)), this, "");

    /**
     * The column <code>inquirio.notification.Visible</code>.
     */
    public final TableField<NotificationRecord, Byte> VISIBLE = createField("Visible", org.jooq.impl.SQLDataType.TINYINT.nullable(false).defaultValue(org.jooq.impl.DSL.inline("1", org.jooq.impl.SQLDataType.TINYINT)), this, "");

    /**
     * Create a <code>inquirio.notification</code> table reference
     */
    public Notification() {
        this(DSL.name("notification"), null);
    }

    /**
     * Create an aliased <code>inquirio.notification</code> table reference
     */
    public Notification(String alias) {
        this(DSL.name(alias), NOTIFICATION);
    }

    /**
     * Create an aliased <code>inquirio.notification</code> table reference
     */
    public Notification(Name alias) {
        this(alias, NOTIFICATION);
    }

    private Notification(Name alias, Table<NotificationRecord> aliased) {
        this(alias, aliased, null);
    }

    private Notification(Name alias, Table<NotificationRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""));
    }

    public <O extends Record> Notification(Table<O> child, ForeignKey<O, NotificationRecord> key) {
        super(child, key, NOTIFICATION);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Schema getSchema() {
        return Inquirio.INQUIRIO;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Index> getIndexes() {
        return Arrays.<Index>asList(Indexes.NOTIFICATION_FK_LOSTITEM_ITEMID, Indexes.NOTIFICATION_FK_USERS_SENDERID, Indexes.NOTIFICATION_PRIMARY);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Identity<NotificationRecord, Integer> getIdentity() {
        return Keys.IDENTITY_NOTIFICATION;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UniqueKey<NotificationRecord> getPrimaryKey() {
        return Keys.KEY_NOTIFICATION_PRIMARY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<UniqueKey<NotificationRecord>> getKeys() {
        return Arrays.<UniqueKey<NotificationRecord>>asList(Keys.KEY_NOTIFICATION_PRIMARY);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ForeignKey<NotificationRecord, ?>> getReferences() {
        return Arrays.<ForeignKey<NotificationRecord, ?>>asList(Keys.FK_USERS_SENDERID, Keys.FK_LOSTITEM_ITEMID);
    }

    public Users users() {
        return new Users(this, Keys.FK_USERS_SENDERID);
    }

    public Lostitems lostitems() {
        return new Lostitems(this, Keys.FK_LOSTITEM_ITEMID);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Notification as(String alias) {
        return new Notification(DSL.name(alias), this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Notification as(Name alias) {
        return new Notification(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public Notification rename(String name) {
        return new Notification(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public Notification rename(Name name) {
        return new Notification(name, null);
    }
}

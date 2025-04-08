package com.phantoms.phantomsbackend.common.utils.PIS;

import com.google.common.base.Strings;
import com.google.common.collect.HashBasedTable;
import net.sf.jsqlparser.schema.Table;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DataPremissionHelper {

    private static final ThreadLocal<HashBasedTable> DATA_PERMISSION_PARAM = new ThreadLocal<>();

    private static final ThreadLocal<Thread> DATA_PERMISSION_REENTRY = new ThreadLocal<>();


    public static void setParams(HashBasedTable table) {
        DATA_PERMISSION_PARAM.set(table);
    }


    public static HashBasedTable getParams() {
        return DATA_PERMISSION_PARAM.get() == null ? HashBasedTable.create() : DATA_PERMISSION_PARAM.get();
    }


    public static void setPremissionReentry() {
        DATA_PERMISSION_REENTRY.set(Thread.currentThread());
    }

    public static boolean getPremissionReentry() {
        return DATA_PERMISSION_REENTRY.get() == null || !Thread.currentThread().equals(DATA_PERMISSION_REENTRY.get());
    }

    public static void clearPremissionReentry() {
        DATA_PERMISSION_REENTRY.remove();
    }


    public static void clearDataPremissionParam() {
        DATA_PERMISSION_PARAM.remove();
    }


    public static HashMap<String, String> aliasMap() {
        HashMap<String, String> hashMap = (HashMap) DataPremissionHelper.getParams().get(Thread.currentThread(), Premissions.TABLE_ALIAS);
        return hashMap;
    }

    public static String currTableAlias() {
        HashMap<String, String> hashMap = DataPremissionHelper.aliasMap();
        Table table = (Table) DataPremissionHelper.getParams().get(Thread.currentThread(), Premissions.CURR_TABLE);
        String tableNameAlias = hashMap.get(table.getName().toLowerCase());
        return tableNameAlias;
    }

    public static void tableScopeSet() {
        Table table = (Table) DataPremissionHelper.getParams().get(Thread.currentThread(), Premissions.CURR_TABLE);
        HashMap<String, Boolean> hashMap = (HashMap) DataPremissionHelper.getParams().get(Thread.currentThread(), Premissions.IS_SET);
        hashMap.put(table.getName().toLowerCase(), Boolean.TRUE);
    }

    public static ArrayList<String> getAllTables() {
        ArrayList<String> allTables = (ArrayList<String>) DataPremissionHelper.getParams().get(Thread.currentThread(), Premissions.TABLES);
        return allTables;
    }


    public static boolean isTableScopeSet() {
        Table table = (Table) DataPremissionHelper.getParams().get(Thread.currentThread(), Premissions.CURR_TABLE);
        HashMap<String, Boolean> hashMap = (HashMap) DataPremissionHelper.getParams().get(Thread.currentThread(), Premissions.IS_SET);
        if (table == null) {
            return false;
        }
        return hashMap.get(table.getName().toLowerCase());
    }

    public static void setDataPremissionType(Integer code) {
        HashBasedTable<Object, Object, Object> table = HashBasedTable.create();
        table.put(Thread.currentThread(), Premissions.TYPE, code);
    }

    public static void extractAlias(Table table) {
        String tableName = table.getName().toLowerCase();
        List<String> tables = (ArrayList) DataPremissionHelper.getParams().get(Thread.currentThread(), Premissions.TABLES);
        HashMap<String, String> hashMap = DataPremissionHelper.aliasMap();
        if (tables.contains(tableName)) {
            String tableAlias = (table.getAlias() != null && !Strings.isNullOrEmpty(table.getAlias().getName()))
                    ? table.getAlias().getName()
                    : "";
            hashMap.put(tableName, tableAlias);
        }
    }
    public class Premissions {

        public static final String TYPE = "type";
        public static final String CLAZZ = "clazz";
        public static final String IS_SET = "is_set";
        public static final String TABLES = "tables";
        public static final String TABLE_ALIAS = "table-alias";
        public static final String PREMISSIONABLE = "premissionable";
        public static final String CURR_TABLE = "curr_table";
    }
}


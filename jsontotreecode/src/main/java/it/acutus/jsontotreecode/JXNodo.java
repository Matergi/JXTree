package it.acutus.jsontotreecode;

import java.io.Serializable;
import java.util.ArrayList;

class JXNodo implements Serializable
{
    private int id = 0;
    private String key;
    private String value;
    private boolean isCreateForXml = false;
    private int level = -1;
    private String group = "mGroup";
    private boolean valueNecessary = false;
    private boolean isArray = false;

    private ArrayList<JXNodo> puntatore = new ArrayList<>();

    JXNodo(int id, String key, String value, int level, String group)
    {
        this.id = id;
        this.key = key;
        this.value = value;
        this.level = level;
        this.group = group;
    }

    JXNodo(int id, int level, String group)
    {
        this.id = id;
        this.level = level;
        this.group = group;
    }

    String getKey() {
        return key;
    }

    void setKey(String key) {
        this.key = key;
    }

    String getValue() {
        return value;
    }

    void setValue(String value) {
        this.value = value;
    }

    ArrayList<JXNodo> getPuntatore() {
        return puntatore;
    }

    int getLevel() {
        return level;
    }

    void setLevel(int level) {
        this.level = level;
    }

    boolean isCreateForXml()
    {
        return isCreateForXml;
    }

    void setCreateForXml(boolean createForXml)
    {
        isCreateForXml = createForXml;
    }

    String getGroup()
    {
        return group;
    }

    void setGroup(String group)
    {
        this.group = group;
    }

    int getId()
    {
        return id;
    }

    void setId(int id)
    {
        this.id = id;
    }

    boolean isValueNecessary()
    {
        return valueNecessary;
    }

    void setValueNecessary(boolean valueNecessary)
    {
        this.valueNecessary = valueNecessary;
    }

    boolean isArray()
    {
        return isArray;
    }

    void setIsArray(boolean array)
    {
        isArray = array;
    }
}

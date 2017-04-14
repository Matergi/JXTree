package it.acutus.jsontotreecode;

import java.io.Serializable;
import java.util.ArrayList;

public class Nodo implements Serializable
{
    private String key;
    private String value;
    private String tag;
    private int level = -100;

    private ArrayList<Nodo> puntatore = new ArrayList<>();

    public Nodo(String key, String value, int level)
    {
        this.key = key;
        this.value = value;
        this.level = level;
    }

    public Nodo(String key, String value)
    {
        this.key = key;
        this.value = value;
    }
    public Nodo()
    {

    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public ArrayList<Nodo> getPuntatore() {
        return puntatore;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }
}

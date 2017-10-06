package it.acutus.jsontotreecode;

import it.acutus.jsontotreecode.parserXml.parser.XmlToJson;

public class JXLeaf
{
    private JXNodo JXNodo;

    JXLeaf(JXNodo JXNodo)
    {
        this.JXNodo = JXNodo;
    }

    JXLeaf(JXTree JXTree)
    {
        this.JXNodo = JXTree.getJXNodo();
    }

    public int size()
    {
        return JXNodo.getPuntatore().size();
    }

    public String getValue()
    {
        if (JXNodo.getValue() != null)
        {
            String value = JXNodo.getValue();
            value = value.replace(XmlToJson.preTag + "element_", XmlToJson.array);
            value = value.replace(XmlToJson.afterTag, XmlToJson.array);
            return value;
        }
        else if (size() > 0)
        {
            String value = buildJsonFromPuntatori();
            value = value.replace(XmlToJson.preTag + "element_", XmlToJson.array);
            value = value.replace(XmlToJson.afterTag, XmlToJson.array);
            return value;
        }
        else
        {
            return null;
        }
    }

    private String buildJsonFromPuntatori()
    {
        String json = "{";

        for (int i = 0 ; i < size() ; i++)
        {
            if (i > 0)
            {
                json += ",\"" + get(i).getKey() + "\" : ";
                if (get(i).getValue().startsWith("{"))
                {
                    json += get(i).getValue();
                }
                else
                {
                    json += "\"" + get(i).getValue() + "\"";
                }
            }
            else
            {
                json += "\"" + get(i).getKey() + "\" : ";
                if (get(i).getValue().startsWith("{") || get(i).getValue().startsWith("["))
                {
                    json += get(i).getValue();
                }
                else
                {
                    json += "\"" + get(i).getValue() + "\"";
                }
            }
        }

        json += "}";
        return json;
    }

    public String getKey()
    {
        return JXNodo.getKey();
    }

    public JXLeaf get(int i)
    {
        return new JXLeaf(JXNodo.getPuntatore().get(i));
    }

    public JXLeaf search(String key)
    {
        JXTree JXTree = new JXTree();
        return JXTree.searchKeySameLevel(key, JXNodo);
    }

    public boolean contain(String key, String value)
    {
        boolean found = false;
        for (int i = 0 ; i < size() ; i++)
        {
            if (getKey().toLowerCase().equals(key.toLowerCase()) && getValue().toLowerCase().equals(value.toLowerCase()))
            {
                found = true;
            }
        }
        return found;
    }

    public boolean isArray()
    {
        return JXNodo.isArray();
    }
}

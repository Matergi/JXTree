package it.acutus.jsontotreecode;

public class JsonLeaf
{
    private JsonNodo jsonNodo;

    JsonLeaf(JsonNodo jsonNodo)
    {
        this.jsonNodo = jsonNodo;
    }

    public JsonLeaf(JsonTree jsonTree)
    {
        this.jsonNodo = jsonTree.getJsonNodo();
    }

    public int size()
    {
        return jsonNodo.getPuntatore().size();
    }

    public String getValue()
    {
        if (jsonNodo.getValue() != null)
        {
            return jsonNodo.getValue();
        }
        else if (size() > 0)
        {
            return buildJsonFromPuntatori();
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
                if (get(i).getValue().startsWith("{"))
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
        return jsonNodo.getKey();
    }

    public JsonLeaf get(int i)
    {
        return new JsonLeaf(jsonNodo.getPuntatore().get(i));
    }

    public JsonLeaf search(String key)
    {
        JsonTree jsonTree = new JsonTree();
        return jsonTree.searchKeySameLevel(key, jsonNodo);
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
}

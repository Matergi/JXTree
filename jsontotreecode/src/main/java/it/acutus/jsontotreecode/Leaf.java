package it.acutus.jsontotreecode;

public class Leaf
{
    private Nodo nodo;

    public Leaf(Nodo nodo)
    {
        this.nodo = nodo;
    }

    public Leaf(Tree tree)
    {
        this.nodo = tree.getNodo();
    }

    public int size()
    {
        return nodo.getPuntatore().size();
    }

    public String getValue()
    {
        if (nodo.getValue() != null)
        {
            return nodo.getValue();
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
        return nodo.getKey();
    }

    public Leaf get(int i)
    {
        return new Leaf(nodo.getPuntatore().get(i));
    }

    public Leaf search(String key)
    {
        Tree tree = new Tree();
        return tree.advanceSearchKey(key, nodo);
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

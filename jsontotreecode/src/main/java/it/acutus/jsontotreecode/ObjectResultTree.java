package it.acutus.jsontotreecode;

import org.apache.commons.lang3.StringUtils;
import java.util.ArrayList;

public class ObjectResultTree
{
    ArrayList<String> result;

    public ObjectResultTree()
    {
        result = new ArrayList<>();
    }

    public ObjectResultTree(ArrayList<String> result) {
        this.result = result;
    }

    public void addResult(String value)
    {
        this.result.add(value);
    }

    public ArrayList<String> getResult() {
        return result;
    }

    public Integer getAttributeInt(String nameAttribute)
    {
        Integer mResult = null;
        for (int i = 0 ; i < result.size() ; i++)
        {
            if (result.get(i).split(Tree.kSeparatore)[0].equals(nameAttribute))
            {
                if (StringUtils.isNumeric(result.get(i).split(Tree.kSeparatore)[1]))
                {
                    mResult = Integer.parseInt(result.get(i).split(Tree.kSeparatore)[1]);
                }
            }
        }
        return mResult;
    }

    public String getAttributeString(String nameAttribute)
    {
        String mResult = null;
        for (int i = 0 ; i < result.size() ; i++)
        {
            if (result.get(i).split(Tree.kSeparatore)[0].equals(nameAttribute))
            {
                mResult = result.get(i).split(Tree.kSeparatore)[1];
            }
        }
        return mResult;
    }
}

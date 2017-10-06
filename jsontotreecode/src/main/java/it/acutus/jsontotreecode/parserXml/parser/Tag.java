package it.acutus.jsontotreecode.parserXml.parser;

/*
    Author Giacomo Materozzi
*/

import java.util.ArrayList;

class Tag
{
    private String mPath;
    private String mName;
    private ArrayList<Tag> mChildren = new ArrayList<>();
    private String mContent;

    Tag(String path, String name)
    {
        mPath = path;
        mName = name;
    }

    void addChild(Tag tag)
    {
        mChildren.add(tag);
    }

    void setContent(String content)
    {
        // checks that there is a relevant content (not only spaces or \n)
        boolean hasContent = false;
        if (content != null) {
            for (int i = 0; i < content.length(); ++i) {
                char c = content.charAt(i);
                if ((c != ' ') && (c != '\n')) {
                    hasContent = true;
                    break;
                }
            }
        }
        if (hasContent) {
            mContent = content;
        }
    }

    String getName()
    {
        return mName;
    }

    String getContent()
    {
        return mContent;
    }

    ArrayList<Tag> getChildren()
    {
        return mChildren;
    }

    boolean hasChildren()
    {
        return (mChildren.size() > 0);
    }

    int getChildrenCount()
    {
        return mChildren.size();
    }

    Tag getChild(int index)
    {
        if ((index >= 0) && (index < mChildren.size())) {
            return mChildren.get(index);
        }
        return null;
    }

    boolean isList()
    {
        if (mChildren.size() > 1) {
            String tagName = getChild(0).mName; // All Tags must have the same name
            for (int i = 1; i < mChildren.size(); ++i) {
                if (!tagName.equals(mChildren.get(i).mName)) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    String getPath()
    {
        return mPath;
    }

    @Override
    public String toString()
    {
        return "Tag: " + mName + ", " + mChildren.size() + " children, Content: " + mContent;
    }
}

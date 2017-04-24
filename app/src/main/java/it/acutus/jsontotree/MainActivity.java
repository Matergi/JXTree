package it.acutus.jsontotree;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import it.acutus.jsontotreecode.Leaf;
import it.acutus.jsontotreecode.Tree;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String json = "{\n" +
                "    \"result\" : [\n" +
                "        {\n" +
                "            \"a\" : \"a1\",\n" +
                "            \"b\" : \"b1\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"a\" : \"a1\",\n" +
                "            \"b\" : \"b1\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"a\" : \"a1\",\n" +
                "            \"b\" : \"b1\"\n" +
                "        }\n" +
                "    ]\n" +
                "}";

        Tree tree = new Tree();
        tree.buildTree(json);

        Leaf foglia = tree.searchKey("result");

        Log.d("a", foglia.getValue());
    }
}

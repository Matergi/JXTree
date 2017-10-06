package it.acutus.jsontotree;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import it.acutus.jsontotreecode.JXLeaf;
import it.acutus.jsontotreecode.JXTree;

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
                "            \"c\" : \"c1\",\n" +
                "            \"d\" : \"d1\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"e\" : \"e1\",\n" +
                "            \"f\" : \"f1\"\n" +
                "        }\n" +
                "    ]\n" +
                "}";

        JXTree jsonTree = new JXTree();
        jsonTree.buildTreeFromJson(json); //costruisce l'albero in base al Json passato

        JXLeaf fogliaJson = jsonTree.searchKey("result"); //cerca la key 'result' nell'albero e restituisce la foglia

// se fogliaJson nel 'value' contiene un Array si puo sfruttare la funzione .size() per sapere la lunghezza dell'array di oggetti che contiene ovvero:
        Log.d("example", "size: " + fogliaJson.size());

        Log.d("example", "all: " + fogliaJson.getValue());
        Log.d("example", "key: " + fogliaJson.get(1).search("c").getValue()); // tramite la funzione get() si accede ad un elemento nell'array, mentre tramite la funzione search si ha la possibilità di cercare un'altra key all'interno della foglia

        String xml = "<catalog>\n" +
                "       <book id=\"1\">\n" +
                "           <title>titolo 1</title>\n" +
                "           <author>autore 1</author>\n" +
                "       </book>\n" +
                "       <book id=\"2\">\n" +
                "           <title>titolo 2</title>\n" +
                "           <author>autore 2</author>\n" +
                "       </book>\n" +
                "       <book id=\"3\">\n" +
                "           <title>titolo 3</title>\n" +
                "           <author>autore 3</author>\n" +
                "       </book>\n" +
                "</catalog>";

        JXTree xmlTree = new JXTree();
        xmlTree.buildTreeFromXml(xml); //costruisce l'albero in base al Xml passato

        JXLeaf fogliaXml = xmlTree.searchKey("book"); //cerca la key 'book' nell'albero e restituisce la foglia

        Log.d("example", "key: " + fogliaXml.get(1).search("title").getValue());
    }
}

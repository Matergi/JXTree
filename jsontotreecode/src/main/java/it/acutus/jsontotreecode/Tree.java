package it.acutus.jsontotreecode;

import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Tree implements Serializable
{
    private static final String TAG = Tree.class.getName();

    public static final String kSeparatore = "%&%&";

    private Nodo nodo = new Nodo();
    private Nodo nodoSearch = new Nodo();
    private ArrayList<ObjectResultTree> arrayObjectResultTree = new ArrayList<>();
    private ObjectResultTree objectResultTree;

    public Tree()
    {
    }

    public Tree buildTree(String json)
    {
        try
        {
            JSONObject jsonObj = new JSONObject(json);
            return jsonToTree(this, jsonObj);
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * classica ricerca di un nodo
     * @param nodo nodo su cui andare a cercarci dentro
     * @param key la chiave da cercare
     * @param downKey la sottochiave da cercare se esiste, altrimenti null
     */
    private void searchNodo(Nodo nodo, String key, String downKey)
    {
        for (int i = 0 ; i < nodo.getPuntatore().size() ; i++)
        {
            if (downKey == null)
            {
                if (nodo.getPuntatore().get(i).getKey().equals(key))
                {
                    nodoSearch = nodo.getPuntatore().get(i);
                }
            }
            else
            {
                if (nodo.getPuntatore().get(i).getKey().equals(key))
                {
                    for (int a = 0 ; a < nodo.getPuntatore().get(i).getPuntatore().size() ; a ++)
                    {
                        if (nodo.getPuntatore().get(i).getPuntatore().get(a).getKey().equals(downKey))
                        {
                            nodoSearch = nodo.getPuntatore().get(i).getPuntatore().get(a);
                        }
                    }
                }
            }

            searchNodo(nodo.getPuntatore().get(i), key, downKey);
        }
    }

    /**
     * ricerca particolare, perchè cerca la key che contiene un certo valore dentro... ma lo trova anche se nel json la key e il valore sono invertiti
     * @param nodo nodo su cui cercare
     * @param key la key che ti serve cercare
     * @param downKey la sottochiave che ti serve cercare
     * @param contain la stringa che contine il value della key
     */
    private void searchAlternativeNodoWithDownKeyAndContain(Nodo nodo, String key, String downKey, String contain)
    {
        for (int i = 0 ; i < nodo.getPuntatore().size() ; i++)
        {
            if (nodo.getPuntatore().get(i).getKey().equals(key) || nodo.getPuntatore().get(i).getValue().equals(key))
            {
                boolean flagFindDownKey = false;
                boolean flagFindContain = false;
                int positionKey = -100;
                int positionContent = -100;

                for (int a = 0 ; a < nodo.getPuntatore().get(i).getPuntatore().size() ; a ++)
                {
                    if (nodo.getPuntatore().get(i).getPuntatore().get(a).getValue().equals(downKey) || nodo.getPuntatore().get(i).getPuntatore().get(a).getKey().equals(downKey))
                    {
                        flagFindDownKey = true;
                        if (positionKey == -100)
                            positionContent = a;
                    }
                    else if (nodo.getPuntatore().get(i).getPuntatore().get(a).getValue().equals(contain) || nodo.getPuntatore().get(i).getPuntatore().get(a).getKey().equals(contain))
                    {
                        flagFindContain = true;
                        positionKey = a;
                    }
                }
                if (flagFindContain && flagFindDownKey && positionKey != -100 && positionContent != -100)
                {
                    nodoSearch = new Nodo(nodo.getPuntatore().get(i).getPuntatore().get(positionKey).getValue().toString(), nodo.getPuntatore().get(i).getPuntatore().get(positionContent).getValue().toString());
                }
            }

            searchAlternativeNodoWithDownKeyAndContain(nodo.getPuntatore().get(i), key, downKey, contain);
        }
    }

    /**
     * ricerca particolare, perchè cerca una key che continete una downkey con il valore
     * @param nodo nodo su cui cercare
     * @param key la key che ti serve cercare
     * @param downKey la sottochiave che ti serve cercare
     * @param value la stringa che contine il value da cercre nel value
     */
    private void searchAlternativeNodoWithDownKeyAndValue(Nodo nodo, String key, String downKey, String value)
    {
        for (int i = 0 ; i < nodo.getPuntatore().size() ; i++)
        {
            if (nodo.getPuntatore().get(i).getKey().equals(key) || nodo.getPuntatore().get(i).getValue().equals(key))
            {
                for (int a = 0 ; a < nodo.getPuntatore().get(i).getPuntatore().size() ; a ++)
                {
                    if (nodo.getPuntatore().get(i).getPuntatore().get(a).getValue().equals(value) && nodo.getPuntatore().get(i).getPuntatore().get(a).getKey().equals(downKey))
                    {
                        nodoSearch = new Nodo(nodo.getPuntatore().get(i).getPuntatore().get(a).getValue(), nodo.getPuntatore().get(i).getPuntatore().get(a).getValue());
                    }
                }
            }

            searchAlternativeNodoWithDownKeyAndValue(nodo.getPuntatore().get(i), key, downKey, value);
        }
    }

    /**
     * questa è una ricerca un po particolare perchè cerca una key che puo stare sia nella "key" che in "value"
     * @param nodo nodo su cui cercare
     * @param key la key da cercare
     * @param downKey la sotto key da cercare se non la vuoi metti null
     */
    private void searchAlternativeNodoWithDownKey(Nodo nodo, String key, String downKey)
    {
        for (int i = 0 ; i < nodo.getPuntatore().size() ; i++)
        {
            if (nodo.getPuntatore().get(i).getKey().equals(key) || nodo.getPuntatore().get(i).getValue().equals(key))
            {
                for (int a = 0 ; a < nodo.getPuntatore().get(i).getPuntatore().size() ; a ++)
                {
                    if (nodo.getPuntatore().get(i).getPuntatore().get(a).getValue().equals(downKey) || nodo.getPuntatore().get(i).getPuntatore().get(a).getKey().equals(downKey))
                    {
                        nodoSearch = nodo.getPuntatore().get(i).getPuntatore().get(a);
                    }
                }
            }

            searchAlternativeNodoWithDownKey(nodo.getPuntatore().get(i), key, downKey);
        }
    }

    /**
     * serve per costruire un array di chiavi
     * @param nodo Nodo di riferimento
     * @param lengthParam lunghezza dei parametri
     * @param key ArrayList<String> da cercare
     */
    private void searchArrayOfNodoWithKeys(Nodo nodo, int lengthParam, ArrayList<String> key)
    {
        for (int i = 0 ; i < nodo.getPuntatore().size() ; i++)
        {
            if (key.contains(nodo.getPuntatore().get(i).getKey()))
            {
                objectResultTree.addResult(nodo.getPuntatore().get(i).getKey() + kSeparatore + nodo.getPuntatore().get(i).getValue());
            }

            if (objectResultTree.getResult().size() % lengthParam == 0 && objectResultTree.getResult().size() != 0)
            {
                arrayObjectResultTree.add(objectResultTree);
                objectResultTree = new ObjectResultTree();
            }

            searchArrayOfNodoWithKeys(nodo.getPuntatore().get(i), lengthParam, key);
        }
    }

    /**
     * cerca una chiave sia nel campo "key", che nel campo "value"
     * @param nodo nodo su cui cercare
     * @param key
     */
    private void searchAlternativeNodo(Nodo nodo, String key)
    {
        for (int i = 0 ; i < nodo.getPuntatore().size() ; i++)
        {
            if (nodo.getPuntatore().get(i).getValue().equals(key) || nodo.getPuntatore().get(i).getKey().equals(key))
            {
                nodoSearch = nodo.getPuntatore().get(i);
            }

            searchAlternativeNodo(nodo.getPuntatore().get(i), key);
        }
    }

    /**
     * cerca luna chiave specifica nell'albero
     * @param key la chiave da cercare
     * @param forceSearch true - false che nella ricerca prova a vedere se la key è nel value, tipo negli array che sono formati da :
     *
     *                    attribute : [
     *                    {
     *                         key : "aaa",
     *                         content : "bbb"
     *                    },
     *                    {
     *                         key : "ccc",
     *                         content : "ddd"
     *                    },
     *                    {
     *                         key : "eee",
     *                         content : "fff"
     *                    }]
     *
     *                    e la chiave che vuoi cercare è nel content
     *
     * @return object trovato
     */
    public Object searchKey(String key, Boolean forceSearch)
    {
        nodoSearch = null;
        if (nodo != null)
        {
            if (forceSearch)
            {
                searchAlternativeNodo(nodo, key);
            }
            else
            {
                searchNodo(nodo, key, null);
            }

            if (nodoSearch != null)
            {
                return nodoSearch.getValue();
            }
            else
            {
                Log.e(TAG, "NON E' STATO TROVATO NESSUNA KEY CHE CORRISPONDA A KEY : '" + key + "'");
            }
        }
        return null;
    }

    /**
     * cerca una sottochiave che il cui padre è la key passata nell'albero
     * @param key key da cercare
     * @param downKey sotto key da cercare
     * @param forceSearch true - false che nella ricerca prova a vedere se la key è nel value, tipo negli array che sono formati da :
     *
     *                    attribute : [
     *                    {
     *                         key : "aaa",
     *                         content : "bbb"
     *                    },
     *                    {
     *                         key : "ccc",
     *                         content : "ddd"
     *                    },
     *                    {
     *                         key : "eee",
     *                         content : "fff"
     *                    }]
     *
     *                    e la chiave che vuoi cercare è nel content
     * @return object trovato
     */
    public Object searchKeyWithDownKey(String key, String downKey, Boolean forceSearch)
    {
        nodoSearch = null;
        if (nodo != null)
        {
            if (forceSearch)
            {
                searchAlternativeNodoWithDownKey(nodo, key, downKey);
            }
            else
            {
                searchNodo(nodo, key, downKey);
            }

            if (nodoSearch != null)
            {
                return nodoSearch.getValue();
            }
            else
            {
                Log.e(TAG, "NON E' STATO TROVATO NESSUNA KEY CHE CORRISPONDA A KEY : '" + key + "' E SOTTO KEY : '" + downKey + "'");
            }
        }
        return null;
    }

    /**
     * cerca una sottochiave (che contine una stringa passata in input ) che il cui padre è la key passata nell'albero
     * @param key key da ricercare
     * @param downKey sotto key da ricercare
     * @param contain cosa deve contenere la sottokey
     * @return object trovato
     */
    public Object searchKeyWithDownKeyAndContain(String key, String downKey, String contain)
    {
        nodoSearch = null;
        if (nodo != null)
        {
            searchAlternativeNodoWithDownKeyAndContain(nodo, key, downKey, contain);

            if (nodoSearch != null)
            {
                return nodoSearch.getValue();
            }
            else
            {
                Log.e(TAG, "NON E' STATO TROVATO NESSUNA KEY CHE CORRISPONDA A KEY : '" + key + "' E SOTTO KEY : '" + downKey + "'");
            }
        }
        return null;
    }

    /**
     * cerca una chiave che continee una sotto chiave con un valore
     * @param key key da ricercare
     * @param downKey sotto key da ricercare
     * @param value value nel value
     * @return object trovato
     */
    public Object searchKeyWithDownKeyAndValue(String key, String downKey, String value)
    {
        nodoSearch = null;
        if (nodo != null)
        {
            searchAlternativeNodoWithDownKeyAndValue(nodo, key, downKey, value);

            if (nodoSearch != null)
            {
                return nodoSearch.getValue();
            }
            else
            {
                Log.e(TAG, "NON E' STATO TROVATO NESSUNA KEY CHE CORRISPONDA A KEY : '" + key + "' E SOTTO KEY : '" + downKey + "'");
            }
        }
        return null;
    }

    /**
     * restituisce un array
     * @param arrayListOfKey
     * @return restituisce un array con chiave e valore, e il separatore tra chiave e valore è la costante kSeparatore
     */
    public ArrayList<ObjectResultTree> buildArrayWithArrayOfKey(ArrayList<String> arrayListOfKey)
    {
        objectResultTree = new ObjectResultTree();
        arrayObjectResultTree = new ArrayList<>();

        if (nodo != null)
        {
            int lengthParam = 0;

            for (int i = 0 ; i < arrayListOfKey.size() ; i++)
            {
                Object keyInJson = searchKey(arrayListOfKey.get(i), false);
                if (keyInJson != null)
                {
                    lengthParam += 1;
                }
            }

            if (lengthParam == arrayListOfKey.size())
            {
                searchArrayOfNodoWithKeys(nodo, lengthParam, arrayListOfKey);
                return arrayObjectResultTree;
            }
        }

        return null;
    }

    /**
     * Cerca una key e ti ritorna un oggetto Leaf.
     * Se la chiave che si vuole cercare è una foglia nel value dell'oggetto Leaf ci sarà il valore della chiave,
     * altrimenti se la key contiene altri oggetti nel value dell'oggetto Leaf ci sarà un json e quindi
     * sarebbe un albero a partire da quella foglia.
     * @param key la chiave che si vuole cercare
     * @return (Leaf) risultato
     */
    public Leaf searchKey(String key)
    {
        nodoSearch = null;
        if (nodo != null)
        {
            searchNodo(nodo, key, null);

            if (nodoSearch != null)
            {
                return new Leaf(nodoSearch);
            }
            else
            {
                Log.e(TAG, "NON E' STATO TROVATO NESSUNA KEY CHE CORRISPONDA A KEY : '" + key + "'");
            }
        }
        return null;
    }

    Leaf advanceSearchKey(String key, Nodo nodo)
    {
        nodoSearch = null;
        searchNodo(nodo, key, null);

        if (nodoSearch != null)
        {
            return new Leaf(nodoSearch);
        }
        else
        {
            Log.e(TAG, "NON E' STATO TROVATO NESSUNA KEY CHE CORRISPONDA A KEY : '" + key + "'");
        }
        return null;
    }

    public Nodo getNodo() {
        return nodo;
    }

    public int lengthNode(Nodo nodo)
    {
        return nodo.getPuntatore().size();
    }

    /**
     * converte un oggetto JSON in una albero
     * @param json oggetto JSONObject che contiene il json
     * @return ritorna una HashMap <String, Object>
     * @throws JSONException
     */
    public Tree jsonToTree(Tree tree, JSONObject json) throws JSONException
    {
        if (json != JSONObject.NULL)
        {
            creazioneAlbero(tree.getNodo(), json, false);
        }

        return tree;
    }

    /**
     * partendo da un JSON§Object ti crea l'apposito albero
     * @param nodo Nodo per la funzione ricorsiva
     * @param object OBJECT in Json
     */
    private void creazioneAlbero(Nodo nodo, JSONObject object, boolean isArray)
    {
        try
        {
            for (int i = 0; i < object.length(); i++)
            {
                //key del json -> "key" : "value"
                String key = object.names().get(i).toString();
                Object value = object.get(key);

                if (value instanceof JSONObject)
                {
                    //è un ramo
                    Nodo nodoFiglio = new Nodo(key, value.toString());
                    nodo.getPuntatore().add(nodoFiglio);
                    creazioneAlbero(nodoFiglio, (JSONObject) value, isArray);
                }
                else if (value instanceof JSONArray)
                {
                    //è un ramo
                    isArray = true;
                    Nodo nodoFiglio = new Nodo(key, value.toString());
                    nodo.getPuntatore().add(nodoFiglio);
                    toList(nodoFiglio, (JSONArray) value, key, isArray);
                }
                else
                {
                    if (isArray)
                    {
                        Nodo nodoFiglio = new Nodo("array", "array");
                        nodo.getPuntatore().add(nodoFiglio);
                        nodo = nodoFiglio;
                        nodo.getPuntatore().add(new Nodo(key, value.toString()));
                        isArray = false;
                    }
                    else
                    {
                        nodo.getPuntatore().add(new Nodo(key, value.toString()));
                    }
                }
            }
        }
        catch (Exception e)
        {
            Log.e(TAG, "C'E' STATO UN ERRORE NELLA CREAZIONE DELL'ALBERO, ERRORE  : " + e.getMessage());
        }
    }

    /**
     * prende un array    key : [ .. , .. , .. ]   in json e lo mette dentro al nodo
     * @param nodo Nodo dell'albero che diventerà l'albero del json
     * @param array array in JSON
     * @param key chiave del json
     * @return Ritorna una lista di Object
     * @throws JSONException
     */
    private List<Object> toList(Nodo nodo, JSONArray array, String key, boolean isArray) throws JSONException {
        List<Object> list = new ArrayList<>();
        for (int i = 0; i < array.length(); i++)
        {
            Object value = array.get(i);
            if (value instanceof JSONArray) {
                Nodo nodoFiglio = new Nodo(key, value.toString());
                nodo.getPuntatore().add(nodoFiglio);
                value = toList(nodo, (JSONArray) value, key, isArray);
            }
            else if (value instanceof JSONObject)
            {
                creazioneAlbero(nodo, (JSONObject) value, isArray);
            }
            list.add(value);
        }
        return list;
    }

}

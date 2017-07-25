package it.acutus.jsontotreecode;

import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class JsonTree implements Serializable
{
    private static final String TAG = JsonTree.class.getName();

    public static final String kSeparatore = "%&%&";

    private JsonNodo jsonNodo = new JsonNodo();
    private JsonNodo jsonNodoSearch = new JsonNodo();
    private ArrayList<ObjectResultTree> arrayObjectResultTree = new ArrayList<>();
    private ObjectResultTree objectResultTree;

    public JsonTree()
    {
    }

    public JsonTree buildTree(String json)
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
     * classica ricerca di un jsonNodo
     * @param jsonNodo jsonNodo su cui andare a cercarci dentro
     * @param key la chiave da cercare
     * @param downKey la sottochiave da cercare se esiste, altrimenti null
     */
    private void searchNodo(JsonNodo jsonNodo, String key, String downKey)
    {
        for (int i = 0; i < jsonNodo.getPuntatore().size() ; i++)
        {
            if (downKey == null)
            {
                if (jsonNodo.getPuntatore().get(i).getKey().equals(key))
                {
                    jsonNodoSearch = jsonNodo.getPuntatore().get(i);
                }
            }
            else
            {
                if (jsonNodo.getPuntatore().get(i).getKey().equals(key))
                {
                    for (int a = 0; a < jsonNodo.getPuntatore().get(i).getPuntatore().size() ; a ++)
                    {
                        if (jsonNodo.getPuntatore().get(i).getPuntatore().get(a).getKey().equals(downKey))
                        {
                            jsonNodoSearch = jsonNodo.getPuntatore().get(i).getPuntatore().get(a);
                        }
                    }
                }
            }

            searchNodo(jsonNodo.getPuntatore().get(i), key, downKey);
        }
    }

    /**
     * classica ricerca di un jsonNodo ma nello stesso livello, non va in profondità
     * @param jsonNodo jsonNodo su cui andare a cercarci dentro
     * @param key la chiave da cercare
     * @param downKey la sottochiave da cercare se esiste, altrimenti null
     */
    private void searchNodoIntoSameLevel(JsonNodo jsonNodo, String key, String downKey)
    {
        for (int i = 0; i < jsonNodo.getPuntatore().size() ; i++)
        {
            if (jsonNodo.getPuntatore().get(i).getKey().equals("array") && jsonNodo.getPuntatore().get(i).getValue().equals("array"))
            {
                searchNodoIntoSameLevel(jsonNodo.getPuntatore().get(i), key, downKey);
            }

            if (downKey == null)
            {
                if (jsonNodo.getPuntatore().get(i).getKey().equals(key))
                {
                    jsonNodoSearch = jsonNodo.getPuntatore().get(i);
                }
            }
            else
            {
                if (jsonNodo.getPuntatore().get(i).getKey().equals(key))
                {
                    for (int a = 0; a < jsonNodo.getPuntatore().get(i).getPuntatore().size() ; a ++)
                    {
                        if (jsonNodo.getPuntatore().get(i).getPuntatore().get(a).getKey().equals(downKey))
                        {
                            jsonNodoSearch = jsonNodo.getPuntatore().get(i).getPuntatore().get(a);
                        }
                    }
                }
            }
        }
    }

    /**
     * cerca la key che contiene un certo valore dentro
     * @param jsonNodo jsonNodo su cui cercare
     * @param key la key che ti serve cercare
     * @param downKey la sottochiave che ti serve cercare
     * @param contain la stringa che contine il value della key
     */
    private void searchNodoWithDownKeyAndContain(JsonNodo jsonNodo, String key, String downKey, String contain)
    {
        for (int i = 0; i < jsonNodo.getPuntatore().size() ; i++)
        {
            if (jsonNodo.getPuntatore().get(i).getKey().equals(key))
            {
                boolean flagFindDownKey = false;
                boolean flagFindContain = false;
                int positionKey = -100;
                int positionContent = -100;

                for (int a = 0; a < jsonNodo.getPuntatore().get(i).getPuntatore().size() ; a ++)
                {
                    if (jsonNodo.getPuntatore().get(i).getPuntatore().get(a).getKey().equals(downKey))
                    {
                        flagFindDownKey = true;
                        if (positionKey == -100)
                            positionContent = a;
                    }
                    else if (jsonNodo.getPuntatore().get(i).getPuntatore().get(a).getValue().equals(contain))
                    {
                        flagFindContain = true;
                        positionKey = a;
                    }
                }
                if (flagFindContain && flagFindDownKey && positionKey != -100 && positionContent != -100)
                {
                    jsonNodoSearch = new JsonNodo(jsonNodo.getPuntatore().get(i).getPuntatore().get(positionKey).getValue(), jsonNodo.getPuntatore().get(i).getPuntatore().get(positionContent).getValue());
                }
            }

            searchNodoWithDownKeyAndContain(jsonNodo.getPuntatore().get(i), key, downKey, contain);
        }
    }

    /**
     * ricerca particolare, perchè cerca una key che continete una downkey con il valore
     * @param jsonNodo jsonNodo su cui cercare
     * @param key la key che ti serve cercare
     * @param downKey la sottochiave che ti serve cercare
     * @param value la stringa che contine il value da cercre nel value
     */
    private void searchNodoWithDownKeyAndValue(JsonNodo jsonNodo, String key, String downKey, String value)
    {
        for (int i = 0; i < jsonNodo.getPuntatore().size() ; i++)
        {
            if (jsonNodo.getPuntatore().get(i).getKey().equals(key))
            {
                for (int a = 0; a < jsonNodo.getPuntatore().get(i).getPuntatore().size() ; a ++)
                {
                    if (jsonNodo.getPuntatore().get(i).getPuntatore().get(a).getValue().equals(value) && jsonNodo.getPuntatore().get(i).getPuntatore().get(a).getKey().equals(downKey))
                    {
                        jsonNodoSearch = new JsonNodo(jsonNodo.getPuntatore().get(i).getPuntatore().get(a).getValue(), jsonNodo.getPuntatore().get(i).getPuntatore().get(a).getValue());
                    }
                }
            }

            searchNodoWithDownKeyAndValue(jsonNodo.getPuntatore().get(i), key, downKey, value);
        }
    }

    /**
     * serve per costruire un array di chiavi
     * @param jsonNodo JsonNodo di riferimento
     * @param lengthParam lunghezza dei parametri
     * @param key ArrayList<String> da cercare
     */
    private void searchArrayOfNodoWithKeys(JsonNodo jsonNodo, int lengthParam, ArrayList<String> key)
    {
        for (int i = 0; i < jsonNodo.getPuntatore().size() ; i++)
        {
            if (key.contains(jsonNodo.getPuntatore().get(i).getKey()))
            {
                objectResultTree.addResult(jsonNodo.getPuntatore().get(i).getKey() + kSeparatore + jsonNodo.getPuntatore().get(i).getValue());
            }

            if (objectResultTree.getResult().size() % lengthParam == 0 && objectResultTree.getResult().size() != 0)
            {
                arrayObjectResultTree.add(objectResultTree);
                objectResultTree = new ObjectResultTree();
            }

            searchArrayOfNodoWithKeys(jsonNodo.getPuntatore().get(i), lengthParam, key);
        }
    }

    /**
     * cerca una sottochiave che il cui padre è la key passata nell'albero
     * @param key key da cercare
     * @param downKey sotto key da cercare
     * @return object trovato
     */
    public Object searchKeyWithDownKey(String key, String downKey)
    {
        jsonNodoSearch = null;
        if (jsonNodo != null)
        {
            searchNodo(jsonNodo, key, downKey);

            if (jsonNodoSearch != null)
            {
                return jsonNodoSearch.getValue();
            }
            else
            {
                Log.e(TAG, "NON E' STATO TROVATO NESSUNA KEY CHE CORRISPONDA A KEY : '" + key + "' E SOTTO KEY : '" + downKey + "'");
            }
        }
        return null;
    }

    /**
     * cerca una sottochiave (che contine una stringa passata in input) che il cui padre è la key passata nell'albero
     * @param key key da ricercare
     * @param downKey sotto key da ricercare
     * @param contain cosa deve contenere la sottokey
     * @return object trovato
     */
    public Object searchKeyWithDownKeyAndContain(String key, String downKey, String contain)
    {
        jsonNodoSearch = null;
        if (jsonNodo != null)
        {
            searchNodoWithDownKeyAndContain(jsonNodo, key, downKey, contain);

            if (jsonNodoSearch != null)
            {
                return jsonNodoSearch.getValue();
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
        jsonNodoSearch = null;
        if (jsonNodo != null)
        {
            searchNodoWithDownKeyAndValue(jsonNodo, key, downKey, value);

            if (jsonNodoSearch != null)
            {
                return jsonNodoSearch.getValue();
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

        if (jsonNodo != null)
        {
            int lengthParam = 0;

            for (int i = 0 ; i < arrayListOfKey.size() ; i++)
            {
                Object keyInJson = searchKey(arrayListOfKey.get(i));
                if (keyInJson != null)
                {
                    lengthParam += 1;
                }
            }

            if (lengthParam == arrayListOfKey.size())
            {
                searchArrayOfNodoWithKeys(jsonNodo, lengthParam, arrayListOfKey);
                return arrayObjectResultTree;
            }
        }

        return null;
    }

    /**
     * Cerca una key e ti ritorna un oggetto JsonLeaf.
     * Se la chiave che si vuole cercare è una foglia nel value dell'oggetto JsonLeaf ci sarà il valore della chiave,
     * altrimenti se la key contiene altri oggetti nel value dell'oggetto JsonLeaf ci sarà un json e quindi
     * sarebbe un albero a partire da quella foglia.
     * @param key la chiave che si vuole cercare
     * @return (JsonLeaf) risultato
     */
    public JsonLeaf searchKey(String key)
    {
        jsonNodoSearch = null;
        if (jsonNodo != null)
        {
            searchNodo(jsonNodo, key, null);

            if (jsonNodoSearch != null)
            {
                return new JsonLeaf(jsonNodoSearch);
            }
            else
            {
                Log.e(TAG, "NON E' STATO TROVATO NESSUNA KEY CHE CORRISPONDA A KEY : '" + key + "'");
            }
        }
        return null;
    }

    JsonLeaf searchKeySameLevel(String key, JsonNodo jsonNodo)
    {
        jsonNodoSearch = null;
        searchNodoIntoSameLevel(jsonNodo, key, null);

        if (jsonNodoSearch != null)
        {
            return new JsonLeaf(jsonNodoSearch);
        }
        else
        {
            Log.e(TAG, "NON E' STATO TROVATO NESSUNA KEY CHE CORRISPONDA A KEY : '" + key + "'");
        }
        return null;
    }

    public JsonNodo getJsonNodo() {
        return jsonNodo;
    }

    public int lengthNode(JsonNodo jsonNodo)
    {
        return jsonNodo.getPuntatore().size();
    }

    /**
     * converte un oggetto JSON in una albero
     * @param json oggetto JSONObject che contiene il json
     * @return ritorna una HashMap <String, Object>
     * @throws JSONException
     */
    public JsonTree jsonToTree(JsonTree jsonTree, JSONObject json) throws JSONException
    {
        if (json != JSONObject.NULL)
        {
            creazioneAlbero(jsonTree.getJsonNodo(), json, false);
        }

        return jsonTree;
    }

    /**
     * partendo da un JSON§Object ti crea l'apposito albero
     * @param jsonNodo JsonNodo per la funzione ricorsiva
     * @param object OBJECT in Json
     */
    private void creazioneAlbero(JsonNodo jsonNodo, JSONObject object, boolean isArray)
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
                    JsonNodo jsonNodoFiglio = new JsonNodo(key, value.toString());
                    jsonNodo.getPuntatore().add(jsonNodoFiglio);
                    creazioneAlbero(jsonNodoFiglio, (JSONObject) value, isArray);
                }
                else if (value instanceof JSONArray)
                {
                    //è un ramo
                    isArray = true;
                    JsonNodo jsonNodoFiglio = new JsonNodo(key, value.toString());
                    jsonNodo.getPuntatore().add(jsonNodoFiglio);
                    toList(jsonNodoFiglio, (JSONArray) value, key, isArray);
                }
                else
                {
                    if (isArray)
                    {
                        JsonNodo jsonNodoFiglio = new JsonNodo("array", "array");
                        jsonNodo.getPuntatore().add(jsonNodoFiglio);
                        jsonNodo = jsonNodoFiglio;
                        jsonNodo.getPuntatore().add(new JsonNodo(key, value.toString()));
                        isArray = false;
                    }
                    else
                    {
                        jsonNodo.getPuntatore().add(new JsonNodo(key, value.toString()));
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
     * prende un array    key : [ .. , .. , .. ]   in json e lo mette dentro al jsonNodo
     * @param jsonNodo JsonNodo dell'albero che diventerà l'albero del json
     * @param array array in JSON
     * @param key chiave del json
     * @return Ritorna una lista di Object
     * @throws JSONException
     */
    private List<Object> toList(JsonNodo jsonNodo, JSONArray array, String key, boolean isArray) throws JSONException {
        List<Object> list = new ArrayList<>();
        for (int i = 0; i < array.length(); i++)
        {
            Object value = array.get(i);
            if (value instanceof JSONArray) {
                JsonNodo jsonNodoFiglio = new JsonNodo(key, value.toString());
                jsonNodo.getPuntatore().add(jsonNodoFiglio);
                value = toList(jsonNodo, (JSONArray) value, key, isArray);
            }
            else if (value instanceof JSONObject)
            {
                creazioneAlbero(jsonNodo, (JSONObject) value, isArray);
            }
            else
            {
                JsonNodo jsonNodoFiglio = new JsonNodo(key, value.toString());
                jsonNodo.getPuntatore().add(jsonNodoFiglio);
            }
            list.add(value);
        }
        return list;
    }

}

package it.acutus.jsontotreecode;

import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import it.acutus.jsontotreecode.parserXml.parser.XmlToJson;

public class JXTree implements Serializable
{
    private static final String TAG = JXTree.class.getName();

    static final String kSeparatore = "%&%&";

    private static final int startLevel = 0;

    private JXNodo JXNodo = new JXNodo(0, startLevel, "firstGroup");
    private JXNodo JXNodoSearch = new JXNodo(0, startLevel, "firstGroup");
    private ArrayList<ObjectResultTree> arrayObjectResultTree = new ArrayList<>();
    private ObjectResultTree objectResultTree;
    private boolean onBuildArray = true;

    private int idAutoincrement = 0;

    private boolean logEnabled = false;

    public JXTree()
    {
    }

    public JXTree buildTreeFromJson(String json)
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

    public JXTree buildTreeFromXml(String xml)
    {
        try
        {
            jsonToTree(this, stringXmlToJson(xml));

            if (JXNodo != null)
            {
                removeReplaceStringIntoSameLevel(JXNodo);

                onBuildArray = true;
                while (onBuildArray)
                {
                    onBuildArray = false;
                    buildArray(JXNodo);
                }

                adjustArray(JXNodo);
                removeReplaceStringIntoSameLevel(JXNodo);

                removeValueNotNecessary(JXNodo);
            }

            return this;
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * converte una String xml in json
     * @param xml stringa xml
     * @return ritorna un oggetto JSONObject con un oggetto in json
     */
    private JSONObject stringXmlToJson(String xml)
    {
        XmlToJson xmlToJson = new XmlToJson.Builder(xml).build();
        JSONObject jsonObject = xmlToJson.toJson();

        String log = "";
        assert jsonObject != null;
        log = log + "\n\nString XML : \n" +
                "{\n\n" +
                xml + "\n\n" +
                "}\n" +
                "Convert to JSON : \n\n" +
                jsonObject.toString() + "\n\n" +
                "}\n ";

        if (logEnabled) Log.d(TAG, log);

        return jsonObject;
    }

    /**
     * creando il json da xml se ci sono tag uguali nello stesso livello gli danno fastidio e non creva il json, quindi a quelli che esistevano di gia gli ho accodato delle substring+
     * che ora vanno tolte
     * @param JXNodo JXNodo
     */
    private void removeReplaceStringIntoSameLevel(JXNodo JXNodo)
    {
        for (int i = 0; i < JXNodo.getPuntatore().size() ; i++)
        {
            checkAndRemoveValue(JXNodo.getPuntatore().get(i), JXNodo.getPuntatore().get(i).getKey(), true);

            removeReplaceStringIntoSameLevel(JXNodo.getPuntatore().get(i));
        }
    }

    /**
     * è necessario prima di questa funzione richiamare la funzione removeReplaceStringIntoSameLevel, da xml a json fa casino quando l'xml è costruito un po male, ovvero quando i tag si ripetono
     * sotto le stesso padre, e per il json è un singolo campo, quindi ci sta che in questo passaggio ne abbiamo 2-3 uguali e dobbiamo raggrupparli in un array. es.
     * questo non sistemerà l'array ma solamente le foglie dell'albero, per poi sitemare l'array si deve richiamare adjustArray()
     *
     *
     * json:
     *
     * {
     *      "a" : { ... },
     *      "a" : { ... },
     *      "a" : { ... },
     *      "a" : { ... },
     * }
     *
     * code:
     * {
     *      "a"
     *      |
     *      |__ { ... }
     *      |__ { ... }
     *      |__ { ... }
     *      |__ { ... }
     * }
     *
     * @param JXNodo
     */
    private void buildArray(JXNodo JXNodo)
    {
        ArrayList<String> keysIntoSameNode = new ArrayList<>();
        for (int i = 0; i < JXNodo.getPuntatore().size() ; i++)
        {
            String keyThisNodo = JXNodo.getPuntatore().get(i).getKey();

            if (keysIntoSameNode.contains(JXNodo.getPuntatore().get(i).getGroup() + XmlToJson.preTag + keyThisNodo) && !keyThisNodo.startsWith(XmlToJson.preTag + "element_"))
            {
                onBuildArray = true;
                int indice = -1;
                for (int j = 0; j < JXNodo.getPuntatore().size() ; j++)
                {
                    // cerco se avevo gia creato una key che raggruppa un determinato array
                    if (JXNodo.getPuntatore().get(j).isCreateForXml() && JXNodo.getPuntatore().get(j).getKey().equals(keyThisNodo + XmlToJson.afterTag + "1"))
                    {
                        indice = j;
                    }
                }

                JXNodo arrayForXml = new JXNodo(JXNodo.getId(), JXNodo.getLevel() + 1, JXNodo.getGroup());

                if (indice == -1)
                {
                    // entra qua se non avevo gia creato una key per raggruppa l'array
                    arrayForXml.setKey(keyThisNodo + XmlToJson.afterTag + "1");
                    arrayForXml.setCreateForXml(true);
                    arrayForXml.setIsArray(true);
                    indice = 0;
                    JXNodo.getPuntatore().add(indice, arrayForXml);

                    idAutoincrement += 1;
                    JXNodo newJXNodo = JXNodo.getPuntatore().get(i);
                    newJXNodo.setKey(XmlToJson.preTag + "element_" + newJXNodo.getKey() + XmlToJson.afterTag + JXNodo.getPuntatore().get(indice).getPuntatore().size()); //gli do solamente un'altra key, in modo da creare un'array
                    newJXNodo.setId(idAutoincrement);

                    JXNodo.getPuntatore().get(indice).getPuntatore().add(newJXNodo);
                }
                else
                {
                    // entra qua se avevo gia creato un key che raggruppa l'array
                    idAutoincrement += 1;
                    JXNodo newJXNodo = JXNodo.getPuntatore().get(i);
                    newJXNodo.setKey(XmlToJson.preTag + "element_" + newJXNodo.getKey() + XmlToJson.afterTag + JXNodo.getPuntatore().get(indice).getPuntatore().size()); //gli do solamente un'altra key, in modo da creare un'array
                    newJXNodo.setId(idAutoincrement);

                    JXNodo.getPuntatore().get(indice).getPuntatore().add(newJXNodo);
                }

                JXNodo.getPuntatore().remove(i);

                i -= 1;
            }
            else
            {
                keysIntoSameNode.add(JXNodo.getPuntatore().get(i).getGroup() + XmlToJson.preTag + keyThisNodo);
            }

            buildArray(JXNodo.getPuntatore().get(i));
        }
    }

    /**
     * è necessario prima di questa funzione richiamare la funzione removeReplaceStringIntoSameLevel e buildArray, da xml a json fa casino quando l'xml è costruito un po male, ovvero quando i tag si ripetono
     * sotto le stesso padre, e per il json è un singolo campo, quindi ci sta che in questo passaggio ne abbiamo 2-3 uguali e dobbiamo raggrupparli in un array. es.
     *
     *
     * code:
     * {
     *      "a"
     *      |
     *      |__ { ... }
     *      |__ { ... }
     *      |__ { ... }
     *      |__ { ... }
     * }
     *
     * json:
     * {
     *      "a" : [
     *          { ... },
     *          { ... },
     *          { ... },
     *          { ... },
     *      ],
     * }
     *
     * @param JXNodo
     */
    private void adjustArray(JXNodo JXNodo)
    {
        for (int i = 0; i < JXNodo.getPuntatore().size() ; i++)
        {
            JXNodo upperNodo = JXNodo.getPuntatore().get(i);

            analizeNodoForarray(upperNodo);

            adjustArray(JXNodo.getPuntatore().get(i));
        }
    }

    private void analizeNodoForarray(JXNodo upperNodo)
    {
        for (int j = 0; j < upperNodo.getPuntatore().size(); j++)
        {
            JXNodo thisNodo = upperNodo.getPuntatore().get(j);

            boolean daRaggruppare = false;
            boolean sonoPassato = false;

            for (int a = 0; a < thisNodo.getPuntatore().size(); a++)
            {
                String keyThisNodo = thisNodo.getPuntatore().get(a).getKey();

                boolean find = checkAndRemoveValue(null, keyThisNodo, false);

                if (!sonoPassato && find)
                {
                    daRaggruppare = true;
                }

                if (daRaggruppare)
                {
                    daRaggruppare = find;
                }

                sonoPassato = true;
            }

            if (daRaggruppare)
            {
                String newValue = upperNodo.getValue();
                String exValue = newValue;

                for (int a = 0; a < thisNodo.getPuntatore().size(); a++)
                {
                    checkAndRemoveValue(thisNodo, thisNodo.getKey(), true);
                    if (newValue != null)
                    {
                        newValue = newValue.replace("{\"" + thisNodo.getKey() + "\":{", "{\"" + thisNodo.getKey() + "\":[{");
                        newValue = newValue.replace(",\"" + thisNodo.getKey() + "\":{", ",\"" + thisNodo.getKey() + "\":[{");

                        newValue = newValue.replace("},\"" + thisNodo.getKey() + XmlToJson.afterTag + a + "\":{", "}, {");
                        newValue = newValue.replace("{\"" + thisNodo.getKey() + XmlToJson.afterTag + a + "\":{", "{");
                    }
                }

                if (newValue != null && !exValue.equals(newValue))
                {
                    newValue = newValue.substring(0, newValue.length() - 1);
                    newValue = newValue + "]}";

                    upperNodo.setValue(newValue);
                    upperNodo.setValueNecessary(true);
                }
            }
        }
    }

    private boolean checkAndRemoveValue(JXNodo nodo, String key, boolean remove)
    {
        Pattern p = Pattern.compile(XmlToJson.afterTag);
        Matcher m = p.matcher(key);

        while (m.find())
        {
            int indexStartCheck = m.start();
            int indexEndCheck = m.end();

            if (indexStartCheck > 0 && key.substring(indexStartCheck, indexEndCheck).equals(XmlToJson.afterTag))
            {
                String numberToCheck = key.substring(indexEndCheck, key.length());
                if (isInteger(numberToCheck))
                {
                    if (nodo != null && remove)
                    {
                        nodo.setKey(key.replace(XmlToJson.afterTag + numberToCheck, ""));

                        String newValue = nodo.getValue();
                        if (newValue != null)
                        {
                            newValue = newValue.replace(XmlToJson.afterTag + numberToCheck, "");
                            nodo.setValue(newValue);
                        }
                    }

                    return true;
                }
            }
        }

        return key.startsWith(XmlToJson.preTag + "element_");
    }

    private void removeValueNotNecessary(JXNodo JXNodo)
    {
        for (int i = 0; i < JXNodo.getPuntatore().size() ; i++)
        {
            if (!JXNodo.getPuntatore().get(i).isValueNecessary())
            {
                JXNodo.getPuntatore().get(i).setValue(null);
            }
            else
            {
                JXNodo.getPuntatore().get(i).setValue(JXNodo.getPuntatore().get(i).getValue().replace(XmlToJson.preTag + "element_", XmlToJson.array));
                JXNodo.getPuntatore().get(i).setValue(JXNodo.getPuntatore().get(i).getValue().replace(XmlToJson.afterTag, XmlToJson.array));
            }

            adjustArray(JXNodo.getPuntatore().get(i));
        }
    }

    /**
     * classica ricerca di un JXNodo
     * @param JXNodo JXNodo su cui andare a cercarci dentro
     * @param key la chiave da cercare
     * @param downKey la sottochiave da cercare se esiste, altrimenti null
     */
    private void searchNodo(JXNodo JXNodo, String key, String downKey)
    {
        for (int i = 0; i < JXNodo.getPuntatore().size() ; i++)
        {
            if (downKey == null)
            {
                if (JXNodo.getPuntatore().get(i).getKey().equals(key))
                {
                    JXNodoSearch = JXNodo.getPuntatore().get(i);
                }
            }
            else
            {
                if (JXNodo.getPuntatore().get(i).getKey().equals(key))
                {
                    for (int a = 0; a < JXNodo.getPuntatore().get(i).getPuntatore().size() ; a ++)
                    {
                        if (JXNodo.getPuntatore().get(i).getPuntatore().get(a).getKey().equals(downKey))
                        {
                            JXNodoSearch = JXNodo.getPuntatore().get(i).getPuntatore().get(a);
                        }
                    }
                }
            }

            searchNodo(JXNodo.getPuntatore().get(i), key, downKey);
        }
    }

    /**
     * classica ricerca di un JXNodo ma nello stesso livello, non va in profondità
     * @param JXNodo JXNodo su cui andare a cercarci dentro
     * @param key la chiave da cercare
     * @param downKey la sottochiave da cercare se esiste, altrimenti null
     */
    private void searchNodoIntoSameLevel(JXNodo JXNodo, String key, String downKey)
    {
        for (int i = 0; i < JXNodo.getPuntatore().size() ; i++)
        {
            if (JXNodo.getPuntatore().get(i).getKey().equals("array") && JXNodo.getPuntatore().get(i).getValue().equals("array"))
            {
                searchNodoIntoSameLevel(JXNodo.getPuntatore().get(i), key, downKey);
            }

            if (downKey == null)
            {
                if (JXNodo.getPuntatore().get(i).getKey().equals(key))
                {
                    JXNodoSearch = JXNodo.getPuntatore().get(i);
                }
            }
            else
            {
                if (JXNodo.getPuntatore().get(i).getKey().equals(key))
                {
                    for (int a = 0; a < JXNodo.getPuntatore().get(i).getPuntatore().size() ; a ++)
                    {
                        if (JXNodo.getPuntatore().get(i).getPuntatore().get(a).getKey().equals(downKey))
                        {
                            JXNodoSearch = JXNodo.getPuntatore().get(i).getPuntatore().get(a);
                        }
                    }
                }
            }
        }
    }

    /**
     * cerca una key che continete una downkey con il valore
     * @param JXNodo JXNodo su cui cercare
     * @param key la key che ti serve cercare
     * @param downKey la sottochiave che ti serve cercare
     * @param value la stringa che contine il value da cercre nel value
     */
    private void searchNodoWithDownKeyAndValue(JXNodo JXNodo, String key, String downKey, String value)
    {
        for (int i = 0; i < JXNodo.getPuntatore().size() ; i++)
        {
            if (JXNodo.getPuntatore().get(i).getKey().equals(key))
            {
                for (int a = 0; a < JXNodo.getPuntatore().get(i).getPuntatore().size() ; a ++)
                {
                    if (JXNodo.getPuntatore().get(i).getPuntatore().get(a).getValue().equals(value) && JXNodo.getPuntatore().get(i).getPuntatore().get(a).getKey().equals(downKey))
                    {
                        JXNodoSearch = new JXNodo(JXNodo.getId(), JXNodo.getPuntatore().get(i).getPuntatore().get(a).getValue(), JXNodo.getPuntatore().get(i).getPuntatore().get(a).getValue(), JXNodo.getPuntatore().get(i).getPuntatore().get(a).getLevel(), JXNodo.getPuntatore().get(i).getPuntatore().get(a).getGroup());
                    }
                }
            }

            searchNodoWithDownKeyAndValue(JXNodo.getPuntatore().get(i), key, downKey, value);
        }
    }

    /**
     * serve per costruire un array di chiavi
     * @param JXNodo JXNodo di riferimento
     * @param lengthParam lunghezza dei parametri
     * @param key ArrayList<String> da cercare
     */
    private void searchArrayOfNodoWithKeys(JXNodo JXNodo, int lengthParam, ArrayList<String> key)
    {
        for (int i = 0; i < JXNodo.getPuntatore().size() ; i++)
        {
            if (key.contains(JXNodo.getPuntatore().get(i).getKey()))
            {
                objectResultTree.addResult(JXNodo.getPuntatore().get(i).getKey() + kSeparatore + JXNodo.getPuntatore().get(i).getValue());
            }

            if (objectResultTree.getResult().size() % lengthParam == 0 && objectResultTree.getResult().size() != 0)
            {
                arrayObjectResultTree.add(objectResultTree);
                objectResultTree = new ObjectResultTree();
            }

            searchArrayOfNodoWithKeys(JXNodo.getPuntatore().get(i), lengthParam, key);
        }
    }

    /**
     * cerca una sottochiave che il cui padre è la key passata nell'albero
     * @param key key da cercare
     * @param downKey sotto key da cercare
     * @return object trovato
     */
    public JXLeaf searchKeyWithDownKey(String key, String downKey)
    {
        JXNodoSearch = null;
        if (JXNodo != null)
        {
            searchNodo(JXNodo, key, downKey);

            if (JXNodoSearch != null)
            {
                return new JXLeaf(JXNodoSearch);
            }
            else
            {
                Log.w(TAG, "NON E' STATO TROVATO NESSUNA KEY CHE CORRISPONDA A KEY : '" + key + "' E SOTTO KEY : '" + downKey + "'");
            }
        }
        return new JXLeaf(new JXTree());
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
        JXNodoSearch = null;
        if (JXNodo != null)
        {
            searchNodoWithDownKeyAndValue(JXNodo, key, downKey, value);

            if (JXNodoSearch != null)
            {
                return JXNodoSearch.getValue();
            }
            else
            {
                Log.w(TAG, "NON E' STATO TROVATO NESSUNA KEY CHE CORRISPONDA A KEY : '" + key + "' E SOTTO KEY : '" + downKey + "'");
            }
        }
        return new JXLeaf(new JXTree());
    }

    /**
     * restituisce un array
     * @param arrayListOfKey lista di chiavi che saranno cercate
     * @return restituisce un array con chiave e valore, e il separatore tra chiave e valore è la costante kSeparatore
     */
    public ArrayList<ObjectResultTree> buildArrayWithArrayOfKey(ArrayList<String> arrayListOfKey)
    {
        objectResultTree = new ObjectResultTree();
        arrayObjectResultTree = new ArrayList<>();

        if (JXNodo != null)
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
                searchArrayOfNodoWithKeys(JXNodo, lengthParam, arrayListOfKey);
                return arrayObjectResultTree;
            }
        }

        return null;
    }

    /**
     * Cerca una key e ti ritorna un oggetto JXLeaf.
     * Se la chiave che si vuole cercare è una foglia nel value dell'oggetto JXLeaf ci sarà il valore della chiave,
     * altrimenti se la key contiene altri oggetti nel value dell'oggetto JXLeaf ci sarà un json e quindi
     * sarebbe un albero a partire da quella foglia.
     * @param key la chiave che si vuole cercare
     * @return (JXLeaf) risultato
     */
    public JXLeaf searchKey(String key)
    {
        JXNodoSearch = null;
        if (JXNodo != null)
        {
            searchNodo(JXNodo, key, null);

            if (JXNodoSearch != null)
            {
                return new JXLeaf(JXNodoSearch);
            }
            else
            {
                Log.w(TAG, "NON E' STATO TROVATO NESSUNA KEY CHE CORRISPONDA A KEY : '" + key + "'");
            }
        }
        return new JXLeaf(new JXTree());
    }

    JXLeaf searchKeySameLevel(String key, JXNodo JXNodo)
    {
        JXNodoSearch = null;
        searchNodoIntoSameLevel(JXNodo, key, null);

        if (JXNodoSearch != null)
        {
            return new JXLeaf(JXNodoSearch);
        }
        else
        {
            Log.w(TAG, "NON E' STATO TROVATO NESSUNA KEY CHE CORRISPONDA A KEY : '" + key + "'");
        }
        return new JXLeaf(new JXTree());
    }

    JXNodo getJXNodo() {
        return JXNodo;
    }

    /**
     * converte un oggetto JSON in una albero
     * @param json oggetto JSONObject che contiene il json
     * @return ritorna una HashMap <String, Object>
     * @throws JSONException
     */
    private JXTree jsonToTree(JXTree JXTree, JSONObject json) throws JSONException
    {
        if (json != JSONObject.NULL)
        {
            resetClass();

            creazioneAlbero(JXTree.getJXNodo(), json, false);
        }

        return JXTree;
    }

    /**
     * partendo da un JSON§Object ti crea l'apposito albero
     * @param JXNodo JXNodo per la funzione ricorsiva
     * @param object OBJECT in Json
     */
    private void creazioneAlbero(JXNodo JXNodo, JSONObject object, boolean isArray)
    {
        try
        {
            for (int i = 0; i < object.length(); i++)
            {
                //key del json -> "key" : "value"
                String key = object.names().get(i).toString();
                Object value = object.get(key);

                String group;
                if (value instanceof JSONObject)
                {
                    //è un ramo
                    group = JXNodo.getKey() + JXNodo.getLevel();
                    idAutoincrement += 1;
                    JXNodo JXNodoFiglio = new JXNodo(idAutoincrement, key, value.toString(), JXNodo.getLevel() + 1, group);
                    JXNodo.getPuntatore().add(JXNodoFiglio);

                    creazioneAlbero(JXNodoFiglio, (JSONObject) value, isArray);
                }
                else if (value instanceof JSONArray)
                {
                    //è un ramo
                    isArray = true;
                    group = JXNodo.getKey() + JXNodo.getLevel();
                    idAutoincrement += 1;
                    JXNodo JXNodoFiglio = new JXNodo(idAutoincrement, key, value.toString(), JXNodo.getLevel() + 1, group);
                    JXNodo.getPuntatore().add(JXNodoFiglio);

                    toList(JXNodoFiglio, (JSONArray) value, key, true);
                }
                else
                {
                    if (isArray)
                    {
                        group = JXNodo.getKey() + JXNodo.getLevel();
                        idAutoincrement += 1;
                        JXNodo JXNodoFiglio = new JXNodo(idAutoincrement, "element_array_" + idAutoincrement, "element_array_" + idAutoincrement, JXNodo.getLevel() + 1, group);

                        JXNodo.getPuntatore().add(JXNodoFiglio);
                        JXNodo.setIsArray(true);
                        JXNodo = JXNodoFiglio;

                        group = JXNodo.getKey() + JXNodo.getLevel();
                        idAutoincrement += 1;
                        JXNodo.getPuntatore().add(new JXNodo(idAutoincrement, key, value.toString(), JXNodo.getLevel() + 1, group));

                        isArray = false;
                    }
                    else
                    {
                        group = JXNodo.getKey() + JXNodo.getLevel();
                        idAutoincrement += 1;
                        JXNodo.getPuntatore().add(new JXNodo(idAutoincrement, key, value.toString(), JXNodo.getLevel() + 1, group));
                        //level += 1;
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
     * prende un array    key : [ .. , .. , .. ]   in json e lo mette dentro al JXNodo
     * @param JXNodo JXNodo dell'albero che diventerà l'albero del json
     * @param array array in JSON
     * @param key chiave del json
     * @return Ritorna una lista di Object
     * @throws JSONException
     */
    private List<Object> toList(JXNodo JXNodo, JSONArray array, String key, boolean isArray) throws JSONException {
        List<Object> list = new ArrayList<>();
        String group;
        for (int i = 0; i < array.length(); i++)
        {
            Object value = array.get(i);
            if (value instanceof JSONArray) {
                group = JXNodo.getKey() + JXNodo.getLevel();
                idAutoincrement += 1;
                JXNodo JXNodoFiglio = new JXNodo(idAutoincrement, key, value.toString(), JXNodo.getLevel() + 1, group);

                JXNodo.getPuntatore().add(JXNodoFiglio);
                value = toList(JXNodo, (JSONArray) value, key, isArray);
            }
            else if (value instanceof JSONObject)
            {
                creazioneAlbero(JXNodo, (JSONObject) value, isArray);
            }
            else
            {
                group = JXNodo.getKey() + JXNodo.getLevel();
                idAutoincrement += 1;
                JXNodo JXNodoFiglio = new JXNodo(idAutoincrement, key, value.toString(), JXNodo.getLevel() + 1, group);
                JXNodo.getPuntatore().add(JXNodoFiglio);
            }
            list.add(value);
        }
        return list;
    }

    private void resetClass()
    {
        JXNodo = new JXNodo(0, startLevel, "firstGroup");
        JXNodoSearch = new JXNodo(0, startLevel, "firstGroup");
        arrayObjectResultTree = new ArrayList<>();
        objectResultTree = null;
        idAutoincrement = 0;
    }

    private void enableLog(boolean enable)
    {
        logEnabled = enable;
    }

    private boolean isInteger(String s)
    {
        return s.matches("\\d+");
    }
}

# JsonToTree
Trasforma un Json in una struttura ad albero       

## Importare la libreria nel progetto     
Inserire nel build.gradle generale del progetto le seguenti righe di codice :  
```java
allprojects 
{
  repositories 
  {
    ...
    maven { url 'https://jitpack.io' }
  }
}
```

Aggiungere la dipendenza al modulo nel rispettivo gradle con :  
```java
dependencies 
{
  compile 'com.github.AcutusDeveloper:JsonToTree:1.0.3'
}
```

## Usare la libreria
```java
Tree JXTree = new Tree();
JXTree.buildTree(jsonData); //costruisce l'albero in base al Json passato
Leaf leafResponse = JXTree.searchKey("result");  //cerca la key 'result' nel Json e restituisce la foglia

// se leafResponse nel 'value' contiene un Array si puo sfruttare la funzione .size() per sapere la lunghezza dell'array di oggetti che contiene ovvero: 

Log.d("TAG", "size : " + leafResponse.size());

// oppure si ha la possibilità di cercare un'altra key all'interno della foglia 'leafResponse'

Leaf title = leafResponse.search("title");

// per stampare il valore o la key si puo sfruttare le due funzioni .getValue() e .getKey() ovvero :

Log.d("TAG", "key : " + title.getKey() + " - value : " + title.getValue());

```

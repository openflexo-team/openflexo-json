# CompleteJSONDemo

Ce projet contient un exemple complet et executable de manipulation JSON depuis FML.

## Fichiers

- `CompleteJSONDemo.fml` : VirtualModel avec un `JSONModelSlot`.
- `../../JSON/CompleteDemo.json` : ressource JSON connectee a la VMI.
- `../AutomatedTests/TestCompleteJSONProject.fmlscript` : creation de la VMI et assertions.

## Scenario teste

1. Chargement du VirtualModel et de la ressource JSON.
2. Creation de `CompleteJSONDemoInstance`.
3. Instanciation de `JSONManipulation` avec la cle `components` et l'index 1.
4. Ajout puis suppression d'une propriete primitive `status`.
5. Ajout puis suppression d'un objet `configuration`.
6. Ajout puis suppression d'un tableau `tags`.
7. Verification de l'URI structurelle non intrusive `#/components/1`.

## Execution

Depuis la racine du depot :

```bash
JAVA_HOME=/Users/chahrazed/Library/Java/JavaVirtualMachines/corretto-1.8.0_462/Contents/Home \
  ./gradlew :json-ta-test:test \
  --tests 'org.openflexo.technologyadapter.json.model.fml.AutomatedTests*TestCompleteJSONProject*'
```

# TestPersonTyped

This VirtualModel tests `Person.json` with the currently available JSON runtime:

- one `JSONModelSlot`;
- one persisted root `JSONNodeRole`;
- concept actors for `Person`, `Company`, and `WorksFor`;
- schema-shape checks based on `PersonSchema.schema.json`.

The real FML type alias:

```fml
import JSONSchemaType PERSON from [PERSON_SCHEMA:"Person"];
typedef JSONIndividualType(schemaType=PERSON) as Person;
```

will become executable after `JSONSchemaResource` and `JSONMetaModelSlot` are implemented.

# TestPersonRole

This virtual model connects to `Person.json` and exercises the current JSON support:

- `JSONModelSlot` for the JSON resource;
- `JSONNodeRole` actors for root sections and concept instances;
- JSON structural checks matching `PersonSchema.schema.json`;
- discriminator checks using the JSON `type` field.

The future typed phase will replace the discriminator checks with real declarations like:

```fml
import JSONSchemaType PERSON from [PERSON_SCHEMA:"Person"];
typedef JSONIndividualType(schemaType=PERSON) as Person;
```

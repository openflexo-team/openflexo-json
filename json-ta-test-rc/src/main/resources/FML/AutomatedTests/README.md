# Automated FML Scripts

These scripts are automated tests. They intentionally call:

```fml
service ResourceCenterService add_temp_rc;
```

That creates a temporary resource center for the duration of the test run. Any
`*.fml.rt` VirtualModelInstance created there is disposable and should not be
expected to appear again after restarting OpenFlexo.

For manual OpenFlexo usage, create the VMI in a persistent project/resource
center and save the project, instead of copying the `add_temp_rc` line.

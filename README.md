# PublicationAndroid
Previous build had Demo and Library combined in one pack now it is striped from Demo.

```java
  Intent intent = new Intent(Intent.ACTION_VIEW);
  intent.addCategory("org.fs.category.READ_MAGAZINE");
  intent.putExtra(ReadActivityPresenterImp.KEY_CONFIGURATION, config);
  startActivity(intent);
``` 
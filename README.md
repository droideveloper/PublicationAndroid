# PublicationAndroid
Previous build had Demo and Library combined in one pack now it is striped from Demo.

```java
  Intent intent = new Intent(this, ReadActivity.class);
  intent.putExtra(ReadActivityPresenterImp.KEY_CONFIGURATION, config);
  startActivity(intent);
``` 

New Demo application at [BakerPublicationAndroid](https://github.com/droideveloper/BakerPublicationAndroid)
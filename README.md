# DemoAppy

## NOTES:

- Internet permissions declared in app manifest.
- Timber logging installed from within DemoApp. (DemoApp extends Application, so it is mentioned in the app manifest.)

### In build.gradle

compile 'com.jakewharton.timber:timber:3.1.0'
compile 'com.squareup.okhttp:okhttp:2.5.0'

### In res/raw

A schema SQL and migration SQL script for example use with DemoDB which has migration code in place. See also the utility method to load raw resource files.

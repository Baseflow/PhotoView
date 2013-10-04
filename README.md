# PhotoView - Github Maven

This is backum maven repository, and is (will be) used for both major releases and snapshot versions of PhotoView library.

## Maven URL

**can be used with both http and https**

```
https://github.com/chrisbanes/PhotoView/raw/maven/
```

## Gradle configuration

```
repositories {
  maven {
    url 'https://raw.github.com/chrisbanes/PhotoView/maven/'
  }
  mavenCentral()
}

dependencies {
  compile 'com.github.chrisbanes.photoview:library:1.2.2'
}
```

# Normalizer
Super lightweight, performant and easy-to-use real-time normalizer for audio streams.

This library is intended for dynamic audio stream normalization, that is, the application of a varying amount of gain to
a stream of audio samples to bring the average sound level (RMS) and peaks to within desired levels.

## Installation

This project is using <a href="http://jitpack.io">JitPack</a> as a repository. Add JitPack as a repository, then the dependency will be available:

**Maven:**
```
<repository>
    <id>jitpack.io</id>
    <url>https://jitpack.io</url>
</repository>
```
```
<dependency>
    <groupId>com.github.memoizr</groupId>
    <artifactId>normalizer</artifactId>
    <version>v1.0.4</version>
</dependency>
```
**Gradle:**
```
repositories {
    maven {
        url "https://jitpack.io"
    }
}
```
```
dependencies {
   compile 'com.github.memoizr:normalizer:v1.0.4'
}
```

## Usage

Simply create an instance of the `SimpleNormalizer` class, then call `normalizeVolume(volume)`.

```
...
// Initialization.
mNormalizer = new SimpleNormalizer();
...

...
rawVolumeData.stream()
  .map(volume -> mNormalizer.normalizeVolume(volume)) // Transform raw volume to normalized volume.
  // Do something with the normalized volume.
...

```

And that's it! Enjoy your newly normalized volume stream!



	

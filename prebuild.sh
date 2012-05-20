SDK_DIR=`cat local.properties | grep sdk.dir= | cut -d = -f 2-`
TARGET=`cat project.properties | grep target= | cut -d = -f 2-`

ANDROID_JAR=$SDK_DIR/platforms/$TARGET/android.jar

mkdir -p res/raw/ && \
mkdir -p prebuild/classes && \
mkdir -p bin-dexter/dexter && \
javac -cp $ANDROID_JAR -sourcepath src-dexter -d bin-dexter src-dexter/dexter/CljActivity.java && \
jar cf libs/dexter.jar -C bin-dexter . && \
$SDK_DIR/platform-tools/aapt package -v -j $ANDROID_JAR -J gen res && \
javac -cp $ANDROID_JAR -sourcepath gen -d bin/classes gen/com/fineshambles/dexter/R.java && \
(cd prebuild/ && \
  java -cp $ANDROID_JAR:../bin/classes:../../clojure/clojure/clojure-1.5.0-master-SNAPSHOT.jar:src:classes:../libs/dexter.jar  clojure.main -e "(set! *warn-on-reflection* true)\n(compile 'com.fineshambles.prebuilt.hello)" && \
  dx --dex --output=../res/raw/hello.apk classes)

mkdir -p res/raw/
mkdir -p prebuild/classes
(cd prebuild/ && \
  java -cp ../../clojure/clojure/clojure-1.5.0-master-SNAPSHOT.jar:src:classes  clojure.main -e "(compile 'com.fineshambles.prebuilt.hello)" && \
  dx --dex --output=../res/raw/hello.apk classes)

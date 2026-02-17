git clone https://github.com/h2oai/sparkling-water.git
cd sparkling-water
git checkout master


Build the image:
python3 -m venv .venv
source .venv/bin/activate
pip install --upgrade pip setuptools wheel r
brew install r

<!-- sudo dnf install -y epel-release
sudo dnf install -y R 
cd /opt
sudo curl -LO https://archive.apache.org/dist/spark/spark-3.5.5/spark-3.5.5-bin-hadoop3.tgz
sudo tar -xzf spark-3.5.5-bin-hadoop3.tgz
sudo ln -sfn /opt/spark-3.5.5-bin-hadoop3 /opt/spark

export SPARK_HOME=/opt/spark
export PATH="$SPARK_HOME/bin:$PATH"
spark-submit --version
sudo sed -i 's/11-jre-slim-buster/11-jre-jammy/g' /opt/spark/kubernetes/dockerfiles/spark/Dockerfile
cd /root/sparkling-water/dist/build/zip/sparkling-water-3.46.0.2-1-3.5
sed -n '1,80p' /opt/spark/kubernetes/dockerfiles/spark/Dockerfile

grep -n "java_image_tag" -n /opt/spark/bin/docker-image-tool.sh | head -n 40

grep -n "11-jre" -n /opt/spark/bin/docker-image-tool.sh | head -n 40
sudo sed -i 's/java_image_tag=11-jre-slim-buster/java_image_tag=17-jammy/g' ./bin/build-kubernetes-images.sh

modify the Spark dockerfile
cat /opt/spark/kubernetes/dockerfiles/spark/Dockerfile
RUN set -ex && \
    apt-get update && \
    apt-get -y upgrade && \
    apt-get -y dist-upgrade && \
    ln -s /lib /lib64 && \


-->

./gradlew clean dist -x check

 cd /Users/michaelm/mygit/sparkling-water/dist/build/zip/sparkling-water-3.46.0.2-1-3.5/
 #  cd /root/sparkling-water/dist/build/zip//sparkling-water-3.46.0.2-1-3.5/

export SPARK_HOME=/Users/michaelm/.sdkman/candidates/spark/3.5.3
export PATH="$SPARK_HOME/bin:$PATH"

./bin/build-kubernetes-images.sh scala


######### ON MAC

cd /opt
sudo curl -LO https://archive.apache.org/dist/spark/spark-3.5.5/spark-3.5.5-bin-hadoop3.tgz
sudo tar -xzf spark-3.5.5-bin-hadoop3.tgz
sudo ln -sfn /opt/spark-3.5.5-bin-hadoop3 /opt/spark

export SPARK_HOME=/opt/spark
export PATH="$SPARK_HOME/bin:$PATH"
spark-submit --version
sudo sed -i '' 's/11-jre-slim-buster/11-jre-jammy/g' /opt/spark/kubernetes/dockerfiles/spark/Dockerfile
cd /root/sparkling-water/dist/build/zip/sparkling-water-3.46.0.2-1-3.5
<!-- sed -n '1,80p' /opt/spark/kubernetes/dockerfiles/spark/Dockerfile

grep -n "java_image_tag" -n /opt/spark/bin/docker-image-tool.sh | head -n 40 -->

<!-- grep -n "11-jre" -n /opt/spark/bin/docker-image-tool.sh | head -n 40 -->
sudo sed -i '' 's/java_image_tag=11-jre-slim-buster/java_image_tag=17-jammy/g' ./bin/build-kubernetes-images.sh

modify the Spark dockerfile
cat /opt/spark/kubernetes/dockerfiles/spark/Dockerfile
RUN set -ex && \
    apt-get update && \
    apt-get -y upgrade && \
    apt-get -y dist-upgrade && \
    ln -s /lib /lib64 && \

./gradlew clean dist -x check
cd dist/build/zip/sparkling-water-*/ 

## BUIlding also Spark with dependency override

cd /Users/michaelm/mygit/runtime-dockyard && curl -L -o spark-3.5.5.tgz https://archive.apache.org/dist/spark/spark-3.5.5/spark-3.5.5.tgz && tar -xzf spark-3.5.5.tgz

cd /Users/michaelm/mygit/runtime-dockyard/spark-3.5.5
cat > ./maven-settings.xml <<'EOF'
<settings>
  <mirrors>
    <mirror>
      <id>maven-central</id>
      <name>Maven Central</name>
      <url>https://repo.maven.apache.org/maven2</url>
      <mirrorOf>central</mirrorOf>
    </mirror>
  </mirrors>
</settings>
EOF

## Removing Derby jars from Spark images
We remove Derby from Spark Kubernetes images to avoid shipping the embedded Hive metastore.
Edit: /Users/michaelm/mygit/runtime-dockyard/spark-3.5.5/resource-managers/kubernetes/docker/src/main/dockerfiles/spark/Dockerfile
Add after `COPY jars /opt/spark/jars`:
  RUN rm -f /opt/spark/jars/derby-*.jar /opt/spark/jars/derbyshared-*.jar

cd /Users/michaelm/mygit/runtime-dockyard/spark-3.5.5
./dev/make-distribution.sh -Phadoop-3 -Pscala-2.12 -Pkubernetes -DskipTests \
  -Dhadoop.version=3.3.6 -Djetty.version=9.4.57.v20241219 -Divy.version=2.5.2 \
  -Dguava.version=32.0.1-jre -Dnetty.version=4.1.125.Final \
  -Dlibthrift.version=0.14.0 -Dzookeeper.version=3.7.2 \
  -Dprotobuf.version=3.25.5 -s ./maven-settings.xml -U

(.venv) [22/01/26 16:14:51] ➜  spark-3.5.5 export SPARK_HOME=/Users/michaelm/mygit/runtime-dockyard/spark-3.5.5/dist
export PATH="$SPARK_HOME/bin:$PATH"
spark-submit --version

# NOTE:
Derby is Apache Derby, a lightweight embedded SQL database (Java). In Spark, it’s mainly used as the embedded Hive metastore for local/dev setups. For production, most people use an external metastore DB (MySQL/Postgres), so Derby can often be removed or ignored if you’re not using the embedded metastore.

# build spark image

cd /Users/michaelm/mygit/runtime-dockyard/spark-3.5.5/dist
./bin/docker-image-tool.sh -t 3.5.5 -b java_image_tag=17-jammy build

# Build with gradle
./gradlew clean dist -x check
# Build Sparkling-water image

export SPARK_HOME=/Users/michaelm/mygit/runtime-dockyard/spark-3.5.5/dist && export PATH="$SPARK_HOME/bin:$PATH"

cd dist/build/zip/sparkling-water-*/
./bin/build-kubernetes-images.sh scala


########## hadoop 3.3.6

cd /Users/michaelm/mygit/runtime-dockyard/spark-3.5.5 && ./dev/make-distribution.sh -Phadoop-3 -Pscala-2.12 -Pkubernetes -DskipTests -Dhadoop.version=3.3.6 -Djetty.version=9.4.57.v20241219 -Divy.version=2.5.2 -Dguava.version=32.0.1-jre -Dnetty.version=4.1.125.Final -Dlibthrift.version=0.14.0 -Dzookeeper.version=3.7.2 -Dprotobuf.version=3.25.5 -s ./maven-settings.xml -U


########## build Spark 3.5 (branch-3.5) with security overrides

cd /Users/michaelm/mygit/runtime-dockyard/spark
git checkout branch-3.5
export MAVEN_OPTS="-Xss64m -Xmx2g -XX:ReservedCodeCacheSize=1g"

# Build Spark 3.5 (branch-3.5) distribution with dependency overrides
./dev/make-distribution.sh -Phadoop-3 -Pscala-2.12 -Pkubernetes -DskipTests \
  -Dhadoop.version=3.3.6 -Djetty.version=9.4.57.v20241219 -Divy.version=2.5.2 \
  -Dguava.version=32.0.1-jre -Dnetty.version=4.1.125.Final \
  -Dlibthrift.version=0.14.0 -Dzookeeper.version=3.7.2 -Dprotobuf.version=3.25.5 -s ./maven-settings.xml -U


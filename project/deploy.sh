#! /bin/sh

REPO="https://oss.sonatype.org/service/local/staging/deploy/maven2/"

VERSION="$1"
KEY="$2"
PASS="$3"

function deploy {
  BASE="$1"
  POM="$BASE.pom"
  FILES="$BASE.jar $BASE-javadoc.jar:javadoc $BASE-sources.jar:sources"

  for FILE in $FILES; do
    JAR=`echo "$FILE" | cut -d ':' -f 1`
    CLASSIFIER=`echo "$FILE" | cut -d ':' -f 2`

    if [ ! "$CLASSIFIER" = "$JAR" ]; then
      ARG="-Dclassifier=$CLASSIFIER"
    else
      ARG=""
    fi

    expect << EOF
set timeout 300
spawn mvn gpg:sign-and-deploy-file -Dkeyname=$KEY -DpomFile=$POM -Dfile=$JAR $ARG -Durl=$REPO -DrepositoryId=sonatype-nexus-staging
expect "GPG Passphrase:"
send "$PASS\r"
expect "BUILD SUCCESS"
expect eof
EOF
  done
}

SCALA_MODULES=".:play-dok"
SCALA_VERSIONS="2.10 2.11"
BASES=""

for V in $SCALA_VERSIONS; do
    for M in $SCALA_MODULES; do
        MP=`echo "$M" | cut -d ':' -f 1`
        MN=`echo "$M" | cut -d ':' -f 2`
        
        BASES="$BASES $MP/target/scala-$V/$MN"_$V-$VERSION
    done
done

for B in $BASES; do
  deploy "$B"
done

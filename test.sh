

export PATH_TEST=$(pwd)
export KAFKA_PATH=/tmp/micro/kafka_2.13-3.7.0
export KAFKA_PATH=/dfs/kafka_2.13-3.5.1
export KAFKA_SERVER=worker01:9092
export TOPIC_BASE=test_bd_microservices
export NUM_RECORDS=1000000
export NUM_SEGUNDOS=$((NUM_RECORDS/10000))

export URL=http://localhost
export URL=http://edge01

export JAVA21=/usr/lib/jvm/jdk-21-oracle-x64/bin/java
export PORTS="8090 8091 8092"
export LIST_THREADS="1 2 4 8 16"
export LIST_MODES="f l d6 s"

OPTION=$1

echo OPTION=$OPTION

if [ "$OPTION" == "" ] ; then
  echo "Falta el parametro: load|test|start|stop|check"
  echo "                   load  : load data into kafka"
  echo "                   test  : perform the tests"
  echo "                   start : start servers"
  echo "                   stop  : stop servers"
  echo "                   check : check servers"
  echo "                   describe : describe topics"
  echo "                   offset : offset of topics"
  exit
fi

if [ "$OPTION" == "load" ] ; then
  echo DELETING TOPICS ....
  $KAFKA_PATH/bin/kafka-topics.sh --bootstrap-server $KAFKA_SERVER --delete --topic "${TOPIC_BASE}_.*" 

  echo CREATING TOPICS ....
  for NN in $LIST_THREADS
  do
   
    $KAFKA_PATH/bin/kafka-topics.sh --bootstrap-server $KAFKA_SERVER --create --topic ${TOPIC_BASE}_in_$NN --partitions $NN
    $KAFKA_PATH/bin/kafka-topics.sh --bootstrap-server $KAFKA_SERVER --create --topic ${TOPIC_BASE}_out_$NN --partitions $NN

  done

  echo
  echo DESCRIBING TOPICS
  $KAFKA_PATH/bin/kafka-topics.sh --bootstrap-server $KAFKA_SERVER --describe --topic "${TOPIC_BASE}.*"

  echo
  echo LOADING EVERY TOPIC WITH $NUM_RECORDS
  for NN in $LIST_THREADS
  do

    echo java -jar $PATH_TEST/data-generator-1.0.0-jar-with-dependencies.jar -b $KAFKA_SERVER -n 10000 -s $NUM_SEGUNDOS -t ${TOPIC_BASE}_in_$NN
    java -jar $PATH_TEST/data-generator-1.0.0-jar-with-dependencies.jar -b $KAFKA_SERVER -n 10000 -s $NUM_SEGUNDOS -t ${TOPIC_BASE}_in_$NN

  done
fi
    
if [ "$OPTION" == "describe" ] ; then
  echo DESCRIBING TOPICS
  for NN in $LIST_THREADS
  do
    $KAFKA_PATH/bin/kafka-topics.sh --bootstrap-server $KAFKA_SERVER --describe --topic "${TOPIC_BASE}_.*_${NN}"
  done
fi

if [ "$OPTION" == "offset" ] ; then
  for NN in $LIST_THREADS
  do
    $KAFKA_PATH/bin/kafka-run-class.sh kafka.tools.GetOffsetShell --bootstrap-server $KAFKA_SERVER --topic ${TOPIC_BASE}_in_${NN} --time -1 | awk -F ":" '{
     topic=$1 ; parts=$2+1; s+=$3; }END { print topic, parts, s}'
  done
fi


if [ "$OPTION" == "start" ] ; then
  cd $PATH_TEST

  IFS=' ' read -r -a arrp <<< "$PORTS"


  echo Starting spring server on port ${arrp[0]}
  $JAVA21 -jar ws-spring-0.1.jar --server.port=${arrp[0]} > log.spring.log 2>&1 & echo $! > pid.spring
  sleep 3
  netstat -tulpn | grep :${arrp[0]}

  echo Starting jersey server on port ${arrp[1]}
  $JAVA21 -jar ws-jersey3-0.1-jar-with-dependencies.jar -p ${arrp[1]} -a 0.0.0.0 > log.jersey.log 2>&1 & echo $! > pid.jersey
  sleep 3
  netstat -tulpn | grep :${arrp[1]}

  echo Starting flask server on port ${arrp[2]}
  cd $PATH_TEST/ws-flask
  python3 ws-process.py ${arrp[2]} > ../log.flask.log 2>&1 & echo $! > ../pid.flask
  sleep 3
  netstat -tulpn | grep :${arrp[2]}

  cd $PATH_TEST

  sleep 5
  for PORT in $PORTS
  do
     echo Asking port $PORT
     curl $URL:$PORT/identity 2>/dev/null
     echo
  done
fi

if [ "$OPTION" == "check" ] ; then
  for PORT in $PORTS
  do
     echo Asking :  curl $URL:$PORT/identity 
     curl $URL:$PORT/identity 2>/dev/null
     echo
  done
fi

if [ "$OPTION" == "test" ] ; then
  cd $PATH_TEST/output
  for NN in $LIST_THREADS
  do
     curl $URL:$PORT/identity 2>/dev/null
     echo

     for PORT in $PORTS
     do
        for MODE in $LIST_MODES
        do
            timeout 600 java -jar ../kafka-streams-microservices-0.1-jar-with-dependencies.jar -b $KAFKA_SERVER -t ${TOPIC_BASE}_in_$NN -o ${TOPIC_BASE}_out_$NN -u $URL:$PORT -n $NN -m $MODE
        done
     done
  done
fi

if [ "$OPTION" == "stop" ] ; then
  cd $PATH_TEST
  kill $(cat pid.*)
  sleep 5
  ps -f $(cat pid.*)
  sleep 5
  kill -9 $(cat pid.*)
fi


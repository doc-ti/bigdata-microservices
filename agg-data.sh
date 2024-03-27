

echo exec regs tsmin tsmax millis ns_avg ns_p90 ns_p95 ns_p99 stdev

for DIR in micro*
do

  REGS=$(cat $DIR/* | wc -l)
  TSMIN=$(awk '{ print $1}' $DIR/* | sort -n | head -1)
  TSMAX=$(awk '{ print $1}' $DIR/* | sort -n | tail -1)
  DATA=$( awk '{ print $2}' $DIR/* | sort -n | awk -v regs=$REGS '{ s+=$1; s2+=($1)*($1)
if ( p90==0 && NR>=90*regs/100 ) p90 = $1
if ( p95==0 && NR>=95*regs/100 ) p95 = $1
if ( p99==0 && NR>=99*regs/100 ) p99 = $1
}END {print s/regs, p90 , p95, p99, sqrt(s2/regs - s*s/regs/regs)}' )

  echo $(hostname | cut -d. -f1) $DIR $REGS $TSMIN $TSMAX $((TSMAX-$TSMIN)) $DATA

done


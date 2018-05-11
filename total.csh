#!/bin/csh -f

if ($#argv != 4) then
    echo "Usage: $0 <min-M+S> <max-M+S> <inc-M+S> <HP>"
    exit
endif

setenv TOTALMIN $1
setenv TOTALMAX $2
setenv TOTALINC $3
setenv HONEYPOTS $4

setenv MACHINES 0
setenv SENSORS 0
setenv PERC 5

setenv TOTAL $TOTALMIN
while ($TOTAL <= $TOTALMAX)

    touch data.total-$TOTAL
    @ PERC = 5

    while ($PERC < 100)
	@ MACHINES = `echo $PERC \* $TOTAL / 100.0 | bc`
	@ SENSORS = $TOTAL - $MACHINES

	cat petri.template | sed -e "s/xxxHONEYPOTSxxx/$HONEYPOTS/" | sed -e "s/xxxSENSORSxxx/$SENSORS/" | sed -e "s/xxxMACHINESxxx/$MACHINES/" > petri.properties

	echo "$PERC $MACHINES $SENSORS" >> data.total-$TOTAL
#	java -cp ./classes petri.Main >> data.total-$TOTAL

	@ PERC = $PERC + 5
    end

    @ TOTAL = $TOTAL + $TOTALINC
end

#Env_PRM_NUM_HONEYPOTS=xxxHONEYPOTSxxx
#Env_PRM_NUM_SENSORS=xxxSENSORSxxx
#Env_PRM_NUM_MACHINES=xxxMACHINESxxx

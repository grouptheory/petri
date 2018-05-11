#!/bin/tcsh -f

setenv TOTALMIN $1
setenv TOTALMAX $2
setenv TOTALINC $3
setenv PERC_HONEYPOTS $4
setenv PERC_SENSORS $5
setenv PERC_MACHINES `echo 100.0 - $PERC_SENSORS -$PERC_HONEYPOTS | bc`

setenv MACHINES 0
setenv SENSORS 0
setenv HONEYPOTS 0


setenv NUMTRIALS 30
setenv NUMTRIALS_DOUBLE 30.0

rm -f output-$PERC_HONEYPOTS-$PERC_SENSORS
touch output-$PERC_HONEYPOTS-$PERC_SENSORS
echo "#honeypots $PERC_HONEYPOTS" >> output-$PERC_HONEYPOTS-$PERC_SENSORS
echo "#sensors $PERC_SENSORS" >> output-$PERC_HONEYPOTS-$PERC_SENSORS

setenv TOTAL $TOTALMIN
while ($TOTAL <= $TOTALMAX)

    @ SENSORS = `echo $PERC_SENSORS \* $TOTAL / 100.0 | bc`
    @ HONEYPOTS = `echo $PERC_HONEYPOTS \* $TOTAL / 100.0 | bc`
    @ MACHINES = `echo $PERC_MACHINES \* $TOTAL / 100.0 | bc`

    cat petri.template | sed -e "s/xxxHONEYPOTSxxx/$HONEYPOTS/" | sed -e "s/xxxSENSORSxxx/$SENSORS/" | sed -e "s/xxxMACHINESxxx/$MACHINES/" > petri.properties

    setenv TRIAL 1
    setenv TOTINF 0.0
    setenv TOTLEAD 0.0
    while ($TRIAL <= $NUMTRIALS)
	echo "Running Trial $TRIAL : $TOTAL : M=$MACHINES S=$SENSORS H=$HONEYPOTS"

	java -Xms64m -Xmx768m -cp ./classes petri.Main | grep -v "#" > tmp.out
	cat tmp.out

	setenv INF `cat tmp.out | sed -e 's/.*infected=//' | sed -e 's/X.*//'`
	setenv LEAD `cat tmp.out | sed -e 's/.*lead=//' | sed -e 's/Y.*//'`
	
	setenv TOTINF `echo scale=10\; $TOTINF + $INF | bc`
	setenv TOTLEAD `echo scale = 10\; $TOTLEAD + $LEAD | bc`
	@ TRIAL = $TRIAL + 1
    end
    @ TRIAL = 1
    setenv AVEINF `echo scale = 10\; $TOTINF / $NUMTRIALS_DOUBLE | bc`
    setenv AVELEAD `echo scale = 10\; $TOTLEAD / $NUMTRIALS_DOUBLE | bc`
    echo "$TOTAL\t$AVEINF\t$AVELEAD" >> output-$PERC_HONEYPOTS-$PERC_SENSORS

    @ TOTAL = $TOTAL + $TOTALINC
end

#Env_PRM_NUM_HONEYPOTS=xxxHONEYPOTSxxx
#Env_PRM_NUM_SENSORS=xxxSENSORSxxx
#Env_PRM_NUM_MACHINES=xxxMACHINESxxx

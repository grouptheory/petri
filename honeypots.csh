#!/bin/csh -f

if ($#argv != 4) then
    echo "Usage: $0 <min-HP> <max-HP> <inc-HP> <M+S>"
    exit
endif

setenv HPMIN $1
setenv HPMAX $2
setenv HPINC $3
setenv TOTAL $4

setenv MACHINES 0
setenv SENSORS 0

setenv PERC 5

setenv HP $HPMIN
while ($HP <= $HPMAX)

    touch data.hp-$HP
    @ PERC = 5

    while ($PERC < 100)
	@ MACHINES = `echo $PERC \* $TOTAL / 100.0 | bc`
	@ SENSORS = $TOTAL - $MACHINES

	cat petri.template | sed -e "s/xxxHONEYPOTSxxx/$HONEYPOTS/" | sed -e "s/xxxSENSORSxxx/$SENSORS/" | sed -e "s/xxxMACHINESxxx/$MACHINES/" > petri.properties

	echo "$PERC $MACHINES $SENSORS" >> data.hp-$HP
#	java -cp ./classes petri.Main >> data.hp-$HP

	@ PERC = $PERC + 5
    end

    @ HP = $HP + $HPINC
end

#Env_PRM_NUM_HONEYPOTS=xxxHONEYPOTSxxx
#Env_PRM_NUM_SENSORS=xxxSENSORSxxx
#Env_PRM_NUM_MACHINES=xxxMACHINESxxx

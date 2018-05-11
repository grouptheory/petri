
all:	*.java
	javac -d ./classes *.java */*.java

run:
	java -cp ./classes petri.Main

tidy:
	rm *~; rm */*~;

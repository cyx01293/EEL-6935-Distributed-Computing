JC = javac 
JAR = jcommon-1.0.23.jar:jfreechart-1.0.19.jar
JFLAGS = -g 
.SUFFIXES: .java .class
.java.class:
		$(JC) $(JFLAGS) $*.java

CLASSES = \
		SORT.java \
		SORTSERVICE.java \
		BROADCASTSERVER.java \
		BROADCASTCLIENT.java

default: classes
classes: $(CLASSES:.java=.class)

clean:
		$(RM) *.class
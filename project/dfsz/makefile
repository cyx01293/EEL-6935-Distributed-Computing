JC = javac 
JAR = jcommon-1.0.23.jar:jfreechart-1.0.19.jar
JFLAGS = -g 
.SUFFIXES: .java .class
.java.class:
		$(JC) $(JFLAGS) $*.java

CLASSES = \
		Storage.java   \
		SERVICE.java \
		DFSZSERVER.java \
		DFSZCLIENT.java 

default: classes
classes: $(CLASSES:.java=.class)

clean:
		$(RM) *.class
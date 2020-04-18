JC = javac 
JAR = .:/home/apache-zookeeper-3.6.0-bin/lib/zookeeper-3.6.0.jar
JFLAGS = -g -cp
.SUFFIXES: .java .class
.java.class:
		$(JC) $(JFLAGS) $(JAR) $*.java

CLASSES = \
		Storage.java \
		SERVICE.java \
		DFSSERVER.java \
		DFSCLIENT.java 
		

default: classes
classes: $(CLASSES:.java=.class)

clean:
		$(RM) *.class
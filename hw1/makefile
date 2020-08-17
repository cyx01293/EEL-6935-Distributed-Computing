JC = javac 
JAR = jcommon-1.0.23.jar:jfreechart-1.0.19.jar
JFLAGS = -g -cp
.SUFFIXES: .java .class
.java.class:
		$(JC) $(JFLAGS) $(JAR) $*.java

CLASSES = \
		TMAN.java 

default: classes
classes: $(CLASSES:.java=.class)

clean:
		$(RM) *.class
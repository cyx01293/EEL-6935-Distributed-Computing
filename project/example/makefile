JC = javac 
# JAR = .:/home/apache-zookeeper-3.6.0-bin/lib/zookeeper-3.6.0.jar:/home/apache-zookeeper-3.6.0-bin/lib/zookeeper-jute-3.6.0.jar
JAR = .:/Users/yixingchen/Applications/eclipse-workspace/DFSZ/apache-zookeeper-3.6.0-bin/lib/zookeeper-3.6.0.jar:/Users/yixingchen/Applications/eclipse-workspace/DFSZ/apache-zookeeper-jute-3.6.0-bin/lib/zookeeper-jute-3.6.0.jar
JFLAGS = -g -cp
.SUFFIXES: .java .class
.java.class:
		$(JC) $(JFLAGS) $(JAR) $*.java

CLASSES = \
		DataMonitor.java \
		Executor.java 
		

default: classes
classes: $(CLASSES:.java=.class)

clean:
		$(RM) *.class
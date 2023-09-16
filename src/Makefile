classes/Qdo.class: Qdo.java
	javac -cp "./classes;../lib/picocli-4.7.4.jar;../lib/picocli-codegen-4.7.4.jar" -d classes Qdo.java FileNavigator.java Task.java
qdo.jar: classes/Qdo.class
	jar -cvef Qdo qdo.jar classes/*
qdo: qdo.jar
	native-image ^ -cp "../lib/picocli-4.7.4.jar;./classes" --static -jar qdo.jar
clean:
	del -r classes
	rm qdo.jar

class: Qdo.java
	javac -cp "./classes;../lib/picocli-4.7.4.jar;../lib/picocli-codegen-4.7.4.jar" -d classes Qdo.java FileNavigator.java Task.java
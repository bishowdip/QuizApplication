#!/bin/bash
# Quiz Application Run Script

cd "$(dirname "$0")"

# Compile all Java files
echo "Compiling..."
javac -cp "lib/sqlite-jdbc-3.51.1.0.jar" -d bin src/*.java

if [ $? -eq 0 ]; then
    echo "Running Quiz Application..."
    java -cp "bin:lib/sqlite-jdbc-3.51.1.0.jar" QuizApp
else
    echo "Compilation failed!"
fi

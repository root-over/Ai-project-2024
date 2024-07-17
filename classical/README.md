1. Preparazione dell'Ambiente
   
Scarica la libreria pddl4j e spostala nella cartella lib:

wget http://pddl4j.imag.fr/repository/pddl4j/binaries/pddl4j-4.0.0.jar
mv pddl4j-4.0.0.jar lib/pddl4j-4.0.0.jar

Apri un terminale nella cartella classical.

2. Compilazione del Codice

Per compilare il codice, esegui il seguente comando:

javac -d classes -cp lib/pddl4j-4.0.0.jar src/planning/HeuristicEvaluation.java src/planning/Algoritmi.java

3. Esecuzione del Programma

Per eseguire il programma, utilizza il comando seguente:

java -cp classes:lib\pddl4j-4.0.0.jar planning.Algoritmi <weight> <timeout> <path_dominio> <path_problema> <tipo_ricerca> <euristica>

4. Regole per l'Esecuzione

    <weight> deve essere un valore maggiore o uguale a 0.
    <timeout> deve essere un valore positivo (in minuti).
    <tipo_ricerca> può essere uno dei seguenti:
        WEIGHTED_ASTAR
        ASTAR
        ENFORCED_HILL_CLIMBING
    <euristica> può essere uno dei seguenti:
        HEURISTIC_EVALUATION
        AJUSTED_SUM
        FAST_FORWARD

5. File PDDL

I file PDDL per il dominio e le istanze del problema devono essere collocati nella cartella pddl.

6. Esempi di Comandi

Per compilare:

javac -d classes -cp lib/pddl4j-4.0.0.jar src/planning/HeuristicEvaluation.java src/planning/Algoritmi.java

Per eseguire (esempio per istanza 1):

java -cp classes:lib/pddl4j-4.0.0.jar planning.Algoritmi 1.5 10 pddl/domain.pddl pddl/ist1.pddl WEIGHTED_ASTAR heuristicEvaluation
Altri esempi di esecuzione:

java -cp classes:lib/pddl4j-4.0.0.jar planning.Algoritmi 1.3 10 pddl/domain.pddl pddl/ist2.pddl WEIGHTED_ASTAR heuristicEvaluation

java -cp classes:lib/pddl4j-4.0.0.jar planning.Algoritmi 1.5 10 pddl/domain.pddl pddl/ist2.pddl WEIGHTED_ASTAR heuristicEvaluation

Segui questi passaggi per configurare correttamente l'ambiente e utilizzare il software in modo efficiente.

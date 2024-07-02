Per prima cosa, se non è già presente nella cartella lib, è necessario scaricare la libreria di pddl4j e spostarla nella cartella lib:

(WINDOWS)
 
curl -o pddl4j-4.0.0.jar http://pddl4j.imag.fr/repository/pddl4j/binaries/pddl4j-4.0.0.jar

move pddl4j-4.0.0.jar lib\pddl4j-4.0.0.jar


(LINUX)

wget http://pddl4j.imag.fr/repository/pddl4j/binaries/pddl4j-4.0.0.jar

mv pddl4j-4.0.0.jar lib/pddl4j-4.0.0.jar


Aprire un terminale nella cartella classical.

PER COMPILARE:

javac -d classes -cp lib/pddl4j-4.0.0.jar src/planning/PlanningHeuristic.java src/planning/MyASP.java

PER ESEGUIRE:

java -cp classes;lib\pddl4j-4.0.0.jar planning.MyASP <weight> <timeout> <path dominio> <path problema> <tipo ricerca> <euristica>

REGOLE:

- <weight> deve essere >= 0;
- <timeout> (in minuti) deve essere > 0;
- nel campo <tipo ricerca> sono ammessi i seguenti valori:
	- ANYTIME_ASTAR,
        - PARALLEL_ANYTIME_ASTAR,
        - WEIGHTED_ASTAR,
        - ASTAR, (in questo caso qualsiasi sia il valore di weight viene ignorato)
	- ENFORCED_HILL_CLIMBING
   ognuno dei quali rappresenta una delle possibili tipologie di ricerca da usare.
- nel campo <euristica> sono ammessi i seguenti valori:
	- PLANNING_HEURISTIC,
        - AJUSTED_SUM,
        - AJUSTED_SUM2,
        - AJUSTED_SUM2M,
        - COMBO,
        - MAX,
        - FAST_FORWARD,
        - SET_LEVEL,
        - SUM,
        - SUM_MUTEX;
   ognuno dei quali rappresenta una delle possibili euristiche da usare.

NB. Se si esegue in ambiente linux è opportuno modificare il formato dei path e nel comando d'esecuzione usare : e non ; dopo classes.

Nel caso del problema i file pddl del dominio e delle istanze sono nella cartella pddl.

Esempio di comandi:

(per compilare)

javac -d classes -cp lib/pddl4j-4.0.0.jar src/planning/PlanningHeuristic.java src/planning/MyASP.java

(per ist1)

**RICERCA**

java -cp classes:lib/pddl4j-4.0.0.jar planning.MyASP 1.5 10 pddl/domain.pddl pddl/ist1.pddl WEIGHTED_ASTAR PLANNING_HEURISTIC

**RICERCA QUALITATIVA**

java -cp classes:lib/pddl4j-4.0.0.jar planning.MyASP 1.2 10 pddl/domain.pddl pddl/ist1.pddl PARALLEL_ANYTIME_ASTAR PLANNING_HEURISTIC

(per ist2)

**RICERCA**

java -cp classes:lib/pddl4j-4.0.0.jar planning.MyASP 1.3 10 pddl/domain.pddl pddl/ist2.pddl WEIGHTED_ASTAR PLANNING_HEURISTIC

**RICERCA QUALITATIVA**

java -cp classes:lib/pddl4j-4.0.0.jar planning.MyASP 1.3 10 pddl/domain.pddl pddl/ist2.pddl PARALLEL_ANYTIME_ASTAR PLANNING_HEURISTIC

(per ist3)

**RICERCA**

java -cp classes:lib/pddl4j-4.0.0.jar planning.MyASP 1.5 10 pddl/domain.pddl pddl/ist2.pddl WEIGHTED_ASTAR PLANNING_HEURISTIC

**RICERCA QUALITATIVA**

java -cp classes:lib/pddl4j-4.0.0.jar planning.MyASP 1.3 10 pddl/domain.pddl pddl/ist2.pddl PARALLEL_ANYTIME_ASTAR PLANNING_HEURISTIC

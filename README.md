# Artificial Intelligence Project: Planning & Robotics

**Course:** Artificial Intelligence (2023/2024)
**Professors:** Prof. Francesco Scarcello, Ing. Antonio Bono

This repository contains the solution for the course assignment, focusing on PDDL modeling, classical planning, and robotic integration in an industrial scenario.

## Project Description

The project aims to design an intelligent system to orchestrate robotic agents in a manufacturing environment. The objective is to deliver boxes containing specific supplies (valves, bolts, tools) to workstations that request them.

The work is divided into three main tasks:

### Task 1: PDDL Modeling
Modeling a classical planning problem using the **PDDL 1.2** language.
* **Scenario:** Robotic agents move between connected locations to fill, transport, and deliver boxes.
* **Entities:** Workstations, Agents, Boxes, Contents.
* **Constraints:** Agents can only move between connected locations; boxes move together with the agent carrying them.

### Task 2: Classical Planning
Implementation of a search algorithm and custom heuristics to solve problem instances.
* **Technology:** Java language based on the **PDDL4J** library.
* **Instance 1:** A single agent delivers supplies from a central warehouse; no restrictions on the number of boxes.
* **Instance 2:** Introduction of capacity constraints. The agent uses a "carrier" with limited capacity and must plan multiple loads if necessary.
* **Metrics:** The system reports execution time (max 10 min), number of evaluated states, and memory usage.

### Task 3: Temporal Planning & Robotics
Integrating the model into a real software architecture.
* **Temporal Planning:** Extending the domain to include durative actions and concurrency.
* **PlanSys2/ROS2:** Executing the generated plan within the **PlanSys2** framework, simulating orchestration on a ROS2-based robotic system.

---

## Environment Setup

Follow these steps to configure the development environment for the Java planner (Task 2).

### 1. Downloading Dependencies

Download the `pddl4j` library and move it to the correct directory.
Open a terminal in the project root folder and run:
```bash
# Download the library
```bash
wget [http://pddl4j.imag.fr/repository/pddl4j/binaries/pddl4j-4.0.0.jar](http://pddl4j.imag.fr/repository/pddl4j/binaries/pddl4j-4.0.0.jar)

# Create the lib folder and move the jar file

mkdir -p lib
mv pddl4j-4.0.0.jar lib/pddl4j-4.0.0.jar

# Compiling the Code
javac -d classes -cp lib/pddl4j-4.0.0.jar src/planning/HeuristicEvaluation.java src/planning/Algoritmi.java

# Running the Planner
```
## Execution

To execute the program, use the following syntax:

```bash
java -cp classes:lib/pddl4j-4.0.0.jar planning.Algoritmi <weight> <timeout> <domain_path> <problem_path> <search_type> <heuristic>
```

Example:

```bash
java -cp classes;lib/pddl4j-4.0.0.jar planning.Algoritmi ...
```
---

## Parameters

| Argument         | Description                 | Constraints                 |
| ---------------- | --------------------------- | --------------------------- |
| `<weight>`       | Algorithm weight (for W-A*) | Value ≥ 0                   |
| `<timeout>`      | Max execution time          | Positive value (in minutes) |
| `<domain_path>`  | Path to PDDL Domain         | e.g. `pddl/domain.pddl`     |
| `<problem_path>` | Path to PDDL Problem        | e.g. `pddl/ist1.pddl`       |
| `<search_type>`  | Search algorithm            | See below                   |
| `<heuristic>`    | Heuristic function          | See below                   |

---

## Algorithms and Heuristics

### `<search_type>` (choose one)

* `WEIGHTED_ASTAR`
* `ASTAR`
* `ENFORCED_HILL_CLIMBING`

### `<heuristic>` (choose one)

* `HEURISTIC_EVALUATION`
* `AJUSTED_SUM`
* `FAST_FORWARD`

---

## PDDL File Structure

Ensure that the PDDL domain and problem instance files are located in the `pddl/` folder.

* `pddl/domain.pddl` – Generic domain
* `pddl/ist1.pddl` – Instance 1 (no carrier limits)
* `pddl/ist2.pddl` – Instance 2 (carrier limits)

---

## Execution Examples

### Example 1

**Solving Instance 1**
Algorithm: Weighted A*
Weight: 1.5
Timeout: 10 minutes

```bash
java -cp classes:lib/pddl4j-4.0.0.jar planning.Algoritmi 1.5 10 pddl/domain.pddl pddl/ist1.pddl WEIGHTED_ASTAR HEURISTIC_EVALUATION
```

---

### Example 2

**Solving Instance 2**
Algorithm: Weighted A*
Weight: 1.3
Timeout: 10 minutes

```bash
java -cp classes:lib/pddl4j-4.0.0.jar planning.Algoritmi 1.3 10 pddl/domain.pddl pddl/ist2.pddl WEIGHTED_ASTAR HEURISTIC_EVALUATION
```

---

### Example 3

**Solving Instance 2 (Higher Weight)**
Algorithm: Weighted A*
Weight: 1.5
Timeout: 10 minutes

```bash
java -cp classes:lib/pddl4j-4.0.0.jar planning.Algoritmi 1.5 10 pddl/domain.pddl pddl/ist2.pddl WEIGHTED_ASTAR HEURISTIC_EVALUATION
```


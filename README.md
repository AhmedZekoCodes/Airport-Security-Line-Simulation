# ✈️ Airport Security Discrete-Event Simulation

## Overview
This project is a **discrete-event simulation of an airport security system**, implemented in Java using object-oriented design and custom data structures. It models how passengers move through security lines, interact with security agents, undergo searches, and may be sent to secondary screening, arrested, or miss their flights based on timing and constraints. The simulation advances strictly through events rather than a global clock, producing deterministic and traceable output.

---

## Key Features
- Event-driven simulation architecture (no global clock)
- Custom event hierarchy (arrival, search start/end, interview start/end, agent shifts)
- Multiple security lines with dynamic closures and passenger rebalancing
- Security agents with shift limits and suspicion thresholds
- Secondary screening area with limited interview room capacity
- Tracks arrests, missed flights, and total passenger waiting time
- Produces detailed event logs and final summary reports

---

## Technical Highlights
- Implemented **custom data structures** (no Java collections):
  - Ordered event list (priority queue behavior)
  - Linked queues for security lines and waiting areas
- Strong use of **object-oriented principles**:
  - Inheritance and polymorphism
  - Abstract base classes
  - Safe runtime behavior checks
- Deterministic event ordering (by time, then passenger ID)
- Clear separation between simulation logic, domain models, and events

---

## Input
The simulation is driven by a text file that specifies:
- Number of security lines
- Number of secondary screening rooms
- Passenger and agent arrival data (arrival times, carry-ons, flight times, flags)

---

## Output
- Timestamped log of all events processed during the simulation
- Final summaries including:
  - Arrested passengers
  - Passengers who missed their flights
  - Average waiting time
  - Per-agent and per-line statistics

---

## How to Compile
```terminal
    javac *.java
```

## How to Run
    java Main

It will prompt you to enter the file name:
```terminal
    <filename>.txt
```

Example:
```terminal
    a3q1data1.txt
```

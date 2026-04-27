# AlgoVisualizer

An interactive, high-performance Data Structures, Algorithms, and Theory of Computation visualizer built completely in native **Java Swing**. This platform allows you to visually trace the real-time execution of code on arrays, graphs, and even infinite Turing Machine tapes without relying on web browsers or third-party engines. 

## 🚀 Features

- **Theory of Computation:** 
  - Watch a **Deterministic Finite Automaton (DFA)** validate sub-strings dynamically.
  - Trace an $A^nB^n$ **Turing Machine** execute over an infinite, automatically-centering virtual tape.
- **Data Structures:** Play, Pause, and Step-Through operations on Arrays, Stacks, Queues, Binary Search Trees, and Graphs.
- **Core Algorithms:** Visualize sorting (Merge, Quick, Bubble, etc.), searching (Linear, Binary), tree traversals, and pathfinding (Breadth-First Search, Depth-First Search, Dijkstra's Algorithm).
- **Interactive Graphs:** Drag and drop graph vertices in real-time. Edge directions, collision detection, and bidirectional mapping are rendered automatically using custom 2D geometrical transformations.
- **Premium UI:** Outfitted with **FlatLaf** for a beautiful, modern Dark Mode aesthetic that sheds the dated look of classical Java desktop applications.

## 🛠️ Tech Stack
- **Language**: Java
- **UI Framework**: Java Swing & Java2D
- **Look and Feel**: FlatLaf (Dark Mode)
- **Build Tool**: Gradle (Kotlin DSL)
- **Serialization**: GSON (for exporting/loading saved experiments)

## 📦 Prerequisites

You only need **Java Development Kit (JDK) 21 or later** installed on your machine to build and run this application. Gradle is bundled inside the project via the Gradle Wrapper, so you do not need to install it globally.

## 🏁 Building and Running

You can compile and launch the visualizer directly from your terminal within the root directory of this repository.

### On Windows
To compile the project:
```powershell
.\gradlew.bat build
```

To run the application:
```powershell
.\gradlew.bat run
```

### On macOS / Linux
To compile the project:
```bash
./gradlew build
```

To run the application:
```bash
./gradlew run
```

> **Note:** The included Gradle wrapper will automatically download the necessary dependencies (such as FlatLaf and Gson) upon its first execution.

## 🎮 How to Use
1. **Select an Algorithm:** Choose an algorithm from the left-hand sidebar navigation.
2. **Custom Input:** By default, sample data is bound to each application. To enter your own arrays, graph adjacency lists, or DFA/TM input strings, click **"Custom Input"**.
3. **Execution Controls:** Click **Start**. Use **Pause** and **Step** to trace the state mutations iteration-by-iteration, allowing for deep algorithmic inspection. Adjust the runtime speed using the slider at the bottom.
4. **Analytics:** Keep an eye on the Info Panel (right column) to watch live accumulation of comparisons, memory swaps, time complexity definitions, and execution durations.

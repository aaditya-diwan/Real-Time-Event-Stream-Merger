# Real-Time Event Stream Merger

Merges K sorted event streams into a single globally time-ordered stream using a min-heap. Implements and benchmarks Binary, d-ary, and Fibonacci heaps.

## Prerequisites

- Java 21+
- Maven 3.8+

## Build

```bash
cd event-stream-merger
mvn clean package
```

## Run Tests

```bash
mvn test
```

## Run Experiments

```bash
# Via Maven (no JAR needed)
mvn exec:java -Dexec.mainClass="com.streammerger.experiments.ExperimentRunner"
```

Results are written as CSV files under `results/`.

## Analyze Results

```bash
python3 scripts/analyze_results.py
```

## Heap Complexity

| Heap            | insert       | extractMin   | peekMin |
|-----------------|-------------|-------------|---------|
| BinaryHeap      | O(log n)    | O(log n)    | O(1)    |
| DAryHeap (d=4)  | O(log₄ n)   | O(4·log₄ n) | O(1)    |
| FibonacciHeap   | O(1) amort. | O(log n)    | O(1)    |

The d-ary heap with d=4 typically offers the best wall-clock throughput due to better cache locality. The Fibonacci heap has theoretically optimal amortized bounds but higher constant-factor overhead.

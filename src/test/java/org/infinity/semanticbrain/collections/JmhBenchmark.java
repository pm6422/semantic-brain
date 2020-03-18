package org.infinity.semanticbrain.collections;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Mode;

@BenchmarkMode(Mode.Throughput)
public class JmhBenchmark {

       @Benchmark
       @Fork(3)
       public static double benchmarkPerformanceCriticalComponent() {
             return 1L;
       }
}
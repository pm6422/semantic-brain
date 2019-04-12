//package org.infinity.semanticbrain.collections;
//
//import org.infinity.semanticbrain.dialog.entity.MatchedSlot;
//import org.openjdk.jmh.annotations.*;
//import org.openjdk.jmh.runner.Runner;
//import org.openjdk.jmh.runner.options.Options;
//import org.openjdk.jmh.runner.options.OptionsBuilder;
//
//import java.util.ArrayList;
//import java.util.HashSet;
//import java.util.List;
//import java.util.Set;
//import java.util.concurrent.TimeUnit;
//
//@BenchmarkMode(Mode.AverageTime)
//@OutputTimeUnit(TimeUnit.NANOSECONDS)
//@Warmup(iterations = 5)
//public class CollectionsBenchmark {
//
//    @State(Scope.Thread)
//    public static class MyState {
//        private Set<MatchedSlot>  employeeSet  = new HashSet<>();
//        private List<MatchedSlot> employeeList = new ArrayList<>();
//
//        private long iterations = 1000;
//
//        private MatchedSlot employee = MatchedSlot.of(1, "test", 1, 5);
//
//        @Setup(Level.Trial)
//        public void setUp() {
//            for (long i = 0; i < iterations; i++) {
//                employeeSet.add(MatchedSlot.of(1, "test2", 1, 6));
//                employeeList.add(MatchedSlot.of(1, "test2", 1, 6));
//            }
//            employeeList.add(employee);
//            employeeSet.add(employee);
//        }
//    }
//
//    @Benchmark
//    public boolean testArrayList(MyState state) {
//        return state.employeeList.contains(state.employee);
//    }
//
//    @Benchmark
//    public boolean testHashSet(MyState state) {
//        return state.employeeSet.contains(state.employee);
//    }
//
//    public static void main(String[] args) throws Exception {
//        Options options = new OptionsBuilder()
//                .include(CollectionsBenchmark.class.getSimpleName())
//                .forks(1).build();
//        new Runner(options).run();
//    }
//}
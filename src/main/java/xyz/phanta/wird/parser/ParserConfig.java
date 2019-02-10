package xyz.phanta.wird.parser;

import xyz.phanta.wird.parser.finalizer.ClassificationFinalizer;

import java.util.*;

public class ParserConfig {

    private final boolean debugPrint;
    private final ClassificationFinalizerSet finalizers;

    private ParserConfig(Builder builder) {
        this.debugPrint = builder.debugPrint;
        this.finalizers = builder.finalizers;
    }

    public boolean shouldDebugPrint() {
        return debugPrint;
    }

    public List<ClassificationFinalizer> getFinalizers(String classification, int bodyIndex) {
        return finalizers.get(classification, bodyIndex);
    }

    public static class Builder {

        private boolean debugPrint;
        private final ClassificationFinalizerSet finalizers = new ClassificationFinalizerSet();

        public Builder enableDebugPrint() {
            debugPrint = true;
            return this;
        }

        public Builder withFinalizers(String identifier, int bodyIndex, ClassificationFinalizer... finalizers) {
            this.finalizers.put(identifier, bodyIndex, finalizers);
            return this;
        }

        public Builder withFinalizers(String identifier, ClassificationFinalizer... finalizers) {
            this.finalizers.putMany(identifier, finalizers);
            return this;
        }

        public ParserConfig build() {
            return new ParserConfig(this);
        }

    }

    private static class ClassificationFinalizerSet {

        private final Map<String, List<List<ClassificationFinalizer>>> classBodyMapping = new HashMap<>();
        private final Map<String, List<ClassificationFinalizer>> classGlobalMapping = new HashMap<>();

        private List<ClassificationFinalizer> get(String identifier, int bodyIndex) {
            List<ClassificationFinalizer> globalFinalizers = classGlobalMapping.getOrDefault(identifier, Collections.emptyList());
            List<List<ClassificationFinalizer>> bodyMapping = classBodyMapping.get(identifier);
            if (bodyMapping == null || bodyIndex >= bodyMapping.size()) return globalFinalizers;
            List<ClassificationFinalizer> finalizers = bodyMapping.get(bodyIndex);
            return finalizers != null ? new ConcatenatedListView<>(finalizers, globalFinalizers) : globalFinalizers;
        }

        private void put(String identifier, int bodyIndex, ClassificationFinalizer[] finalizers) {
            List<List<ClassificationFinalizer>> bodyMapping =
                    classBodyMapping.computeIfAbsent(identifier, k -> new ArrayList<>(bodyIndex));
            while (bodyMapping.size() <= bodyIndex) bodyMapping.add(null);
            List<ClassificationFinalizer> finalizerList = bodyMapping.get(bodyIndex);
            if (finalizerList == null) {
                finalizerList = new ArrayList<>();
                bodyMapping.set(bodyIndex, finalizerList);
            }
            finalizerList.addAll(Arrays.asList(finalizers));
        }

        private void putMany(String identifier, ClassificationFinalizer[] finalizers) {
            classGlobalMapping.computeIfAbsent(identifier, k -> new ArrayList<>()).addAll(Arrays.asList(finalizers));
        }

    }

    private static class ConcatenatedListView<T> extends AbstractList<T> {

        private final List<T> left, right;

        private ConcatenatedListView(List<T> left, List<T> right) {
            this.left = left;
            this.right = right;
        }

        @Override
        public T get(int index) {
            int partition = left.size();
            return index >= partition ? right.get(index - partition) : left.get(index);
        }

        @Override
        public int size() {
            return left.size() + right.size();
        }

    }

}

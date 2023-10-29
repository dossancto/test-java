package org.example.lib;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class Box {
    public static <P> BoxContent<P> from(P initialValue){
        return new BoxContent<P>(initialValue);
    }

    public interface RunBox<R> { R run(); }
    public interface RunBoxWithP<P> { void run(P param); }

    public interface RunBoxWithReturn<T, R> { R run(T value) throws Exception; }
    public static class BoxContent<T>
    {
        private final RunBox<T> theValue;

        public BoxContent(T value){
            theValue = () -> value;
        }

        private BoxContent(RunBox<T> input){
            theValue = input;
        }

        public <R> BoxContent<R> then(RunBoxWithReturn<T, R> input){
            RunBox f = () -> {
                try {
                    return input.run(theValue.run());
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            };
            return new BoxContent<R>(f);
        }

        public <R> BoxContent<R> then(RunBoxWithReturn<T, R> input, RunBoxWithP<Exception> ex){
            RunBox f = () -> {
                try {
                    return input.run(theValue.run());
                } catch (Exception e) {
                    ex.run(e);
                    return null;
                }
            };
            return new BoxContent<R>(f);
        }

        public void thenFinal(RunBoxWithP<T> input){
            var val = theValue.run();
            if(val == null) return;

            input.run(val);
        }

        public <R, S> BoxContent<List<R>> each(RunBoxWithReturn<S, R> function) throws Exception {
            List<R> mappedList = new ArrayList<>();

            var value = theValue.run();
            if(value == null) return new BoxContent<>(null);

            if (!(value instanceof List<?>)){
                throw new Exception("Isso não é uma lista " + theValue);
            }

            var listVal = (List<S>) value;

            for(var a : listVal){
                mappedList.add(function.run(a));
            }

            return new BoxContent<>(mappedList);
        }

        public <S> void eachFinal(RunBoxWithP<S> function) throws Exception {
            each((p) -> {
                function.run((S) p);

                return 0;
            });
        }
        public <S> void eachFinalAsync(RunBoxWithP<S> function){
            new Fetcher().async(() -> {
                try {
                    eachFinal(function);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
        }

        public <S> void eachFinalAsync(RunBoxWithP<S> function, RunBoxWithP<Exception> ex){
            new Fetcher().async(() -> {
                try {
                    eachFinal(function);
                } catch (Exception e) {
                    ex.run(e);
                }
            });
        }

        public T unwrap(){
            return theValue.run();
        }

        @Override
        public String toString() {
            return unwrap().toString();
        }
    }
}


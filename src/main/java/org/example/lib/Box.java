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

        public <R> BoxContent<R> then(RunBoxWithReturn<T, R> input) {
            return then(input, null);
        }

        public <R> BoxContent<R> then(RunBoxWithReturn<T, R> input, RunBoxWithP<Exception> ex){
            return new BoxContent<R>(() -> {
                try {
                    return input.run(theValue.run());
                } catch (Exception e) {
                    if(ex == null){
                        throw new RuntimeException(e);
                    }

                    ex.run(e);
                    return null;
                }
            });
        }

        public void thenFinal(RunBoxWithP<T> input){
            var val = theValue.run();
            if(val == null) return;

            input.run(val);
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

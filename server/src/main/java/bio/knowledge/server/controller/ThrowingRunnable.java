package bio.knowledge.server.controller;

@FunctionalInterface
public interface ThrowingRunnable {
    void run() throws Exception;
}

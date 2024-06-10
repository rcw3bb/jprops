package xyz.ronella.tool.jprops;

/**
 * The Processor interface provides a contract for classes that will implement a specific process.
 * Implementing classes must provide an implementation for the process method.
 *
 * @author Ron Webb
 * @since 1.0.0
 */
public interface Processor {

    /**
     * The process method is responsible for executing the specific process logic.
     * Implementing classes must provide a concrete implementation for this method.
     */
    void process();

}

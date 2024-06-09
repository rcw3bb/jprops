/**
 * This is module-info.java file for the module xyz.ronella.util.jprops
 *
 * @author Ron Webb
 * @since 1.0.0
 */
module xyz.ronella.util.jprops {

    requires org.slf4j;
    requires ch.qos.logback.classic;

    requires org.apache.commons.cli;
    requires java.scripting;
    requires java.naming;

    requires xyz.ronella.casual.trivial;
    requires xyz.ronella.logging.logger.plus;
}
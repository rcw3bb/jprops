package xyz.ronella.util.jprops.impl;

import xyz.ronella.trivial.handy.RegExMatcher;
import xyz.ronella.util.jprops.Processor;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.LinkedHashMap;
import java.util.Optional;
import java.util.Scanner;

public class DuplicateProcessor implements Processor {

    public static Long serialVersionUID = 1L;

    @Override
    public void process(final File props) {
        try(final var fileReader = new Scanner(props)) {
            final var pairs = new LinkedHashMap<String, String>();
            while(fileReader.hasNextLine()) {
                final var rawLine = fileReader.nextLine();
                RegExMatcher.find("^(\\s*[a-zA-Z_].*?)=(.*)$", rawLine, ___matcher -> {
                    final var key = ___matcher.group(1);
                    final var value = ___matcher.group(2);
                    final var pair = Optional.of(pairs.get(key));



                    System.out.println(">>>" + ___matcher.group(1) + "," + ___matcher.group(2));
                });
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static record MetaData(String value, int count) {}
}

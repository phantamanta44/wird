package xyz.phanta.wird;

import xyz.phanta.wird.parser.Grammar;
import xyz.phanta.wird.parser.Parser;
import xyz.phanta.wird.parser.ParserConfig;
import xyz.phanta.wird.parsetree.ParseTreeParentNode;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.stream.Collectors;

public class Main {

    public static void main(String[] args) throws IOException {
        if (args.length == 0) {
            printHelp();
        } else {
            switch (args[0].toLowerCase()) {
                case "check":
                    if (args.length != 2) {
                        printHelp();
                    } else {
                        System.out.println(readGrammar(args[1]));
                    }
                    break;

                case "parse":
                    if (args.length < 4) {
                        printHelp();
                    } else {
                        System.out.println(readParser(args[1], args[2], false)
                                .parse(Arrays.stream(args).skip(3).collect(Collectors.joining(" "))));
                    }
                    break;

                case "dparse":
                    if (args.length < 4) {
                        printHelp();
                    } else {
                        System.out.println(readParser(args[1], args[2], true)
                                .parse(Arrays.stream(args).skip(3).collect(Collectors.joining(" "))));
                    }
                    break;

                case "read":
                    if (args.length < 4) {
                        printHelp();
                    } else {
                        String text = readFile(args[3]);
                        System.out.println(readParser(args[1], args[2], false).parse(text));
                    }
                    break;

                case "dread":
                    if (args.length < 4) {
                        printHelp();
                    } else {
                        String text = readFile(args[3]);
                        System.out.println(readParser(args[1], args[2], true).parse(text));
                    }
                    break;

                case "generate":
                    System.out.println("Not implemented!");
                    break;

                default:
                    printHelp();
                    break;
            }
        }
    }

    private static Parser readParser(String file, String root, boolean debug) throws IOException {
        Grammar grammar = readGrammar(file);
        return debug ? grammar.newParser(root, new ParserConfig.Builder().enableDebugPrint().build()) : grammar.newParser(root);
    }

    private static Grammar readGrammar(String file) throws IOException {
        return Grammar.create(readFile(file));
    }

    private static String readFile(String file) throws IOException {
        return new String(Files.readAllBytes(Paths.get(file)), StandardCharsets.UTF_8);
    }

    private static void printHelp() {
        System.out.println("Usage:");
        System.out.println("wird check <grammar-file>");
        System.out.println("wird parse <grammar-file> <root> <text>");
        System.out.println("wird dparse <grammar-file> <root> <text>");
        System.out.println("wird read <grammar-file> <root> <test-file>");
        System.out.println("wird dread <grammar-file> <root> <test-file>");
        System.out.println("wird generate <grammar-file> <output-file>");
    }

}

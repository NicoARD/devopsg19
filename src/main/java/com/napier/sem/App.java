package com.napier.sem;

/**
 * Main application class for SEM Methods project
 * Includes database connectivity testing
 */
public class App {
    
    public static void main(String[] args) {
        System.out.println("SEM Methods Application Starting...");
        System.out.println("=======================================");
        
        // Parse command line arguments
        boolean runDbTests = false;
        boolean showHelp = false;
        
        for (String arg : args) {
            switch (arg.toLowerCase()) {
                case "--test-db":
                case "-t":
                    runDbTests = true;
                    break;

                default:
                    System.out.println("WARNING: Unknown argument: " + arg);
                    showHelp = true;
                    break;
            }
        }
        
        if (showHelp) {
            printUsage();
            return;
        }
        
        if (runDbTests) {
            System.out.println("Testing database connectivity...");
            
            // Test database connectivity
            boolean dbConnected = DatabaseTest.testDatabaseConnection();
            
            if (dbConnected) {
                System.out.println("\nAll systems operational!");
                System.out.println("Application ready for development");
            } else {
                System.out.println("\nDatabase connectivity issues occurred");
                System.exit(1);
            }
            
            // Gracefully close database connections
            DatabaseConfig.closeDataSource();
            System.out.println("\nApplication completed successfully!");
        } else {
            System.out.println("Application started without database testing");
            System.out.println("TIP: Use --test-db or -t to run database connectivity tests");
            System.out.println("TIP: Use --help or -h for usage information");
        }
    }
    
    /**
     * Print usage information
     */
    private static void printUsage() {
        System.out.println("\nUsage: java -jar seMethods-1.0-SNAPSHOT.jar [OPTIONS]");
        System.out.println("\nOptions:");
        System.out.println("  --test-db, -t    Run database connectivity tests");
        System.out.println("  --help, -h       Show this help message");
        System.out.println("\nExamples:");
        System.out.println("  java -jar seMethods-1.0-SNAPSHOT.jar --test-db");
        System.out.println("  java -jar seMethods-1.0-SNAPSHOT.jar -t");
        System.out.println("  docker run --rm devopsg19-app --test-db");
    }
}
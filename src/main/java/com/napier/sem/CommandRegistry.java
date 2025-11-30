package com.napier.sem;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Registry for managing commands with automatic command discovery
 */
public class CommandRegistry {
    
    private static final Map<String, ICommand> commands = new HashMap<>();
    private static final String COMMANDS_PACKAGE = "com.napier.sem.commands";
    
    /**
     * Initialize and register all available commands automatically.
     * Scans the commands package and discovers all ICommand implementations.
     */
    public static void initializeCommands() {
        try {
            List<Class<?>> commandClasses = findAllCommandClasses(COMMANDS_PACKAGE);
            
            for (Class<?> clazz : commandClasses) {
                if (ICommand.class.isAssignableFrom(clazz) && 
                    !clazz.isInterface() && 
                    !Modifier.isAbstract(clazz.getModifiers())) {
                    try {
                        ICommand command = (ICommand) clazz.getDeclaredConstructor().newInstance();
                        registerCommand(command);
                    } catch (Exception e) {
                        System.err.println("  Failed to instantiate command: " + clazz.getName() + " - " + e.getMessage());
                    }
                }
            }
            
            System.out.println("  Total commands registered: " + commands.size());
            
        } catch (Exception e) {
            System.err.println("  Error during command discovery: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Find all classes in the specified package and its subpackages
     * @param packageName Package to scan
     * @return List of classes found
     */
    private static List<Class<?>> findAllCommandClasses(String packageName) throws Exception {
        List<Class<?>> classes = new ArrayList<>();
        String path = packageName.replace('.', '/');
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        Enumeration<URL> resources = classLoader.getResources(path);
        
        while (resources.hasMoreElements()) {
            URL resource = resources.nextElement();
            String protocol = resource.getProtocol();
            
            if ("file".equals(protocol)) {
                // Running from IDE or extracted files
                classes.addAll(findClassesInDirectory(new File(resource.getFile()), packageName));
            } else if ("jar".equals(protocol)) {
                // Running from JAR file
                classes.addAll(findClassesInJar(resource, path));
            }
        }
        
        return classes;
    }
    
    /**
     * Find classes in a directory (for IDE/development environment)
     * @param directory Directory to scan
     * @param packageName Package name
     * @return List of classes found
     */
    private static List<Class<?>> findClassesInDirectory(File directory, String packageName) {
        List<Class<?>> classes = new ArrayList<>();
        
        if (!directory.exists()) {
            return classes;
        }
        
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    classes.addAll(findClassesInDirectory(file, packageName + "." + file.getName()));
                } else if (file.getName().endsWith(".class")) {
                    String className = packageName + '.' + file.getName().substring(0, file.getName().length() - 6);
                    try {
                        classes.add(Class.forName(className));
                    } catch (ClassNotFoundException e) {
                        System.err.println("  Class not found: " + className);
                    }
                }
            }
        }
        
        return classes;
    }
    
    /**
     * Find classes in a JAR file (for production environment)
     * @param resource JAR resource URL
     * @param path Package path
     * @return List of classes found
     */
    private static List<Class<?>> findClassesInJar(URL resource, String path) {
        List<Class<?>> classes = new ArrayList<>();
        String jarPath = resource.getPath().substring(5, resource.getPath().indexOf("!"));
        
        try (JarFile jar = new JarFile(jarPath)) {
            Enumeration<JarEntry> entries = jar.entries();
            
            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                String name = entry.getName();
                
                if (name.startsWith(path) && name.endsWith(".class")) {
                    String className = name.substring(0, name.length() - 6).replace('/', '.');
                    try {
                        classes.add(Class.forName(className));
                    } catch (ClassNotFoundException e) {
                        System.err.println("  Class not found: " + className);
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("  Error reading JAR file: " + e.getMessage());
        }
        
        return classes;
    }

    /**
     * Register a command using its execution command
     * @param command Command implementation
     */
    public static void registerCommand(ICommand command) {
        commands.put(command.getExcecutionCommand().toLowerCase(), command);
    }
    
    /**
     * Register a command with a specific name (for backwards compatibility or aliasing)
     * @param name Command name
     * @param command Command implementation
     * @deprecated Use {@link #registerCommand(ICommand)} instead to use the command's execution command
     */
    @Deprecated
    public static void registerCommand(String name, ICommand command) {
        commands.put(name.toLowerCase(), command);
    }
    
    /**
     * Get a command by name
     * @param name Command name
     * @return Command implementation or null if not found
     */
    public static ICommand getCommand(String name) {
        return commands.get(name.toLowerCase());
    }
    
    /**
     * Check if a command is registered
     * @param name Command name
     * @return true if command exists
     */
    public static boolean hasCommand(String name) {
        return commands.containsKey(name.toLowerCase());
    }
    
    /**
     * Get all registered commands
     * @return Map of command names to implementations
     */
    public static Map<String, ICommand> getAllCommands() {
        return new HashMap<>(commands);
    }

}
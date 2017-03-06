package com.velti.template.utils;

import com.velti.template.core.exceptions.TestInterruptException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;



public class GeneralUtils {
    private static final Logger logger = LoggerFactory.getLogger(GeneralUtils.class);
    private static String os = System.getProperty("os.name").toLowerCase();
    private static String rootFolder;


    /**
     * Sleeps for some time
     *
     * @param milliseconds time to sleep
     */
    public static void sleep(long milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            // do nothing
        }
    }

    /**
     * Returns the path to the folder from which the application has been run.
     * The path is returned without tailing slash
     *
     * @return path to the folder from which the application has been run
     */
    public static String getRootFolder() {
        if (rootFolder != null) {
            return rootFolder;
        }
        // get path from the class loader for any object
        String path = "".getClass().getResource("/").getPath();
        // On Windows the path returns as "/<Disk>:/<folders>" - so weed to remove first slash
        if (path.contains(":") && path.startsWith("/")) {
            path = path.replaceFirst("/", "");
        }
        if (path.endsWith("/")) {
            path = path.substring(0, path.length() - 1);
        }
        return path;
    }

    /**
     * Gets the files and dirs hierarchy of specified folder on local machine
     *
     * @param path to the folder on local machine
     * @return List<String> with paths relative to specified folder
     */
    public static List<String> listHierarchy(String path) {
        List<String> list = new ArrayList<String>();
        File[] childFiles = new File(path).listFiles();
        if (childFiles == null) {
            return list;
        }
        for (File file : childFiles) {
            list.addAll(listHierarchyRecursive(file, ""));
        }
        return list;
    }

    private static List<String> listHierarchyRecursive(File currentFile, String path) {
        String currentPath = ("".equals(path) ? "" : path + "/") + currentFile.getName();
        List<String> list = new ArrayList<String>();
        if (currentFile.isDirectory()) {
            list.add(currentPath + "/");
            for (File file : currentFile.listFiles()) {
                list.addAll(listHierarchyRecursive(file, currentPath));
            }
        } else {
            list.add(currentPath);
        }
        return list;
    }

    /**
     * Gets the name of file
     *
     * @param filePath path fo the file
     * @return file name
     */
    public static String getFileName(String filePath) {
        int lastIndex = filePath.contains("/") ? filePath.lastIndexOf("/") : filePath.lastIndexOf("\\");
        return filePath.substring(lastIndex + 1);
    }

    /**
     * Close the passed object (usually it's stream)
     *
     * @param stream a stream to close
     */
    public static void closeStream(Closeable stream) {
        if (stream != null) {
            try {
                stream.close();
            } catch (IOException e) {
                logger.warn("Cannot close {} stream ", stream);
            }
        }
    }

    /**
     * Deletes folder recursively
     *
     * @param file - File object to deleteRecursive
     * @return true if folder is deleted
     */
    public static boolean delete(File file) {
        try {
            deleteRecursive(file);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private static void deleteRecursive(File file) {
        if (file.isDirectory()) {
            for (File c : file.listFiles())
                deleteRecursive(c);
        }
        file.delete();
    }

    /**
     * Returns true if current OS is unix-based OS
     *
     * @return true if current OS is unix-based OS
     */
    public static boolean isUnix() {
        return os.contains("nix") || os.contains("nux") || os.contains("aix");
    }

    /**
     * Returns true if current OS is MacOS
     *
     * @return true if current OS is MacOS
     */
    public static boolean isMac() {
        return os.contains("mac");
    }

    /**
     * Returns true if current OS is Windows
     *
     * @return true if current OS is Windows
     */
    public static boolean isWin() {
        return (os.indexOf("win") >= 0);
    }

    /**
     * Writes the data from the input stream to the output stream
     *
     * @param inputStream  input stream
     * @param outputStream output stream
     * @throws IOException
     */
    public static void writeFromInputToOutputStream(InputStream inputStream, OutputStream outputStream) throws IOException {
        byte[] buffer = new byte[4096 * 4];
        int readBytes;
        while (true) {
            readBytes = inputStream.read(buffer);
            if (readBytes == -1) {
                return;
            }
            // for last read iteration we should write only read number of bytes
            if (readBytes < buffer.length) {
                buffer = Arrays.copyOf(buffer, readBytes);
            }
            outputStream.write(buffer);
        }
    }

    /**
     * Reads the file content and returns it as a string
     *
     * @param file file path
     * @return text from a file
     */
    public static String readFromFile(String file) {
        try {
            return readInputStream(new FileInputStream(file));
        } catch (FileNotFoundException e) {
            throw new TestInterruptException("There is no '" + file + "' file", e);
        }
    }

    /**
     * Reads data from input stream and returns it as a string
     *
     * @param inputStream input stream
     * @return text from input stream
     */
    public static String readInputStream(InputStream inputStream) {
        return readInputStream(inputStream, 0);
    }

    /**
     * Reads data from input stream and returns it as a string
     *
     * @param inputStream input stream
     * @param bytesToSkip bytes to skip
     * @return text from input stream
     */
    public static String readInputStream(InputStream inputStream, long bytesToSkip) {
        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder builder = new StringBuilder();
        String line;
        try {
            if (bytesToSkip != 0) {
                long skippedBytes = inputStream.skip(bytesToSkip);
                if (skippedBytes != bytesToSkip) {
                    logger.warn("We expect that there should be {} bytes skipped, instead it was {} bytes skipped", bytesToSkip, skippedBytes);
                }
            }
            while ((line = br.readLine()) != null) {
                builder.append(line).append("\n");
            }
        } catch (IOException e) {
            throw new TestInterruptException("An exception occurred", e);
        } finally {
            closeStream(br);
        }
        return builder.toString();
    }

    /**
     * Gets the line from file that contains specified matchString
     *
     * @param filePath    file path
     * @param matchString String to match
     * @return String
     */
    public static String getMatchLineFromFile(String filePath, String matchString) {
        LineIterator it = null;
        try {
            it = FileUtils.lineIterator(new File(filePath));
            while (it.hasNext()) {
                String line = it.nextLine();
                if (line.contains(matchString)) {
                    return line;
                }
            }
        } catch (IOException e) {
            throw new TestInterruptException("Cannot work with file " + filePath, e);
        } finally {
            LineIterator.closeQuietly(it);
        }
        return "";
    }
}

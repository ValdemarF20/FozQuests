package cc.valdemar.foz.fozquests.utils;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Path;

public class ResourceUtils {

    /**
     * Use this method when using velocity, because JavaPlugin is not a thing there
     *
     * @param loader       ClassLoader to load the resource from
     * @param dataPath     The data directory path at runtime
     * @param resourcePath The path to the resource within the jar
     * @return File for the data file at runtime
     */
    public static File saveResource(ClassLoader loader, Path dataPath, String resourcePath) {
        if (resourcePath == null || resourcePath.isEmpty()) {
            throw new IllegalArgumentException("ResourcePath cannot be null or empty");
        }

        resourcePath = resourcePath.replace('\\', '/');
        InputStream in = getResource(loader, resourcePath);
        if (in == null) {
            throw new IllegalArgumentException("The embedded resource '" + resourcePath + "' cannot be found");
        }

        File outFile = new File(dataPath.resolve(resourcePath).toString());
        int lastIndex = resourcePath.lastIndexOf('/');
        File outDir = new File(dataPath.resolve(resourcePath.substring(0, Math.max(lastIndex, 0))).toString());

        if (!outDir.exists()) {
            outDir.mkdirs();
        }

        try {
            if (!outFile.exists()) {
                OutputStream out = new FileOutputStream(outFile);
                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
                out.close();
                in.close();
            }
        } catch (IOException e) {
            throw new RuntimeException("Could not save " + outFile.getName() + " to " + outFile, e);
        }

        return outFile;
    }

    /**
     * Use this method when using velocity, because JavaPlugin is not a thing there
     * @param loader ClassLoader to load the resource from
     * @param filename The name of the resource to load
     * @return The InputStream of the resource
     */
    @Nullable
    public static InputStream getResource(ClassLoader loader, @NotNull String filename) {
        try {
            URL url = loader.getResource(filename);

            if (url == null) {
                return null;
            }

            URLConnection connection = url.openConnection();
            connection.setUseCaches(false);
            return connection.getInputStream();
        } catch (IOException ex) {
            return null;
        }
    }
}

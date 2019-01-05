package info.golushkov.music.sort;

import com.google.common.collect.Lists;

import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

import static java.util.stream.Collectors.toList;

public class Main {

    private static boolean isRun(List<Queue<File>> tracks) {
        for (Queue<File> t : tracks) {
            if (t.isEmpty()) return false;
        }
        return true;
    }

    private static void copyFile(File source, File dest) throws IOException {
        try (InputStream is = new FileInputStream(source);
             OutputStream os = new FileOutputStream(dest)) {

            byte[] buffer = new byte[1024];
            int length;
            while ((length = is.read(buffer)) > 0) {
                os.write(buffer, 0, length);
            }
        }
    }

    public static void main(String[] args) {
        File root = new File("music");
        File[] dirs = root.listFiles(File::isDirectory);
        List<File> outTracks = new ArrayList<>();

        List<Queue<File>> tracks = Arrays.stream(dirs)
                .map(f -> f.listFiles(File::isFile))
                .map(Arrays::asList)
                .map(Lists::reverse)
                .map(ConcurrentLinkedQueue::new)
                .collect(toList());

        int c = 1;
        while (isRun(tracks)) {
            for (Queue<File> t : tracks) {
                for (int j = 0; j < c; j++) {
                    if (!t.isEmpty()) {
                        outTracks.add(t.remove());
                    }
                }
            }
        }

        File resultDir = new File("result");
        if (resultDir.exists()) {
            Arrays.stream(resultDir.listFiles()).forEach(File::delete);
            resultDir.delete();
        }
        resultDir.mkdir();

        for (int j = 0; j < outTracks.size(); j++) {
            File src = outTracks.get(j);
            String fileName = String.format("%04d_%s", j, src.getName());
            File newFile = new File(resultDir, fileName);
            try {
                copyFile(src, newFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}

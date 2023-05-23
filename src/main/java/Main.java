import io.lindstrom.m3u8.model.MediaPlaylist;
import io.lindstrom.m3u8.model.MediaSegment;
import io.lindstrom.m3u8.model.SegmentKey;
import io.lindstrom.m3u8.parser.MediaPlaylistParser;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {

    public static String url = "https://hls2.videos.sproutvideo.com/7f4566d903d2c1cb99d623cc0496f2c0/d388cd09256fb83ea104610e778bed0e/video/";
    public static String params = "?Policy=eyJTdGF0ZW1lbnQiOlt7IlJlc291cmNlIjoiaHR0cHM6Ly9obHMyLnZpZGVvcy5zcHJvdXR2aWRlby5jb20vN2Y0NTY2ZDkwM2QyYzFjYjk5ZDYyM2NjMDQ5NmYyYzAvZDM4OGNkMDkyNTZmYjgzZWExMDQ2MTBlNzc4YmVkMGUvKi5tM3U4P3Nlc3Npb25JRD0zNmI4M2JkNi0wZDIwLTRiMDgtOGZiMi1iZmExNmY0Njk5MmEiLCJDb25kaXRpb24iOnsiRGF0ZUxlc3NUaGFuIjp7IkFXUzpFcG9jaFRpbWUiOjE2ODQ4NzczMjN9fX1dfQ__&Signature=UE9vHdNaykD6WZsxrnsD-xyB8hZKDHOxIQf79hpdZZx0cl0KeGMzjDKJIy-y1vxLo09k6m6aclYdJ2cb~9wYYge06IPDHyf5SkjTP0jiXNcDSqBJ1ulKhvYh0MGo-Oi8j3gZHmCfTBqVFA1wZzDlZ14jkDmrFAXqw4v7G6QXPpiyKGUBpAVB7dxqwyjQglyzp9DlJ6GQUsnKMeCngwcJ9cOdJ8QDADD3JV-OsTAgMDWInev3OoScGPLesQQB6dPEfySryrl28hQQYwMkQY8h6AQ3TnTwJjjZxtmTcuYD4sEQoQL71TZPjsBk0yLPtfxTOFDTbn1CCnDUEj5HclxHyA__&Key-Pair-Id=APKAIB5DGCGAQJ4GGIUQ&sessionID=36b83bd6-0d20-4b08-8fb2-bfa16f46992a";

    public static String tsParams = "?Policy=eyJTdGF0ZW1lbnQiOlt7IlJlc291cmNlIjoiaHR0cHM6Ly9obHMyLnZpZGVvcy5zcHJvdXR2aWRlby5jb20vN2Y0NTY2ZDkwM2QyYzFjYjk5ZDYyM2NjMDQ5NmYyYzAvZDM4OGNkMDkyNTZmYjgzZWExMDQ2MTBlNzc4YmVkMGUvKi50cz9zZXNzaW9uSUQ9MzZiODNiZDYtMGQyMC00YjA4LThmYjItYmZhMTZmNDY5OTJhIiwiQ29uZGl0aW9uIjp7IkRhdGVMZXNzVGhhbiI6eyJBV1M6RXBvY2hUaW1lIjoxNjg0ODc3MzIzfX19XX0_&Signature=DuQ46W1x9jtui7HPaO5OyMLz1zLK-H9j3B8IB2GFcFTLJcM8GELjgT803nVN47QMyoYDG07-YAIewCAjEH0VSOJQTpqQ2g4lrCDt91m4qO6iIWxYHn~4CqoFtn7kHee9mIutPd1fSj93TKctpKiA~xW0LRxE4xwAnSLp8gFYSE00ChBtvGQIKZsxnKxekS0zSv9vVVEKV7Kq8ICjM8mt1jUFTKVcDwgsg6nfevYugot8o3kPW65LvfPO100B-KochZg~C93dt~2YzOUcJuzQhVpDdLVJ792PZ4qhPag2rjNrJl-2AnOyTfSD1305kk6sHegfb3RGFGURy5~Lsa39sw__&Key-Pair-Id=APKAIB5DGCGAQJ4GGIUQ&sessionID=36b83bd6-0d20-4b08-8fb2-bfa16f46992a";

    public static ExecutorService executorService = Executors.newFixedThreadPool(4);

    public static void main(String[] args) {
        String folderName = "2"; //dat ten cho thu muc chua cac file ts, key và m3u8
        String listFilesName = "files.txt";
        String rootFileName = "720.m3u8";

        File file = new File(folderName);
        if (!file.exists()) {
            file.mkdir();
        } else {
            deleteFolder(file);
        }

        try {
            downloadUsingStream(makeUrl(rootFileName, params), folderName + "/" + rootFileName);

            File rootFile = new File(folderName + "/" + rootFileName);

            if (rootFile.exists()) {
                MediaPlaylistParser parser = new MediaPlaylistParser();
                MediaPlaylist playlist = parser.readPlaylist(Paths.get(folderName + "/" + rootFileName));
                System.out.println(playlist.mediaSegments());

                List<MediaSegment> segments = playlist.mediaSegments();
                for (MediaSegment segment : segments) {
                    Optional<SegmentKey> keyOp = segment.segmentKey();
//                    if (keyOp.isPresent()) {
//                        SegmentKey key = keyOp.get();
//                        String keyUri = key.uri().isPresent() ? key.uri().get() : "";
//                        if (keyUri.length() > 0) {
//                            downloadUsingStream(makeUrl(keyUri), folderName + "/" + keyUri);
//                        }
//                        Thread.sleep(100);
//                    }
                    String fileUri = segment.uri();
                    if (fileUri.length() > 0) {
                        executorService.execute(() -> {
                            try {
                                downloadUsingStream(makeUrl(fileUri, tsParams), folderName + "/" + fileUri);
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        });
                    }
                }
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    private static String makeUrl(String file, String params) {
        return url + file + params;
    }

    public static void deleteFolder(File folder) {
        File[] files = folder.listFiles();
        if (files != null) {
            for (File f : files) {
                if (f.isDirectory()) {
                    deleteFolder(f);
                } else {
                    f.delete();
                }
            }
        }
    }

    private static void downloadUsingStream(String urlStr, String file) throws IOException {
        URL url = new URL(urlStr);
        BufferedInputStream bis = new BufferedInputStream(url.openStream());
        FileOutputStream fis = new FileOutputStream(file);
        byte[] buffer = new byte[1024];
        int count = 0;
        while ((count = bis.read(buffer, 0, 1024)) != -1) {
            fis.write(buffer, 0, count);
        }
        fis.close();
        bis.close();
        System.out.println("Downloaded " + file);
    }

}

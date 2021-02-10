package no.microdata.datastore.repository;

import com.google.api.gax.paging.Page;
import com.google.cloud.storage.*;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.DriverManager;
import java.sql.SQLException;

public class CloudStorageRepositoryTest {

    @Test
    public void testAuth(){
        // If you don't specify credentials when constructing the client, the client library will
        // look for credentials via the environment variable GOOGLE_APPLICATION_CREDENTIALS.
//        Storage storage = StorageOptions.newBuilder().setProjectId(projectId).build().getService();
        Storage storage = StorageOptions.getDefaultInstance().getService();
        Bucket bucket = storage.get("test-datastore-bucket-microdata-1");
        Page<Blob> blobs = bucket.list();

        for (Blob blob : blobs.iterateAll()) {
                System.out.println(blob.getName());
        }
    }

    @Test
    public void testContent() throws IOException, SQLException {
        String bucketName = "test-datastore-bucket-microdata-1";
        String blobName = "test-datastore/dataset/GRUNNSTFDT_MOTTAK/GRUNNSTFDT_MOTTAK__1_0.db";
        Storage storage = StorageOptions.getDefaultInstance().getService();
        BlobId blobId = BlobId.of(bucketName, blobName);
        Blob blob = storage.get(blobId);
        System.out.println(blob.getSize());
        System.out.println(blob.getName());

        byte[] content = blob.getContent();

        String file = "/Users/vak/projects/github/microdata-test-datastore/src/test/resources/sqlite.db";
        FileOutputStream fos = new FileOutputStream(file);
        fos.write(content);
        fos.close();

        DriverManager.getConnection("jdbc:sqlite:" + file);


    }
}

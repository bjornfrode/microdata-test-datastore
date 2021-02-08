package no.microdata.datastore.repository;

import com.google.api.gax.paging.Page;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import org.junit.jupiter.api.Test;

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
}

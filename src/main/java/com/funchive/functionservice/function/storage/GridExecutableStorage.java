package com.funchive.functionservice.function.storage;

import com.funchive.functionservice.function.ExecutableStorage;
import com.funchive.functionservice.function.model.dto.FileDto;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSBuckets;
import com.mongodb.client.gridfs.model.GridFSUploadOptions;
import lombok.RequiredArgsConstructor;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GridExecutableStorage implements ExecutableStorage {
    private static final String BUCKET_NAME = "executables";
    private final MongoTemplate mongoTemplate;

    private GridFSBucket getGridFSBucket() {
        return GridFSBuckets.create(mongoTemplate.getDb(), BUCKET_NAME);
    }

    @Override
    public void storeExecutable(String fileId, FileDto fileDto) {
        GridFSUploadOptions options = new GridFSUploadOptions()
                .metadata(new Document()
                        .append("contentType", fileDto.getMimeType()));

        try {
            getGridFSBucket().uploadFromStream(
                    fileDto.getFilename(),
                    fileDto.getFileStream(),
                    options
            );
        } catch (Exception e) {
            throw new RuntimeException("Failed to store file", e);
        }
    }

    @Override
    public FileDto loadExecutable(String fileId) {
        try {
            FileDto fileDto = new FileDto();
            fileDto.setFileStream(getGridFSBucket().openDownloadStream(new ObjectId(fileId)));
            // TODO: Set filename and content type from GridFS metadata
            return fileDto;
        } catch (Exception e) {
            throw new RuntimeException("Failed to retrieve file", e);
        }
    }

    public void deleteExecutable(String fileId) {
        getGridFSBucket().delete(new ObjectId(fileId));
    }
} 
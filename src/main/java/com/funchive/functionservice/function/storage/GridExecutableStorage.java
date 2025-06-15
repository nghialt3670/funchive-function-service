package com.funchive.functionservice.function.storage;

import com.funchive.functionservice.function.ExecutableStorage;
import com.funchive.functionservice.function.model.dto.FileDto;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSBuckets;
import com.mongodb.client.gridfs.model.GridFSUploadOptions;
import lombok.RequiredArgsConstructor;
import org.bson.Document;
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
                        .append("contentType", fileDto.getMimeType())
                        .append("originalFilename", fileDto.getFilename()));

        try {
            getGridFSBucket().uploadFromStream(
                    fileId,  // Use fileId as the filename in GridFS
                    fileDto.getFileStream(),
                    options
            );
        } catch (Exception e) {
            throw new RuntimeException("Failed to store executable with ID: " + fileId, e);
        }
    }

    @Override
    public FileDto loadExecutable(String fileId) {
        try {
            var gridFSFile = getGridFSBucket().find(new Document("filename", fileId)).first();
            if (gridFSFile == null) {
                throw new RuntimeException("Executable not found with ID: " + fileId);
            }

            FileDto fileDto = new FileDto();
            fileDto.setFileStream(getGridFSBucket().openDownloadStream(fileId));

            // Set metadata from GridFS
            var metadata = gridFSFile.getMetadata();
            if (metadata != null) {
                fileDto.setMimeType(metadata.getString("contentType"));
                fileDto.setFilename(metadata.getString("originalFilename"));
            }

            // Fallback if no metadata
            if (fileDto.getFilename() == null) {
                fileDto.setFilename("main_executable");
            }
            if (fileDto.getMimeType() == null) {
                fileDto.setMimeType("application/octet-stream");
            }

            return fileDto;
        } catch (Exception e) {
            throw new RuntimeException("Failed to retrieve executable with ID: " + fileId, e);
        }
    }

    public void deleteExecutable(String fileId) {
        try {
            var gridFSFile = getGridFSBucket().find(new Document("filename", fileId)).first();
            if (gridFSFile != null) {
                getGridFSBucket().delete(gridFSFile.getObjectId());
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete executable with ID: " + fileId, e);
        }
    }
} 
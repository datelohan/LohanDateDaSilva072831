package lohan.seletivo.album.service;

import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.http.Method;
import java.io.InputStream;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lohan.seletivo.album.dto.AlbumImageResponse;
import lohan.seletivo.album.model.Album;
import lohan.seletivo.album.model.AlbumImage;
import lohan.seletivo.album.repository.AlbumImageRepository;
import lohan.seletivo.storage.MinioProperties;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AlbumImageService {

    private final AlbumService albumService;
    private final AlbumImageRepository albumImageRepository;
    private final MinioClient minioClient;
    private final MinioProperties minioProperties;

    public AlbumImageService(AlbumService albumService,
            AlbumImageRepository albumImageRepository,
            MinioClient minioClient,
            MinioProperties minioProperties) {
        this.albumService = albumService;
        this.albumImageRepository = albumImageRepository;
        this.minioClient = minioClient;
        this.minioProperties = minioProperties;
    }

    public List<AlbumImageResponse> upload(Long albumId, List<MultipartFile> files) {
        if (files == null || files.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Envie ao menos um arquivo");
        }
        Album album = albumService.getById(albumId);
        return files.stream().map(file -> uploadOne(album, file)).collect(Collectors.toList());
    }

    public List<AlbumImageResponse> list(Long albumId) {
        albumService.getById(albumId);
        return albumImageRepository.findByAlbumId(albumId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    private AlbumImageResponse uploadOne(Album album, MultipartFile file) {
        if (file.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Arquivo vazio");
        }
        if (file.getContentType() == null || !file.getContentType().startsWith("image/")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Arquivo deve ser uma imagem");
        }
        String originalName = file.getOriginalFilename() != null ? file.getOriginalFilename() : "arquivo";
        String objectKey = buildObjectKey(album.getId(), originalName);

        try (InputStream inputStream = file.getInputStream()) {
            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(minioProperties.getBucket())
                    .object(objectKey)
                    .contentType(file.getContentType())
                    .stream(inputStream, file.getSize(), -1)
                    .build());
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Falha ao enviar imagem", ex);
        }

        AlbumImage image = new AlbumImage();
        image.setAlbum(album);
        image.setObjectKey(objectKey);
        image.setOriginalName(originalName);
        image.setContentType(file.getContentType());
        image.setSizeBytes(file.getSize());
        AlbumImage saved = albumImageRepository.save(image);
        return toResponse(saved);
    }

    private AlbumImageResponse toResponse(AlbumImage image) {
        String url = buildPresignedUrl(image.getObjectKey());
        OffsetDateTime expiresAt = OffsetDateTime.now(ZoneOffset.UTC).plus(minioProperties.getPresignedTtl());
        return new AlbumImageResponse(
                image.getId(),
                image.getOriginalName(),
                image.getContentType(),
                image.getSizeBytes(),
                url,
                expiresAt
        );
    }

    private String buildObjectKey(Long albumId, String originalName) {
        String safeName = originalName.replaceAll("\\s+", "_");
        return "albums/" + albumId + "/" + UUID.randomUUID() + "_" + safeName;
    }

    private String buildPresignedUrl(String objectKey) {
        try {
            int expirySeconds = (int) minioProperties.getPresignedTtl().toSeconds();
            return minioClient.getPresignedObjectUrl(GetPresignedObjectUrlArgs.builder()
                    .method(Method.GET)
                    .bucket(minioProperties.getBucket())
                    .object(objectKey)
                    .expiry(expirySeconds)
                    .build());
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Falha ao gerar link", ex);
        }
    }
}

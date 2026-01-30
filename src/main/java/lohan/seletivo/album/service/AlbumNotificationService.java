package lohan.seletivo.album.service;

import lohan.seletivo.album.dto.AlbumCreatedNotification;
import lohan.seletivo.album.model.Album;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class AlbumNotificationService {

    private final SimpMessagingTemplate messagingTemplate;

    public AlbumNotificationService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public void notifyAlbumCreated(Album album) {
        AlbumCreatedNotification payload = new AlbumCreatedNotification(
                album.getId(),
                album.getTitle(),
                album.getCreatedAt()
        );
        messagingTemplate.convertAndSend("/topic/albums", payload);
    }
}

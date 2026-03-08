import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Note {
    private int id;
    private String title;
    private String note;

    DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");

    LocalDateTime creationdDate = LocalDateTime.now();
    LocalDateTime modifiedDate = LocalDateTime.now();
    String createdDate = creationdDate.format(dateFormatter);
    String dateModified = modifiedDate.format(dateFormatter);

    public int getPrivateId() {
        return id;
    }

    public String getPrivateTitle() {
        return  title;
    }

    public String getPrivateNote() {
        return note;
    }
}

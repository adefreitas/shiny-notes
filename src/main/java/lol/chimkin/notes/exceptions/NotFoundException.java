package lol.chimkin.notes.exceptions;

import java.util.UUID;

public class NotFoundException extends RuntimeException {
  public NotFoundException(UUID noteId) {
    super("Note id " + noteId + "does not exist");
  }
}

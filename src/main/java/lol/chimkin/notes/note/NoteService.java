package lol.chimkin.notes.note;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class NoteService {
  private final NoteRepository noteRepository;

  public NoteService(NoteRepository noteRepository) {
    this.noteRepository = noteRepository;
  }

  public List<Note> getNotes() {
    return noteRepository.findAll();
//    List.of(
//        new Note(
//            UUID.randomUUID(),
//            "Title",
//            "Content",
//            LocalDate.of(2022, Month.DECEMBER, 29),
//            null,
//            null,
//            ""))

  }

  public Note createNote(String title, String content, String tags) {
    return noteRepository.save(new Note(title, content, LocalDate.now(), tags));
  }
}

package lol.chimkin.notes.note;

import java.util.List;

import java.util.Optional;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/note")
public class NoteController {
  private final NoteService noteService;

  @Autowired
  public NoteController(NoteService noteService) {
    this.noteService = noteService;
  }

  @GetMapping()
  public List<Note> list(@RequestParam("soft_deleted") Optional<Boolean> softDeleted) {
    if (softDeleted.isPresent() && softDeleted.get()) {
      return noteService.getSoftDeletedNotes();
    }
    return noteService.getNotes();
  }

  @PostMapping
  public Note create(@RequestBody CreateNoteRequest body) {
    return noteService.createNote(body.title, body.body, body.tags);
  }

  @DeleteMapping(path = "{noteId}")
  public void delete(@PathVariable("noteId") UUID noteId) {
    noteService.softDelete(noteId);
  }

  @DeleteMapping
  public void delete() {
    noteService.hardDelete();
  }

  @NoArgsConstructor
  @AllArgsConstructor
  @Getter
  static class CreateNoteRequest {
    private String title;
    private String body;
    private String tags;
  }

}

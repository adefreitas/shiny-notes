package lol.chimkin.notes.note;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/note")
public class NoteController {
  private final NoteService noteService;

  @Autowired
  public NoteController(NoteService noteService) {
    this.noteService = noteService;
  }

  @GetMapping
  public List<Note> list() {
    return noteService.getNotes();
  }

  @PostMapping
  public Note create(@RequestBody CreateNoteRequest body) {
    return noteService.createNote(body.title, body.body, body.tags);
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

package lol.chimkin.notes;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import java.util.UUID;
import lol.chimkin.notes.note.NoteController;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework	.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;
import org.springframework.test.web.servlet.MockMvc;

@SqlGroup(value = {
	@Sql(statements = "DELETE FROM note WHERE 1 = 1", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
})
@SpringBootTest
@AutoConfigureMockMvc
class NotesApplicationTests {
	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Test
	@DisplayName("Should return an empty list when calling listing endpoint with no entries exist")
	void shouldReturnEmptyListWhenNoNotesExist() throws Exception {
		mockMvc.perform(get("/api/v1/note"))
				.andDo(print())
				.andExpect(jsonPath("$").isEmpty());
	}


	@Test
	@DisplayName("Should create note with valid request")
	void shouldCreateNoteWithValidRequest() throws Exception {
		var noteRequest = new NoteController.NoteRequest("Title", "Content", "the,tags");
		mockMvc.perform(
					post("/api/v1/note")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(noteRequest))
				)
				.andDo(print())
				.andExpect(status().isCreated());
	}

	@Test
	@DisplayName("Should fail to create a note with an invalid request")
	void shouldFailToCreateNoteWithInvalidRequest() throws Exception {
		mockMvc.perform(
					post("/api/v1/note")
						.contentType(MediaType.APPLICATION_JSON)
						.content("{}")
				)
				.andDo(print())
				.andExpect(status().isBadRequest());
	}

	@Test
	@DisplayName("Should return only existing note")
	void shouldReturnOnlyExistingNote() throws Exception {
		var noteRequest = new NoteController.NoteRequest("Title", "Content", "the,tags");

		mockMvc.perform(post("/api/v1/note").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(noteRequest)));

		mockMvc.perform(get("/api/v1/note"))
				.andDo(print())
				.andExpect(jsonPath("$").isNotEmpty())
				.andExpect(jsonPath("$.length()").value(1))
				.andExpect(jsonPath("$.[0].title").value("Title"))
				.andExpect(jsonPath("$.[0].content").value("Content"))
				.andExpect(jsonPath("$.[0].tags").value("the,tags"));

	}

	@Test
	@DisplayName("Should return only non soft deleted notes if no filter is provided")
	void shouldReturnOnlyNonSoftDeletedNotes() throws Exception {
		var note1Request = new NoteController.NoteRequest("Note 1", "Content 1", "the,tags,one");
		var result1 = mockMvc.perform(post("/api/v1/note").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(note1Request))).andReturn();

		var note2Request = new NoteController.NoteRequest("Note 2", "Content 2", "the,tags,two");
		mockMvc.perform(post("/api/v1/note").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(note2Request))).andReturn();

		var note1Id = JsonPath.read(result1.getResponse().getContentAsString(), "$.id");
		mockMvc.perform(delete("/api/v1/note/" + note1Id));

		mockMvc.perform(get("/api/v1/note"))
				.andDo(print())
				.andExpect(jsonPath("$").isNotEmpty())
				.andExpect(jsonPath("$.length()").value(1))
				.andExpect(jsonPath("$.[0].title").value("Note 2"))
				.andExpect(jsonPath("$.[0].content").value("Content 2"))
				.andExpect(jsonPath("$.[0].tags").value("the,tags,two"));
	}


	@Test
	@DisplayName("Should return only soft deleted notes if filter is provided")
	void shouldReturnOnlySoftDeletedNotes() throws Exception {
		var note1Request = new NoteController.NoteRequest("Note 1", "Content 1", "the,tags,one");
		var result1 = mockMvc.perform(post("/api/v1/note").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(note1Request))).andReturn();

		var note2Request = new NoteController.NoteRequest("Note 2", "Content 2", "the,tags,two");
		mockMvc.perform(post("/api/v1/note").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(note2Request))).andReturn();

		var note1Id = JsonPath.read(result1.getResponse().getContentAsString(), "$.id");
		mockMvc.perform(delete("/api/v1/note/" + note1Id));

		mockMvc.perform(get("/api/v1/note?soft_deleted=true"))
				.andDo(print())
				.andExpect(jsonPath("$").isNotEmpty())
				.andExpect(jsonPath("$.length()").value(1))
				.andExpect(jsonPath("$.[0].title").value("Note 1"))
				.andExpect(jsonPath("$.[0].content").value("Content 1"))
				.andExpect(jsonPath("$.[0].tags").value("the,tags,one"));
	}

	@Test
	@DisplayName("Should return all notes if no filter is provided")
	void shouldReturnAllNotes() throws Exception {
		var note1Request = new NoteController.NoteRequest("Note 1", "Content 1", "the,tags,one");
		mockMvc.perform(post("/api/v1/note").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(note1Request))).andReturn();

		var note2Request = new NoteController.NoteRequest("Note 2", "Content 2", "the,tags,two");
		mockMvc.perform(post("/api/v1/note").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(note2Request))).andReturn();

		mockMvc.perform(get("/api/v1/note"))
				.andDo(print())
				.andExpect(jsonPath("$").isNotEmpty())
				.andExpect(jsonPath("$.length()").value(2))
				.andExpect(jsonPath("$.[0].title").value("Note 1"))
				.andExpect(jsonPath("$.[0].content").value("Content 1"))
				.andExpect(jsonPath("$.[0].tags").value("the,tags,one"))
				.andExpect(jsonPath("$.[1].title").value("Note 2"))
				.andExpect(jsonPath("$.[1].content").value("Content 2"))
				.andExpect(jsonPath("$.[1].tags").value("the,tags,two"));
	}

	@Test
	@DisplayName("Should exclude hard deleted notes from soft deleted when filter is provided")
	void shouldExcludeHardDeletedNotesFromDeleted() throws Exception {
		var note1Request = new NoteController.NoteRequest("Note 1", "Content 1", "the,tags,one");
		var result1 = mockMvc.perform(post("/api/v1/note").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(note1Request))).andReturn();

		var note2Request = new NoteController.NoteRequest("Note 2", "Content 2", "the,tags,two");
		mockMvc.perform(post("/api/v1/note").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(note2Request))).andReturn();

		var note1Id = JsonPath.read(result1.getResponse().getContentAsString(), "$.id");
		mockMvc.perform(delete("/api/v1/note/" + note1Id));
		mockMvc.perform(delete("/api/v1/note"));

		mockMvc.perform(get("/api/v1/note?soft_deleted=true"))
				.andDo(print())
				.andExpect(jsonPath("$").isEmpty());
	}

	@Test
	@DisplayName("Should update note when note exists")
	void shouldUpdateNote() throws Exception {
		var note1Request = new NoteController.NoteRequest("Note 1", "Content 1", "the,tags,one");
		var result = mockMvc.perform(post("/api/v1/note").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(note1Request))).andReturn();

		mockMvc.perform(get("/api/v1/note"))
				.andDo(print())
				.andExpect(jsonPath("$").isNotEmpty())
				.andExpect(jsonPath("$.length()").value(1))
				.andExpect(jsonPath("$.[0].title").value("Note 1"))
				.andExpect(jsonPath("$.[0].content").value("Content 1"))
				.andExpect(jsonPath("$.[0].tags").value("the,tags,one"));

		var noteId = JsonPath.read(result.getResponse().getContentAsString(), "$.id");
		var note1UpdateRequest = new NoteController.NoteRequest("Updated note 1", "Updated content 1", "updated,the,tags,one");
		mockMvc.perform(put("/api/v1/note/" + noteId).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(note1UpdateRequest)));

		mockMvc.perform(get("/api/v1/note"))
				.andDo(print())
				.andExpect(jsonPath("$").isNotEmpty())
				.andExpect(jsonPath("$.length()").value(1))
				.andExpect(jsonPath("$.[0].title").value("Updated note 1"))
				.andExpect(jsonPath("$.[0].content").value("Updated content 1"))
				.andExpect(jsonPath("$.[0].tags").value("updated,the,tags,one"));
	}


	@Test
	@DisplayName("Should fail to create note when body is invalid")
	void shouldFailToCreateNoteWhenBodyIsInvalid() throws Exception {
		var note1Request = new NoteController.NoteRequest("Note 1", "Content 1", "the,tags,one");
		var result = mockMvc.perform(post("/api/v1/note").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(note1Request))).andReturn();

		mockMvc.perform(get("/api/v1/note"))
				.andDo(print())
				.andExpect(jsonPath("$").isNotEmpty())
				.andExpect(jsonPath("$.length()").value(1))
				.andExpect(jsonPath("$.[0].title").value("Note 1"))
				.andExpect(jsonPath("$.[0].content").value("Content 1"))
				.andExpect(jsonPath("$.[0].tags").value("the,tags,one"));

		var noteId = JsonPath.read(result.getResponse().getContentAsString(), "$.id");
		mockMvc.perform(put("/api/v1/note/" + noteId).contentType(MediaType.APPLICATION_JSON).content("{}")).andExpect(status().isBadRequest());
	}


	@Test
	@DisplayName("Should fail to create note when id doesnt exist")
	void shouldFailToCreateNoteWhenIdDoesNotExist() throws Exception {
		var note1Request = new NoteController.NoteRequest("Note 1", "Content 1", "the,tags,one");
		mockMvc.perform(post("/api/v1/note").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(note1Request))).andReturn();

		mockMvc.perform(get("/api/v1/note"))
				.andDo(print())
				.andExpect(jsonPath("$").isNotEmpty())
				.andExpect(jsonPath("$.length()").value(1))
				.andExpect(jsonPath("$.[0].title").value("Note 1"))
				.andExpect(jsonPath("$.[0].content").value("Content 1"))
				.andExpect(jsonPath("$.[0].tags").value("the,tags,one"));

		mockMvc.perform(put("/api/v1/note/" + UUID.randomUUID()).contentType(MediaType.APPLICATION_JSON).content("{}")).andExpect(status().isBadRequest());
	}
}

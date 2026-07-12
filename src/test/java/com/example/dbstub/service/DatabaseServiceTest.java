package com.example.dbstub.service;

import com.example.dbstub.entity.Request;
import com.example.dbstub.entity.Response;
import com.example.dbstub.entity.Task;
import com.example.dbstub.exception.DbException;
import com.example.dbstub.repository.RequestRepository;
import com.example.dbstub.repository.ResponseRepository;
import com.example.dbstub.repository.TaskRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DatabaseServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private ResponseRepository responseRepository;

    @Mock
    private RequestRepository requestRepository;

    @InjectMocks
    private DatabaseService databaseService;

    @Test
    void testSaveText_Success() {
        Task task = new Task();
        task.setId(1L);
        
        Response response = new Response();
        response.setId(1L);
        
        Request request = new Request();
        request.setId(1L);
        request.setTask(task);
        request.setResponse(response);

        when(taskRepository.save(any(Task.class))).thenReturn(task);
        when(responseRepository.save(any(Response.class))).thenReturn(response);
        when(requestRepository.save(any(Request.class))).thenReturn(request);

        Request result = databaseService.saveText("Test");

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void testUpdateResponse_Success() {
        Long requestId = 1L;
        String aiAnswer = "AI ответ";
        
        Response response = new Response();
        response.setAnswer("Old answer");
        response.setDescription("Old description");
        
        Request request = new Request();
        request.setId(requestId);
        request.setResponse(response);

        when(requestRepository.findById(requestId)).thenReturn(Optional.of(request));
        when(responseRepository.save(any(Response.class))).thenReturn(response);

        databaseService.updateResponse(requestId, aiAnswer);

        assertEquals(aiAnswer, response.getAnswer());
        assertEquals("Processed by Yandex GPT", response.getDescription());
    }

    @Test
    void testUpdateResponse_NotFound() {
        Long requestId = 999L;
        when(requestRepository.findById(requestId)).thenReturn(Optional.empty());

        assertThrows(DbException.class, () -> {
            databaseService.updateResponse(requestId, "AI ответ");
        });
    }

    @Test
    void testSaveError_Success() {
        Task task = new Task();
        task.setId(1L);
        
        Response response = new Response();
        response.setId(1L);
        
        Request request = new Request();
        request.setId(1L);

        when(taskRepository.save(any(Task.class))).thenReturn(task);
        when(responseRepository.save(any(Response.class))).thenReturn(response);
        when(requestRepository.save(any(Request.class))).thenReturn(request);

        databaseService.saveError("Invalid text", "Error message");

        verify(taskRepository).save(any(Task.class));
        verify(responseRepository).save(any(Response.class));
        verify(requestRepository).save(any(Request.class));
    }

    @Test
    void testGetRequestById_Success() {
        Long requestId = 1L;
        Request request = new Request();
        request.setId(requestId);
        
        when(requestRepository.findById(requestId)).thenReturn(Optional.of(request));

        Request result = databaseService.getRequestById(requestId);

        assertNotNull(result);
        assertEquals(requestId, result.getId());
    }

    @Test
    void testGetRequestById_NotFound() {
        Long requestId = 999L;
        when(requestRepository.findById(requestId)).thenReturn(Optional.empty());

        assertThrows(DbException.class, () -> {
            databaseService.getRequestById(requestId);
        });
    }
}

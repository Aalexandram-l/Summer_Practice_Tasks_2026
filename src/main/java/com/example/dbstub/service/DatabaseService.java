package com.example.dbstub.service;

import com.example.dbstub.entity.Task;
import com.example.dbstub.entity.Response;
import com.example.dbstub.entity.Request;
import com.example.dbstub.repository.TaskRepository;
import com.example.dbstub.repository.ResponseRepository;
import com.example.dbstub.repository.RequestRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class DatabaseService {

    private final TaskRepository taskRepository;
    private final ResponseRepository responseRepository;
    private final RequestRepository requestRepository;

    private static final String DEFAULT_RESPONSE = "The request has not yet been processed.";

    @Transactional
    public Request saveText(String text) {
        log.info("Saving text to database: {}", text);

        Task task = new Task();
        task.setQuestion(text);
        task.setDescription("Original text");
        Task savedTask = taskRepository.save(task);
        log.info("Task saved with id: {}", savedTask.getId());

        Response response = new Response();
        response.setAnswer(DEFAULT_RESPONSE);
        response.setDescription("Not processed yet");
        Response savedResponse = responseRepository.save(response);
        log.info("Response saved with id: {}", savedResponse.getId());

        Request request = new Request();
        request.setTask(savedTask);
        request.setResponse(savedResponse);
        request.setDescription("Request linking task and response");
        Request savedRequest = requestRepository.save(request);
        log.info("Request saved with id: {}", savedRequest.getId());

        return savedRequest;
    }

    @Transactional
    public void updateResponse(Long requestId, String aiAnswer) {
        log.info("Updating response for request id: {}", requestId);

        Request request = requestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found with id: " + requestId));

        Response response = request.getResponse();
        response.setAnswer(aiAnswer);
        response.setDescription("Processed by Yandex GPT");
        responseRepository.save(response);

        log.info("Response updated for request id: {}", requestId);
    }

    @Transactional
    public void saveError(String text, String errorMessage) {
        log.info("Saving error to database: {}", errorMessage);

        Task task = new Task();
        task.setQuestion(text);
        task.setDescription("ERROR: " + errorMessage);
        Task savedTask = taskRepository.save(task);
        log.info("Task saved with id: {}", savedTask.getId());

        Response response = new Response();
        response.setAnswer("ERROR: " + errorMessage);
        response.setDescription("Error - no keywords found");
        Response savedResponse = responseRepository.save(response);
        log.info("Error response saved with id: {}", savedResponse.getId());

        Request request = new Request();
        request.setTask(savedTask);
        request.setResponse(savedResponse);
        request.setDescription("Request with error: no keywords found");
        Request savedRequest = requestRepository.save(request);
        log.info("Error request saved with id: {}", savedRequest.getId());
    }

    public Request getRequestById(Long id) {
        return requestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Request not found with id: " + id));
    }
}

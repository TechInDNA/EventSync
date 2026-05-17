package com.techindna.eventsync.controller;

import com.techindna.eventsync.dto.PaginationRequestDto;
import com.techindna.eventsync.exception.BadRequestException;
import com.techindna.eventsync.exception.InternalServerErrorException;
import com.techindna.eventsync.exception.NotFoundException;
import com.techindna.eventsync.exception.UnauthorizedException;
import com.techindna.eventsync.service.QuestionService;
import com.techindna.eventsync.validator.DataValidator;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/sessions")
public class QuestionController {

    private final QuestionService questionService;
    private final DataValidator dataValidator;

    public QuestionController(QuestionService questionService, DataValidator dataValidator) {
        this.questionService = questionService;
        this.dataValidator = dataValidator;
    }

    @GetMapping("/{id}/questions")
    public ResponseEntity<?> getQuestionsBySessionId(
            @PathVariable String id,
            @RequestParam(required = false, defaultValue = "creationDate") String sort,
            @RequestParam(required = false, defaultValue = "1") String page,
            @RequestParam(required = false, defaultValue = "5") String size) {
        try {
            dataValidator.validatePageAndSize(page, size);

            int pageVal = Integer.parseInt(page);
            int sizeVal = Integer.parseInt(size);

            PaginationRequestDto pagination = new PaginationRequestDto(pageVal, sizeVal);

            return ResponseEntity.status(HttpStatus.OK)
                    .body(questionService.getQuestionsBySessionId(id, sort, pagination));
        } catch (BadRequestException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());
        } catch (InternalServerErrorException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An unexpected error occurred, please try again later");
        }
    }

    @PostMapping("/{id}/questions/{qid}/upvote")
    public ResponseEntity<?> upvoteQuestion(@PathVariable String id, @PathVariable String qid) {
        try {
            return ResponseEntity.status(HttpStatus.OK)
                    .body(questionService.upvoteQuestion(id, qid));
        } catch (BadRequestException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());
        } catch (UnauthorizedException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(e.getMessage());
        } catch (InternalServerErrorException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An unexpected error occurred, please try again later");
        }
    }
}
